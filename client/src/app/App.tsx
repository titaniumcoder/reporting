import React from 'react';
import {Route, Switch} from 'react-router-dom'
import './App.css';
import Navigation from "../Navigation";
import {Container} from "reactstrap";
import Clients from "../clients/Clients";
import ClientInfo from "../clients/ClientInfo";
import ClientAdmin from "../clients/ClientAdmin";
import ProjectAdmin from "../projects/ProjectAdmin";
import CurrentTimeEntry from "../timeentry/CurrentTimeEntry";
import TimeEntries from "../timeentry/TimeEntries";
import ClientSelector from "../clients/ClientSelector";

const App = () => {
        return (
            <div>
                <Container fluid={true}>
                    <Navigation/>
                    <Switch>
                        <Route path="/admin">
                                    <div>
                                        <ClientAdmin/>
                                        <ProjectAdmin/>
                                    </div>
                        </Route>
                        <Route path="/">
                            <CurrentTimeEntry/>

                            <Clients/>
                            <ClientSelector/>
                            <div className="mt-3">
                                <ClientInfo/>
                            </div>
                            <TimeEntries/>
                        </Route>
                    </Switch>
                </Container>
            </div>
        );
};

export default App;
