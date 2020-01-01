import auth, {loginFailed, login, logout} from './authSlice'
import moment from "moment";

const sampleState = {
    username: undefined,
    email: undefined,
    authToken: undefined,
    authExpiration: undefined,
    error: 'my error',
    loggedIn: false
};

const m = moment();

describe('authSlice', () => {
    it('handles logout correctly', () => {
        expect(
            auth({
                ...sampleState,
                authToken: 'abc',
                email: 'fred@test.org',
                authExpiration: moment(),
                loggedIn: true
            }, {
                type: logout.type
            })
        ).toEqual({
            ...sampleState,
            error: undefined,
            loggedIn: false
        })
    });
    it('handles authFailed correctly', () => {
        expect(
            auth(sampleState, {
                type: loginFailed.type,
                payload: {
                    error: 'I have failed'
                }
            })
        ).toEqual({
            ...sampleState,
            error: 'I have failed',
            loggedIn: false
        })
    });
    it('handles login correctly', () => {
        expect(
            auth(sampleState, {
                type: login.type,
                payload: {
                    authToken: 'aaa',
                    authExpiration: 9000,
                    email: 'loggedin@test.org'
                }
            })
        ).toEqual({
            ...sampleState,
            authToken: 'aaa',
            email: 'loggedin@test.org',
            authExpiration: moment.utc(9000),
            error: undefined,
            loggedIn: true
        })
    });
});
