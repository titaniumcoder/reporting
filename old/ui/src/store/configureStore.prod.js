import {applyMiddleware, createStore} from 'redux'
import rootReducer from '../reducers'
import createSagaMiddleware from 'redux-saga'
import rootSaga from "../sagas";

const sagaMiddleware = createSagaMiddleware();

const configureStore = preloadedState => createStore(
    rootReducer,
    preloadedState,
    applyMiddleware(sagaMiddleware)
);

sagaMiddleware.run(rootSaga);

export default configureStore
