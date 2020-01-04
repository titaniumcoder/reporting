import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import reportingApi, {TimeEntry} from "../api/reportingApi";
import {AppThunk} from "../store";

type TimeentryState = {
    loading: boolean;
    error?: string;
    currentTimeEntry?: TimeEntry;
}

let initialState: TimeentryState = {
    loading: false,
    error: undefined,
    currentTimeEntry: undefined
};

const timeentrySlice = createSlice({
    name: 'timeentry',
    initialState,
    reducers: {
        currentTimeEntrySuccess(state, action: PayloadAction<TimeEntry | undefined>) {
            state.loading = false;
            state.error = undefined;
            state.currentTimeEntry = action.payload;
        },
        currentTimeEntryFailed(state, action: PayloadAction<string>) {
            state.loading = false;
            state.currentTimeEntry = undefined;
            state.error = action.payload;
        },
        currentTimeEntryStarted(state) {
            state.loading = true;
            state.error = undefined;
            state.currentTimeEntry = undefined;
        }
    }
});

export const {
    currentTimeEntryStarted,
    currentTimeEntrySuccess,
    currentTimeEntryFailed
} = timeentrySlice.actions;

export default timeentrySlice.reducer;

export const fetchCurrentTimeEntry = (): AppThunk => async dispatch => {
    try {
        dispatch(currentTimeEntryStarted());
        const timeentry = await reportingApi.fetchCurrentTimeetnry();
        dispatch(currentTimeEntrySuccess(timeentry));
    } catch (err) {
        dispatch(currentTimeEntryFailed(err.toString()));
    }
};
