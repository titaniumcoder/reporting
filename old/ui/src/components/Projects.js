/* eslint-disable no-undef */

import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Table} from "reactstrap";
import {formatDecimal, formatMinutes} from "../actions/utils";

class Project extends Component {
    static propTypes = {
        name: PropTypes.string.isRequired,
        minutes: PropTypes.string.isRequired,
        decimal: PropTypes.string.isRequired
    };

    render() {
        const {name, minutes, decimal} = this.props;
        return (
            <tr>
                <td>{name}</td>
                <td>{minutes}</td>
                <td>{decimal}</td>
            </tr>
        )
    }
}

export default class Projects extends Component {
    static propTypes = {
        projects: PropTypes.arrayOf(
            PropTypes.shape({
                name: PropTypes.string.isRequired,
                minutes: PropTypes.number.isRequired
            })
        )
    };

    render() {
        const {projects} = this.props;

        return (projects ?
            <div>
                <h2>Projekte</h2>
                <Table>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Zeit (hh:mm)</th>
                        <th>Zeit (hh.mm)</th>
                    </tr>
                    </thead>
                    <tbody>
                    {projects.map(p => <Project key={p.name} name={p.name} minutes={formatMinutes(p.minutes)} decimal={formatDecimal(p.minutes)}/>)}
                    </tbody>
                </Table>
            </div> : null
        )
    }
}