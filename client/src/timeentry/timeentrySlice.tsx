import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {TimeEntry} from "../api/reportingApi";

type TimeentryState = {
    error?: string;
    currentTimeEntry?: TimeEntry;
}

let initialState: TimeentryState = {
    error: undefined,
    currentTimeEntry: undefined
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
    }
});

export const {
    currentTimeEntrySuccess,
    currentTimeEntryFailed
} = timeentrySlice.actions;

export default timeentrySlice.reducer;
