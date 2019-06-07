import React from 'react';
import './App.css';
import { Container } from 'reactstrap';
import moment from 'moment';
import Header from './Header';
import Cashout from './Cashout';
import Navigation from './Navigation';
import Projects from './Projects';
import Timesheet from './Timesheet';

class App extends React.Component<{}, {}> {
    render() {
        const totalCashout = 1000;
        const clients = [{ name: 'First', id: 50 }, { name: 'Second', id: 90 }, { name: 'Third', id: 100 }, { name: 'Fourth', id: 120 }];
        const projects = [{ name: 'Fred', minutes: 100 }];
        const activeClient = 100;
        const cashout = [
            { client: 'Fred', amount: 1000 },
            { client: 'Wilma', amount: 1233.22 }
        ];

        const timesheet = [[{
            "id": 1206006509,
            "day": "2019-06-03",
            "project": "A",
            "startdate": "2019-06-03T07:04:00Z",
            "enddate": "2019-06-03T10:16:00Z",
            "minutes": 191,
            "description": "Test Data",
            "tags": []
        }, {
            "id": 1206375059,
            "day": "2019-06-03",
            "project": "A",
            "startdate": "2019-06-03T12:15:00Z",
            "enddate": "2019-06-03T17:46:00Z",
            "minutes": 331,
            "description": "Test Data",
            "tags": []
        }], [{
            "id": 1207960354,
            "day": "2019-06-04",
            "project": "A",
            "startdate": "2019-06-04T07:05:00Z",
            "enddate": "2019-06-04T09:14:00Z",
            "minutes": 128,
            "description": "Test Data",
            "tags": []
        }, {
            "id": 1207960603,
            "day": "2019-06-04",
            "project": "A",
            "startdate": "2019-06-04T09:57:00Z",
            "enddate": "2019-06-04T14:52:00Z",
            "minutes": 295,
            "description": "Test Data",
            "tags": []
        }], [{
            "id": 1208751903,
            "day": "2019-06-05",
            "project": "A",
            "startdate": "2019-06-05T07:57:00Z",
            "enddate": "2019-06-05T09:39:00Z",
            "minutes": 101,
            "description": "Test Data",
            "tags": []
        }, {
            "id": 1208914422,
            "day": "2019-06-05",
            "project": "A",
            "startdate": "2019-06-05T10:23:00Z",
            "enddate": "2019-06-05T14:29:00Z",
            "minutes": 246,
            "description": "Test Data",
            "tags": ["billed"]
        }]];

        return (
            <div id="app">
                <Container fluid={true}>
                    <Header dateFrom={moment().startOf('month')} excel={null} dateTo={moment().endOf('month')} loadFromTo={() => {
                        console.log('Received load')
                    }} createExcel={() => console.log('Received excl')} setBilled={() => console.log('Received set billded')}
                            setUnbilled={() => console.log('Received set unbileed')}/>
                    <Cashout cashout={cashout} totalCashout={totalCashout}/>
                    <hr/>
                    <Navigation clients={clients} activeClient={activeClient}/>
                    <Projects projects={projects}/>

                    <Timesheet timesheet={timesheet} tagBilled={(id) => console.log(`Received tag for ${id}`)} tagUnbilled={(id) => console.log(`Received untag for ${id}`)}/>
                </Container>
            </div>
        );
    }
}

export default App;
