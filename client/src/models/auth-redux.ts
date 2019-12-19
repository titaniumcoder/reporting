const LOGIN = 'LOGIN';
const LOGOUT = 'LOGOUT';

const AUTH_SUCCESS = 'AUTH_SUCCESS';
const AUTH_FAILED = 'AUTH_FAILED';

function login(username: string, password: string) {
    return {
        type: LOGIN,
        username, password
    };
}

function logout() {
    return {
        type: LOGOUT
    }
}

function authSuccess() {
    return {
        type: AUTH_SUCCESS
    }
}

function authFailed() {
    return {
        type: AUTH_FAILED
    }
}


function auth(state = {}, action) {

}
