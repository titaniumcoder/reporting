import React, {useEffect, useState} from 'react';

import './UserAdmin.css';
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
import {fetchUsers} from "./userSlice";
import reportingApi, {User} from "../api/reportingApi";
import Modal from "reactstrap/lib/Modal";
import {Field, Formik} from "formik";

const UserAdmin = () => {
    const [editing, setEditing] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [editingUser, setEditingUser] = useState({} as User);

    const dispatch = useDispatch();
    const {users, error, loading} = useSelector((state: RootState) => {
        const {users, error, loading} = state.user;
        return {users, error, loading};
    });
    const {loggedIn, email} = useSelector((state: RootState) => {
        const {loggedIn, email} = state.auth;
        return {loggedIn, email};
    });

    useEffect(() => {
        dispatch(fetchUsers());
    }, [loggedIn, email]);

    const cancelEditing = () => {
        setEditing(false);
    };

    const cancelDeleting = () => {
        setDeleting(false);
    };
    const showDeleteUserDialog = (user: User) => {
        setEditingUser(user);
        setDeleting(true);
    };

    const createUser = () => {
        setEditingUser({
            email: '',
            clients: [],
            admin: false,
            canBook: false,
            canViewMoney: false
        });
        setEditing(true);
    };

    const updateUser = (user: User) => {
        setEditingUser(user);
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
                        <Button onClick={createUser}><FontAwesomeIcon icon="plus"/></Button>
                    </th>
                </tr>
                </thead>
                <tbody>
                {users.map(user => (
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
                                    <Button color="light" onClick={() => updateUser(user)}>
                                        <FontAwesomeIcon icon="pen"/>
                                    </Button>
                                    <Button color="danger" onClick={() => showDeleteUserDialog(user)}>
                                        <FontAwesomeIcon icon="trash"/>
                                    </Button>
                                </ButtonGroup>
                            </td>
                        </tr>
                    )
                )}
                </tbody>
            </Table>

            <UpdateForm dispatch={dispatch} editingUser={editingUser} endEditing={cancelEditing} shown={editing}/>

            <DeleteDialog dispatch={dispatch} editingUser={editingUser} endDeleting={cancelDeleting} shown={deleting}/>
        </div>
    );
};

export default UserAdmin;

const UpdateForm = ({editingUser, dispatch, endEditing, shown}) => {
    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    const clients = useSelector((state: RootState) => state.client.clients);

    const cancelEditing = () => {
        endEditing();
    };

    const handleSave = async (user: User) => {
        try {
            const result = await reportingApi.saveUser(user);
            if (result) {
                endEditing();
                dispatch(fetchUsers());
            } else {
                setRemoteError('Could not save, check the log');
            }
        } catch (err) {
            setRemoteError(err.toString());
        }
    };

    return (
        <Formik
            initialValues={{...editingUser}}
            validateOnChange={true}
            validateOnMount={true}
            validate={values => {
                let errors: any = {};
                if (!values.email) {
                    errors.email = 'Email is required';
                }
                return errors;
            }}
            onSubmit={async (values, {setSubmitting}) => {
                setSubmitting(true);
                await handleSave({
                    email: values.email,
                    admin: !!values.admin,
                    clients: values.clients,
                    canViewMoney: !!values.canViewMoney,
                    canBook: !!values.canBook
                });
                setSubmitting(false);
            }}>
            {({
                  values,
                  errors,
                  handleChange,
                  handleBlur,
                  handleSubmit,
                  isSubmitting,
                  submitForm
              }) => (
                <Form onSubmit={handleSubmit}>
                    <Modal isOpen={shown} toggle={cancelEditing}>
                        <ModalHeader toggle={cancelEditing}>Editing User {editingUser.email}</ModalHeader>
                        <ModalBody>
                            {remoteError &&
                            <Alert color="danger">{remoteError}</Alert>
                            }
                            <div>
                                <FormGroup>
                                    <Label for="email">Email:</Label>
                                    <Input
                                        type="email"
                                        name="email"
                                        value={values.email}
                                        valid={!errors.email}
                                        onBlur={handleBlur}
                                        onChange={handleChange}/>
                                </FormGroup>
                                <FormGroup check>
                                    <Field name="admin">
                                        {({field}) => (
                                            <Label check>
                                                <Input
                                                    type="checkbox"
                                                    {...field}
                                                    checked={field.value}
                                                />
                                                {' '}Admin?</Label>
                                        )}
                                    </Field>
                                </FormGroup>
                                <FormGroup check>
                                    <Field name="canBook">
                                        {({field}) => (
                                            <Label check>
                                                <Input
                                                    type="checkbox"
                                                    {...field}
                                                    checked={field.value}
                                                />
                                                {' '}Can Book?</Label>
                                        )}
                                    </Field>
                                </FormGroup>
                                <FormGroup check>
                                    <Field name="canViewMoney">
                                        {({field}) => (
                                            <Label check>
                                                <Input
                                                    type="checkbox"
                                                    {...field}
                                                    checked={field.value}
                                                />
                                                {' '}Can View Money?</Label>
                                        )}
                                    </Field>
                                </FormGroup>
                                <FormGroup>
                                    <Label for="clients">Clients:</Label>
                                    <Field name="clients">{
                                        ({
                                             field
                                         }) => (
                                            <Input
                                                type="select"
                                                {...field}
                                                multiple={true}
                                            >
                                                {clients.map(client => (
                                                    <option value={client.id}>client.name</option>
                                                ))}
                                            </Input>
                                        )}</Field>
                                </FormGroup>
                            </div>
                        </ModalBody>
                        <ModalFooter>
                            <Button type="submit" color="primary" disabled={isSubmitting}
                                    onClick={() => submitForm()}>Save</Button>{' '}
                            <Button color="secondary" onClick={cancelEditing}>Cancel</Button>
                        </ModalFooter>
                    </Modal>
                </Form>

            )}
        </Formik>
    );
};
const DeleteDialog = ({editingUser, dispatch, endDeleting, shown}) => {
    const [remoteError, setRemoteError] = useState<string | undefined>(undefined);

    const handleDelete = async () => {
        try {
            const result = await reportingApi.deleteUser(editingUser);
            if (result) {
                endDeleting();
                dispatch(fetchUsers());
            } else {
                setRemoteError('Could not delete, check the log');
            }
        } catch (err) {
            setRemoteError(err.toString());
        }
    };

    return (
        <Modal isOpen={shown} toggle={endDeleting}>
            <ModalHeader toggle={endDeleting}>Deleting User</ModalHeader>
            <ModalBody>
                {remoteError && (
                    <Alert color="danger">{remoteError}</Alert>
                )}
                <div>
                    Are you sure you want to delete user {editingUser.email}?
                </div>
            </ModalBody>
            <ModalFooter>
                <Button color="danger" onClick={handleDelete}>Delete</Button>{' '}
                <Button color="secondary" onClick={endDeleting}>Cancel</Button>
            </ModalFooter>
        </Modal>
    );
};
