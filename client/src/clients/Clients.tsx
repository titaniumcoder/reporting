import React, {useEffect} from "react";
import {Nav, NavItem} from "reactstrap";
import {NavLink} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../rootReducer";
import {fetchClientList} from "./clientSlice";

const Clients = () => {
    const clientList = useSelector((state: RootState) => state.client.clientList);
    const dispatch = useDispatch();

    useEffect(() => {
        const loadClients = () => {
            dispatch(fetchClientList());
        };

        loadClients();
    }, [dispatch]);

    return (
        <Nav tabs>
            <NavItem>
                <NavLink to='' activeClassName="active" className="nav-link" exact>None</NavLink>
            </NavItem>
            {clientList.map(({clientId, name}) =>
                <NavItem key={clientId}>
                    <NavLink to={`/client/${clientId}`} activeClassName="active"
                             className="nav-link">{name}</NavLink>
                </NavItem>
            )}
        </Nav>
    );
};

export default Clients;
