import React, {useEffect, useState} from 'react';

import './UserAdmin.css';
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
import {fetchUsers} from "./userSlice";
import reportingApi, {UpdatingUser, User} from "../api/reportingApi";
import Modal from "reactstrap/lib/Modal";
import {FormHandler} from "../components/FormHandler";

const EMPTY_USER_FORM: UpdatingUser = {
    email: '',
    clients: [],
    admin: false,
    canBook: false,
    canViewMoney: false
};

const UserAdmin = () => {
    const [newRecord, setNewRecord] = useState(false);
    const [editing, setEditing] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [instance, setInstance] = useState(EMPTY_USER_FORM);
    const [editingId, setEditingId] = useState(undefined as string | undefined);

    const dispatch = useDispatch();
    const {users, error, loading} = useSelector((state: RootState) => {
        const {users, error, loading} = state.user;
        return {users, error, loading};
    });
    const {loggedIn, email} = useSelector((state: RootState) => {
        const {loggedIn, email} = state.auth;
        return {loggedIn, email};
    });

    const updateRecord = async (user: UpdatingUser) => {
        if (user.clients === undefined) {
            user.clients = [];
        }
        await reportingApi.saveUser(user);
        setInstance(EMPTY_USER_FORM);
        setEditingId(undefined);
        setEditing(false);
        setNewRecord(false);
        dispatch(fetchUsers());
    };

    const deleteRecord = async (email: string) => {
        await reportingApi.deleteUser(email);
        setDeleting(false);
        setInstance(EMPTY_USER_FORM);
        dispatch(fetchUsers());
    };

    const toUserForm = (user: User) => ({
        admin: user.admin,
        email: user.email,
        canBook: user.canBook,
        canViewMoney: user.canViewMoney,
        clients: user.clients.map(i => i.id)
    });

    const updatingRecord = (user: User) => {
        setInstance(toUserForm(user));
        setEditing(true);
        setNewRecord(false);
        setEditingId(user.email);
    };

    useEffect(() => {
        dispatch(fetchUsers());
    }, [loggedIn, email, dispatch]);

    const cancelEditing = () => {
        setInstance(EMPTY_USER_FORM);
        setEditingId(undefined);
        setNewRecord(false);
        setEditing(false);
    };

    const cancelDeleting = () => {
        setInstance(EMPTY_USER_FORM);
        setDeleting(false);
    };
    const showDeleteDialog = (user: User) => {
        setInstance(toUserForm(user));
        setDeleting(true);
    };

    const createNewRecord = () => {
        setInstance(EMPTY_USER_FORM);
        setEditingId(undefined);
        setNewRecord(true);
        setEditing(true);
    };

    return (
        <div className="userAdmin">
            <h1>Users</h1>
            {error &&
            <Alert>{error}</Alert>
            }
            <Table color={loading ? 'dark' : 'light'}>
                <thead>
                <tr className="row">
                    <th className="col">Email</th>
                    <th className="col-1 text-center">Admin?</th>
                    <th className="col-1 text-center">Book?</th>
                    <th className="col-1 text-center">Money?</th>
                    <th className="col">Clients</th>
                    <th className="text-right col-auto">
                        <Button onClick={createNewRecord} size="sm"><FontAwesomeIcon icon="plus"/></Button>
                    </th>
                </tr>
                </thead>
                <tbody>
                {users.map(user =>
                    (editing && !newRecord && editingId === user.email && instance) ? (
                        <tr key={user.email}>
                            <td colSpan={6}>
                                <UpdateForm instance={instance} cancel={cancelEditing} update={updateRecord}/>
                            </td>
                        </tr>
                    ) : (
                        <tr key={user.email} className="row">
                            <td className="col">{user.email}</td>
                            <td className="col-1 text-center"><Checkbox value={user.admin}/></td>
                            <td className="col-1 text-center"><Checkbox value={user.canBook}/></td>
                            <td className="col-1 text-center"><Checkbox value={user.canViewMoney}/></td>
                            <td className="col">
                                {user.clients && (
                                    <ul>
                                        {user.clients.map(client => (
                                            <li key={client.id}>{client.name}</li>
                                        ))}
                                    </ul>
                                )
                                }
                            </td>
                            <td className="col-auto">
                                <ButtonGroup>
                                    <Button color="light" onClick={() => updatingRecord(user)}>
                                        <FontAwesomeIcon icon="pen"/>
                                    </Button>
                                    <Button color="danger" onClick={() => showDeleteDialog(user)}>
                                        <FontAwesomeIcon icon="trash"/>
                                    </Button>
                                </ButtonGroup>
                            </td>
                        </tr>
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

export default UserAdmin;

interface UpdateFormProps {
    instance: UpdatingUser;
    cancel: () => void;
    update: (user: UpdatingUser) => Promise<void>;
}

const UpdateForm = ({instance, cancel, update}: UpdateFormProps) => {
    const validator = (value) => {
        let errors: Map<string, string | undefined> = new Map();
        if (!value.email) {
            errors.set('email', 'Email is required');
        } else {
            const re = /^(([^<>()[\].,;:\s@"]+(\.[^<>()[\].,;:\s@"]+)*)|(".+"))@(([^<>()[\].,;:\s@"]+\.)+[^<>()[\].,;:\s@"]{2,})$/i;
            if (!re.test(value.email.toLowerCase())) {
                errors.set('email', 'Email is not valid');
            }
        }
        return errors;
    };

    const handleSave = async (value) => {
        try {
            await update(value);
        } catch (err) {
            setRemoteError(err.toString());
        }
    };

    const {values, errors, submitDisabled, doSubmit, handleChange, handleChecked, handleMultiple} = FormHandler(instance, validator, handleSave);

    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    const clients = useSelector((state: RootState) => state.client.clients);

    return (
        <Form onSubmit={doSubmit}>
            <div>
                {remoteError &&
                <Alert color="danger">{remoteError}</Alert>
                }
                <FormGroup>
                    <Label for="email">Email:</Label>
                    <Input
                        autoFocus={true}
                        type="email"
                        name="email"
                        valid={!errors.get('email')}
                        invalid={!!errors.get('email')}
                        value={values.email}
                        onChange={handleChange}
                    />
                    <FormFeedback>{errors.get('email')}</FormFeedback>
                </FormGroup>
                <FormGroup check>
                    <Label check>
                        <Input
                            type="checkbox"
                            checked={values.admin}
                            onChange={handleChecked}
                            name="admin"
                        />
                        {' '}Admin?</Label>
                </FormGroup>
                <FormGroup check>
                    <Label check>
                        <Input
                            type="checkbox"
                            checked={values.canBook}
                            onChange={handleChecked}
                            name="canBook"
                        />
                        {' '}Can Book?</Label>
                </FormGroup>
                <FormGroup check>
                    <Label check>
                        <Input
                            type="checkbox"
                            checked={values.canViewMoney}
                            onChange={handleChecked}
                            name="canViewMoney"
                        />
                        {' '}Can View Money?</Label>
                </FormGroup>
                <FormGroup>
                    <Label for="clients">Clients:</Label>
                    <Input
                        type="select"
                        name="clients"
                        onChange={handleMultiple}
                        multiple={true}
                        value={values.clients}
                    >
                        {clients.map(client => (
                            <option key={client.id} value={client.id}>{client.name}</option>
                        ))}
                    </Input>
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
    instance: UpdatingUser;
    cancel: () => void;
    shown: boolean;
    execute: (string) => Promise<void>;
}

const DeleteDialog = ({instance, cancel, execute, shown}: IDeleteDialogProps) => {
    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    const handleDelete = async () => {
        try {
            await execute(instance.email);
        } catch (err) {
            setRemoteError(err.toString());
        }
    };

    if (!shown) {
        return null;
    }

    return (
        <Modal isOpen={shown} toggle={cancel}>
            <ModalHeader toggle={cancel}>Deleting User</ModalHeader>
            <ModalBody>
                {remoteError && (
                    <Alert color="danger">{remoteError}</Alert>
                )}
                <div>
                    Are you sure you want to delete user {instance.email}?
                </div>
            </ModalBody>
            <ModalFooter>
                <Button color="danger" type="submit" onClick={handleDelete}>Delete</Button>{' '}
                <Button color="secondary" onClick={cancel}>Cancel</Button>
            </ModalFooter>
        </Modal>
    );
};
