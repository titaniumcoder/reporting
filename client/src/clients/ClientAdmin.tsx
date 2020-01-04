import React, {useEffect, useState} from 'react';

import './ClientAdmin.css';
import {useDispatch, useSelector} from "react-redux";
import {
    Alert,
    Button,
    ButtonGroup,
    Form,
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
import reportingApi, {Client} from "../api/reportingApi";
import Modal from "reactstrap/lib/Modal";
import ShowMinutes from "../components/ShowMinutes";
import ShowRate from "../components/ShowRate";

const ClientAdmin = () => {
    const [newRecord, setNewRecord] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [instance, setInstance] = useState(undefined as Client | undefined);
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

    const updateRecord = async (client: Client) => {
        if (client.rateInCentsPerHour === 0) {
            client.rateInCentsPerHour = undefined;
        }
        if (client.maxMinutes === 0) {
            client.maxMinutes = undefined;
        }
        if (!client.notes) {
            client.notes = undefined;
        }
        await reportingApi.saveClient(client);
        setInstance(undefined);
        setEditingId(undefined);
        dispatch(fetchClients());
    };

    const deleteRecord = async (client: Client) => {
        await reportingApi.deleteClient(client);
        setDeleting(false);
        setInstance(undefined);
        dispatch(fetchClients());
    };

    const updatingRecord = (client: Client) => {
        setInstance(client);
        setEditingId(client.id);
        setNewRecord(false);
    };

    useEffect(() => {
        dispatch(fetchClients());
    }, [loggedIn, email, dispatch]);

    const cancelEditing = () => {
        setInstance(undefined);
        setEditingId(undefined);
        setNewRecord(false);
    };

    const cancelDeleting = () => {
        setInstance(undefined);
        setDeleting(false);
    };
    const showDeleteDialog = (client: Client) => {
        setInstance(client);
        setDeleting(true);
    };

    const createNewRecord = () => {
        setInstance({
            notes: '',
            maxMinutes: undefined,
            rateInCentsPerHour: undefined,
            name: '',
            id: '',
            active: true
        });
        setEditingId(undefined);
        setNewRecord(true);
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
                    <th className="col-2 text-right">Max Time</th>
                    <th className="col-2 text-right">Rate</th>
                    <th className="text-right col-auto">
                        <Button onClick={createNewRecord} size="sm"><FontAwesomeIcon icon="plus"/></Button>
                    </th>
                </tr>
                </thead>
                <tbody>
                {clients.map(client => (
                        (editingId && editingId === client.id) ? (
                            <tr key={client.id}>
                                <td colSpan={6}>
                                    <UpdateForm instance={instance} cancel={cancelEditing} update={updateRecord}/>
                                </td>
                            </tr>
                        ) : (
                            <tr key={client.id} className="row">
                                <td className="col-1 text-center"><Checkbox value={client.active}/></td>
                                <td className="col-1">{client.id}</td>
                                <td className="col-3">{client.name}</td>
                                <td className="col">{client.notes}</td>
                                <td className="col-2 text-right"><ShowMinutes minutes={client.maxMinutes}/></td>
                                <td className="col-2 text-right"><ShowRate rate={client.rateInCentsPerHour}/></td>
                                <td className="col-auto">
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

const UpdateForm = ({instance, cancel, update}) => {
    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    const cancelEditing = () => {
        cancel();
    };

    const handleSave = async (client: Client) => {
        try {
            update(client);
        } catch (err) {
            setRemoteError(err.toString());
        }
    };

    /*
                    setSubmitting(true);
                const {id, name, active, maxMinutes, rateInCentsPerHour, notes} = values;
                await handleSave({
                    id, name, active, maxMinutes, rateInCentsPerHour, notes
                });
                setSubmitting(false);
     */

    const handleSubmit = () => {
    };

    return (
        <Form onSubmit={handleSubmit}>
            <div>
                {remoteError &&
                <Alert color="danger">{remoteError}</Alert>
                }
                <FormGroup check>
                    <Label check>
                        <Input
                            type="checkbox"
                        />
                        {' '}Active?</Label>
                </FormGroup>
                <FormGroup>
                    <Label for="id">Id:</Label>
                    <Input
                        autoFocus={true}
                    />
                </FormGroup>
                <FormGroup>
                    <Label for="name">Name:</Label>
                    <Input
                    />
                </FormGroup>
                <FormGroup>
                    <Label for="notes">Notes:</Label>
                    <Input
                    />
                </FormGroup>
                <FormGroup>
                    <Label for="maxMinutes">Max Time:</Label>
                    <Input
                    />
                </FormGroup>
                <FormGroup>
                    <Label for="rate">Rate:</Label>
                    <Input
                    />
                </FormGroup>
                <ButtonGroup>
                    <Button type="submit" color="primary" disabled={true}
                            onClick={() => handleSubmit()}>Save</Button>{' '}
                    <Button color="secondary" onClick={cancelEditing}>Cancel</Button>
                </ButtonGroup>
            </div>
        </Form>
    );
};
const DeleteDialog = ({instance, cancel, execute, shown}) => {
    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    const handleDelete = async () => {
        try {
            await execute(instance);
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
                <Button color="danger" onClick={handleDelete}>Delete</Button>{' '}
                <Button color="secondary" onClick={cancel}>Cancel</Button>
            </ModalFooter>
        </Modal>
    );
};
