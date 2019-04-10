/* eslint-disable no-undef */

import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {connect} from 'react-redux'
import {withRouter} from 'react-router-dom'
import {loadTimesheet} from "../actions";
import Projects from "../components/Projects";
import Timesheet from "../components/Timesheet";

class ClientPage extends Component {
    static propTypes = {
        loggedIn: PropTypes.bool.isRequired,
        client: PropTypes.string.isRequired,
        name: PropTypes.string,
        projects: PropTypes.array,
        timeEntries: PropTypes.array,
        loadTimesheet: PropTypes.func.isRequired
    };

    componentDidMount() {
        this.props.loadTimesheet(this.props.client)
    }

    componentDidUpdate(prevProps) {
        if ((prevProps.client !== this.props.client) || (!prevProps.loggedIn && this.props.loggedIn)){
            this.props.loadTimesheet(this.props.client)
        }
    }

    render() {
        const {name, projects, timeEntries} = this.props;

        return (
            <div className="container-fluid">
                <h2>Timesheet für {name}</h2>
                <hr/>
                {projects &&
                <Projects projects={projects}/>
                }
                <hr/>
                {timeEntries &&
                <Timesheet timesheet={timeEntries}/>
                }
            </div>
        );
    }
}

const mapStateToProps = (state, ownProps) => {
    const client = ownProps.match.params.client;
    const name = state.client.client;
    const loggedIn = state.authentication.loggedIn;
    const {projects, timeEntries} = state.client;

    return {
        loggedIn,
        client,
        name,
        projects,
        timeEntries
    };
};

export default withRouter(connect(mapStateToProps, {
    loadTimesheet
})(ClientPage))