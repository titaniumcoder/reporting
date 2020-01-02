import user, {loadingUsers, loadUsers, loadUsersFailed} from './projectSlice'

const sampleState = {
    users: [],
    loading: false,
    error: undefined
};

describe('userSlice', () => {
    it('handles loadingUsers correctly', () => {
        expect(
            user({
                ...sampleState,
                authToken: 'abc',
                email: 'fred@test.org',
                authExpiration: 1,
                loggedIn: true,
                admin: true,
                canBook: true,
                canViewMoney: true
            }, {
                type: loadingUsers.type
            })
        ).toEqual({
            ...sampleState,
            error: undefined,
            loggedIn: false
        })
    });
    it('handles loadUsers correctly', () => {
        expect(
            user(sampleState, {
                type: loadUsers.type,
                payload: {
                    error: 'I have failed'
                }
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
    it('handles loading failure correctly', () => {
        expect(
            user(sampleState, {
                type: loadUsersFailed.type,
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
});
