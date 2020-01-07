import client, {loadClientsFailed, loadClientsSuccess, loadClientsStarted, loadClientListFailed, loadClientListSuccess, loadClientListStarted, selectClient} from './clientSlice'

const sampleState = {
    clients: [
        {
            active: false,
            clientId: 'ff',
            maxMinutes: undefined,
            name: 'FFFF',
            notes: undefined,
            rateInCentsPerHour: undefined
        }
    ],
    clientList: [
        {
            clientId: 'ff',
            name: 'FFFF'
        }
    ],
    error: undefined,
    loading: false,
    selectedClient: undefined
};

describe('clientSlice', () => {
    it('handles loadClientsStarted correctly', () => {
        expect(
            client({
                ...sampleState,
                error: 'blabla',
                loading: false
            }, {
                type: loadClientsStarted.type
            })
        ).toEqual({
            ...sampleState,
            error: undefined,
            loading: true
        })
    });
    it('handles loadClientsSuccess correctly', () => {
        expect(
            client(sampleState, {
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
            clientList: sampleState.clientList,
            loading: false
        })
    });
    it('handles loadClientsFailed correctly', () => {
        expect(
            client(sampleState, {
                type: loadClientsFailed.type,
                payload: 'Hilfe!!'
            })
        ).toEqual({
            clients: [],
            loading: false,
            error: 'Hilfe!!',
            clientList: sampleState.clientList
        })
    });
    it('handles loadClientListStarted correctly', () => {
        expect(
            client({
                ...sampleState,
                error: 'blabla',
                loading: false
            }, {
                type: loadClientListStarted.type
            })
        ).toEqual({
            ...sampleState,
            error: undefined,
            loading: true
        })
    });
    it('handles loadClientListSuccess correctly', () => {
        expect(
            client(sampleState, {
                type: loadClientListSuccess.type,
                payload:
                    [
                        {
                            id: 'ff1',
                            name: 'FFFF1'
                        }
                    ]
            })
        ).toEqual({
            error: undefined,
            clients: sampleState.clients,
            clientList: [{
                id: 'ff1',
                name: 'FFFF1'
            }],
            loading: false
        })
    });
    it('handles loadClientListFailed correctly', () => {
        expect(
            client(sampleState, {
                type: loadClientListFailed.type,
                payload: 'Hilfe!!'
            })
        ).toEqual({
            clients: sampleState.clients,
            loading: false,
            error: 'Hilfe!!',
            clientList: []
        })
    });
    it('allows selection of a client', () => {
        expect(
            client(sampleState, {
                type: selectClient.type,
                payload: '111'
            })
        ).toEqual({
            ...sampleState,
            selectedClient: '111'
        })
    })
});
