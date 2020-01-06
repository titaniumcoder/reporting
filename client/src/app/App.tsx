import React from 'react';
import {BrowserRouter as Router, Redirect, Route, Switch} from 'react-router-dom'
import './App.css';
import Navigation from "../Navigation";
import {Container} from "reactstrap";
import Clients from "../clients/Clients";
import ClientInfo from "../clients/ClientInfo";
import Login from "../auth/Login";
import {useSelector} from "react-redux";
import UserAdmin from "../users/UserAdmin";
import {RootState} from "../rootReducer";
import ClientAdmin from "../clients/ClientAdmin";
import ProjectAdmin from "../projects/ProjectAdmin";
import CurrentTimeEntry from "../timeentry/CurrentTimeEntry";
import TimeEntries from "../timeentry/TimeEntries";

const App = () => {
    const auth = useSelector((state: RootState) => state.auth);

    const {loggedIn, admin, canBook} = auth;

    if (!loggedIn) {
        return <Login/>
    } else {
        return (
            <Router>
                <div>
                    <Container fluid={true}>
                        <Navigation/>
                        <Switch>
                            {admin &&
                            <Route path="/admin">
                                {
                                    admin ? (
                                        <div>
                                            <UserAdmin/>
                                            <ClientAdmin/>
                                            <ProjectAdmin/>
                                        </div>) : <Redirect to="/"/>
                                }
                            </Route>
                            }
                            <Route path="/">
                                {canBook &&
                                <CurrentTimeEntry/>
                                }

                                <Clients />
                                <Route path="/client/:client">
                                    <div className="mt-3">
                                        <ClientInfo />
                                        <hr/>
                                    </div>
                                </Route>
                                <TimeEntries />
                            </Route>
                        </Switch>
                    </Container>
                </div>
            </Router>
        );
    }
};

export default App;
