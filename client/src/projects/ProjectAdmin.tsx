import React, {useEffect, useState} from 'react';

import './ProjectAdmin.css';
import {useDispatch, useSelector} from "react-redux";
import {
    Alert,
    Button,
    ButtonGroup,
    Form,
    FormFeedback,
    FormGroup,
    Input,
    Label,
    ModalBody,
    ModalFooter,
    ModalHeader,
    Table
} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {RootState} from "../rootReducer";
import Checkbox from "../components/Checkbox";
import reportingApi, {Project, UpdatingProject} from "../api/reportingApi";
import Modal from "reactstrap/lib/Modal";
import ShowHours, {toHours, toMinutes} from "../components/ShowHours";
import ShowRate from "../components/ShowRate";
import {FormHandler} from "../components/FormHandler";
import {fetchProjects} from "./projectSlice";
import {fetchClientList} from "../clients/clientSlice";

const EMPTY_PROJECT_FORM: UpdatingProject = {
    id: undefined,
    billable: true,
    clientId: '',
    maxHours: '',
    name: '',
    rate: 0
};


const ProjectAdmin = () => {
    const [newRecord, setNewRecord] = useState(false);
    const [editing, setEditing] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [instance, setInstance] = useState(EMPTY_PROJECT_FORM);
    const [editingId, setEditingId] = useState(undefined as number | undefined);


    const dispatch = useDispatch();
    const {projects, error, loading} = useSelector((state: RootState) => {
        const {projects, error, loading} = state.project;
        return {projects, error, loading};
    });
    const {loggedIn, email} = useSelector((state: RootState) => {
        const {loggedIn, email} = state.auth;
        return {loggedIn, email};
    });

    const updateRecord = async (project: UpdatingProject) => {
        await reportingApi.saveProject(toProject(project));
        setInstance(EMPTY_PROJECT_FORM);
        setEditingId(undefined);
        setEditing(false);
        setNewRecord(false);
        dispatch(fetchProjects());
    };

    const deleteRecord = async (id: string) => {
        await reportingApi.deleteClient(id);
        setDeleting(false);
        setInstance(EMPTY_PROJECT_FORM);
        dispatch(fetchProjects());
    };

    const toProjectForm = (project: Project) => ({
        clientId: project.clientId,
        id: project.id,
        name: project.name,
        maxHours: project.maxMinutes ? toHours(project.maxMinutes, false) : '0:00',
        rate: project.rateInCentsPerHour ? project.rateInCentsPerHour / 100 : 0,
    } as UpdatingProject);

    const toProject = (project: UpdatingProject) => ({
        id: project.id,
        name: project.name,
        maxMinutes: toMinutes(project.maxHours),
        rateInCentsPerHour: Math.round(project.rate * 100),
        billable: project.billable,
        clientId: project.clientId,
        clientName: project.clientId
    } as Project);

    const updatingRecord = (project: Project) => {
        setInstance(toProjectForm(project));
        setEditing(true);
        setNewRecord(false);
        setEditingId(project.id);
    };

    useEffect(() => {
        dispatch(fetchProjects());
    }, [loggedIn, email, dispatch]);

    const cancelEditing = () => {
        setInstance(EMPTY_PROJECT_FORM);
        setEditingId(undefined);
        setNewRecord(false);
        setEditing(false);
    };

    const cancelDeleting = () => {
        setInstance(EMPTY_PROJECT_FORM);
        setDeleting(false);
    };
    const showDeleteDialog = (project: Project) => {
        setInstance(toProjectForm(project));
        setDeleting(true);
    };

    const createNewRecord = () => {
        setInstance(EMPTY_PROJECT_FORM);
        setEditingId(undefined);
        setNewRecord(true);
        setEditing(true);
    };

    return (
        <div className="clientAdmin">
            <h1>Projects</h1>
            {error &&
            <Alert>{error}</Alert>
            }
            <Table color={loading ? 'dark' : 'light'}>
                <thead>
                <tr className="row">
                    <th className="col-3">Name</th>
                    <th className="col-3">Client</th>
                    <th className="col-1 text-center">Billable</th>
                    <th className="col-2">Max Time</th>
                    <th className="col-2">Rate</th>
                    <th className="text-right col-1">
                        <Button onClick={createNewRecord} size="sm"><FontAwesomeIcon icon="plus"/></Button>
                    </th>
                </tr>
                </thead>
                <tbody>
                {projects.map(project => (
                        (editing && !newRecord && editingId === project.id) ? (
                            <tr key={project.id}>
                                <td colSpan={6}>
                                    <UpdateForm instance={instance} cancel={cancelEditing} update={updateRecord}/>
                                </td>
                            </tr>
                        ) : (
                            <tr key={project.id} className="row">
                                <td className="col-3">{project.name}</td>
                                <td className="col-3">{project.clientName}</td>
                                <td className="col-1 text-center"><Checkbox value={project.billable}/></td>
                                <td className="col-2"><ShowHours minutes={project.maxMinutes}/></td>
                                <td className="col-2"><ShowRate rate={project.rateInCentsPerHour}/></td>
                                <td className="col-1 text-right">
                                    <ButtonGroup>
                                        <Button color="light" onClick={() => updatingRecord(project)}>
                                            <FontAwesomeIcon icon="pen"/>
                                        </Button>
                                        <Button color="danger" onClick={() => showDeleteDialog(project)}>
                                            <FontAwesomeIcon icon="trash"/>
                                        </Button>
                                    </ButtonGroup>
                                </td>
                            </tr>
                        )
                    )
                )}
                {newRecord && (
                    <tr>
                        <td colSpan={6}>
                            <UpdateForm instance={instance} cancel={cancelEditing} update={updateRecord}/>
                        </td>
                    </tr>
                )}
                </tbody>
            </Table>

            <DeleteDialog shown={deleting} instance={instance} cancel={cancelDeleting} execute={deleteRecord}/>
        </div>
    );
};

export default ProjectAdmin;

interface UpdateFormProps {
    instance: UpdatingProject;
    cancel: () => void;
    update: (client: UpdatingProject) => Promise<void>;
}

const UpdateForm = ({instance, cancel, update}: UpdateFormProps) => {
    const clientList = useSelector((state: RootState) => state.client.clientList);

    const loggedIn = useSelector((state: RootState) => state.auth.loggedIn);

    const dispatch = useDispatch();

    useEffect(() => {
        if (loggedIn) {
            dispatch(fetchClientList());
        }
    }, [dispatch, loggedIn]);

    const validator = (value) => {
        let errors: Map<string, string | undefined> = new Map();
        if (!value.name || value.name.length === 0) {
            errors.set('name', 'Name is required');
        }
        if (!value.clientId || value.clientId.length === 0) {
            errors.set('clientId', 'Client is required');
        }
        return errors;
    };

    const handleSave = async (client: UpdatingProject) => {
        try {
            setRemoteError(undefined);
            await update(client);
        } catch (err) {
            setRemoteError(err.toString());
        }
    };

    const {values, errors, submitDisabled, doSubmit, handleChange, handleChecked} = FormHandler(instance, validator, handleSave);

    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    return (
        <Form onSubmit={doSubmit}>
            <div>
                {remoteError &&
                <Alert color="danger">{remoteError}</Alert>
                }
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
                <FormGroup>
                    <Label for="name">Name:</Label>
                    <Input
                        autoFocus={true}
                        type="text"
                        name="name"
                        valid={!errors.get('name')}
                        invalid={!!errors.get('name')}
                        value={values.name}
                        onChange={handleChange}
                    />
                    <FormFeedback>{errors.get('name')}</FormFeedback>
                </FormGroup>
                <FormGroup>
                    <Label for="clientId">Client:</Label>
                    <Input
                        type="select"
                        name="clientId"
                        valid={!errors.get('clientId')}
                        invalid={!!errors.get('clientId')}
                        value={values.clientId}
                        onChange={handleChange}>
                        <option value=''>-- Please choose a client --</option>
                        {clientList.map(client => (
                            <option key={client.id} value={client.id}>{client.name}</option>
                        ))}
                    </Input>
                    <FormFeedback>{errors.get('name')}</FormFeedback>
                </FormGroup>
                <FormGroup>
                    <Label for="maxHours">Max Time:</Label>
                    <Input
                        type="text"
                        name="maxHours"
                        valid={!errors.get('maxHours')}
                        invalid={!!errors.get('maxHours')}
                        value={values.maxHours}
                        onChange={handleChange}
                    />
                    <FormFeedback>{errors.get('maxHours')}</FormFeedback>
                </FormGroup>
                <FormGroup>
                    <Label for="rate">Rate:</Label>
                    <Input
                        type="text"
                        name="rate"
                        valid={!errors.get('rate')}
                        invalid={!!errors.get('rate')}
                        value={values.rate}
                        onChange={handleChange}
                    />
                    <FormFeedback>{errors.get('rate')}</FormFeedback>
                </FormGroup>
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
    instance: UpdatingProject;
    cancel: () => void;
    shown: boolean;
    execute: (string) => Promise<void>;
}

const DeleteDialog = ({instance, cancel, execute, shown}: IDeleteDialogProps) => {
    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    const handleDelete = async () => {
        try {
            await execute(instance.id);
        } catch (err) {
            setRemoteError(err.toString());
        }
    };

    if (!shown) {
        return null;
    }

    return (
        <Modal isOpen={shown} toggle={cancel}>
            <ModalHeader toggle={cancel}>Deleting Project</ModalHeader>
            <ModalBody>
                {remoteError && (
                    <Alert color="danger">{remoteError}</Alert>
                )}
                <div>
                    Are you sure you want to delete project {instance.name}?
                </div>
            </ModalBody>
            <ModalFooter>
                <Button color="danger" type="submit" onClick={handleDelete}>Delete</Button>{' '}
                <Button color="secondary" onClick={cancel}>Cancel</Button>
            </ModalFooter>
        </Modal>
    );
};
