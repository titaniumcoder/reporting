import auth, {loginFailed, login, logout, userinfoUpdate} from './authSlice'

const sampleState = {
    username: '',
    email: '',
    authToken: '',
    authExpiration: 0,
    error: 'my error',
    loggedIn: false,
    admin: false,
    canViewMoney: false,
    canBook: false
};

describe('authSlice', () => {
    it('handles logout correctly', () => {
        expect(
            auth({
                ...sampleState,
                authToken: 'abc',
                email: 'fred@test.org',
                authExpiration: 1,
                loggedIn: true,
                admin: true,
                canBook: true,
                canViewMoney: true
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
                payload: 'I have failed'
            })
        ).toEqual({
            ...sampleState,
            error: 'I have failed',
            loggedIn: false,
            admin: false,
            canBook: false,
            canViewMoney: false
        })
    });
    it('handles login correctly', () => {
        expect(
            auth(sampleState, {
                type: login.type,
                payload: {
                    authToken: 'aaa',
                    authExpiration: 9000,
                    email: 'loggedin@test.org',
                    username: 'Fred Feuerstein'
                }
            })
        ).toEqual({
            ...sampleState,
            authToken: 'aaa',
            email: 'loggedin@test.org',
            authExpiration: 9000,
            error: undefined,
            loggedIn: false,
            username: 'Fred Feuerstein'
        })
    });
    it('handles the server update correctly', () => {
        expect(
            auth(sampleState, {
                type: userinfoUpdate.type,
                payload: {
                    admin: true,
                    canBook: true,
                    canViewMoney: true
                }
            })
        ).toEqual({
            ...sampleState,
            error: "my error",
            admin: true,
            canBook: true,
            canViewMoney: true,
            loggedIn: true
        })
    });
});
