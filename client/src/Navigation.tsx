import React from 'react';
import './App.css';
import { Nav, NavItem, NavLink } from 'reactstrap';

interface INavigationProps {
    clients: {
        name: string;
        id: number;
    }[];
    activeClient: number | null;

    selectClient: (number) => void;
}

const Navigation: React.FC<INavigationProps> = ({clients, activeClient, selectClient}) => {

    return (
        <Nav tabs fill>
            {clients && clients.map(c =>
                <NavItem key={c.name} style={{ cursor: "pointer" }}>
                    <NavLink onClick={() => {selectClient(c.id)}} active={activeClient !== null && c.id === activeClient}>{c.name}</NavLink>
                </NavItem>
            )}
        </Nav>
    );
};

export default Navigation;
