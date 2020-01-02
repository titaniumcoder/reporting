import {createSlice} from "@reduxjs/toolkit";

const authSlice = createSlice({
    name: 'auth',
    initialState: {
        username: undefined,
        email: undefined,
        authToken: undefined,
        authExpiration: undefined,
        error: undefined,
        loggedIn: false,
        admin: false,
        canBook: false,
        canViewMoney: false
    },
    reducers: {
        login: {
            reducer(state, action) {
                const {username, email, authToken, authExpiration} = action.payload;
                state.username = username;
                state.email = email;
                state.authToken = authToken;
                state.authExpiration = authExpiration;
                state.error = undefined;
                state.loggedIn = true;
            },
            prepare(username, email, authToken, authExpiration) {
                return {
                    payload: {username, email, authToken, authExpiration}
                }
            }
        },
        infoUpdate: {
            reducer(state, action) {
                const {admin, canBook, canViewMoney}=action.payload;
                state.admin = admin;
                state.canBook = canBook;
                state.canViewMoney = canViewMoney;
            },
            prepare(admin, canBook, canViewMoney) {
                return {
                    payload: {admin, canBook, canViewMoney}
                }
            }
        },
        logout(state) {
            state.authToken = undefined;
            state.authExpiration = undefined;
            state.username = undefined;
            state.email = undefined;
            state.error = undefined;
            state.loggedIn = false;
            state.canBook = false;
            state.admin = false;
            state.canViewMoney = false;
        },
        loginFailed: {
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

export const {logout, login, loginFailed, infoUpdate} = authSlice.actions;
export default authSlice.reducer;
