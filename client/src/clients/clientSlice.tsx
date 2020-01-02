import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import reportingApi, {Client} from "../api/reportingApi";
import {AppThunk} from "../store";
import {loadingUsers, loadUsersFailed, loadUsersSuccess} from "../users/userSlice";

type UserState = {
    loading: boolean;
    error?: string;
    clients: Client[];
}

let initialState: UserState = {
    clients: [],
    loading: false,
    error: undefined
};

const clientSlice = createSlice({
    name: 'client',
    initialState,
    reducers: {
        loadClientsSuccess(state, action: PayloadAction<Client[]>) {
            state.loading = false;
            state.error = undefined;
            state.clients = action.payload;
        },
        loadClientsFailed(state, action: PayloadAction<string>) {
            state.loading = false;
            state.clients = [];
            state.error = action.payload;
        },
        loadingClients(state) {
            state.loading = true;
            state.error = undefined;
            state.clients = [];
        }
    }
});

export const {loadClientsSuccess, loadClientsFailed, loadingClients} = clientSlice.actions;
export default clientSlice.reducer;

export const fetchClients = (): AppThunk => async dispatch => {
    try {
        dispatch(loadingClients());
        const clients = await reportingApi.fetchClients();
        dispatch(loadClientsSuccess(clients.data));
    } catch (err) {
        dispatch(loadClientsFailed(err.toString()));
    }
};
