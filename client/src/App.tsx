import React from 'react';
import {Container} from 'reactstrap';
import moment, {Moment} from 'moment';
import Header from './Header';
import Cashout from './Cashout';
import Navigation from './Navigation';
import Projects from './Projects';
import Timesheet from './Timesheet';
import Login from './Login';
import {ITogglReportingApi, TogglReportingApi} from './api';
import {saveAs} from 'file-saver';

import './App.css';
import {ICashoutInfo, IClient, IProject, ITimeEntry} from "./models/models";

export interface IAppState {
    username: string | null;
    password: string | null;
    from: Moment;
    to: Moment;
    clients: IClient[];
    projects: IProject[];
    activeClient: number | null;
    cashout: ICashoutInfo;
    timesheet: ITimeEntry[][];
    regularFetcher: number | null;
    loggedIn: boolean;
}

class App extends React.Component<{}, IAppState> {
    api: ITogglReportingApi = new TogglReportingApi();

    state: Readonly<IAppState> = {
        username: null,
        password: null,
        clients: [],
        activeClient: null,
        cashout: {cashouts: [], clientLimits: [], projectLimits: [], totalCashout: 0},
        projects: [],
        timesheet: [],
        regularFetcher: null,
        from: moment().startOf('month'),
        to: moment().endOf('month'),
        loggedIn: false
    };

    errorHandler = (err) => {
        this.setState({loggedIn: false});
        return err;
    };

    loadData = async (withClient) => {
        const clients = await this.api.fetchClients().catch(this.errorHandler);
        const cashout = await this.api.fetchCash().catch(this.errorHandler);
        this.setState({clients: clients.data, cashout: cashout.data});
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
            this.setState({regularFetcher});
            await this.loadData(false);
        }
    };

    componentDidUpdate(prevProps: Readonly<{}>, prevState: Readonly<IAppState>, snapshot?: any): void {
        if (prevState.loggedIn !== this.state.loggedIn) {
            if (this.state.loggedIn) {
                this.startFetching()
                    .then(() => {
                        // ignore result but probably there are better ways...
                    });
            } else {
                this.stopFetching();
            }
        }
    }

    stopFetching = () => {
        if (this.state.regularFetcher !== null) {
            clearInterval(this.state.regularFetcher);
        }
        this.setState({regularFetcher: null});
    };

    componentWillUnmount(): void {
        this.stopFetching();
    }

    componentDidMount(): void {
        if (localStorage) {
            const username = localStorage.getItem('username');
            const password = localStorage.getItem('password');

            this.setState({username, password});

            if (username && password) {
                this.api.login(username, password)
                    .then(loggedIn => {
                        this.setState({loggedIn})
                    });
            }
        }
    }

    login = async (username, password) => {
        localStorage.setItem('username', username);
        localStorage.setItem('password', password);

        const loggedIn = await this.api.login(username, password);

        this.setState({username, password, loggedIn});
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
        this.setState({activeClient: id});
        setTimeout(async () => await this.fetchClient().catch(this.errorHandler), 100);
    };

    loadFromTo = (from: Moment, to: Moment) => {
        this.setState({from, to});
        setTimeout(async () => await this.loadData(true), 100);
    };

    loadExcel = async () => {
        if (this.state.activeClient) {
            const excel = await this.api.fetchExcel(this.state.activeClient, this.state.from, this.state.to).catch(this.errorHandler);
            const disposition: string = excel.headers["content-disposition"];
            const filesep = 'filename="';
            const filesepLength = filesep.length;

            const filename = disposition.split(';')
                .map((x) => x.trim())
                .filter((x) => x.startsWith(filesep))
                .map((x) => x.substr(filesepLength, x.length - filesepLength - 1))[0];

            saveAs(excel.data, filename);
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

        this.setState({loggedIn: false, username: null, password: null})
    };

    render() {
        const clients = this.state.clients;
        const projects = this.state.projects;
        const activeClient = this.state.activeClient;
        const cashout = this.state.cashout;

        const timesheet = this.state.timesheet;

        const showModal = !this.state.username || !this.state.password;

        const internal = showModal ?
            <Login executeLogin={this.login}/> :
            <Container fluid={true}>
                <Header enabled={!!this.state.activeClient}
                        dateFrom={this.state.from}
                        dateTo={this.state.to}
                        loadFromTo={this.loadFromTo}
                        createExcel={this.loadExcel}
                        setBilled={this.tagClient}
                        setUnbilled={this.untagClient}
                        logout={this.logout}/>
                <Cashout cashout={cashout}/>
                <hr/>
                <Navigation clients={clients} activeClient={activeClient} selectClient={this.selectClient}/>

                <Projects projects={projects}/>

                <Timesheet timesheet={timesheet} tagBilled={(id) => this.tagEntry(id)}
                           tagUnbilled={(id) => this.untagEntry(id)}/>
            </Container>;

        return (
            <div id="app">
                {internal}
            </div>
        );
    }
}

export default App;
