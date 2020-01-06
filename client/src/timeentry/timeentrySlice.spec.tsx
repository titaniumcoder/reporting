import timeentry, {
    currentTimeEntryFailed,
    currentTimeEntrySuccess,
    loadTimeEntriesStarted,
    loadTimeEntriesSuccess,
    loadTimeEntriesFailed,
    selectTimeRange
} from './timeentrySlice'

const sampleState = {
    currentTimeEntry: undefined,
    error: undefined,
    timeentries: [],
    loading: false,
    from: undefined,
    to: undefined,
    clientId: undefined
};

describe('timeentrySlice', () => {
    it('handles currentTimeEntrySuccess correctly', () => {
        expect(
            timeentry(sampleState, {
                type: currentTimeEntrySuccess.type,
                payload:
                    {
                        amount: 1000,
                        billed: false,
                        date: '2019-11-12',
                        description: 'ich',
                        ending: undefined,
                        projectId: 101,
                        projectName: 'P101',
                        starting: '2019-11-12T14:00:00',
                        timeUsed: '33:22',
                        username: 'ABC'
                    }
            })
        ).toEqual({
            ...sampleState,
            error: undefined,
            currentTimeEntry: {
                amount: 1000,
                billed: false,
                date: '2019-11-12',
                description: 'ich',
                ending: undefined,
                projectId: 101,
                projectName: 'P101',
                starting: '2019-11-12T14:00:00',
                timeUsed: '33:22',
                username: 'ABC'
            },
        })
    });
    it('handles currentTimeEntryFailed correctly', () => {
        expect(
            timeentry(sampleState, {
                type: currentTimeEntryFailed.type,
                payload: 'Hilfe!!'
            })
        ).toEqual({
            ...sampleState,
            currentTimeEntry: undefined,
            error: 'Hilfe!!'
        })
    });
    it('handles loadTimeEntriesStarted correctly', () => {
        expect(
            timeentry(sampleState, {
                type: loadTimeEntriesStarted.type
            })
        ).toEqual({
            ...sampleState,
            timeentries: [],
            loading: true
        })
    });
    it('handles loadTimeEntriesSuccess correctly', () => {
        expect(
            timeentry(sampleState, {
                type: loadTimeEntriesSuccess.type,
                payload: [{a: 'b'}]
            })
        ).toEqual({
            ...sampleState,
            error: undefined,
            timeentries: [{
                a: 'b'
            }]
        })
    });
    it('handles loadTimeEntriesFailed correctly', () => {
        expect(
            timeentry(sampleState, {
                type: loadTimeEntriesFailed.type,
                payload: 'Hilfe!!'
            })
        ).toEqual({
            ...sampleState,
            currentTimeEntry: undefined,
            error: 'Hilfe!!'
        })
    });
    it('handles loadTimeEntriesFailed correctly', () => {
        expect(
            timeentry(sampleState, {
                type: selectTimeRange.type,
                payload: {
                    from: '2010-01-01',
                    to: '2019-01-01',
                    clientId: 'test'
                }
            })
        ).toEqual({
            ...sampleState,
            from: '2010-01-01',
            to: '2019-01-01',
            clientId: 'test'
        })
    });
});
