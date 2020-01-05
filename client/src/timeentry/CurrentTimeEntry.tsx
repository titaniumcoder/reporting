import React, {useEffect, useMemo, useState} from "react";
import {
    Alert,
    Button,
    ButtonGroup,
    Col,
    Form,
    FormFeedback,
    FormGroup,
    Input,
    Label,
    Modal,
    ModalBody,
    ModalFooter,
    ModalHeader,
    Row
} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../rootReducer";
import reportingApi, {TimeEntry} from "../api/reportingApi";
import {currentTimeEntrySuccess} from "./timeentrySlice";
import * as moment from 'moment';
import ShowHours from "../components/ShowHours";
import {FormErrors, FormHandler} from "../components/FormHandler";

interface SavingTimeEntry {
    id: number;
    starting: string;
    ending: string;

    projectId: number;

    description: string;

    username: string;

    billed: boolean;
    billable: boolean;
}

const CurrentTimeEntry = () => {
    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);
    const [isOpen, setIsOpen] = useState(true);
    const [savingTimeEntry, setSavingTimeEntry] = useState<SavingTimeEntry>({
        ending: '',
        starting: '',
        username: '',
        projectId: -1,
        description: '',
        id: -1,
        billable: false,
        billed: false
    });

    const {loggedIn, admin, canBook, authToken} = useSelector((root: RootState) => root.auth);

    const isAllowed = useMemo(() => loggedIn && (admin || canBook), [loggedIn, admin, canBook]);

    const currentTimeEntry: TimeEntry | undefined = useSelector((root: RootState) => root.timeentry.currentTimeEntry);

    const validateTimeEntry = (te: SavingTimeEntry) => {
        let errors: FormErrors = {};

        if (!te.starting) {
            errors['starting'] = 'Starting is required';
        }
        if (!te.username) {
            errors['username'] = 'User is required';
        }
        return errors;
    };

    const updateTimeEntry = async (timeEntry: SavingTimeEntry) => {
        setRemoteError(undefined);
        try {
            await reportingApi.updateTimeEntry({
                username: timeEntry.username,
                starting: timeEntry.starting,
                projectId: timeEntry.projectId === -1 ? undefined : timeEntry.projectId,
                id: timeEntry.id,
                ending: timeEntry.ending ? timeEntry.ending : undefined,
                description: timeEntry.description ? timeEntry.description : undefined,
                billed: false,
                billable: false
            });
        } catch (err) {
            setRemoteError(err.toString);
        }
    };

    const {values, errors, submitDisabled, doSubmit, handleChange, handleChecked} = FormHandler<SavingTimeEntry>(savingTimeEntry, validateTimeEntry, updateTimeEntry);

    const dispatch = useDispatch();

    useEffect(() => {
        if (isAllowed && authToken) {
            const sse = new EventSource('/sse/current-timeentry?token=' + authToken);
            sse.onmessage = (e) => {
                dispatch(currentTimeEntrySuccess(JSON.parse(e.data)));
            };
            return () => {
                sse.close()
            };
        } else {

            return () => {
            }
        }
    }, [isAllowed, authToken, dispatch]);

    const stopCurrentEntry = async () => {
        const result = await reportingApi.stopTimeEntry(currentTimeEntry?.id || -1);

        const {id, description, projectId, billable, billed, starting, ending, username} = result.data;

        setSavingTimeEntry({
            id: id || -1,
            description: description || '',
            projectId: projectId || -1,
            billable,
            billed,
            starting,
            ending: ending || '',
            username
        });

        setIsOpen(true);
    };
    const startNewEntry = async () => {
        await reportingApi.startTimeEntry(undefined);
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
                Projekt:
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
            <Col xs={1}><Button size="lg" onClick={startNewEntry} color="info"><FontAwesomeIcon
                icon="play-circle"/></Button></Col>
        </Row>;

    const closeModal = () => {
        setIsOpen(false);
    };

    return (
        <div>
            {row}

            <Form onSubmit={doSubmit}>
                <Modal isOpen={isOpen} toggle={closeModal}>
                    <ModalHeader toggle={closeModal}>Saving entry</ModalHeader>
                    <ModalBody>
                        {remoteError &&
                        <Alert color="danger">{remoteError}</Alert>
                        }
                        <FormGroup>
                            <Label for="starting">Starting:</Label>
                            <Input
                                type="datetime-local"
                                name="starting"
                                valid={!errors['starting']}
                                invalid={!!errors['starting']}
                                value={values.starting}
                                onChange={handleChange}
                            />
                            <FormFeedback>{errors['email']}</FormFeedback>
                        </FormGroup>
                        <FormGroup>
                            <Label for="ending">Ending:</Label>
                            <Input
                                type="datetime-local"
                                name="ending"
                                valid={!errors['ending']}
                                invalid={!!errors['ending']}
                                value={values.ending}
                                onChange={handleChange}
                            />
                            <FormFeedback>{errors['email']}</FormFeedback>
                        </FormGroup>
                        <FormGroup>
                            <Label for="projectId">Project:</Label>
                            <Input
                                autoFocus={true}
                                type="select"
                                name="projectId"
                                valid={!errors['projectId']}
                                invalid={!!errors['projectId']}
                                value={values.projectId}
                                onChange={handleChange}>
                                <option value={-1}>- No Project -</option>
                                <option value={1}>Project 1</option>
                            </Input>
                            <FormFeedback>{errors['projectId']}</FormFeedback>
                        </FormGroup>
                        <FormGroup>
                            <Label for="description">Description:</Label>
                            <Input
                                type="text"
                                name="description"
                                valid={!errors['description']}
                                invalid={!!errors['description']}
                                value={values.description}
                                onChange={handleChange}
                            />
                            <FormFeedback>{errors['email']}</FormFeedback>
                        </FormGroup>
                        <FormGroup>
                            <Label for="username">User:</Label>
                            <Input
                                type="text"
                                name="username"
                                valid={!errors['username']}
                                invalid={!!errors['username']}
                                value={values.username}
                                onChange={handleChange}
                            />
                            <FormFeedback>{errors['email']}</FormFeedback>
                        </FormGroup>
                        <FormGroup check>
                            <Label check>
                                <Input
                                    type="checkbox"
                                    checked={values.billable}
                                    onChange={handleChecked}
                                    name="billable"
                                />
                                {' '}Billable?</Label>
                        </FormGroup>
                        <FormGroup check>
                            <Label check>
                                <Input
                                    type="checkbox"
                                    checked={values.billed}
                                    onChange={handleChecked}
                                    name="billed"
                                />
                                {' '}Billed?</Label>
                        </FormGroup>
                    </ModalBody>
                    <ModalFooter>
                        <Button type="submit" color="primary"
                                disabled={submitDisabled}>Save</Button>{' '}
                        <Button color="secondary" onClick={() => {
                            setIsOpen(false);
                        }}>Cancel</Button>
                    </ModalFooter>
                </Modal>
            </Form>
        </div>
    );
};

export default CurrentTimeEntry;
