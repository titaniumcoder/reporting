import React from 'react';

import './Login.css';
import GoogleLogin, {GoogleLogout} from "react-google-login";

const Login = ({executeLogin}) => {
    const responseSuccessGoogle = (response) => {
        console.log('Success', response);
    };

    const responseFailureGoogle = (response) => {
        console.log('Failure', response);
    };

    const logout = (response) => {
        console.log('Logout', response);
    };

    return (
        <div>
            <GoogleLogin
                clientId="483387557047-olrn4mkp6m7gltp5fqhgr76094q6qf6e.apps.googleusercontent.com"
                buttonText="Login"
                responseType="code"
                isSignedIn={true}
                onSuccess={responseSuccessGoogle}
                onFailure={responseFailureGoogle}
                cookiePolicy={'single_host_origin'}
            />

            <GoogleLogout
                clientId="483387557047-olrn4mkp6m7gltp5fqhgr76094q6qf6e.apps.googleusercontent.com"
                buttonText="Logout"
                onLogoutSuccess={logout}
            />
        </div>
    )        ;
};

export default Login;
