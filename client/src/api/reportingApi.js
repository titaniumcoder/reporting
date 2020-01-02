import axios from 'axios';

/*
export
interface
ITogglReportingApi
{
    auth(idToken
:
    string
):
    Promise < boolean >;

    fetchClients()
:
    Promise < AxiosResponse < IClient[] >>;

    fetchCash(): Promise<AxiosResponse<IHeaderInfo>>;

    fetchClient(id: number, from: Moment, to: Moment): Promise<AxiosResponse<ITogglClientResponse>>;

    fetchExcel(id: number, from: Moment, to: Moment): Promise<AxiosResponse<any>>;

    tagEntry(id: number): Promise<AxiosResponse<void>>;

    untagEntry(id: number): Promise<AxiosResponse<void>>;

    tagClient(id: number, from: Moment, to: Moment): Promise<AxiosResponse<void>>;

    untagClient(id: number, from: Moment, to: Moment): Promise<AxiosResponse<void>>;
    logout(): void;
}
*/

/* this method will mostly be called as first and set the defaults*/
export const auth = async (idToken) => {
    axios.defaults.baseURL = '/api';
    axios.defaults.headers['Authorization'] = `Bearer ${idToken}`;

    return await axios.get('current-user');
};

export const logout = () => {
    delete axios.defaults.headers['Authorization'];
};
