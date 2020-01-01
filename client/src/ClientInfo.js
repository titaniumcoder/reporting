import React from "react";
import * as moment from 'moment';
import {Route} from "react-router-dom";

const ClientInfo = ({canViewMoney}) => {
    // TODO replace this with the reality via an API call
    const name = 'SRF';
    const notes = 'This are some notes';
    const maxMinutes = 100;
    const rateInCentsPerHour = 1000;
    const projects = [
        {
            name: "P1",
            maxMinutes: 13,
            rateInCentsPerHour: 10110,
            minutesUsed: 10,
            minutesRemaining: 90,
            minutesPercentage: 10.0,
            amountBilled: 1000.0,
            amountOpen: 100.0,
            amountRemaining: 10000.0
        },
        {
            name: "P1",
            minutesUsed: 10,
            amountBilled: 1000.0,
            amountOpen: 100.0
        }
    ];
    const timeEntries = [
        {
            id: 100,
            description: "Hier kommt die Beschreibung",
            date: moment(),
            starting: moment(),
            ending: moment(),
            projectId: 1,
            projectName: "P1",
            username: "Fred",
            billable: true,
            billed: false
        }, {
            id: 101,
            description: "Hier kommt die Beschreibung 2",
            date: moment(),
            starting: moment(),
            ending: moment(),
            projectId: 1,
            projectName: "P1",
            username: "Fred",
            billable: true,
            billed: false
        }
    ];

    const minutesUsed = 10;
    const minutesRemaining = 90;
    const minutesPercentage = 10.0;
    const amountBilled = 1000.0;
    const amountOpen = 100.0;
    const amountRemaining = 10000.0;

    return (
        <div>
            <div><h4>For Current Client:</h4></div>
            <div>
                <ul>
                    <li>Time Status</li>
                    <li>Money (only if allowed to see)</li>
                    <li>Project List:
                        <ul>
                            <li>Name</li>
                            <li>Time Status</li>
                            <li>Money (if allowed)</li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    );
};

export default ClientInfo;
