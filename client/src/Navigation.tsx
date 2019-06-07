import React from 'react';
import './App.css';
import { Nav, NavItem, NavLink } from 'reactstrap';

interface INavigationProps {
    clients: {
        name: string;
        id: number;
    }[];
    activeClient: number | null;
}

const Navigation: React.FC<INavigationProps> = ({clients, activeClient}) => {

    return (
        <Nav tabs fill>
            {clients.map(c =>
                <NavItem key={c.name} style={{ cursor: "pointer" }}>
                    <NavLink onClick={(e) => {
                        console.log('Nav: ', e)
                    }} active={activeClient !== null && c.id === activeClient}>{c.name}</NavLink>
                </NavItem>
            )}
        </Nav>
    );
};

export default Navigation;
