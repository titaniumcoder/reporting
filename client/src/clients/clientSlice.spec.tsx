import client, {loadClientsFailed, loadClientsSuccess, loadClientsStarted, loadClientListFailed, loadClientListSuccess, loadClientListStarted, selectClient, loadClientinfoStarted, loadClientInfoSuccess, loadClientInfoFailed} from './clientSlice'

const sampleState = {
    clients: [],
    clientList: [],
    error: undefined,
    loading: false,
    selectedClient: undefined,
    clientInfo: []
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
            ...sampleState,
            error: undefined,
            clients: [{
                active: true,
                id: 'ff1',
                maxMinutes: 100,
                name: 'FFFF1',
                notes: 'Notes',
                rateInCentsPerHours: 150
            }]
        })
    });
    it('handles loadClientsFailed correctly', () => {
        expect(
            client(sampleState, {
                type: loadClientsFailed.type,
                payload: 'Hilfe!!'
            })
        ).toEqual({
            ...sampleState,
            clients: [],
            loading: false,
            error: 'Hilfe!!'
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
            ...sampleState,
            error: undefined,
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
            ...sampleState,
            loading: false,
            error: 'Hilfe!!',
            clientList: []
        })
    });
    it('handles loadClientinfoStarted correctly', () => {
        expect(
            client({
                ...sampleState,
                error: 'blabla',
                loading: false
            }, {
                type: loadClientinfoStarted.type
            })
        ).toEqual({
            ...sampleState,
            error: undefined,
            loading: true
        })
    });
    it('handles loadClientInfoSuccess correctly', () => {
        expect(
            client(sampleState, {
                type: loadClientInfoSuccess.type,
                payload:
                    [
                        {
                            id: 'abc',
                            name: 'def',

                        }
                    ]
            })
        ).toEqual({
            ...sampleState,
            clientInfo: [{
                id: 'abc',
                name: 'def',

            }]
        })
    });
    it('handles loadClientInfoFailed correctly', () => {
        expect(
            client(sampleState, {
                type: loadClientInfoFailed.type,
                payload: 'Hilfe!!'
            })
        ).toEqual({
            ...sampleState,
            loading: false,
            error: 'Hilfe!!',
            clientInfo: []
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
