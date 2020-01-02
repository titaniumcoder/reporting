import axios, {AxiosResponse} from 'axios';

export interface User {
    email: string;
    admin: boolean;
    canViewMoney: boolean;
    canBook: boolean;
    clients: { id: string; name: string; }[];
}

export interface Project {
}

export interface Client {
    id: string;
    active: boolean;
    name: string;
    notes?: string;
    maxMinutes?: number;
    rateInCentsPerHours?: number;
}


export interface IReportingApi {
    auth(idToken: string): Promise<AxiosResponse<ICurrentUser>>;

    logout(): void;

    fetchUsers(): Promise<AxiosResponse<User[]>>
    saveUser(user: User): Promise<boolean>
    deleteUser(user: User): Promise<boolean>

    fetchClients(): Promise<AxiosResponse<Client[]>>
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

    async saveUser(user: User) {
        const result = await axios.post<User>('users', user);
        return result.status === 200 || result.status === 201;
    }

    async deleteUser(user: User) {
        const result = await axios.delete<void>('users/' + user.email);
        return result.status >= 200 && result.status < 400;
    }

    async fetchClients() {
        return axios.get<Client[]>('clients');
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
