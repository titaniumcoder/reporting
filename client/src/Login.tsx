import React from 'react';
import './App.css';
import { Button, Col, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { AvField, AvForm } from "availity-reactstrap-validation";

interface ILoginProps {
    showModal: boolean;

    executeLogin: (username: string, password: string) => void;
}

const Login: React.FC<ILoginProps> = ({ showModal, executeLogin }) => {
    const handleSubmit = (event, values) => {
        executeLogin(values.username, values.password);
    };

    return (
        <Modal isOpen={showModal} autoFocus={false}>
            <ModalHeader>Anmeldung</ModalHeader>
            <AvForm onValidSubmit={handleSubmit}>
                <ModalBody>
                    <AvField name="username" type="string" label="Benutzername" required autoFocus/>
                    <AvField name="password" type="password" label="Passwort" required/>
                </ModalBody>
                <ModalFooter>
                    <Col xs="auto">
                        <Button type="submit" color="primary">Anmelden</Button>
                    </Col>
                </ModalFooter>
            </AvForm>
        </Modal>
    );
};

export default Login;
