import React from "react";
import {Nav, NavItem} from "reactstrap";
import {NavLink} from "react-router-dom";

const Clients = ({clients}) => {
    return (
        <Nav tabs>
            {clients.map(({id, name}) =>
                <NavItem key={id}>
                    <NavLink to={`/client/${id}`} activeClassName="active"
                             className="nav-link">{name}</NavLink>
                </NavItem>
            )}
        </Nav>
    );
};

export default Clients;
