import React from 'react';
import './App.css';
import { Container } from 'reactstrap';
import moment, { Moment } from 'moment';
import Header from './Header';
import Cashout from './Cashout';
import Navigation from './Navigation';
import Projects from './Projects';
import Timesheet from './Timesheet';
import Login from './Login';

const API_BASE_URL = process.env.NODE_ENV === 'production' ? '/api' : 'http://localhost:8080/api';

export interface IAppState {
    basicAuth: string | null;
    from: Moment;
    to: Moment;
    clients: {
        name: string;
        id: number;
    }[];
    projects: {
        name: string;
        minutes: number;
    }[];
    activeClient: number | null;
    cashout: {
        client: string;
        amount: number;
    }[];
    totalCashout: number;
    timesheet: {
        id: number;
        day: string;
        project: string;
        startdate: string;
        enddate: string;
        minutes: number;
        description: string;
        tags: string[];
    }[][];
    regularFetcher: number | null;
    excel: any | null;
}

class App extends React.Component<{}, IAppState> {
    state: Readonly<IAppState> = {
        basicAuth: null,
        clients: [],
        activeClient: null,
        cashout: [],
        totalCashout: 0,
        projects: [],
        timesheet: [],
        regularFetcher: null,
        from: moment().startOf('month'),
        to: moment().endOf('month'),
        excel: null
    };

    createFetchHeaders = (basicAuth = undefined) => {
        const headers = { 'Authorization': basicAuth ? basicAuth : this.state.basicAuth };
        if (process.env.NODE_ENV === 'production') {
            console.log('production with ', headers);
            return { headers } as RequestInit
        } else {
            console.log('development with ', headers);
            return {
                headers,
                mode: 'cors'
            } as RequestInit;
        }
    };

    fetchClients = () => {
        fetch(`${API_BASE_URL}/clients`, this.createFetchHeaders())
            .then(this.checkLogin)
            .then(app => app.json())
            .then(clients => {
                this.setState({
                    clients: clients,
                })
            })
            .catch(this.catchError);
    };

    fetchCash = () => {
        fetch(`${API_BASE_URL}/cash`, this.createFetchHeaders())
            .then(this.checkLogin)
            .then(app => app.json())
            .then(cashout => {
                this.setState({
                    cashout,
                    totalCashout: cashout.map(x => x.amount).reduce((a, v) => a + v)
                })
            })
            .catch(this.catchError);
    };

    untagEntry = (id: number) => {
        fetch({
            method: 'DELETE',
            url: `${API_BASE_URL}/tag/${id}`
        } as RequestInfo, this.createFetchHeaders())
            .then(this.checkLogin)
            .then(() => {
                this.fetchClient();
                this.fetchCash();
            })
            .catch(this.catchError);
    };

    tagEntry = (id: number) => {
        fetch({
            method: 'PUT',
            url: `${API_BASE_URL}/tag/${id}`
        } as RequestInfo, this.createFetchHeaders())
            .then(this.checkLogin)
            .then(() => {
                this.fetchClient();
                this.fetchCash();
            })
            .catch(this.catchError);
    };

    untagClient = () => {
        const id = this.state.activeClient;
        const from = this.state.from.format('YYYY-MM-DD');
        const to = this.state.to.format('YYYY-MM-DD');
        fetch({
            method: 'DELETE',
            url: `${API_BASE_URL}/tag/client/${id}/billed?from=${from}&to=${to}`
        } as RequestInfo, this.createFetchHeaders())
            .then(this.checkLogin)
            .then(() => {
                this.fetchClient();
                this.fetchCash();
            })
            .catch(this.catchError);
    };

    tagClient = () => {
        const id = this.state.activeClient;
        const from = this.state.from.format('YYYY-MM-DD');
        const to = this.state.to.format('YYYY-MM-DD');
        fetch({
            method: 'PUT',
            url: `${API_BASE_URL}/tag/client/${id}/billed?from=${from}&to=${to}`
        } as RequestInfo, this.createFetchHeaders())
            .then(this.checkLogin)
            .then(() => {
                this.fetchClient();
                this.fetchCash();
            })
            .catch(this.catchError);
    };

    fetchClient = (mayBeId = undefined) => {
        const id = mayBeId ? mayBeId : this.state.activeClient;
        const from = this.state.from.format('YYYY-MM-DD');
        const to = this.state.to.format('YYYY-MM-DD');
        fetch(`${API_BASE_URL}/client/${id}?from=${from}&to=${to}`, this.createFetchHeaders())
            .then(this.checkLogin)
            .then(res => res.json())
            .then(client => {
                this.setState({
                    projects: client.projects,
                    timesheet: client.timeEntries
                });
            })
            .catch(this.catchError);
    };

    fetchExcel = () => {
        const id = this.state.activeClient;
        const from = this.state.from.format('YYYY-MM-DD');
        const to = this.state.to.format('YYYY-MM-DD');
        fetch(`${API_BASE_URL}/timesheet/${id}?from=${from}&to=${to}`, this.createFetchHeaders())
            .then(this.checkLogin)
            .then(res => res.blob())
            .then(excel => {
                this.setState({ excel })
            })
            .catch(this.catchError);
    };

    startFetching = () => {
        if (this.state.regularFetcher !== null && this.state.basicAuth === null) {
            this.stopFetching();
        } else if (this.state.regularFetcher === null && this.state.basicAuth !== null) {
            const fetchAction = () => {
                this.fetchClients();
                this.fetchCash();
                if (this.state.activeClient !== null) {
                    this.fetchClient();
                }
            };
            const regularFetcher = setInterval(fetchAction, 10000);
            // @ts-ignore
            this.setState({ regularFetcher });
            fetchAction();
        }
    };

    componentDidUpdate(prevProps: Readonly<{}>, prevState: Readonly<IAppState>, snapshot?: any): void {
        if (prevState.basicAuth !== this.state.basicAuth) {
            if (this.state.basicAuth !== null) {
                this.startFetching();
            } else {
                this.stopFetching();
            }
        }
    }

    stopFetching = () => {
        if (this.state.regularFetcher !== null) {
            clearInterval(this.state.regularFetcher);
        }
        this.setState({ regularFetcher: null });
    };

    componentWillUnmount(): void {
        this.stopFetching();
    }

    componentDidMount(): void {
        if (localStorage && localStorage.getItem('auth')) {
            this.setState({
                basicAuth: localStorage.getItem('auth')
            });
        }
    }

    checkLogin = (res) => {
        if (res.status === 401 || res.status === 403) {
            localStorage.removeItem('auth');
            this.setState({ basicAuth: null });
            return new Error('authentication failed');
        } else {
            return res;
        }
    };

    catchError = (res) => {
        localStorage.removeItem('auth');
        this.setState({ basicAuth: null });
        return res;
    };

    login = (username, password) => {
        const basicAuth = 'Basic ' + btoa(`${username}:${password}`);
        this.setState({
            basicAuth
        });
        localStorage.setItem('auth', basicAuth);
    };

    selectClient = (id) => {
        this.setState({activeClient: id});
        this.fetchClient(id);
    };

    render() {
        const totalCashout = this.state.totalCashout;
        const clients = this.state.clients;
        const projects = this.state.projects;
        const activeClient = this.state.activeClient;
        const cashout = this.state.cashout;

        const timesheet = this.state.timesheet;

        return (
            <div id="app">
                <Login showModal={!this.state.basicAuth} executeLogin={this.login}/>
                <Container fluid={true}>
                    <Header dateFrom={this.state.from} excel={this.state.excel} dateTo={this.state.to} loadFromTo={(from, to) => {
                        this.setState({ from, to });
                        setTimeout(() => {this.fetchClient()}, 100);
                    }} createExcel={this.fetchExcel} setBilled={this.tagClient}
                            setUnbilled={this.untagClient}/>
                    <Cashout cashout={cashout} totalCashout={totalCashout}/>
                    <hr/>
                    <Navigation clients={clients} activeClient={activeClient} selectClient={this.selectClient}/>
                    <Projects projects={projects}/>

                    <Timesheet timesheet={timesheet} tagBilled={(id) => this.tagEntry(id)} tagUnbilled={(id) => this.untagEntry(id)}/>
                </Container>
            </div>
        );
    }
}

export default App;
