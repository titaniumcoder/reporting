import React, {useEffect, useState} from 'react';

import './ClientAdmin.css';
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
import {fetchClients} from "./clientSlice";
import reportingApi, {Client, UpdatingClient} from "../api/reportingApi";
import Modal from "reactstrap/lib/Modal";
import ShowHours, {toHours, toMinutes} from "../components/ShowHours";
import ShowRate from "../components/ShowRate";
import {FormErrors, FormHandler} from "../components/FormHandler";

const EMPTY_CLIENT_FORM: UpdatingClient = {
    active: true,
    clientId: '',
    maxHours: '',
    name: '',
    notes: '',
    rate: 0
};


const ClientAdmin = () => {
    const [newRecord, setNewRecord] = useState(false);
    const [editing, setEditing] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [instance, setInstance] = useState(EMPTY_CLIENT_FORM);
    const [editingId, setEditingId] = useState(undefined as string | undefined);

    const dispatch = useDispatch();
    const {clients, error, loading} = useSelector((state: RootState) => {
        const {clients, error, loading} = state.client;
        return {clients, error, loading};
    });
    const {loggedIn, email} = useSelector((state: RootState) => {
        const {loggedIn, email} = state.auth;
        return {loggedIn, email};
    });

    const updateRecord = async (client: UpdatingClient) => {
        await reportingApi.saveClient(toClient(client));
        setInstance(EMPTY_CLIENT_FORM);
        setEditingId(undefined);
        setEditing(false);
        setNewRecord(false);
        dispatch(fetchClients());
    };

    const deleteRecord = async (id: string) => {
        await reportingApi.deleteClient(id);
        setDeleting(false);
        setInstance(EMPTY_CLIENT_FORM);
        dispatch(fetchClients());
    };

    const toClientForm = (client: Client) => ({
        active: client.active,
        clientId: client.clientId,
        name: client.name,
        notes: client.notes || '',
        maxHours: client.maxMinutes ? toHours(client.maxMinutes, false) : '0:00',
        rate: client.rateInCentsPerHour ? client.rateInCentsPerHour / 100 : 0,
    } as UpdatingClient);

    const toClient = (client: UpdatingClient) => ({
        active: client.active,
        clientId: client.clientId,
        name: client.name,
        notes: client.notes === '' ? undefined : client.notes,
        maxMinutes: toMinutes(client.maxHours),
        rateInCentsPerHour: Math.round(client.rate * 100)
    } as Client);

    const updatingRecord = (client: Client) => {
        setInstance(toClientForm(client));
        setEditing(true);
        setNewRecord(false);
        setEditingId(client.clientId);
    };

    useEffect(() => {
        dispatch(fetchClients());
    }, [loggedIn, email, dispatch]);

    const cancelEditing = () => {
        setInstance(EMPTY_CLIENT_FORM);
        setEditingId(undefined);
        setNewRecord(false);
        setEditing(false);
    };

    const cancelDeleting = () => {
        setInstance(EMPTY_CLIENT_FORM);
        setDeleting(false);
    };
    const showDeleteDialog = (client: Client) => {
        setInstance(toClientForm(client));
        setDeleting(true);
    };

    const createNewRecord = () => {
        setInstance(EMPTY_CLIENT_FORM);
        setEditingId(undefined);
        setNewRecord(true);
        setEditing(true);
    };

    return (
        <div className="clientAdmin">
            <h1>Clients</h1>
            {error &&
            <Alert>{error}</Alert>
            }
            <Table color={loading ? 'dark' : 'light'}>
                <thead>
                <tr className="row">
                    <th className="col-1 text-center">Active</th>
                    <th className="col-1">Id</th>
                    <th className="col-3">Name</th>
                    <th className="col">Notes</th>
                    <th className="col-1">Max Time</th>
                    <th className="col-1">Rate</th>
                    <th className="text-right col-1">
                        <Button onClick={createNewRecord} size="sm"><FontAwesomeIcon icon="plus"/></Button>
                    </th>
                </tr>
                </thead>
                <tbody>
                {clients.map(client => (
                        (editing && !newRecord && editingId === client.clientId) ? (
                            <tr key={client.clientId}>
                                <td colSpan={6}>
                                    <UpdateForm instance={instance} cancel={cancelEditing} update={updateRecord}/>
                                </td>
                            </tr>
                        ) : (
                            <tr key={client.clientId} className="row">
                                <td className="col-1 text-center"><Checkbox value={client.active}/></td>
                                <td className="col-1">{client.clientId}</td>
                                <td className="col-3">{client.name}</td>
                                <td className="col">{client.notes}</td>
                                <td className="col-1"><ShowHours minutes={client.maxMinutes}/></td>
                                <td className="col-1"><ShowRate rate={client.rateInCentsPerHour}/></td>
                                <td className="col-1 text-right">
                                    <ButtonGroup>
                                        <Button color="light" onClick={() => updatingRecord(client)}>
                                            <FontAwesomeIcon icon="pen"/>
                                        </Button>
                                        <Button color="danger" onClick={() => showDeleteDialog(client)}>
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

export default ClientAdmin;

interface UpdateFormProps {
    instance: UpdatingClient;
    cancel: () => void;
    update: (client: UpdatingClient) => Promise<void>;
}

const UpdateForm = ({instance, cancel, update}: UpdateFormProps) => {
    const validator = (value) => {
        let errors: FormErrors = {};
        if (!value.id || value.id.length === 0) {
            errors['id'] = 'ID is required';
        }
        if (!value.name || value.name.length === 0) {
            errors['name'] = 'Name is required';
        }
        // TODO think about testing the rate and max minutes
        return errors;
    };

    const handleSave = async (client: UpdatingClient) => {
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
                            checked={values.active}
                            onChange={handleChecked}
                            name="active"
                        />
                        {' '}Active?</Label>
                </FormGroup>
                <FormGroup>
                    <Label for="clientId">Id:</Label>
                    <Input
                        autoFocus={true}
                        type="text"
                        name="clientId"
                        valid={!errors['clientId']}
                        invalid={!!errors['clientId']}
                        value={values.clientId}
                        onChange={handleChange}
                    />
                    <FormFeedback>{errors['clientId']}</FormFeedback>
                </FormGroup>
                <FormGroup>
                    <Label for="name">Name:</Label>
                    <Input
                        type="text"
                        name="name"
                        valid={!errors['name']}
                        invalid={!!errors['name']}
                        value={values.name}
                        onChange={handleChange}
                    />
                    <FormFeedback>{errors['name']}</FormFeedback>
                </FormGroup>
                <FormGroup>
                    <Label for="notes">Notes:</Label>
                    <Input
                        type="textarea"
                        rows={3}
                        name="notes"
                        valid={!errors['notes']}
                        invalid={!!errors['notes']}
                        value={values.notes}
                        onChange={handleChange}
                    />
                    <FormFeedback>{errors['notes']}</FormFeedback>
                </FormGroup>
                <FormGroup>
                    <Label for="maxHours">Max Time:</Label>
                    <Input
                        type="text"
                        name="maxHours"
                        valid={!errors['maxHours']}
                        invalid={!!errors['maxHours']}
                        value={values.maxHours}
                        onChange={handleChange}
                    />
                    <FormFeedback>{errors['maxHours']}</FormFeedback>
                </FormGroup>
                <FormGroup>
                    <Label for="rate">Rate:</Label>
                    <Input
                        type="text"
                        name="rate"
                        valid={!errors['rate']}
                        invalid={!!errors['rate']}
                        value={values.rate}
                        onChange={handleChange}
                    />
                    <FormFeedback>{errors['rate']}</FormFeedback>
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
    instance: UpdatingClient;
    cancel: () => void;
    shown: boolean;
    execute: (string) => Promise<void>;
}

const DeleteDialog = ({instance, cancel, execute, shown}: IDeleteDialogProps) => {
    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    const handleDelete = async () => {
        try {
            await execute(instance.clientId);
        } catch (err) {
            setRemoteError(err.toString());
        }
    };

    if (!shown) {
        return null;
    }

    return (
        <Modal isOpen={shown} toggle={cancel}>
            <ModalHeader toggle={cancel}>Deleting Client</ModalHeader>
            <ModalBody>
                {remoteError && (
                    <Alert color="danger">{remoteError}</Alert>
                )}
                <div>
                    Are you sure you want to delete client {instance.name}?
                </div>
            </ModalBody>
            <ModalFooter>
                <Button color="danger" type="submit" onClick={handleDelete}>Delete</Button>{' '}
                <Button color="secondary" onClick={cancel}>Cancel</Button>
            </ModalFooter>
        </Modal>
    );
};
