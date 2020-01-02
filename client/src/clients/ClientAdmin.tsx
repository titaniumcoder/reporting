import React from 'react';

import './ClientAdmin.css';
import {useDispatch, useSelector} from "react-redux";
import {Alert, Button, ButtonGroup, Table} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {RootState} from "../rootReducer";
import Checkbox from "../components/Checkbox";

const ClientAdmin = () => {
    const dispatch = useDispatch();
    const {clients, error, loading} = useSelector((state: RootState) => {
        const {clients, error, loading} = state.client;
        return {clients, error, loading};
    });

    return (
        <div className="clientAdmin mt-5">
            <h1>Clients</h1>
            {error &&
            <Alert>{error}</Alert>
            }
            <Table color={loading ? 'dark' : 'light'}>
                <thead>
                <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>Notes</th>
                    <th>Max Minutes</th>
                    <th>Rate</th>
                    <th className="text-right">
                        <Button><FontAwesomeIcon icon="plus"/></Button>
                    </th>
                </tr>
                </thead>
                <tbody>
                {clients.map(client => (
                        <tr key={client.id} className={client.active ? 'light' : 'warning'}>
                            <td>{client.id}</td>
                            <td>{client.name}</td>
                            <td>{client.notes}</td>
                            <td>{client.maxMinutes}</td>
                            <td>{client.rateInCentsPerHours}</td>
                            <td>
                                <ButtonGroup>
                                    <Button color="light">
                                        <FontAwesomeIcon icon="pen"/>
                                    </Button>
                                    <Button color="danger">
                                        <FontAwesomeIcon icon="trash"/>
                                    </Button>
                                </ButtonGroup>
                            </td>
                        </tr>
                    )
                )}
                </tbody>
            </Table>
        </div>
    );
};

export default ClientAdmin;
