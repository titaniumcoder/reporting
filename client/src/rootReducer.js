import {combineReducers} from "redux";
import authSlice from "./auth/authSlice";

const rootReducer = combineReducers({
        auth: authSlice
    }
);


export default rootReducer;
