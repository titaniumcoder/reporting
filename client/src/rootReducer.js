import {combineReducers} from "redux";
import authSlice from "./auth/authSlice";
import userSlice from "./users/userSlice";

const rootReducer = combineReducers({
        auth: authSlice,
        user: userSlice
    }
);


export default rootReducer;
