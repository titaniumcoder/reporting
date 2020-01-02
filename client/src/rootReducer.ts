import {combineReducers} from "redux";
import authSlice from "./auth/authSlice";
import userSlice from "./users/userSlice";

const rootReducer = combineReducers({
        auth: authSlice,
        user: userSlice
    }
);

export type RootState = ReturnType<typeof rootReducer>;

export default rootReducer;
