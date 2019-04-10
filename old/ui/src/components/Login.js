/* eslint-disable no-undef */

import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Button, Col, Form, FormGroup, Input, Label, Row} from "reactstrap";

export default class Login extends Component {
    static propTypes = {
        username: PropTypes.string.isRequired,
        password: PropTypes.string.isRequired,
        loggedIn: PropTypes.bool.isRequired,
        login: PropTypes.func.isRequired,
        logout: PropTypes.func.isRequired,
        updateUsernamePassword: PropTypes.func.isRequired
    };

    handleKeyUp = (e) => {
        if (e.keyCode === 13) {
            this.handleLoginClick();
        }
    };

    handleLoginClick = () => {
        if (this.props.username && this.props.password)
            this.props.login();
    };

    handleLogoutClick = () => {
        this.props.logout();
    };

    updateUsername = (e) => {
        this.props.updateUsernamePassword(e.target.value, undefined);
    };

    updatePassword = (e) => {
        this.props.updateUsernamePassword(undefined, e.target.value);
    };

    render() {
        const {username, password, loggedIn} = this.props;

        const loginPossible = username && password;
        if (!loggedIn) {
            return (
                <Form>
                    <FormGroup row>
                        <Label for="username" sm={2}>Username</Label>
                        <Col sm={10}>
                            <Input
                                id="username"
                                name="username"
                                value={username}
                                onChange={this.updateUsername}
                                onKeyUp={this.handleKeyUp}/>
                        </Col>
                    </FormGroup>
                    <FormGroup row>
                        <Label for="password" sm={2}>Password</Label>
                        <Col sm={10}>
                            <Input
                                type="password"
                                id="password"
                                name="password"
                                value={password}
                                onChange={this.updatePassword}
                                onKeyUp={this.handleKeyUp}/>
                        </Col>
                    </FormGroup>
                    <FormGroup row>
                        <Col sm={{size: 10, offset: 2}}>
                            <Button color="primary"
                                    onClick={this.handleLoginClick}
                                    disabled={!loginPossible}
                            >Login</Button>
                        </Col>
                    </FormGroup>
                </Form>
            )
        } else {
            return <Row>
                <Col/>
                <Col sm="auto">
                    <Button color="danger" size="sm"
                            onClick={this.handleLogoutClick}>Logout</Button>
                </Col>
            </Row>
        }
    }
}