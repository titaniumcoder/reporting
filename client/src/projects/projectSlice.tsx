import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import reportingApi, {Project, ProjectList} from "../api/reportingApi";
import {AppThunk} from "../store";

type ProjectState = {
    loading: boolean;
    error?: string;
    projects: Project[];
    projectList: ProjectList[];
}

let initialState: ProjectState = {
    projects: [],
    loading: false,
    error: undefined,
    projectList: []
};

const projectSlice = createSlice({
    name: 'project',
    initialState,
    reducers: {
        loadProjectsSuccess(state, action: PayloadAction<Project[]>) {
            state.loading = false;
            state.error = undefined;
            state.projects = action.payload;
        },
        loadProjectsFailed(state, action: PayloadAction<string>) {
            state.loading = false;
            state.projects = [];
            state.error = action.payload;
        },
        loadProjectsStarted(state) {
            state.loading = true;
            state.error = undefined;
        },
        loadProjectListSuccess(state, action: PayloadAction<ProjectList[]>) {
            state.loading = false;
            state.error = undefined;
            state.projectList = action.payload;
        },
        loadProjectListFailed(state, action: PayloadAction<string>) {
            state.loading = false;
            state.projectList = [];
            state.error = action.payload;
        },
        loadProjectListStarted(state) {
            state.loading = true;
            state.error = undefined;
        }
    }
});

export const {
    loadProjectListStarted,
    loadProjectListSuccess,
    loadProjectListFailed,
    loadProjectsStarted,
    loadProjectsSuccess,
    loadProjectsFailed
} = projectSlice.actions;

export default projectSlice.reducer;

export const fetchProjectList = (): AppThunk => async dispatch => {
    try {
        dispatch(loadProjectListStarted());
        const projects = await reportingApi.fetchProjectList();
        dispatch(loadProjectListSuccess(projects.data));
    } catch (err) {
        dispatch(loadProjectListFailed(err.toString()));
    }
};

export const fetchProjects = (): AppThunk => async dispatch => {
    try {
        dispatch(loadProjectsStarted());
        const projects = await reportingApi.fetchProjects();
        dispatch(loadProjectsSuccess(projects.data));
    } catch (err) {
        dispatch(loadProjectsFailed(err.toString()));
    }
};
