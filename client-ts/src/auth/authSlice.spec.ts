import auth, {authFailed, authSuccess, IAuthState, login, logout} from './authSlice'
import moment from "moment";

const sampleState: IAuthState = {
    username: undefined,
    password: undefined,
    authToken: undefined,
    authExpiration: undefined,
    refreshToken: undefined,
    refreshExpiration: undefined,
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
                authExpiration: moment(),
                refreshToken: 'xxx',
                refreshExpiration: moment(),
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
                type: authFailed.type,
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
    it('handles authSuccess correctly', () => {
        expect(
            auth(sampleState, {
                type: authSuccess.type,
                payload: {
                    authToken: 'aaa',
                    authExpiration: m,
                    refreshToken: 'bbb',
                    refreshExpiration: m
                }
            })
        ).toEqual({
            ...sampleState,
            authToken: 'aaa',
            authExpiration: m,
            refreshToken: 'bbb',
            refreshExpiration: m,
            error: undefined,
            loggedIn: true
        })
    });
    it('handles login correctly', () => {
        expect(
            auth(sampleState, {
                type: login.type,
                payload: {
                    username: 'fred',
                    password: 'fredP'
                }
            })
        ).toEqual({
            ...sampleState,
            username: 'fred',
            password: 'fredP',
            error: undefined
        })
    });
});
