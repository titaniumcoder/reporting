import React from 'react';
import {render} from 'react-dom'
import {BrowserRouter as Router} from 'react-router-dom'
import Root from './containers/Root'
import configureStore from './store/configureStore'
import 'bootstrap/dist/css/bootstrap.min.css';

import {loadState, saveState} from './localStorage';
import {throttle} from 'lodash';
import {loadCash} from './actions';

import * as serviceWorker from './serviceWorker';

const persistedState = loadState();

const store = configureStore(persistedState);

setInterval(() => store.dispatch(loadCash()), 15000);

store.dispatch(loadCash());

store.subscribe(throttle(() => {
    const {
        authentication
    } = store.getState();
    saveState({
        authentication
    });
}, 3000));

render(
    <Router>
        <Root store={store}/>
    </Router>,
    document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
