import React, {useEffect, useState} from "react";
import {Alert, Button, ButtonGroup, Col, Form, Modal, ModalBody, ModalFooter, ModalHeader, Row} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../rootReducer";
import reportingApi, {TimeEntry} from "../api/reportingApi";
import {currentTimeEntrySuccess} from "./timeentrySlice";
import * as moment from 'moment';
import ShowHours from "../components/ShowHours";
import {FormHandler, SaveFunc, ValidatorFunc} from "../components/FormHandler";
import {fetchProjectList} from "../projects/projectSlice";
import {SavingTimeEntry, TimeEntryForm, toUpdatingTimeEntry, validateTimeEntry} from "./TimeEntryForm";

const CurrentTimeEntry = () => {
    const [remoteError, setRemoteError] = useState('');
    const [isOpen, setIsOpen] = useState(false);
    const [savingTimeEntry, setSavingTimeEntry] = useState<SavingTimeEntry>({
        ending: '',
        starting: '',
        username: '',
        projectId: -1,
        description: '',
        id: -1,
        billed: false
    });

    const currentTimeEntry: TimeEntry | undefined = useSelector((root: RootState) => root.timeentry.currentTimeEntry);

    const updateTimeEntry = async (timeEntry: SavingTimeEntry) => {
        setRemoteError('');
        try {
            await reportingApi.updateTimeEntry(toUpdatingTimeEntry(timeEntry));
            setIsOpen(false);
        } catch (err) {
            try {
                setRemoteError(err.toString);
            } catch (err2) {
                console.log('Could not set remote error: ', err);
            }
        }
    };

    const dispatch = useDispatch();

    useEffect(() => {
        const sse = new EventSource(`/sse/current-timeentry`);
        sse.onmessage = (e) => {
            const parsed = JSON.parse(e.data);
            if (!parsed.id) {
                dispatch(currentTimeEntrySuccess(undefined))
            } else {
                dispatch(currentTimeEntrySuccess(parsed));
            }
        };

        dispatch(fetchProjectList());

        return () => {
            sse.close()
        };
    }, [dispatch]);

    const stopCurrentEntry = async () => {
        const result = await reportingApi.stopTimeEntry(currentTimeEntry?.id || -1);

        dispatch(currentTimeEntrySuccess(undefined));

        const {id, description, projectId, billed, starting, ending, username} = result.data;

        setSavingTimeEntry({
            id: id || -1,
            description: description || '',
            projectId: projectId || -1,
            billed,
            starting,
            ending: ending || '',
            username
        });

        setIsOpen(true);
    };
    const startNewEntry = async () => {
        const result = await reportingApi.startTimeEntry(undefined);
        dispatch(currentTimeEntrySuccess(result.data));
    };

    let starting = '';
    let ending = '';

    if (currentTimeEntry) {
        if (currentTimeEntry.starting) {
            starting = moment.default(currentTimeEntry.starting).format('DD.MM.YY HH:mm');
        }
        if (currentTimeEntry.ending) {
            ending = moment.default(currentTimeEntry.ending).format('HH:mm');
        }
    }

    const row = currentTimeEntry ?
        <Row className="mt-3 mb-2 bg-info">
            <Col xs={2} className="my-auto">
                {starting} - {ending}
            </Col>
            <Col xs="auto" className="my-auto">
                Project:
            </Col>
            <Col xs={2} className="my-auto">
                {currentTimeEntry.projectName}
            </Col>

            <Col className="my-auto">
                {currentTimeEntry.description}
            </Col>

            <Col xs="auto" className="my-auto text-white text-right">
                <ShowHours minutes={currentTimeEntry?.timeUsed} decimal={false}/>
            </Col>
            <Col xs="auto">
                <ButtonGroup>
                    <Button size="lg" onClick={stopCurrentEntry} color="info">
                        <FontAwesomeIcon icon="stop-circle"/>
                    </Button>
                </ButtonGroup>
            </Col>
        </Row> :
        <Row className="bg-light mt-2 mb-2">
            <Col/>
            <Col xs={1}><Button size="lg" onClick={startNewEntry} color="info">
                <FontAwesomeIcon icon="play-circle"/>
            </Button></Col>
        </Row>;

    const closeModal = () => {
        setIsOpen(false);
    };

    return (
        <div>
            {row}

            {isOpen &&
            <UpdateDialog
                savingTimeEntry={savingTimeEntry}
                validateTimeEntry={validateTimeEntry}
                updateTimeEntry={updateTimeEntry}
                closeModal={closeModal}
                remoteError={remoteError}
                isOpen={isOpen}
            />
            }
        </div>
    );
};

export default CurrentTimeEntry;

interface UpdateDialogProps {
    savingTimeEntry: SavingTimeEntry;
    validateTimeEntry: ValidatorFunc<SavingTimeEntry>;
    updateTimeEntry: SaveFunc<SavingTimeEntry>;
    closeModal: () => void;
    remoteError: string | undefined;
    isOpen: boolean;
}

const UpdateDialog = ({savingTimeEntry, validateTimeEntry, updateTimeEntry, closeModal, remoteError, isOpen}: UpdateDialogProps) => {
    const {values, errors, submitDisabled, doSubmit, handleChange, handleChecked} =
        FormHandler<SavingTimeEntry>(savingTimeEntry, validateTimeEntry, updateTimeEntry);

    const projectList = useSelector((root: RootState) => root.project.projectList);

    return (
        <Form onSubmit={doSubmit}>
            <Modal isOpen={isOpen} toggle={closeModal}>
                <ModalHeader toggle={closeModal}>Saving entry</ModalHeader>
                <ModalBody>
                    {remoteError &&
                    <Alert color="danger">{remoteError}</Alert>
                    }
                    <TimeEntryForm errors={errors} values={values} handleChange={handleChange}
                                   handleChecked={handleChecked} projectList={projectList}/>
                </ModalBody>
                <ModalFooter>
                    <Button type="submit" color="primary"
                            disabled={submitDisabled} onClick={doSubmit}>Save</Button>{' '}
                    <Button color="secondary" onClick={closeModal}>Cancel</Button>
                </ModalFooter>
            </Modal>
        </Form>
    )
};
