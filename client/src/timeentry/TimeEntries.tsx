import React, {useEffect, useMemo, useState} from 'react';

import './TimeEntries.css';
import {useDispatch, useSelector} from "react-redux";
import {
    Alert,
    Button,
    ButtonGroup,
    Form,
    ModalBody,
    ModalFooter,
    ModalHeader,
    Table,
    Input,
    Col,
    Row, FormGroup, Label
} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {RootState} from "../rootReducer";
import Checkbox from "../components/Checkbox";
import reportingApi, {TimeEntry, UpdatingTimeEntry} from "../api/reportingApi";
import Modal from "reactstrap/lib/Modal";
import ShowHours from "../components/ShowHours";
import ShowRate from "../components/ShowRate";
import {FormHandler} from "../components/FormHandler";
import {fetchClientList} from "../clients/clientSlice";
import {currentTimeEntrySuccess, fetchTimeEntries, selectTimeRange} from "./timeentrySlice";
import {SavingTimeEntry, TimeEntryForm, validateTimeEntry} from "./TimeEntryForm";
import moment from "moment";
import * as filesaver from 'file-saver';

const EMPTY_TIMEENTRY_FORM: SavingTimeEntry = {
    id: -1,
    username: '',
    starting: '',
    ending: '',
    projectId: -1,
    description: '',
    billed: false
};

const TimeEntries = () => {
    const [editing, setEditing] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [instance, setInstance] = useState(EMPTY_TIMEENTRY_FORM);
    const [editingId, setEditingId] = useState(undefined as number | undefined);
    const [selectedItems, setSelectedItems] = useState<number[]>([]);
    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    const nothingSelected = useMemo(() => selectedItems.length === 0, [selectedItems]);

    const dispatch = useDispatch();
    const {loggedIn, admin, canBook, canViewMoney, from, to, selectedClient, allEntries, timeentries, error, loading, currentTimeEntry} =
        useSelector((state: RootState) => {
            const {loggedIn, admin, canBook, canViewMoney} = state.auth;
            const {from, to, allEntries, timeentries, error, loading, currentTimeEntry} = state.timeentry;
            const {selectedClient} = state.client;

            return {
                loggedIn,
                admin,
                canBook,
                canViewMoney,
                from,
                to,
                selectedClient,
                allEntries,
                timeentries,
                error,
                loading,
                currentTimeEntry
            };
        });

    const noClientSelected = useMemo(() => !selectedClient, [selectedClient]);

    const timeStatus = useMemo(() => !!currentTimeEntry?.starting, [currentTimeEntry]);

    useEffect(() => {
        if (loggedIn) {
            dispatch(fetchTimeEntries(from, to, selectedClient, allEntries));
        }
    }, [dispatch, loggedIn, from, to, selectedClient, allEntries, timeStatus]);

    const updateRecord = async (updatingTimeEntry: UpdatingTimeEntry) => {
        await reportingApi.updateTimeEntry(updatingTimeEntry);
        setInstance(EMPTY_TIMEENTRY_FORM);
        setEditingId(undefined);
        setEditing(false);
        dispatch(fetchTimeEntries(from, to, selectedClient, allEntries));
    };

    const deleteRecord = async (ids: number[]) => {
        await Promise.all(ids.map(id =>
            reportingApi.deleteTimeEntry(id)
        ));
        setDeleting(false);
        setSelectedItems([]);
        dispatch(fetchTimeEntries(from, to, selectedClient, allEntries));
    };

    const selectItem = (evt) => {
        const id = parseInt(evt.target.name.substring(3));

        const current = [...selectedItems];
        const currentIdx = current.indexOf(id);

        if (currentIdx >= 0) {
            current.splice(currentIdx, 1);
            const target = [...current];
            setSelectedItems(target);
        } else {
            const target = [...current, id];
            setSelectedItems(target);
        }
    };

    const selectAll = () => {
        if (selectedItems.length === timeentries.length) {
            setSelectedItems([]);
        } else {
            setSelectedItems(timeentries.map(x => x.id || -1))
        }
    };

    const toTimeEntryForm = (te: TimeEntry) => ({
        id: te.id || -1,
        projectId: te.projectId || -1,
        starting: te.starting,
        ending: te.ending || '',
        username: te.username,
        description: te.description || '',
        billed: te.billed
    } as SavingTimeEntry);

    const updatingRecord = (timeEntry: TimeEntry) => {
        setInstance(toTimeEntryForm(timeEntry));
        setEditing(true);
        setEditingId(timeEntry.id);
    };

    const startingDuplicateEntry = async (timeEntry: TimeEntry) => {
        const result = await reportingApi.startTimeEntry(timeEntry.id);
        dispatch(currentTimeEntrySuccess(result.data));
    };

    const cancelEditing = () => {
        setInstance(EMPTY_TIMEENTRY_FORM);
        setEditingId(undefined);
        setEditing(false);
    };

    const cancelDeleting = () => {
        setInstance(EMPTY_TIMEENTRY_FORM);
        setDeleting(false);
    };

    const showDeleteDialog = (evt) => {
        evt.preventDefault();
        setDeleting(true);
    };

    const togglBilled = async (evt) => {
        evt.preventDefault();
        await reportingApi.togglTimeEntries(selectedItems);
        dispatch(fetchTimeEntries(from, to, selectedClient, allEntries));
    };

    const createExcel = async (evt) => {
        evt.preventDefault();

        const name = evt.target.value;

        if (selectedClient != null) {
            setRemoteError(undefined);
            try {
                const timesheet = await reportingApi.timesheet(selectedClient, name !== 'fullsheet');
                if (timesheet.status === 204) {
                    setRemoteError('No data for the timesheet available');
                } else {
                    filesaver.saveAs(timesheet.data, timesheet.headers['filename'], {
                        autoBom: false
                    })
                }
            } catch (err) {
                setRemoteError('Error while trying to get excel: ' + err.toString());
            }
        } else {
            setRemoteError('Select a client first');
        }
    };

    return (
        <div className="timeEntry">
            <h1 className="mt-4">Time-Entries</h1>
            {error &&
            <Alert color="danger">{error}</Alert>
            }
            {remoteError &&
            <Alert color="danger">{remoteError}</Alert>
            }
            <Form onSubmit={(e) => {
                e.preventDefault();
            }}>
                <Row>
                    <Col>
                        <FormGroup>
                            <Label for="from">From:</Label>
                            <Input type="date"
                                   value={from}
                                   onChange={(e) => dispatch(selectTimeRange({
                                       from: e.target.value,
                                       to,
                                       clientId: selectedClient,
                                       allEntries
                                   }))}/>
                        </FormGroup>
                    </Col>
                    <Col>
                        <FormGroup>
                            <Label for="to">To:</Label>
                            <Input type="date"
                                   value={to}
                                   onChange={(e) => dispatch(selectTimeRange({
                                       from,
                                       to: e.target.value,
                                       clientId: selectedClient,
                                       allEntries
                                   }))}/>
                        </FormGroup>
                    </Col>
                    <Col className="my-auto">
                        <FormGroup check>
                            <Label check>
                                <Input
                                    type="checkbox"
                                    checked={allEntries}
                                    onChange={(e) => {
                                        const checked = e.target.checked;
                                        if (checked) {
                                            let iFrom = from;
                                            let iTo = to;
                                            if (!from) {
                                                iFrom = moment().startOf("month").format("YYYY-MM-DD");
                                            }
                                            if (!from) {
                                                iTo = moment().endOf("month").format("YYYY-MM-DD");
                                            }
                                            dispatch(selectTimeRange({
                                                from: iFrom, to: iTo, clientId: selectedClient, allEntries: true
                                            }));
                                        } else {
                                            dispatch(selectTimeRange({
                                                from, to, clientId: selectedClient, allEntries: false
                                            }));
                                        }
                                    }}
                                    name="allEntries"
                                />
                                {' '}All Entries?</Label>
                        </FormGroup>
                    </Col>
                    <Col className="text-right my-auto" xs="auto">
                        {canViewMoney ?
                            <>
                                <Button color="primary" value="worksheet" disabled={noClientSelected}
                                        onClick={createExcel}>Create
                                    Worksheet</Button><br/>
                                <Button color="secondary" value="fullsheet" disabled={noClientSelected}
                                        onClick={createExcel}>Create Full
                                    Sheet</Button>
                            </> : <div/>
                        }
                    </Col>
                    <Col className="text-right" xs="auto">
                        <Button color="light" onClick={() => {
                            dispatch(selectTimeRange({
                                from: moment(from).add(-1, "month").format("YYYY-MM-DD"),
                                to: moment(to).add(-1, "month").format("YYYY-MM-DD"),
                                clientId: selectedClient, allEntries
                            }))
                        }}>Previous Month</Button><br/>
                        <Button color="light" onClick={() => {
                            dispatch(selectTimeRange({
                                from: moment(from).add(1, "month").format("YYYY-MM-DD"),
                                to: moment(to).add(1, "month").format("YYYY-MM-DD"),
                                clientId: selectedClient, allEntries
                            }))
                        }}>Next Month</Button><br/>
                    </Col>
                </Row>
            </Form>
            <Table color={loading ? 'dark' : 'light'} size="sm">
                <thead>
                <tr>
                    {(canBook || admin) &&
                    <th className="text-center">
                        <input
                            type="checkbox"
                            checked={selectedItems.length === timeentries.length}
                            onChange={selectAll}
                        />
                    </th>
                    }
                    <th className="text-center">Date</th>
                    <th className="text-center">Start</th>
                    <th className="text-center">End</th>
                    <th>Project</th>
                    <th>Description</th>
                    <th className="d-none d-md-block">User</th>
                    <th className="text-center d-none d-md-block">Billed</th>
                    <th className="text-right">Duration</th>
                    {(canViewMoney || admin) &&
                    <th className="text-right d-none d-md-block">Earning</th>
                    }
                    <th className="text-right">
                        <ButtonGroup>
                            {admin &&
                            <Button color="danger" onClick={showDeleteDialog} disabled={nothingSelected}>
                                <FontAwesomeIcon icon="trash"/>
                            </Button>
                            }
                            {admin &&
                            <Button color="light" onClick={togglBilled} disabled={nothingSelected}>
                                <FontAwesomeIcon icon="toggle-on"/>
                            </Button>
                            }
                        </ButtonGroup>
                    </th>
                </tr>
                </thead>
                <tbody>
                {timeentries.map((timeentry, idx, entries) => {
                        const selected = selectedItems.indexOf(timeentry.id || -1) >= 0;

                        const sameDate = idx > 0 && entries[idx - 1].date === timeentry.date;

                        return (
                            (editing && editingId === timeentry.id) ? (
                                <tr key={timeentry.id}>
                                    <td colSpan={6}>
                                        <UpdateForm instance={instance} cancel={cancelEditing} update={updateRecord}/>
                                    </td>
                                </tr>
                            ) : (
                                <tr key={timeentry.id}>
                                    {(canBook || admin) &&
                                    <td className="text-center">
                                        <input type="checkbox"
                                               name={`id-${timeentry.id}`}
                                               checked={selected}
                                               onChange={selectItem}/>
                                    </td>
                                    }
                                    <td className="text-center d-none d-md-block">{sameDate || <ShowDate date={timeentry.date}/>}</td>
                                    <td className="text-center"><ShowTime time={timeentry.starting}/></td>
                                    <td className="text-center"><ShowTime time={timeentry.ending}/></td>
                                    <td>{timeentry.projectName}</td>
                                    <td>{timeentry.description}</td>
                                    <td className="d-none d-md-block">{timeentry.username}</td>
                                    <td className="text-center d-none d-md-block"><Checkbox value={timeentry.billed}/></td>
                                    <td className="text-right"><ShowHours minutes={timeentry.timeUsed}/></td>
                                    {(canViewMoney || admin) &&
                                    <td className="text-right d-none d-md-block"><ShowRate rate={timeentry.amount * 100}/></td>
                                    }
                                    <td className="text-right">
                                        {(admin || canBook) &&
                                        <Button color="light" onClick={() => updatingRecord(timeentry)}>
                                            <FontAwesomeIcon icon="pen"/>
                                        </Button>
                                        }
                                        {(admin || canBook) && !timeStatus &&
                                        <Button color="light" onClick={() => startingDuplicateEntry(timeentry)}>
                                            <FontAwesomeIcon icon="play-circle"/>
                                        </Button>
                                        }
                                    </td>
                                </tr>
                            )
                        )
                    }
                )}
                </tbody>
            </Table>

            <DeleteDialog shown={deleting} instances={selectedItems} cancel={cancelDeleting} execute={deleteRecord}/>
        </div>
    );
};

export default TimeEntries;

interface UpdateFormProps {
    instance: SavingTimeEntry;
    cancel: () => void;
    update: (client: SavingTimeEntry) => Promise<void>;
}

const UpdateForm = ({instance, cancel, update}: UpdateFormProps) => {
    const projectList = useSelector((state: RootState) => state.project.projectList);

    const loggedIn = useSelector((state: RootState) => state.auth.loggedIn);

    const dispatch = useDispatch();

    useEffect(() => {
        if (loggedIn) {
            dispatch(fetchClientList());
        }
    }, [dispatch, loggedIn]);

    const handleSave = async (te: SavingTimeEntry) => {
        try {
            setRemoteError(undefined);
            await update(te);
        } catch (err) {
            setRemoteError(err.toString());
        }
    };

    const {values, errors, submitDisabled, doSubmit, handleChange, handleChecked} = FormHandler(instance, validateTimeEntry, handleSave);

    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    return (
        <Form onSubmit={doSubmit}>
            <div>
                {remoteError &&
                <Alert color="danger">{remoteError}</Alert>
                }
                <TimeEntryForm errors={errors} values={values} handleChange={handleChange} handleChecked={handleChecked}
                               projectList={projectList}/>
                <ButtonGroup>
                    <Button type="submit" color="primary"
                            disabled={submitDisabled}>Save</Button>{' '}
                    <Button color="secondary" onClick={() => {
                        cancel();
                    }}>Cancel</Button>
                </ButtonGroup>
            </div>
        </Form>
    );
};

interface IDeleteDialogProps {
    instances: number[];
    cancel: () => void;
    shown: boolean;
    execute: (NumberArray) => Promise<void>;
}

const DeleteDialog = ({instances, cancel, execute, shown}: IDeleteDialogProps) => {
    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    const handleDelete = async () => {
        try {
            await execute(instances);
        } catch (err) {
            setRemoteError(err.toString());
        }
    };

    if (!shown) {
        return null;
    }

    return (
        <Modal isOpen={shown} toggle={cancel}>
            <ModalHeader toggle={cancel}>Deleting Timeentry</ModalHeader>
            <ModalBody>
                {remoteError && (
                    <Alert color="danger">{remoteError}</Alert>
                )}
                <div>
                    Are you sure you want to
                    delete {instances.length} {instances.length === 1 ? 'timeentry' : 'timeentries'}?
                </div>
            </ModalBody>
            <ModalFooter>
                <Button color="danger" type="submit" onClick={handleDelete}>Delete</Button>{' '}
                <Button color="secondary" onClick={cancel}>Cancel</Button>
            </ModalFooter>
        </Modal>
    );
};

const ShowDate = ({date}) => {
    if (!date) {
        return <span/>
    } else {
        const x = moment(date).format("DD.MM.YY");
        return <span>{x}</span>
    }
};

const ShowTime = ({time}) => {
    if (!time) {
        return <span/>
    } else {
        const x = moment(time).format("HH:mm");
        return <span>{x}</span>
    }
};
