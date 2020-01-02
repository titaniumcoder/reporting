import auth, {loginFailed, login, logout, infoUpdate} from './authSlice'

const sampleState = {
    username: undefined,
    email: undefined,
    authToken: undefined,
    authExpiration: undefined,
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
            authExpiration: 9000,
            error: undefined,
            loggedIn: true
        })
    });
    it('handles the server update correctly', () => {
        expect(
            auth(sampleState, {
                type: infoUpdate.type,
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
            canViewMoney: true
        })
    });
});
