import React from 'react';
import './App.css';
import { formatDate, formatDayDecimal, formatDayMinutes, formatDecimal, formatMinutes, formatTime } from './utils';

interface ITimesheetProps {
    timesheet: {
        id: number;
        day: string;
        startdate: string;
        enddate: string;
        minutes: number;
        project: string;
        description: string;
        tags: string[];
    }[][];
    tagBilled: (number) => void;
    tagUnbilled: (number) => void;
}

const Timesheet: React.FC<ITimesheetProps> = ({ timesheet, tagBilled, tagUnbilled }) =>
    (
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
                <th/>
            </tr>
            </thead>
            <tbody>
            {timesheet.map(day => (
                day.map((time) => (
                    <tr key={time.id}>
                        <td>{formatDate(time.day)}</td>
                        <td>{formatDayMinutes(day)}<br/>{formatDayDecimal(day)}</td>
                        <td>{formatTime(time.startdate)}</td>
                        <td>{formatTime(time.enddate)}</td>
                        <td>{formatMinutes(time.minutes)}<br/>{formatDecimal(time.minutes)}</td>
                        <td>{time.project}</td>
                        <td>{time.description}</td>
                        <td>{time.tags.join(", ")}</td>
                        <td>
                            {time.tags.indexOf('billed') === -1 ?
                                <button className="btn btn-sm" onClick={() => tagBilled(time.id)}>+</button> :
                                <button className="btn btn-sm" onClick={() => tagUnbilled(time.id)}>-</button>
                            }
                        </td>
                    </tr>
                ))
            ))}
            </tbody>
        </table>
    );

export default Timesheet;
