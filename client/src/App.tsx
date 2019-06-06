import React from 'react';
import './App.css';
import { Container } from 'reactstrap';
import moment from 'moment';

const formatMinutes = (minutes) => {
    const hours = Math.floor(minutes / 60);
    const remaining = minutes % 60;
    return hours + ':' + remaining.toLocaleString('de-CH', {minimumIntegerDigits: 2, maximumFractionDigits: 0})
};

const formatDecimal = (minutes) => {
    return (minutes / 60.0).toLocaleString('de-CH', {
        maximumFractionDigits: 2,
        minimumFractionDigits: 2
    })
};

const formatDate = (d) => {
    return moment(d).format('DD.MM.YYYY');
};

const formatTime = (d)  => {
    return moment(d).format('HH:mm');
};

const formatDayMinutes = (day) => {
    const sum = day.map(x => x.minutes).reduce((acc, c) => acc + c);
    return formatMinutes(sum)
};

const formatDayDecimal = (day) => {
    const sum = day.map(x => x.minutes).reduce((acc, c) => acc + c);
    return formatDecimal(sum)
};

const App: React.FC = () => {
    const client = { client: 'Fred', amount: 1000 };
    const totalCashout = 1000;
    const clients = [{ name: 'First' }];
    const projects = [{ name: 'Fred', minutes: 100 }]
    return (
        <div id="app">
            <Container fluid={true}>
                <div className="row mt-5">
                    <div className="col">
                        <form>
                            <div className="row">
                                <div className="col-4">
                                    <input type="date" className="form-control"/> { /* v-model="dateFrom"/> */}
                                </div>
                                <div className="col-4">
                                    <input type="date" className="form-control"/> { /* v-model="dateTo"/> */}
                                </div>
                                <div className="col-auto">
                                    <button type="submit" className="btn btn-primary">{ /* v-on:click.prevent="updateDates */}Laden</button>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div className="col-auto">
                        <div className="btn-group">
                            <button className="btn btn-secondary">{ /* v-on:click="prepare" v-if="excel == null"> */}Excel</button>
                            <a download="excel.xlsx" className="btn btn-secondary">{ /* v-on:click="download" v-bind:href="excel" v-if="excel != null"> */}Excel herunterladen</a>
                            <button className="btn btn-danger">{ /*  v-on:click="tagRange"> */}Abgerechnet</button>
                            <button className="btn btn-danger">{ /* v-on:click="untagRange"> */}Zur&uuml;cksetzen</button>
                        </div>
                    </div>
                </div>
                <div className="row">
                    <div className="col pl-4 mt-2">
                        <div className="row">
                            { /* <template v-if="cashout" v-for="client in cashout"> */}
                            <div className="col-2"><b>{client.client}:</b></div>
                            <div className="col-2">{client.amount /* | currency  */}</div>
                            { /* </template> */}
                        </div>
                        <div className="row">
                            <div className="col-2"><b>Total:</b></div>
                            <div className="col-2">{totalCashout /*| currency */}</div>
                        </div>
                    </div>
                </div>
                <hr/>
                <ul className="nav nav-pills nav-fill">
                    {clients.map((c, idx) =>
                        (
                            <li className="nav-item" key={idx}>
                                <a className="nav-link">{ /*v-bind:class="{ active: (idx === activeClient) }"
                                 v-on:click.prevent="switchClient(idx)"> */}{c.name}</a>
                            </li>
                        ))
                    }
                </ul>
                <hr/>
                <table className="table table-bordered">{ /* v-if="projects"> */}
                    <thead>
                    <tr>
                        <th>Projekt</th>
                        <th className="text-right">h:m</th>
                        <th className="text-right">h.m</th>
                    </tr>
                    </thead>
                    <tbody>
                    {projects.map((project, idx) =>
                        <tr key={idx}>
                            <td>{project.name}</td>
                            <td className="text-right">{formatMinutes(project.minutes)}</td>
                            <td className="text-right">{formatDecimal(project.minutes)}</td>
                        </tr>
                    )}
                    </tbody>
                </table>

                { /* if timesheet: */}
                <table className="table table-bordered table-sm">
                    <thead>
                    <tr>
                        <th>Datum</th>
                        <th>Tagestotal</th>
                        <th>Von</th>
                        <th>Bis</th>
                        <th>Dauer</th>
                        <th>Projekt</th>
                        <th>Beschreibung</th>
                        <th>Tags</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    { /* <template v-for="day in timesheet"> */}
                    <tr>{/* v-for="(time, idx) in day" */}
                        <td>{ /* v-bind:rowspan="day.length" v-if="idx == 0">*/}01.02.33{ /* 01.02.33formatDate(time.day) */}</td>
                        <td>{ /* v-bind:rowspan="day.length" v-if="idx == 0"> */}04:23<br/>04.33{ /* { formatDayMinutes(day) }}<br/>{{ formatDayDecimal(day) } */} </td>
                        <td>03:22 { /* { formatTime(time.startdate)} */}</td>
                        <td>05:28 { /* { formatTime(time.enddate)} */}</td>
                        <td>04:23<br/>04.33 { /* { formatMinutes(time.minutes)}}<br/>{{ formatDecimal(time.minutes)} */}</td>
                        <td>Bla Bla {/* { time.project } */}</td>
                        <td>Ble Blo { /* { time.description } */}</td>
                        <td>{/* { time.tags } */}</td>
                        <td>
                            <button className="btn btn-sm">{/* v-on:click="tagBilled(time.id)" v-if="time.tags.indexOf('billed') == -1"> */}+</button>
                            <button className="btn btn-sm">{ /* v-on:click="untagBilled(time.id)" v-if="time.tags.indexOf('billed') >= 0"> */}-</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </Container>
        </div>
    );
};

export default App;
