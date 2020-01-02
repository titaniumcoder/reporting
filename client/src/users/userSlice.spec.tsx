import user, {loadingUsers, loadUsersSuccess, loadUsersFailed} from './userSlice'

const sampleState = {
    users: [{admin: false, canBook: false, canViewMoney: false, clients: [], email: 'fred@feuerstein.org'}],
    error: undefined,
    loading: false
};

describe('userSlice', () => {
    it('handles loadingUsers correctly', () => {
        expect(
            user({
                ...sampleState,
                error: 'blabla',
                loading: false
            }, {
                type: loadingUsers.type
            })
        ).toEqual({
            error: undefined,
            loading: true,
            users: []
        })
    });
    it('handles loadUsersSuccess correctly', () => {
        expect(
            user(sampleState, {
                type: loadUsersSuccess.type,
                payload:
                    [
                        {
                            admin: false,
                            canBook: true,
                            canViewMoney: true,
                            clients: ['A', 'B'],
                            email: 'wilma@feuerstein.org'
                        }
                    ]
            })
        ).toEqual({
            error: undefined,
            users: [{
                admin: false,
                canBook: true,
                canViewMoney: true,
                clients: ['A', 'B'],
                email: 'wilma@feuerstein.org'
            }],
            loading: false
        })
    });
    it('handles loading failure correctly', () => {
        expect(
            user(sampleState, {
                type: loadUsersFailed.type,
                payload: 'Hilfe!!'
            })
        ).toEqual({
            users: [],
            loading: false,
            error: 'Hilfe!!'
        })
    });
});
