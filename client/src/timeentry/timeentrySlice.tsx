import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import reportingApi, {TimeEntry} from "../api/reportingApi";
import {AppThunk} from "../store";

type TimeentryRangeSelection = {
    from: string | undefined;
    to: string | undefined;
    clientId: string | undefined;
    allEntries: boolean;
}

type TimeentryState = {
    error?: string;
    currentTimeEntry?: TimeEntry;
    loading: boolean;
    timeentries: TimeEntry[];
} & TimeentryRangeSelection;

let initialState: TimeentryState = {
    error: undefined,
    currentTimeEntry: undefined,
    loading: false,
    timeentries: [],
    from: undefined,
    to: undefined,
    clientId: undefined,
    allEntries: false
};

const timeentrySlice = createSlice({
    name: 'timeentry',
    initialState,
    reducers: {
        currentTimeEntrySuccess(state, action: PayloadAction<TimeEntry | undefined>) {
            state.error = undefined;
            state.currentTimeEntry = action.payload;
        },
        currentTimeEntryFailed(state, action: PayloadAction<string>) {
            state.currentTimeEntry = undefined;
            state.error = action.payload;
        },
        loadTimeEntriesSuccess(state, action: PayloadAction<TimeEntry[]>) {
            state.loading = false;
            state.error = undefined;
            state.timeentries = action.payload;
        },
        loadTimeEntriesFailed(state, action: PayloadAction<string>) {
            state.loading = false;
            state.timeentries = [];
            state.error = action.payload;
        },
        loadTimeEntriesStarted(state) {
            state.loading = true;
            state.error = undefined;
        },
        selectTimeRange(state, action: PayloadAction<TimeentryRangeSelection>) {
            state.from = action.payload.from;
            state.to = action.payload.to;
            state.clientId = action.payload.clientId;
            state.allEntries = action.payload.allEntries;
        }
    }
});

export const {
    currentTimeEntrySuccess,
    currentTimeEntryFailed,
    loadTimeEntriesSuccess,
    loadTimeEntriesFailed,
    loadTimeEntriesStarted,
    selectTimeRange
} = timeentrySlice.actions;

export default timeentrySlice.reducer;

export const fetchTimeEntries = (from, to, clientId, allEntries): AppThunk => async dispatch => {
    try {
        dispatch(loadTimeEntriesStarted());
        const projects = await reportingApi.loadTimeEntries(from, to, clientId, allEntries);
        dispatch(loadTimeEntriesSuccess(projects.data));
    } catch (err) {
        dispatch(loadTimeEntriesFailed(err.toString()));
    }
};
