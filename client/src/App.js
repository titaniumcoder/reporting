import React, {useState} from 'react';
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link
} from 'react-router-dom'
import './App.css';
import {Collapse, Nav, Navbar, NavbarToggler, NavItem, NavLink} from "reactstrap";

/*
import React from "react";
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link
} from "react-router-dom";

export default function App() {
    return (
        <Router>
            <div>
                <nav>
                    <li>
                        <Link to="/">Home</Link>
                    </li>
                    <li>
                        <Link to="/about">About</Link>
                    </li>
                    <li>
                        <Link to="/users">Users</Link>
                    </li>
                </nav>

                <Switch>
                    <Route path="/about">
                        <div>About</div>
                    </Route>
                    <Route path="/users">
                        <div>Users</div>
                    </Route>
                    <Route path="/">
                        <div>Home</div>
                    </Route>
                </Switch>
            </div>
        </Router>
    )
}

 */
const App = (props) => {
    const [isOpen, setIsOpen] = useState(false);

    const toggle = () => setIsOpen(!isOpen);

    return (
        <Router>
            <div>
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
                <nav>
                    <li>
                        <Link to="/">Time Entries</Link>
                    </li>
                    <li>
                        <Link to="/admin">Administration</Link>
                    </li>
                </nav>
                <Switch>
                    <Route path="/admin">
                        <div>Administration</div>
                    </Route>
                    <Route path="/">
                        <div>Time Entries</div>
                    </Route>
                </Switch>
            </div>
        </Router>
    );
};

export default App;
