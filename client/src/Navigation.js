import React, {useState} from "react";
import {Collapse, Nav, Navbar, NavbarToggler, NavItem, NavLink} from "reactstrap";

const Navigation = ({admin}) => {
    const [isOpen, setIsOpen] = useState(false);

    const toggle = () => setIsOpen(!isOpen);

    if (admin) {
        return (
            <Navbar color="light" light expand="md">
                <NavbarToggler onClick={toggle}/>
                <Collapse isOpen={isOpen} navbar>
                    <Nav className="mr-auto" navbar>
                        <NavItem>
                            <NavLink href="/">Time</NavLink>
                        </NavItem>
                        <NavItem>
                            <NavLink href="/admin">Administration</NavLink>
                        </NavItem>
                    </Nav>
                </Collapse>
            </Navbar>
        );
    } else {
        return null;
    }
};

export default Navigation;
