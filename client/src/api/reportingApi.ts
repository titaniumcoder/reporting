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
    billable: boolean;
}

export interface Client {
    id: string;
    active: boolean;
    name: string;
    notes?: string;
    maxMinutes?: number;
    rateInCentsPerHour?: number;
    project?: Project[];
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

    billed: boolean;
}

export interface ClientInfo {
    id: string;
    name: string;
    rateInCentsPerHour?: number;
    maxMinutes?: number;
    billedMinutes: number;
    billedAmount?: number;
    openMinutes: number;
    openAmount?: number;
    remainingMinutes?: number;
    remainingAmount?: number;

    projects: ProjectInfo[];
}

export interface ProjectInfo {
    projectId?: number;
    name: string;
    billable: boolean;
    rateInCentsPerHour?: number;
    maxMinutes?: number;
    billedMinutes: number;
    billedAmount?: number;
    openMinutes: number;
    openAmount?: number;
    remainingMinutes?: number;
    remainingAmount?: number;
}

export interface IReportingApi {
    auth(idToken: string): Promise<AxiosResponse<ICurrentUser>>;

    logout(): void;

    fetchClients(): Promise<AxiosResponse<Client[]>>

    fetchClientList(): Promise<AxiosResponse<ClientList[]>>

    saveClient(client: Client): Promise<AxiosResponse<void>>

    deleteClient(id: string): Promise<AxiosResponse<void>>

    startTimeEntry(ref: number | undefined): Promise<AxiosResponse<TimeEntry>>

    stopTimeEntry(id: number): Promise<AxiosResponse<TimeEntry>>

    updateTimeEntry(te: UpdatingTimeEntry): Promise<AxiosResponse<TimeEntry>>

    deleteTimeEntry(id: number): Promise<AxiosResponse<void>>

    loadTimeEntries(from?: number, to?: number, clientId?: number, allEntries?: boolean): Promise<AxiosResponse<TimeEntry[]>>

    loadClientinfo(clientId?: number | undefined): Promise<AxiosResponse<ClientInfo[]>>;

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

    async loadTimeEntries(from?: number, to?: number, clientId?: number, allEntries?: boolean) {
        return await axios.get<TimeEntry[]>('timeentries', {
            params: {
                from, to, clientId, allEntries
            }
        });
    }

    async togglTimeEntries(ids: number[]) {
        return await axios.post<void>('toggl-timeentries', ids);
    }

    async loadClientinfo(clientId?: number | undefined) {
        return await axios.get<ClientInfo[]>('client-info', {
            params: {
                clientId
            }
        });
    }

    async timesheet(id: string, billableOnly: boolean) {
        return await axios.get<Blob>(`timesheet/${id}`, {
            params: {billableOnly},
            maxContentLength: 10000000,
            responseType: 'blob'
        });
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
