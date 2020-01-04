import {combineReducers} from "redux";
import authSlice from "./auth/authSlice";
import userSlice from "./users/userSlice";
import projectSlice from "./projects/projectSlice";
import clientSlice from "./clients/clientSlice";
import timeentrySlice from "./timeentry/timeentrySlice";

const rootReducer = combineReducers({
        auth: authSlice,
        user: userSlice,
        project: projectSlice,
        client: clientSlice,
        timeentry: timeentrySlice
    }
);

export type RootState = ReturnType<typeof rootReducer>;

export default rootReducer;
