import timeentry, {currentTimeEntryFailed, currentTimeEntryStarted, currentTimeEntrySuccess} from './timeentrySlice'
import {Project, ProjectList, TimeEntry} from "../api/reportingApi";

const sampleState = {
    currentTimeEntry: undefined,
    error: undefined,
    loading: false
};

describe('timeentrySlice', () => {
    it('handles currentTimeEntryStarted correctly', () => {
        expect(
            timeentry({
                ...sampleState,
                error: 'blabla',
                loading: false
            }, {
                type: currentTimeEntryStarted.type
            })
        ).toEqual({
            error: undefined,
            loading: true,
            currentTimeEntry: undefined
        })
    });
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
            loading: false
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
            loading: false,
            error: 'Hilfe!!'
        })
    });
});
