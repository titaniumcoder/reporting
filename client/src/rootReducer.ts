import {combineReducers} from "redux";
import projectSlice from "./projects/projectSlice";
import clientSlice from "./clients/clientSlice";
import timeentrySlice from "./timeentry/timeentrySlice";

const appReducer = combineReducers({
        project: projectSlice,
        client: clientSlice,
        timeentry: timeentrySlice
    }
);

const rootReducer = (state, action) => {
    return appReducer(state, action);
};

export type RootState = ReturnType<typeof rootReducer>;

export default rootReducer;
