/* eslint-disable no-undef */

import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Button, Table} from "reactstrap";
import {formatDecimal, formatMinutes} from "../actions/utils";
import * as moment from "moment";

class Day extends Component {
    static propTypes = {
        firstRow: PropTypes.bool.isRequired,
        rows: PropTypes.number.isRequired,
        dayTotal: PropTypes.number.isRequired,
        id: PropTypes.number.isRequired,
        day: PropTypes.string.isRequired,
        from: PropTypes.string.isRequired,
        to: PropTypes.string,
        description: PropTypes.string,
        minutes: PropTypes.number,
        project: PropTypes.string,
        tags: PropTypes.arrayOf(PropTypes.string)
    };

    render() {
        const {firstRow, rows, dayTotal, id, day, from, to, description, minutes, project, tags} = this.props;

        return (
            <tr>
                {firstRow &&
                <td rowSpan={rows}>{day}</td>
                }
                {firstRow &&
                <td rowSpan={rows}>{formatMinutes(dayTotal)} h<br/>{formatDecimal(dayTotal)}</td>
                }
                <td>{from}</td>
                <td>{to}</td>
                <td>{formatMinutes(minutes)} h<br/>{formatDecimal(minutes)}</td>
                <td>{project}</td>
                <td>{description}</td>
                <td>{tags.join(',')}</td>
                <td><Button size='sm' onClick={(e) => {
                    console.log('Delete ' + id);
                    e.preventDefault();
                }}>Delete</Button></td>
            </tr>
        );
    }
}

/*
timeEntries: [[{id: 1044601474, day: "2018-12-04", project: "4-20192-0001 - Archivöffnung",…},…],…]
 */
export default class Timesheet extends Component {
    static propTypes = {
        timesheet: PropTypes.arrayOf(
            PropTypes.arrayOf(
                PropTypes.shape({
                    id: PropTypes.number.isRequired,
                    day: PropTypes.string.isRequired,
                    startdate: PropTypes.string.isRequired,
                    enddate: PropTypes.string,
                    description: PropTypes.string,
                    minutes: PropTypes.number,
                    project: PropTypes.string,
                    tags: PropTypes.arrayOf(PropTypes.string)
                })
            )
        )
    };

    render() {
        const {timesheet} = this.props;

        return (timesheet ?
                <div>
                    <h2>Projekte</h2>
                    <Table>
                        <thead>
                        <tr>
                            <th>Tag</th>
                            <th>Minuten total</th>
                            <th>Von</th>
                            <th>Bis</th>
                            <th>Dauer</th>
                            <th>Projekt</th>
                            <th>Beschreibung</th>
                            <th>Tags</th>
                        </tr>
                        </thead>
                        <tbody>
                        {timesheet.map(day => {
                            const total = day.reduce((a, v) => a + v.minutes, 0);
                            const date = moment(day[0].day).format('D.M.Y');
                            return day.map((line, idx) => <Day key={line.id} firstRow={idx === 0} rows={day.length}
                                                               id={line.id}
                                                               day={date} description={line.description}
                                                               project={line.project}
                                                               from={moment(line.startdate).format('HH:mm')}
                                                               to={moment(line.enddate).format('HH:mm')}
                                                               minutes={line.minutes} dayTotal={total}
                                                               tags={line.tags}/>)
                        })}
                        </tbody>
                    </Table>
                </div> : null
        )
    }
}