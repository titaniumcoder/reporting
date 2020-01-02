import React from 'react';
import {BrowserRouter as Router, Redirect, Route, Switch} from 'react-router-dom'
import './App.css';
import Navigation from "../Navigation";
import {Container} from "reactstrap";
import CurrentTimeEntry from "../CurrentTimeEntry";
import Clients from "../clients/Clients";
import ClientInfo from "../clients/ClientInfo";
import TimeLog from "../TimeLog";
import Login from "../auth/Login";
import {useSelector} from "react-redux";
import UserAdmin from "../users/UserAdmin";
import {RootState} from "../rootReducer";
import ClientAdmin from "../clients/ClientAdmin";
import ProjectAdmin from "../projects/ProjectAdmin";

const App = () => {
    const auth = useSelector((state: RootState) => state.auth);
    const {loggedIn, admin, canBook, canViewMoney} = auth;

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

                                <Clients clients={clients}/>
                                <Route path="/client/:client">
                                    <div className="mt-3">
                                        <ClientInfo canViewMoney={canViewMoney}/>
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
