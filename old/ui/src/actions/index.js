export const RESET_ERROR_MESSAGE = 'RESET_ERROR_MESSAGE';
export const GENERAL_ERROR = 'GENERAL_ERROR';

// Resets the currently visible error message.
export const resetErrorMessage = () => ({
    type: RESET_ERROR_MESSAGE
});

// Resets the currently visible error message.
export const generalError = (error) => ({
    type: GENERAL_ERROR,
    error
});

export const CASH_REQUEST = 'CASH_REQUEST';
export const CASH_SUCCESS = 'CASH_SUCCESS';
export const CASH_FAILURE = 'CASH_FAILURE';

export const loadCash = () => ({
    type: CASH_REQUEST
});

export const CLIENTS_REQUEST = 'CLIENTS_REQUEST';
export const CLIENTS_SUCCESS = 'CLIENTS_SUCCESS';
export const CLIENTS_FAILURE = 'CLIENTS_FAILURE';

export const loadClients = () => ({
    type: CLIENTS_REQUEST
});

export const TIMESHEET_REQUEST = 'TIMESHEET_REQUEST';
export const TIMESHEET_SUCCESS = 'TIMESHEET_SUCCESS';
export const TIMESHEET_FAILURE = 'TIMESHEET_FAILURE';

export const loadTimesheet = (id) => ({
    type: TIMESHEET_REQUEST,
    payload: {client: id}
});

export const EXCEL_REQUEST = 'EXCEL_REQUEST';
export const EXCEL_SUCCESS = 'EXCEL_SUCCESS';
export const EXCEL_FAILURE = 'EXCEL_FAILURE';
export const EXCEL_RESET = 'EXCEL_RESET';

export const loadExcel = () => ({
    type: EXCEL_REQUEST
});

export const resetExcel = () => ({
    type: EXCEL_RESET
});

export const BILLED_REQUEST = 'BILLED_REQUEST';
export const BILLED_SUCCESS = 'BILLED_SUCCESS';
export const BILLED_FAILURE = 'BILLED_FAILURE';

export const billed = () => ({
    type: BILLED_REQUEST
});

export const UNBILLED_REQUEST = 'UNBILLED_REQUEST';
export const UNBILLED_SUCCESS = 'UNBILLED_SUCCESS';
export const UNBILLED_FAILURE = 'UNBILLED_FAILURE';

export const unbilled = () => ({
    type: UNBILLED_REQUEST
});

export const LOGIN = 'LOGIN';
export const LOGOUT = 'LOGOUT';
export const UPDATE_LOGIN = 'UPDATE_LOGIN';

export const login = () => ({
    type: LOGIN,
});

export const logout = () => ({
    type: LOGOUT,
});

export const updateLogin = (username, password) => ({
    type: UPDATE_LOGIN,
    payload: {username, password}
});

export const UPDATE_FROM_TO = 'UPDATE_FROM_TO';

export const updateFromTo = (from, to) => ({
    type: UPDATE_FROM_TO,
    payload: {from, to}
});

