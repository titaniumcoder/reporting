import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {AppThunk} from "../store";
import reportingApi, {User} from "../api/reportingApi";

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

const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        loadUsersSuccess(state, action: PayloadAction<User[]>) {
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

export const {loadUsersSuccess, loadUsersFailed, loadingUsers} = userSlice.actions;
export default userSlice.reducer;

export const fetchUsers = (): AppThunk => async dispatch => {
    try {
        dispatch(loadingUsers());
        const users = await reportingApi.fetchUsers();
        dispatch(loadUsersSuccess(users.data));
    } catch (err) {
        dispatch(loadUsersFailed(err.toString()));
    }
};
