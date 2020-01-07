import React, {useEffect, useMemo, useState} from 'react';

import './TimeEntries.css';
import {useDispatch, useSelector} from "react-redux";
import {Alert, Button, ButtonGroup, Form, ModalBody, ModalFooter, ModalHeader, Table, Input} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {RootState} from "../rootReducer";
import Checkbox from "../components/Checkbox";
import reportingApi, {TimeEntry, UpdatingTimeEntry} from "../api/reportingApi";
import Modal from "reactstrap/lib/Modal";
import ShowHours from "../components/ShowHours";
import ShowRate from "../components/ShowRate";
import {FormHandler} from "../components/FormHandler";
import {fetchClientList} from "../clients/clientSlice";
import {fetchTimeEntries} from "./timeentrySlice";
import {SavingTimeEntry, TimeEntryForm, validateTimeEntry} from "./TimeEntryForm";
import moment from "moment";

const EMPTY_TIMEENTRY_FORM: SavingTimeEntry = {
    id: -1,
    username: '',
    starting: '',
    ending: '',
    projectId: -1,
    description: '',
    billed: false,
    billable: true
};

const TimeEntries = () => {
    const [editing, setEditing] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [instance, setInstance] = useState(EMPTY_TIMEENTRY_FORM);
    const [editingId, setEditingId] = useState(undefined as number | undefined);
    const [selectedItems, setSelectedItems] = useState<number[]>([]);
    
    const nothingSelected = useMemo(() => selectedItems.length === 0, [selectedItems]);

    const dispatch = useDispatch();
    const {loggedIn, admin, canBook, canViewMoney, from, to, clientId, allEntries, timeentries, error, loading, currentTimeEntry} =
        useSelector((state: RootState) => {
            const {loggedIn, admin, canBook, canViewMoney} = state.auth;
            const {from, to, clientId, allEntries, timeentries, error, loading, currentTimeEntry} = state.timeentry;
            return {
                loggedIn,
                admin,
                canBook,
                canViewMoney,
                from,
                to,
                clientId,
                allEntries,
                timeentries,
                error,
                loading,
                currentTimeEntry
            };
        });

    useEffect(() => {
        if (loggedIn) {
            dispatch(fetchTimeEntries(from, to, clientId, allEntries));
        }
    }, [dispatch, loggedIn, from, to, clientId, currentTimeEntry, allEntries]);

    const updateRecord = async (updatingTimeEntry: UpdatingTimeEntry) => {
        await reportingApi.updateTimeEntry(updatingTimeEntry);
        setInstance(EMPTY_TIMEENTRY_FORM);
        setEditingId(undefined);
        setEditing(false);
        dispatch(fetchTimeEntries(from, to, clientId, allEntries));
    };

    const deleteRecord = async (ids: number[]) => {
        await Promise.all(ids.map(id =>
            reportingApi.deleteTimeEntry(id)
        ));
        setDeleting(false);
        setSelectedItems([]);
        dispatch(fetchTimeEntries(from, to, clientId, allEntries));
    };

    const selectItem = (evt) => {
        const id = parseInt(evt.target.name.substring(3));

        const current = [...selectedItems];
        const currentIdx = current.indexOf(id);

        if (currentIdx >= 0) {
            current.splice(currentIdx, 1)
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
        billable: te.billable,
        billed: te.billed
    } as SavingTimeEntry);

    const updatingRecord = (timeEntry: TimeEntry) => {
        setInstance(toTimeEntryForm(timeEntry));
        setEditing(true);
        setEditingId(timeEntry.id);
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
        // TODO find selected items and change them all (after validating they are all the same!)
        await reportingApi.togglTimeEntries(selectedItems);
        dispatch(fetchTimeEntries(from, to, clientId, allEntries));
    };

    return (
        <div className="timeEntry">
            <h1>Time-Entries</h1>
            {error &&
            <Alert>{error}</Alert>
            }
            <p>From: {from}<br/>To: {to}<br/>ClientId: {clientId}<br/>AllEntries <Checkbox value={allEntries}/></p>
            <Table color={loading ? 'dark' : 'light'}>
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
                    <th>Date</th>
                    <th>Start</th>
                    <th>End</th>
                    <th>Project</th>
                    <th>User</th>
                    <th>Billable</th>
                    <th>Billed</th>
                    <th>Duration</th>
                    {(canViewMoney || admin) &&
                    <th>Earning</th>
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
                {timeentries.map(timeentry => {
                        const selected = selectedItems.indexOf(timeentry.id || -1) >= 0;

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
                                        <Input type="checkbox"
                                               name={`id-${timeentry.id}`}
                                               checked={selected}
                                               onChange={selectItem}/>
                                    </td>
                                    }
                                    <td><ShowDate date={timeentry.date} /></td>
                                    <td><ShowTime time={timeentry.starting} /></td>
                                    <td><ShowTime time={timeentry.ending} /></td>
                                    <td>{timeentry.projectName}</td>
                                    <td>{timeentry.username}</td>
                                    <td><Checkbox value={timeentry.billable}/></td>
                                    <td><Checkbox value={timeentry.billed}/></td>
                                    <td><ShowHours minutes={timeentry.timeUsed}/></td>
                                    {(canViewMoney || admin) &&
                                    <td><ShowRate rate={timeentry.amount}/></td>
                                    }
                                    <td className="text-right">
                                        {(admin || canBook) &&
                                        <Button color="light" onClick={() => updatingRecord(timeentry)}>
                                            <FontAwesomeIcon icon="pen"/>
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

type NumberArray = number[];

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
        return <span />
    } else {
        const x = moment(date).format("DD.MM.YY")
        return <span>{x}</span>
    }
};

const ShowTime = ({time}) => {
    if (!time) {
        return <span />
    } else {
        const x = moment(time).format("HH:mm")
        return <span>{x}</span>
    }
};
