import {createSlice, PayloadAction} from "@reduxjs/toolkit";

interface LoginState {
    username: string;
    email: string;
    authToken: string;
    authExpiration: number;
}

interface UserInfoState {
    admin: boolean,
    canBook: boolean,
    canViewMoney: boolean
}

type AuthState = {
    error?: string;
    loggedIn: boolean;
} & UserInfoState & LoginState;

let initialState: AuthState = {
    username: '',
    email: '',
    authToken: '',
    authExpiration: 0,
    error: undefined,
    loggedIn: false,
    admin: false,
    canBook: false,
    canViewMoney: false
};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        login(state, action: PayloadAction<LoginState>) {
            const {username, email, authToken, authExpiration} = action.payload;
            state.username = username;
            state.email = email;
            state.authToken = authToken;
            state.authExpiration = authExpiration;
            state.error = undefined;
            state.loggedIn = false;
        },
        userinfoUpdate(state, action: PayloadAction<UserInfoState>) {
            const {admin, canBook, canViewMoney} = action.payload;
            state.admin = admin;
            state.canBook = canBook;
            state.canViewMoney = canViewMoney;
            state.loggedIn = true;
        },
        logout(state) {
            state.authToken = '';
            state.authExpiration = 0;
            state.username = '';
            state.email = '';
            state.error = undefined;
            state.loggedIn = false;
            state.canBook = false;
            state.admin = false;
            state.canViewMoney = false;
        },
        loginFailed(state, action: PayloadAction<string>) {
            // TODO what do we do here?
            state.error = action.payload;
            state.loggedIn = false;
            state.admin = false;
            state.canBook = false;
            state.canViewMoney = false;
            state.username = '';
            state.email = '';
        }
    }
});

export const {logout, login, loginFailed, userinfoUpdate} = authSlice.actions;

export default authSlice.reducer;
