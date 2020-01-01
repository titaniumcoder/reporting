import React from 'react';
import {BrowserRouter as Router, Redirect, Route, Switch} from 'react-router-dom'
import './App.css';
import Navigation from "./Navigation";
import {Container} from "reactstrap";
import CurrentTimeEntry from "./CurrentTimeEntry";
import Clients from "./Clients";
import ClientInfo from "./ClientInfo";
import TimeLog from "./TimeLog";

const Login = ({admin, canBook, canViewMoney}) => {
    // TODO get from DB
    const clients = [{id: 'rsi', name: 'RSI'}, {id: 'srf', name: 'SRF'}, {
        id: 'hvh',
        name: 'Handballverein Herzogenbuchsee'
    }];

    return (
        <div>
            <h1>Log me in</h1>
        </div>
    );
};

export default Login;
