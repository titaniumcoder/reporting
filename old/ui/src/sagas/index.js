import {call, put, select, takeEvery, takeLatest} from 'redux-saga/effects'
import {callApi, generalApi} from "../middleware/api";
import {
    BILLED_FAILURE,
    BILLED_REQUEST,
    BILLED_SUCCESS,
    CASH_FAILURE,
    CASH_REQUEST,
    CASH_SUCCESS,
    CLIENTS_FAILURE,
    CLIENTS_REQUEST,
    CLIENTS_SUCCESS,
    EXCEL_FAILURE,
    EXCEL_REQUEST,
    EXCEL_SUCCESS,
    LOGIN,
    TIMESHEET_FAILURE,
    TIMESHEET_REQUEST,
    TIMESHEET_SUCCESS,
    UNBILLED_FAILURE,
    UNBILLED_REQUEST,
    UNBILLED_SUCCESS,
    UPDATE_FROM_TO
} from "../actions";

const extractLogin = (state) => ({
    loggedIn: state.authentication.loggedIn,
    username: state.authentication.username,
    password: state.authentication.password
});

const extractFromTo = (state) => ({
    from: state.form.from,
    to: state.form.to,
    clientId: state.client.id
});

export function* fetchCash() {
    try {
        const login = yield select(extractLogin);
        if (login.loggedIn) {
            const data = yield call(callApi, '/api/cash', login.username, login.password);
            yield put({type: CASH_SUCCESS, payload: data})
        }
    } catch (error) {
        yield put({type: CASH_FAILURE, error})
    }
}

export function* fetchClients() {
    try {
        const login = yield select(extractLogin);
        if (login.loggedIn) {
            const data = yield call(callApi, '/api/clients', login.username, login.password);
            yield put({type: CLIENTS_SUCCESS, payload: data})
        }
    } catch (error) {
        yield put({type: CLIENTS_FAILURE, error})
    }
}

export function* fetchTimesheet(action) {
    try {
        const login = yield select(extractLogin);
        const fromto = yield select(extractFromTo);
        const id = (action && action.payload && action.payload.client) || fromto.clientId;
        if (login.loggedIn) {
            const data = yield call(
                callApi,
                `/api/client/${id}?from=${fromto.from}&to=${fromto.to}`,
                login.username,
                login.password);
            yield put({type: TIMESHEET_SUCCESS, payload: data})
        }
    } catch (error) {
        yield put({type: TIMESHEET_FAILURE, error})
    }
}

export function* fetchExcel() {
    try {
        const login = yield select(extractLogin);
        const fromto = yield select(extractFromTo);
        if (login.loggedIn) {
            const data = yield call(
                generalApi,
                `/api/timesheet/${fromto.clientId}?from=${fromto.from}&to=${fromto.to}`,
                'GET',
                null,
                login.username,
                login.password,
                true
            );
            yield put({type: EXCEL_SUCCESS, payload: data})
        }
    } catch (error) {
        yield put({type: EXCEL_FAILURE, error})
    }
}

export function* tagBilled() {
    try {
        const login = yield select(extractLogin);
        const fromto = yield select(extractFromTo);
        if (login.loggedIn) {
            const data = yield call(
                generalApi,
                `/api/client/${fromto.clientId}/billed?from=${fromto.from}&to=${fromto.to}`,
                'PUT',
                null,
                login.username,
                login.password,
                false,
                true
            );
            yield put({type: BILLED_SUCCESS, payload: data});
            yield put({type: TIMESHEET_REQUEST});
        }
    } catch (error) {
        yield put({type: BILLED_FAILURE, error})
    }
}

export function* untagBilled() {
    try {
        const login = yield select(extractLogin);
        const fromto = yield select(extractFromTo);
        if (login.loggedIn) {
            const data = yield call(
                generalApi,
                `/api/client/${fromto.clientId}/billed?from=${fromto.from}&to=${fromto.to}`,
                'DELETE',
                null,
                login.username,
                login.password,
                false,
                true
            );
            yield put({type: UNBILLED_SUCCESS, payload: data});
            yield put({type: TIMESHEET_REQUEST});
        }
    } catch (error) {
        yield put({type: UNBILLED_FAILURE, error})
    }
}

// use them in parallel
export default function* rootSaga() {
    yield takeEvery(CASH_REQUEST, fetchCash);
    yield takeLatest(LOGIN, fetchClients);
    yield takeLatest(CLIENTS_REQUEST, fetchClients);
    yield takeLatest(UPDATE_FROM_TO, fetchTimesheet);
    yield takeLatest(TIMESHEET_REQUEST, fetchTimesheet);
    yield takeLatest(EXCEL_REQUEST, fetchExcel);
    yield takeLatest(BILLED_REQUEST, tagBilled);
    yield takeLatest(UNBILLED_REQUEST, untagBilled);
}