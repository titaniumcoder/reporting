import timeentry, {currentTimeEntryFailed, currentTimeEntrySuccess} from './timeentrySlice'

const sampleState = {
    currentTimeEntry: undefined,
    error: undefined,
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
            currentTimeEntry: undefined,
            error: 'Hilfe!!'
        })
    });
});
