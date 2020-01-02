import React from 'react';

import './Login.css';
import GoogleLogin from "react-google-login";
import {GoogleClientId} from "../constants";
import {login, loginFailed, userinfoUpdate} from "./authSlice";
import {useDispatch} from "react-redux";
import api, {ICurrentUser} from "../api/reportingApi";
import {AxiosResponse} from "axios";

const Login = () => {
    const dispatch = useDispatch();

    const responseSuccessGoogle = async (response) => {
        dispatch(login({
            username: response.profileObj.name,
            email: response.profileObj.email,
            authToken: response.tokenObj.id_token,
            authExpiration: response.tokenObj.expires_at
        }));
        try {
            const serverResponse: AxiosResponse<ICurrentUser> = await api.auth(response.tokenObj.id_token);
            if (serverResponse.status === 200) {
                const {admin, canBook, canViewMoney} = serverResponse.data;
                dispatch(userinfoUpdate({admin, canBook, canViewMoney}))
            } else {
                dispatch(loginFailed("Unexpected Server Response Status: " + serverResponse.status));
            }
        } catch (err) {
            dispatch(loginFailed(err.toString()));
        }
    };

    const responseFailureGoogle = (response) => {
        dispatch(loginFailed(response.toString()));
    };

    return (
        <div className="login">
            <GoogleLogin
                clientId={GoogleClientId}
                buttonText="Login"
                isSignedIn={true}
                onSuccess={responseSuccessGoogle}
                onFailure={responseFailureGoogle}
                cookiePolicy={'single_host_origin'}
            />
        </div>
    );
};

export default Login;
