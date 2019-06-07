import React from 'react';
import './App.css';
import { Button, ButtonGroup, Col, Container, Nav, NavItem, Row } from 'reactstrap';
import moment from 'moment';
import { AvForm, AvField } from 'availity-reactstrap-validation';

const formatMinutes = (minutes) => {
    const hours = Math.floor(minutes / 60);
    const remaining = minutes % 60;
    return hours + ':' + remaining.toLocaleString('de-CH', { minimumIntegerDigits: 2, maximumFractionDigits: 0 })
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

const formatTime = (d) => {
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

const formatCurrency = (value) => {
    return new Intl.NumberFormat('de-CH', {
        style: 'currency',
        currency: 'CHF'
    }).format(value)
};

const App: React.FC = () => {
    const totalCashout = 1000;
    const clients = [{ name: 'First', id: 50 }, { name: 'Second', id: 90 }, { name: 'Third', id: 100 }, { name: 'Fourth', id: 120 }];
    const projects = [{ name: 'Fred', minutes: 100 }];
    const activeClient = 100;
    const cashout = [
        { client: 'Fred', amount: 1000 },
        { client: 'Wilma', amount: 1233.22 }
    ];

    const defaultFromTo = {
        dateFrom: moment().startOf('month').format('YYYY-MM-DD'),
        dateTo: moment().endOf('month').format('YYYY-MM-DD')
    };

    const handleSubmit = (event, errors, values) => {
        console.log('Errors_ ', errors, ' Values: ', values);
    };

    const excel: (any[] | null) = null;

    return (
        <div id="app">
            <Container fluid={true}>
                <Row style={{ paddingTop: "0.5rem" }}>
                    <Col>
                        <AvForm onValidSubmit={handleSubmit} model={defaultFromTo}>
                            <Row>
                                <Col xs="4">
                                    <AvField name="dateFrom" type="date" required/>
                                </Col>
                                <Col xs="4">
                                    <AvField name="dateTo" type="date" required/>
                                </Col>
                                <Col xs="auto">
                                    <Button type="submit" color="primary">Laden</Button>
                                </Col>
                            </Row>
                        </AvForm>
                    </Col>
                    <Col xs="auto">
                        <ButtonGroup>
                            {excel === null ?
                                <Button color="secondary">Excel</Button> :
                                <Button color="secondary">Excel herunterladen</Button>}
                            <Button color="danger">Abgerechnet</Button>
                            <Button color="danger">Zurücksetzen</Button>
                        </ButtonGroup>
                    </Col>
                </Row>
                <Row>
                    <Col style={{ paddingLeft: '0.4rem', marginTop: '0.2rem' }}>
                        <Row>
                            {cashout.map((client) => (
                                <React.Fragment key={client.client}>
                                    <Col xs={2}><b>{client.client}</b>:</Col>
                                    <Col xs={2}>{formatCurrency(client.amount)}</Col>
                                </React.Fragment>
                            ))}
                        </Row>
                        <Row>
                            <Col xs={{ size: 2, offset: 4 }}><b>Total:</b></Col>
                            <Col xs={2}>{formatCurrency(totalCashout)}</Col>
                        </Row>
                    </Col>
                </Row>
                <hr/>
                <Nav pills={true} fill={true}>
                    {clients.map((c, idx) =>
                        <NavItem key={c.name} onClick={(e) => {
                            console.log('Nav: ', e)
                        }} active={c.id === activeClient}>{c.name}</NavItem>
                    )}
                </Nav>
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
