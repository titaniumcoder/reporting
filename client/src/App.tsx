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
import { ICashout, IClient, IProject, ITimeEntry } from './model';
import { ITogglReportingApi, TogglReportingApi } from './api';

export interface IAppState {
    username: string | null;
    password: string | null;
    from: Moment;
    to: Moment;
    clients: IClient[];
    projects: IProject[];
    activeClient: number | null;
    cashout: ICashout[];
    totalCashout: number;
    timesheet: ITimeEntry[][];
    regularFetcher: number | null;
    excel: any | null;
    loggedIn: boolean;
}

class App extends React.Component<{}, IAppState> {
    api: ITogglReportingApi = new TogglReportingApi();

    state: Readonly<IAppState> = {
        username: null,
        password: null,
        clients: [],
        activeClient: null,
        cashout: [],
        totalCashout: 0,
        projects: [],
        timesheet: [],
        regularFetcher: null,
        from: moment().startOf('month'),
        to: moment().endOf('month'),
        excel: null,
        loggedIn: false
    };

    errorHandler = (err) => {
        console.log('Got fetch error', err);
        this.setState({ loggedIn: false });
        return err;
    };

    loadData = async (withClient) => {
        const clients = await this.api.fetchClients().catch(this.errorHandler);
        const cashout = await this.api.fetchCash().catch(this.errorHandler);
        this.setState({ clients: clients.data, cashout: cashout.data });
        if (withClient && this.state.activeClient !== null) {
            const client = await this.api.fetchClient(this.state.activeClient, this.state.from, this.state.to).catch(this.errorHandler);
            this.setState({
                projects: client.data.projects,
                timesheet: client.data.timeEntries
            })

        }
    };

    startFetching = async () => {
        if (this.state.regularFetcher !== null && this.state.username === null) {
            this.stopFetching();
        } else if (this.state.regularFetcher === null && this.state.username !== null) {
            const regularFetcher = setInterval(this.loadData, 10000);
            // @ts-ignore
            this.setState({ regularFetcher });
            await this.loadData(false);
        }
    };

    componentDidUpdate(prevProps: Readonly<{}>, prevState: Readonly<IAppState>, snapshot?: any): void {
        if (prevState.username !== this.state.username || prevState.password !== this.state.password) {
            if (this.state.username !== null) {
                this.startFetching()
                    .then(() => {
                        // ignore result but probably there are better ways...
                    })
                ;
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
        if (localStorage) {
            const username = localStorage.getItem('username');
            const password = localStorage.getItem('password');
            if (username && password) {
                this.setState({ username, password, loggedIn: true });
                this.api.saveLogin(username, password);
            }
        }
    }

    login = (username, password) => {
        localStorage.setItem('username', username);
        localStorage.setItem('password', password);

        this.api.saveLogin(username, password);

        this.setState({ username, password, loggedIn: true });
    };

    fetchClient = async () => {
        if (this.state.activeClient) {
            const data = await this.api.fetchClient(this.state.activeClient, this.state.from, this.state.to).catch(this.errorHandler);
            this.setState({
                projects: data.data.projects,
                timesheet: data.data.timeEntries
            });
        }
    };

    selectClient = async (id) => {
        this.setState({ activeClient: id });
        setTimeout(async () => await this.fetchClient().catch(this.errorHandler), 100);
    };

    loadFromTo = (from: Moment, to: Moment) => {
        this.setState({ from, to });
        setTimeout(async () => await this.fetchClient().catch(this.errorHandler), 100);
    };

    loadExcel = async () => {
        if (this.state.activeClient) {
            const excel = await this.api.fetchExcel(this.state.activeClient, this.state.from, this.state.to).catch(this.errorHandler);
            this.setState({ excel })
        }
    };

    tagClient = async () => {
        if (this.state.activeClient) {
            await this.api.tagClient(this.state.activeClient, this.state.from, this.state.to).catch(this.errorHandler);
            await this.loadData(true);
        }
    };

    untagClient = async () => {
        if (this.state.activeClient) {
            await this.api.untagClient(this.state.activeClient, this.state.from, this.state.to).catch(this.errorHandler);
            await this.loadData(true);
        }
    };

    tagEntry = async (id: number) => {
        await this.api.tagEntry(id).catch(this.errorHandler);
        await this.loadData(true);
    };

    untagEntry = async (id: number) => {
        await this.api.untagEntry(id).catch(this.errorHandler);
        await this.loadData(true);
    };

    logout = () => {
        this.stopFetching();

        localStorage.removeItem("username");
        localStorage.removeItem("password");

        this.api.logout();

        this.setState({ loggedIn: false, username: null, password: null })
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
                <Login showModal={!this.state.username || !this.state.password} executeLogin={this.login}/>
                <Container fluid={true}>
                    <Header dateFrom={this.state.from}
                            excel={this.state.excel}
                            dateTo={this.state.to}
                            loadFromTo={this.loadFromTo}
                            createExcel={this.loadExcel}
                            setBilled={this.tagClient}
                            setUnbilled={this.untagClient}
                            logout={this.logout}/>
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
