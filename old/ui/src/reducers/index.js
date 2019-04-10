import * as ActionTypes from '../actions'
import {combineReducers} from 'redux'
import * as moment from 'moment';

const authentication = (state = {loggedIn: false, username: '', password: ''}, action) => {
    const {type, payload} = action;

    switch (type) {
        case ActionTypes.LOGIN:
            return {...state, loggedIn: true};
        case ActionTypes.LOGOUT:
            return {...state, loggedIn: false};
        case ActionTypes.UPDATE_LOGIN:
            const {username, password} = payload;
            return {
                ...state,
                username: username !== undefined ? username : state.username,
                password: password !== undefined ? password : state.password
            };
        default:
            return state;
    }
};

// Updates error message to notify about the failed fetches.
const errorMessage = (state = null, action) => {
    const {type, error} = action;

    if (type === ActionTypes.RESET_ERROR_MESSAGE) {
        return null
    } else if (error) {
        return typeof (error) === "string" ? error : error.toString()
    }

    return state
};

const clients = (state = {clients: []}, action) => {
    const {type, payload} = action;

    switch (type) {
        case ActionTypes.CLIENTS_SUCCESS:
            return {...state, clients: payload.clients};
        case ActionTypes.LOGOUT:
            return {...state, clients: []};
        default:
            return state;
    }
};

const system = (state = {cash: []}, action) => {
    const {type, payload} = action;

    switch (type) {
        case ActionTypes.CASH_SUCCESS:
            return {...state, cash: payload.cash};
        case ActionTypes.CASH_FAILURE:
            return {...state, cash: []};
        case ActionTypes.LOGOUT:
            return {...state, cash: []};
        default:
            return state;
    }
};

const formFormat = 'YYYY-MM-DD';

const form = (state = {
    from: moment().startOf('month').format(formFormat),
    to: moment().endOf('month').format(formFormat)
}, action) => {
    const {type, payload} = action;

    switch (type) {
        case ActionTypes.UPDATE_FROM_TO:
            const {from, to} = payload;
            return {...state, from, to};
        default:
            return state;
    }
};

const client = (state = {id: null, client: null, projects: [], timeEntries: []}, action) => {
    const {type, payload} = action;

    switch (type) {
        case ActionTypes.TIMESHEET_REQUEST:
            if (payload && payload.client) {
                return {...state, id: payload.client};
            } else
                return state;
        case ActionTypes.TIMESHEET_SUCCESS:
            return {...state, ...payload};
        case ActionTypes.LOGOUT:
            return {...state, id: null, client: null, from: null, to: null, projects: [], timeEntries: []};
        default:
            return state;
    }
};

const excel = (state = { blob: null, url: null}, action) => {
    const {type, payload} = action;

    switch (type) {
        case ActionTypes.EXCEL_REQUEST:
        case ActionTypes.EXCEL_FAILURE:
        case ActionTypes.EXCEL_RESET:
            return {blob: null, url: null};
        case ActionTypes.EXCEL_SUCCESS:
            return {...state, blob: payload, url: URL.createObjectURL(payload)};
        default:
            return state;
    }
};

const rootReducer = combineReducers({
    clients,
    authentication,
    errorMessage,
    client,
    system,
    form,
    excel
});

export default rootReducer
