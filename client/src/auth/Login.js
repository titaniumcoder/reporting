import React from 'react';

import './Login.css';
import GoogleLogin from "react-google-login";
import {GoogleClientId} from "../constants";
import {login, logout} from "./authSlice";
import {useDispatch} from "react-redux";

const Login = () => {
    const dispatch = useDispatch();

    const responseSuccessGoogle = (response) => {
        dispatch(login(response.profileObj.name, response.profileObj.email, response.tokenObj.access_token, response.tokenObj.expires_at));
    };

    const responseFailureGoogle = (response) => {
        console.error('Google Failure during login', response);
        dispatch(logout());
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
