import React, {useState} from "react";
import {Button, Collapse, Nav, Navbar, NavbarText, NavbarToggler, NavItem} from "reactstrap";
import {GoogleLogout} from "react-google-login";
import {GoogleClientId} from "./constants";
import {useDispatch, useSelector} from "react-redux";
import {logout} from "./auth/authSlice";
import api from './api/reportingApi';

import * as moment from 'moment';
import {NavLink} from "react-router-dom";
import {RootState} from "./rootReducer";

const Navigation = () => {
    const [isOpen, setIsOpen] = useState(false);

    const dispatch = useDispatch();

    const {admin, username, expiration} = useSelector((state: RootState) => ({
        username: state.auth.username,
        expiration: state.auth.authExpiration,
        admin: state.auth.admin
    }));

    const expirationFormatted = moment.utc(expiration).local().format("HH:mm");

    const toggle = () => setIsOpen(!isOpen);

    const executeLogout = () => {
        dispatch(logout());
        api.logout();
    };

    return (
        <Navbar color="light" light expand="md">
            <NavbarToggler onClick={toggle}/>
            <Collapse isOpen={isOpen} navbar>
                <Nav className="mr-auto" navbar>
                    <NavItem>
                        <NavLink to="/" activeClassName="active" exact={true}
                                 className="nav-link">Time</NavLink>
                    </NavItem>
                    {admin && (
                        <NavItem>
                            <NavLink to="/admin" activeClassName="active"
                                     className="nav-link">Administration</NavLink>
                        </NavItem>
                    )}
                </Nav>
                <Nav>
                    <GoogleLogout
                        clientId={GoogleClientId}
                        buttonText="Logout"
                        onLogoutSuccess={executeLogout}
                        render={renderProps => (
                            <NavItem>
                                <Button color="light" onClick={renderProps.onClick}
                                        disabled={renderProps.disabled}>Logout</Button>
                            </NavItem>
                        )}
                    />
                    <NavbarText className="ml-2">{username} (till {expirationFormatted})</NavbarText>
                </Nav>
            </Collapse>
        </Navbar>
    );
};

export default Navigation;
