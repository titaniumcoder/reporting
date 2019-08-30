import React from 'react';
import {Button, Col, Container, Form, FormGroup, Input, Label} from 'reactstrap';
import {Formik} from "formik";

import './Login.css';

interface ILoginProps {
    executeLogin: (username: string, password: string) => void;
}

const Login: React.FC<ILoginProps> = ({executeLogin}) => {
    const handleSubmit = (event, values) => {
        executeLogin(values.username, values.password);
    };

    return (
        <div className="login">
            <Container>
                <Formik
                    initialValues={{username: '', password: ''}}
                    onSubmit={(values, {setSubmitting}) => {
                        setSubmitting(true);
                        handleSubmit(null, values);
                        setSubmitting(false);
                    }}>
                    {({
                          values,
                          errors,
                          touched,
                          handleChange,
                          handleBlur,
                          handleSubmit,
                          isSubmitting,
                          /* and other goodies */
                      }) => (
                        <Form onSubmit={handleSubmit}>
                            <FormGroup>
                                <Input
                                    type="text"
                                    required
                                    autoFocus
                                    name="username"
                                    placeholder="Benutzername"
                                    value={values.username}
                                    onChange={handleChange}
                                    onBlur={handleBlur}
                                />
                            </FormGroup>
                            <FormGroup>
                                <Input
                                    type="password"
                                    required
                                    name="password"
                                    placeholder="Passwort"
                                    value={values.password}
                                    onChange={handleChange}
                                    onBlur={handleBlur}
                                />
                            </FormGroup>
                            <Button type="submit" color="primary" block size="lg" className="mt-5 mb-3">Anmelden</Button>
                        </Form>
                    )}
                </Formik>
            </Container>
        </div>
    );
};

export default Login;
