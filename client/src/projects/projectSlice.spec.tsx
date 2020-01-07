import project, {loadProjectListStarted, loadProjectListSuccess, loadProjectListFailed, loadProjectsStarted, loadProjectsSuccess, loadProjectsFailed} from './projectSlice'
import {Project, ProjectList} from "../api/reportingApi";

const sampleState: {projects: Project[], projectList: ProjectList[], error: string | undefined, loading: boolean} = {
    projects: [
        {
            id: 1,
            name: 'P1',
            clientId: 'C2',
            clientName: 'CN2',
            billable: true,
            maxMinutes: 100,
            rateInCentsPerHour: 10
        }
    ],
    projectList: [
        {
            id: 1,
            name: 'P1',
            clientName: 'CN2'
        }
    ],
    error: undefined,
    loading: false
};

describe('projectSlice', () => {
    it('handles loadProjectsStarted correctly', () => {
        expect(
            project({
                ...sampleState,
                error: 'blabla',
                loading: false
            }, {
                type: loadProjectsStarted().type
            })
        ).toEqual({
            ...sampleState,
            error: undefined,
            loading: true
        })
    });
    it('handles loadProjectsSuccess correctly', () => {
        expect(
            project(sampleState, {
                type: loadProjectsSuccess.type,
                payload:
                    [
                        {
                            id: 1,
                            name: 'P1',
                            clientId: 'C2',
                            clientName: 'CN2',
                            billable: true,
                            maxMinutes: 100,
                            rateInCentsPerHours: 10
                        }
                    ]
            })
        ).toEqual({
            error: undefined,
            projects: [{
                id: 1,
                name: 'P1',
                clientId: 'C2',
                clientName: 'CN2',
                billable: true,
                maxMinutes: 100,
                rateInCentsPerHours: 10
            }],
            projectList: sampleState.projectList,
            loading: false
        })
    });
    it('handles loadProjectsFailed correctly', () => {
        expect(
            project(sampleState, {
                type: loadProjectsFailed.type,
                payload: 'Hilfe!!'
            })
        ).toEqual({
            projects: [],
            loading: false,
            error: 'Hilfe!!',
            projectList: sampleState.projectList
        })
    });
    it('handles loadProjectListStarted correctly', () => {
        expect(
            project({
                ...sampleState,
                error: 'blabla',
                loading: false
            }, {
                type: loadProjectListStarted().type
            })
        ).toEqual({
            ...sampleState,
            error: undefined,
            loading: true
        })
    });
    it('handles loadProjectListSuccess correctly', () => {
        expect(
            project(sampleState, {
                type: loadProjectListSuccess.type,
                payload:
                    [
                        {
                            id: 'ff1',
                            name: 'FFFF1',
                            clientName: 'abc'
                        }
                    ]
            })
        ).toEqual({
            error: undefined,
            projects: sampleState.projects,
            projectList: [{
                id: 'ff1',
                name: 'FFFF1',
                clientName: 'abc'
            }],
            loading: false
        })
    });
    it('handles loadProjectListFailed correctly', () => {
        expect(
            project(sampleState, {
                type: loadProjectListFailed.type,
                payload: 'Hilfe!!'
            })
        ).toEqual({
            projects: sampleState.projects,
            loading: false,
            error: 'Hilfe!!',
            projectList: []
        })
    });
});
