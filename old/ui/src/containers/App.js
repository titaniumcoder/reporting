/* eslint-disable no-undef */

import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {NavLink, withRouter} from 'react-router-dom';
import Login from '../components/Login';
import {
    billed,
    loadClients,
    loadExcel,
    login,
    logout,
    resetErrorMessage,
    resetExcel, unbilled,
    updateFromTo,
    updateLogin
} from '../actions';
import {Alert, Button, Col, Row} from "reactstrap";
import Systeminfo from "../components/Systeminfo";
import {ErrorMessage, Field, Form, Formik} from 'formik';
import * as moment from 'moment';
import * as Yup from 'yup';

class App extends Component {
    static propTypes = {
        // Injected by React Redux
        errorMessage: PropTypes.string,
        resetErrorMessage: PropTypes.func.isRequired,
        loggedIn: PropTypes.bool.isRequired,
        clients: PropTypes.array.isRequired,
        cash: PropTypes.array,
        loadClients: PropTypes.func.isRequired,
        // Injected by React Router
        children: PropTypes.node
    };

    handleDismissClick = e => {
        this.props.resetErrorMessage();
        e.preventDefault()
    };

    componentDidMount() {
        if (this.props.loggedIn) {
            this.props.loadClients();
        }
    }

    renderErrorMessage() {
        const {errorMessage} = this.props;
        if (!errorMessage) {
            return null
        }

        return (
            <Alert color="danger" toggle={this.handleDismissClick}>
                {errorMessage}
            </Alert>
        )
    }

    render() {
        const {children, clients, username, password, loggedIn, cash, from, to, excel} = this.props;

        const listClients = loggedIn && clients ?
            <ul className="nav nav-pills nav-fill mb-3">
                {clients.map(client =>
                    <li className="nav-item" key={client.id}>
                        <NavLink exact={true} activeClassName="active" className="nav-link"
                                 to={'/' + client.id}>{client.name}</NavLink>
                    </li>
                )}
            </ul> : null;
        return (
            <div className="container-fluid">
                <Row>
                    <Col>
                        <Login username={username}
                               password={password}
                               loggedIn={loggedIn}
                               login={this.props.login}
                               logout={this.props.logout}
                               updateUsernamePassword={this.props.updateLogin}/>
                    </Col>
                    <Col className="pl-4">
                        <Systeminfo loggedIn={loggedIn} cash={cash}/>
                    </Col>
                </Row>
                <hr/>
                {this.renderErrorMessage()}
                <Row className="mb-5">
                    <Col>
                        <Formik
                            initialValues={{from: from, to: to}}
                            validationSchema={Yup.object({
                                from: Yup.string().required('Von Datum muss eingegeben werden'),
                                to: Yup.string().required('Bis Datum muss eingegeben werden')
                            })
                            }
                            validate={values => {
                                let errors = {};
                                if (value.from && values.to && moment(values.to).isBefore(moment(values.from))) {
                                    errors.to = '<Von> muss vor <Bis> sein.'
                                }
                                return errors;
                            }}
                            onSubmit={(values, {setSubmitting}) => {
                                this.props.updateFromTo(values.from, values.to);
                                setSubmitting(false);
                            }}
                        >
                            {({isSubmitting}) => (
                                <Form>
                                    <Field type="date" name="from"/>
                                    <ErrorMessage name="from" component="div"/>
                                    <Field type="date" name="to"/>
                                    <ErrorMessage name="to" component="div"/>
                                    <Button type="submit" color="primary" disabled={isSubmitting}>
                                        Laden
                                    </Button>
                                </Form>
                            )}
                        </Formik>
                    </Col>

                    <Col xs="auto">
                        <div className="btn-group">
                            {!excel.url && <Button color="secondary" onClick={() => this.props.loadExcel()}>Excel</Button>}
                            {excel.url && <a href={excel.url} className="btn btn-secondary" onClick={() => this.props.resetExcel()}>Herunterladen</a>}
                            <Button color="danger" onClick={() => this.props.billed()}>Abgerechnet</Button>
                            <Button color="danger" onClick={() => this.props.unbilled()}>Zurücksetzen</Button>
                        </div>
                    </Col>
                </Row>
                {listClients}
                {children}
            </div>
        )
    }
}

const mapStateToProps = (state) => ({
    errorMessage: state.errorMessage,
    loggedIn: state.authentication.loggedIn,
    username: state.authentication.username,
    password: state.authentication.password,
    clients: state.clients.clients,
    from: state.form.from,
    to: state.form.to,
    cash: state.system.cash,
    excel: state.excel,
});

export default withRouter(connect(mapStateToProps, {
    resetErrorMessage,
    login,
    logout,
    updateLogin,
    loadClients,
    updateFromTo,
    loadExcel,
    resetExcel,
    billed,
    unbilled
})(App))