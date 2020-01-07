import axios, {AxiosResponse} from 'axios';

export interface User {
    email: string;
    admin: boolean;
    canViewMoney: boolean;
    canBook: boolean;
    clients: { id: string; name: string; }[];
}

export interface UpdatingUser {
    email: string;
    admin: boolean;
    canBook: boolean;
    canViewMoney: boolean;
    clients: string[]
}

export interface Project {
    id?: number;
    clientId: string;
    clientName: string;
    name: string;
    maxMinutes?: number;
    rateInCentsPerHour?: number;
    billable: boolean;
}

export interface UpdatingProject {
    id?: number;
    clientId: string;
    name: string;
    billable: boolean;
    maxHours: string;
    rate: number;
}

export interface ProjectList {
    id?: number;
    clientName: string;
    name: string;
}

export interface Client {
    clientId: string;
    active: boolean;
    name: string;
    notes?: string;
    maxMinutes?: number;
    rateInCentsPerHour?: number;
}

export interface ClientList {
    clientId: string;
    name: string;
}

export interface UpdatingClient {
    clientId: string;
    active: boolean;
    name: string;
    notes: string;
    maxHours: string;
    rate: number;
}

export interface TimeEntry {
    id?: number;
    date: string;
    starting: string;
    ending?: string;

    projectId?: number;
    projectName?: string;

    description?: string;

    username: string;

    billable: boolean;
    billed: boolean;

    timeUsed: number;
    amount: number;
}

export interface UpdatingTimeEntry {
    id: number;
    starting: string;
    ending?: string;

    projectId?: number;

    description?: string;

    username: string;

    billable: boolean;
    billed: boolean;
}

export interface IReportingApi {
    auth(idToken: string): Promise<AxiosResponse<ICurrentUser>>;

    logout(): void;

    fetchUsers(): Promise<AxiosResponse<User[]>>

    saveUser(user: UpdatingUser): Promise<AxiosResponse<void>>

    deleteUser(email: string): Promise<AxiosResponse<void>>

    fetchClients(): Promise<AxiosResponse<Client[]>>

    fetchClientList(): Promise<AxiosResponse<ClientList[]>>

    saveClient(client: Client): Promise<AxiosResponse<void>>

    deleteClient(id: string): Promise<AxiosResponse<void>>

    fetchProjects(): Promise<AxiosResponse<Project[]>>

    fetchProjectList(): Promise<AxiosResponse<ProjectList[]>>

    saveProject(client: Project): Promise<AxiosResponse<void>>

    deleteProject(id: number): Promise<AxiosResponse<void>>

    startTimeEntry(ref: number | undefined): Promise<AxiosResponse<TimeEntry>>

    stopTimeEntry(id: number): Promise<AxiosResponse<TimeEntry>>

    updateTimeEntry(te: UpdatingTimeEntry): Promise<AxiosResponse<TimeEntry>>

    deleteTimeEntry(id: number): Promise<AxiosResponse<void>>

    fetchTimeEntries(from?: number, to?: number, clientId?: number, allEntries?: boolean): Promise<AxiosResponse<TimeEntry[]>>

    togglTimeEntries(ids: number[]): Promise<AxiosResponse<void>>
}

export interface ICurrentUser {
    admin: boolean;
    canBook: boolean;
    canViewMoney: boolean;
}

export class ReportingApi implements IReportingApi {
    constructor() {
        axios.defaults.baseURL = '/api';
        delete axios.defaults.headers['Authorization'];
    }

    // Authorization Stuff
    async auth(idToken: string) {
        axios.defaults.headers['Authorization'] = `Bearer ${idToken}`;

        return await axios.get<ICurrentUser>('current-user');
    }

    logout(): void {
        delete axios.defaults.headers['Authorization'];
    }

    async fetchUsers() {
        return axios.get<User[]>('users');
    }

    async saveUser(user: UpdatingUser) {
        return await axios.post<void>('users', user);
    }

    async deleteUser(email: string) {
        return await axios.delete<void>('users/' + email);
    }

    async fetchClients() {
        return axios.get<Client[]>('clients');
    }

    async fetchClientList() {
        return axios.get<ClientList[]>('client-list');
    }

    async saveClient(client: Client) {
        return await axios.post<void>('clients', client);
    }

    async deleteClient(id: string) {
        return await axios.delete<void>('clients/' + id);
    }

    async fetchProjects() {
        return axios.get<Project[]>('projects');
    }

    async fetchProjectList() {
        return axios.get<ProjectList[]>('project-list');
    }

    async saveProject(project: Project) {
        return await axios.post<void>('projects', project);
    }

    async deleteProject(id: number) {
        return await axios.delete<void>('projects/' + id);
    }

    async startTimeEntry(ref: number | undefined) {
        return await axios.post<TimeEntry>('start-timeentry', {}, {
            params: {ref}
        });
    }

    async stopTimeEntry(id: number) {
        return await axios.delete<TimeEntry>('current-timeentry/' + id);
    }

    async updateTimeEntry(te: UpdatingTimeEntry) {
        return await axios.post<TimeEntry>('timeentries', te);
    }

    async deleteTimeEntry(id: number) {
        return await axios.delete<void>(`timeentries/${id}`);
    }

    async fetchTimeEntries(from?: number, to?: number, clientId?: number, allEntries?: boolean) {
        return await axios.get<TimeEntry[]>('timeentries', {
            params: {
                from, to, clientId, allEntries
            }
        });
    }

    async togglTimeEntries(ids: number[]) {
        return await axios.post<void>('toggl-timeentries', ids);
    }
}

export default new ReportingApi();
/*
async fetchExcel(id: number, fromM: Moment, toM: Moment) {
    const from = fromM.format('YYYY-MM-DD');
    const to = toM.format('YYYY-MM-DD');
    return axios.get<any>(`timesheet/${id}`, {
        params: { from, to },
        maxContentLength: 10000000,
        responseType: 'blob',
    });
};
 */
