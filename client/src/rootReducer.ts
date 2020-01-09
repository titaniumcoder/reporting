import {combineReducers} from "redux";
import authSlice, {logout} from "./auth/authSlice";
import userSlice from "./users/userSlice";
import projectSlice from "./projects/projectSlice";
import clientSlice from "./clients/clientSlice";
import timeentrySlice from "./timeentry/timeentrySlice";

const appReducer = combineReducers({
        auth: authSlice,
        user: userSlice,
        project: projectSlice,
        client: clientSlice,
        timeentry: timeentrySlice
    }
);

const rootReducer = (state, action) => {
    if (action.type === logout.type) {
        return appReducer(undefined, action);
    }

    return appReducer(state, action);
};

export type RootState = ReturnType<typeof rootReducer>;

export default rootReducer;
