import {createSlice} from "@reduxjs/toolkit";
import {Moment} from "moment";

export interface IAuthState {
    username?: string;
    password?: string;
    authToken?: string;
    authExpiration?: Moment;
    refreshToken?: string;
    refreshExpiration?: Moment;
    error?: string;
    loggedIn: boolean;
}

const authSlice = createSlice({
    name: 'auth',
    initialState: {
        username: undefined,
        password: undefined,
        authToken: undefined,
        authExpiration: undefined,
        refreshToken: undefined,
        refreshExpiration: undefined,
        error: undefined,
        loggedIn: false
    } as IAuthState,
    reducers: {
        login: {
            reducer(state, action) {
                const {username, password} = action.payload;
                state.username = username;
                state.password = password;
                state.error = undefined;
            },
            prepare(username, password) {
                return {
                    payload: {username, password}
                }
            }
        },
        logout(state) {
            state.authToken = undefined;
            state.authExpiration = undefined;
            state.refreshToken = undefined;
            state.refreshExpiration = undefined;
            state.error = undefined;
            state.loggedIn = false;
        },
        authSuccess: {
            reducer(state, action) {
                const {authToken, authExpiration, refreshToken, refreshExpiration} = action.payload;
                state.authToken = authToken;
                state.authExpiration = authExpiration;
                state.refreshToken = refreshToken;
                state.refreshExpiration = refreshExpiration;
                state.error = undefined;
                state.loggedIn = true;
            },
            prepare(authToken, authExpiration, refreshToken, refreshExpiration) {
                return {
                    payload: {
                        authToken, authExpiration, refreshToken, refreshExpiration
                    }
                }
            }
        },
        authFailed: {
            reducer(state, action) {
                // TODO what do we do here?
                const {error} = action.payload;
                state.error = error;
                state.loggedIn = false;
            },
            prepare(error) {
                return {
                    payload: {
                        error
                    }
                }
            }
        }
    }
});

export const {authFailed, authSuccess, login, logout} = authSlice.actions;
export default authSlice.reducer;
