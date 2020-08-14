import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import {HashRouter as Router} from 'react-router-dom'

import {library} from '@fortawesome/fontawesome-svg-core'
import {
    faCheck,
    faCog,
    faFileExcel,
    faPen,
    faPlayCircle,
    faPlus,
    faSignOutAlt,
    faStopCircle,
    faTimes,
    faToggleOn,
    faTrash,
    faUndo
} from "@fortawesome/free-solid-svg-icons";
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
library.add(faPlus);
library.add(faPen);
library.add(faTrash);
library.add(faTimes);
library.add(faToggleOn);

const store = configureStore({
    reducer: rootReducer
});

const render = () => {
    const App = require('./app/App').default;

    ReactDOM.render(
        <Provider store={store}>
            <Router>
                <App/>
            </Router>
        </Provider>,
        document.getElementById('root'));
};

render();

// @ts-ignore
if (process.env.NODE_ENV === 'development' && module.hot) {
    // @ts-ignore
    module.hot.accept('./app/App', render);
}