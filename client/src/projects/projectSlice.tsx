import {createSlice, PayloadAction} from "@reduxjs/toolkit";

interface User {
    email: string;
    admin: boolean;
    canViewMoney: boolean;
    canBook: boolean;
    clients: string[];
}

type UserState = {
    loading: boolean;
    error?: string;
    users: User[];
}

let initialState: UserState = {
    users: [],
    loading: false,
    error: undefined
};

const projectSlice = createSlice({
    name: 'project',
    initialState,
    reducers: {
        loadUsers(state, action: PayloadAction<User[]>) {
            state.loading = false;
            state.error = undefined;
            state.users = action.payload;
        },
        loadUsersFailed(state, action: PayloadAction<string>) {
            state.loading = false;
            state.users = [];
            state.error = action.payload;
        },
        loadingUsers(state) {
            state.loading = true;
            state.error = undefined;
            state.users = [];
        }
    }
});

export const {loadUsers, loadUsersFailed, loadingUsers} = projectSlice.actions;
export default projectSlice.reducer;
