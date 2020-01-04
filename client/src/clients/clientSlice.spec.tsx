import user, {loadClientsFailed, loadClientsSuccess, loadingClients} from './clientSlice'

const sampleState = {
    clients: [
        {
            active: false,
            id: 'ff',
            maxMinutes: undefined,
            name: 'FFFF',
            notes: undefined,
            rateInCentsPerHours: undefined
        }
    ],
    error: undefined,
    loading: false
};

describe('clientSlice', () => {
    it('handles loadingClients correctly', () => {
        expect(
            user({
                ...sampleState,
                error: 'blabla',
                loading: false
            }, {
                type: loadingClients.type
            })
        ).toEqual({
            error: undefined,
            loading: true,
            clients: []
        })
    });
    it('handles loadClientsSuccess correctly', () => {
        expect(
            user(sampleState, {
                type: loadClientsSuccess.type,
                payload:
                    [
                        {
                            active: true,
                            id: 'ff1',
                            maxMinutes: 100,
                            name: 'FFFF1',
                            notes: 'Notes',
                            rateInCentsPerHours: 150
                        }
                    ]
            })
        ).toEqual({
            error: undefined,
            clients: [{
                active: true,
                id: 'ff1',
                maxMinutes: 100,
                name: 'FFFF1',
                notes: 'Notes',
                rateInCentsPerHours: 150
            }],
            loading: false
        })
    });
    it('handles loadClientsFailed correctly', () => {
        expect(
            user(sampleState, {
                type: loadClientsFailed.type,
                payload: 'Hilfe!!'
            })
        ).toEqual({
            clients: [],
            loading: false,
            error: 'Hilfe!!'
        })
    });
});
