import axios, { AxiosResponse } from 'axios';
import { ICashout, IClient, IProject, ITimeEntry } from './model';
import { Moment } from 'moment';

export interface ITogglClientResponse {
    projects: IProject[];
    timeEntries: ITimeEntry[][];
}

export interface ITogglReportingApi {
    login(username: string, password: string): Promise<boolean>;

    fetchClients(): Promise<AxiosResponse<IClient[]>>;

    fetchCash(): Promise<AxiosResponse<ICashout[]>>;

    fetchClient(id: number, from: Moment, to: Moment): Promise<AxiosResponse<ITogglClientResponse>>;

    fetchExcel(id: number, from: Moment, to: Moment): Promise<AxiosResponse<any>>;

    tagEntry(id: number): Promise<AxiosResponse<void>>;

    untagEntry(id: number): Promise<AxiosResponse<void>>;

    tagClient(id: number, from: Moment, to: Moment): Promise<AxiosResponse<void>>;

    untagClient(id: number, from: Moment, to: Moment): Promise<AxiosResponse<void>>;

    logout(): void;
}

export class TogglReportingApi implements ITogglReportingApi {
    constructor() {
        axios.defaults.baseURL = '/api';
    }

    async login(username: string, password: string) {
        delete axios.defaults.headers['Authorization'];

        const login = await axios.post('login', { username: username, password: password });
        console.log('Login: ', login);
        if (login.status === 401) {
            // do nothing
            return false;
        } else {
            axios.defaults.headers['Authorization'] = 'Bearer ' + login.data.token;
            return true;
        }
    }

    logout() {
        delete axios.defaults.headers['Authorization'];
    }

    async fetchClients() {
        return await axios.get<IClient[]>('clients');
    }

    async fetchCash() {
        return await axios.get<ICashout[]>('cash');
    }

    async tagEntry(id: number) {
        return await axios.put<void>(`tag/${id}`);
    };

    async untagEntry(id: number) {
        return await axios.delete<void>(`tag/${id}`);
    };

    async untagClient(id: number, fromM: Moment, toM: Moment) {
        const from = fromM.format('YYYY-MM-DD');
        const to = toM.format('YYYY-MM-DD');
        return await axios.delete<void>(`tag/client/${id}/billed`, {
            params: {
                from, to
            }
        });
    };

    async tagClient(id: number, fromM: Moment, toM: Moment) {
        const from = fromM.format('YYYY-MM-DD');
        const to = toM.format('YYYY-MM-DD');
        return await axios.put<void>(`tag/client/${id}/billed`, {
            params: {
                from, to
            }
        });
    };

    async fetchClient(id: number, fromM: Moment, toM: Moment) {
        const from = fromM.format('YYYY-MM-DD');
        const to = toM.format('YYYY-MM-DD');
        return axios.get<ITogglClientResponse>(`client/${id}`, { params: { from, to } });
    };

    async fetchExcel(id: number, fromM: Moment, toM: Moment) {
        const from = fromM.format('YYYY-MM-DD');
        const to = toM.format('YYYY-MM-DD');
        return axios.get<any>(`timesheet/${id}`, { params: { from, to }, maxContentLength: 10000000 });
    };
}

