import React, {useEffect} from "react";
import {Nav, NavItem} from "reactstrap";
import {NavLink} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../rootReducer";
import {fetchClientList} from "./clientSlice";

const Clients = () => {
    const clientList = useSelector((state: RootState) => state.client.clientList);
    const loggedIn = useSelector((state: RootState) => state.auth.loggedIn);

    const dispatch = useDispatch();

    useEffect(() => {
        const loadClients = () => {
            if (loggedIn) {
                dispatch(fetchClientList());
            }
        };

        loadClients();
    }, [loggedIn, dispatch]);

    return (
        <Nav tabs>
            <NavItem>
                <NavLink to='' activeClassName="active" className="nav-link" exact>None</NavLink>
            </NavItem>
            {clientList.map(({id, name}) =>
                <NavItem key={id}>
                    <NavLink to={`/client/${id}`} activeClassName="active"
                             className="nav-link">{name}</NavLink>
                </NavItem>
            )}
        </Nav>
    );
};

export default Clients;
