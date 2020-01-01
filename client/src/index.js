import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import * as serviceWorker from './serviceWorker';

import {library} from '@fortawesome/fontawesome-svg-core'
import {
    faCheck,
    faCog,
    faFileExcel,
    faPlayCircle,
    faSignOutAlt,
    faStopCircle,
    faUndo
} from "@fortawesome/free-solid-svg-icons";
import App from './app/App';
import {Provider} from "react-redux";
import rootReducer from "./rootReducer";
import {configureStore} from "@reduxjs/toolkit";

library.add(faFileExcel);
library.add(faCheck);
library.add(faUndo);
library.add(faSignOutAlt);
library.add(faCog);
library.add(faStopCircle);
library.add(faPlayCircle);

const store = configureStore({
    reducer: rootReducer
});

ReactDOM.render(
    <Provider store={store}>
        <App auth={null} admin={true}/>
    </Provider>
    , document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
