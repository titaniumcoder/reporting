import React from 'react';
import {BrowserRouter as Router, Redirect, Route, Switch} from 'react-router-dom'
import './App.css';
import Navigation from "../Navigation";
import {Container} from "reactstrap";
import CurrentTimeEntry from "../CurrentTimeEntry";
import Clients from "../Clients";
import ClientInfo from "../ClientInfo";
import TimeLog from "../TimeLog";
import Login from "../auth/Login";
import {useSelector} from "react-redux";

const App = ({auth, admin, canBook, canViewMoney}) => {
    const loggedIn = useSelector(state => state.auth.loggedIn);

    // TODO get from DB
    const clients = [{id: 'rsi', name: 'RSI'}, {id: 'srf', name: 'SRF'}, {
        id: 'hvh',
        name: 'Handballverein Herzogenbuchsee'
    }];

    if (!loggedIn) {
        return <Login/>
    } else {
        return (
            <Router>
                <div>
                    <Container fluid={true}>
                        <Navigation admin={admin}/>
                        <Switch>
                            {admin &&
                            <Route path="/admin">
                                {
                                    admin ? (
                                        <div>
                                            <div><h4>User Administration</h4></div>
                                            <div><h4>Client Administration</h4></div>
                                            <div><h4>Project Administration</h4></div>
                                        </div>) : <Redirect to="/"/>
                                }
                            </Route>
                            }
                            <Route path="/">
                                {canBook &&
                                <CurrentTimeEntry/>
                                }

                                <Clients clients={clients}/>
                                <Route path="/client/:client">
                                    <div className="mt-3">
                                        <ClientInfo className="mt-3" canViewMoney={canViewMoney}/>
                                        <hr/>
                                    </div>
                                </Route>
                                <TimeLog/>
                            </Route>
                        </Switch>
                    </Container>
                </div>
            </Router>
        );
    }
};

export default App;
