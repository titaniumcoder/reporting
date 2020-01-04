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
}

export interface Client {
    id: string;
    active: boolean;
    name: string;
    notes?: string;
    maxMinutes?: number;
    rateInCentsPerHour?: number;
}


export interface IReportingApi {
    auth(idToken: string): Promise<AxiosResponse<ICurrentUser>>;

    logout(): void;

    fetchUsers(): Promise<AxiosResponse<User[]>>
    saveUser(user: UpdatingUser): Promise<AxiosResponse<void>>
    deleteUser(email: string): Promise<AxiosResponse<void>>

    fetchClients(): Promise<AxiosResponse<Client[]>>
    saveClient(client: Client): Promise<AxiosResponse<void>>
    deleteClient(client: Client): Promise<AxiosResponse<void>>
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

    async saveClient(client: Client) {
        return await axios.post<void>('clients', client);
    }

    async deleteClient(client: Client) {
        return await axios.delete<void>('users/' + client.id);
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
