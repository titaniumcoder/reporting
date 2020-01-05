import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import reportingApi, {Client, ClientList} from "../api/reportingApi";
import {AppThunk} from "../store";

type ClientState = {
    loading: boolean;
    error?: string;
    clients: Client[];
    clientList: { id: string, name: string }[]
    selectedClient: string | undefined
}

let initialState: ClientState = {
    clients: [],
    loading: false,
    error: undefined,
    clientList: [],
    selectedClient: undefined
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
        loadClientsStarted(state) {
            state.loading = true;
            state.error = undefined;
            state.clients = [];
        },
        loadClientListSuccess(state, action: PayloadAction<ClientList[]>) {
            state.loading = false;
            state.error = undefined;
            state.clientList = action.payload;
        },
        loadClientListFailed(state, action: PayloadAction<string>) {
            state.loading = false;
            state.clientList = [];
            state.error = action.payload;
        },
        loadClientListStarted(state) {
            state.loading = true;
            state.error = undefined;
            state.clientList = [];
        },
        selectClient(state, action: PayloadAction<string | undefined>) {
            state.selectedClient = action.payload;
        }
    }
});

export const {
    loadClientsSuccess,
    loadClientsFailed,
    loadClientsStarted,
    loadClientListSuccess,
    loadClientListFailed,
    loadClientListStarted,
    selectClient
} = clientSlice.actions;

export default clientSlice.reducer;

export const fetchClientList = (): AppThunk => async dispatch => {
    try {
        dispatch(loadClientListStarted());
        const clients = await reportingApi.fetchClientList();
        dispatch(loadClientListSuccess(clients.data));
    } catch (err) {
        dispatch(loadClientListFailed(err.toString()));
    }
};

export const fetchClients = (): AppThunk => async dispatch => {
    try {
        dispatch(loadClientsStarted());
        const clients = await reportingApi.fetchClients();
        dispatch(loadClientsSuccess(clients.data));
    } catch (err) {
        dispatch(loadClientsFailed(err.toString()));
    }
};
