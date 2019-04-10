/* eslint-disable no-undef */

import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Col, Row} from "reactstrap";

export default class Systeminfo extends Component {
    static propTypes = {
        cash: PropTypes.arrayOf(
            PropTypes.shape({
                client: PropTypes.string,
                amount: PropTypes.number.isRequired
            })
        )
    };

    render() {
        const {cash} = this.props;

        return <div>
            <Row>
                {cash.map(c =>
                    <Col xs={4} key={c.client}><b>{c.client}</b>: {c.amount.toLocaleString("de-CH", {
                        useGrouping: true,
                        maximumFractionDigits: 2
                    })}</Col>)}
                <Col
                    xs={4}><b>Total</b>: {(cash.map(c => Number(c.amount)).reduce((a, b) => a + b, 0)).toLocaleString("de-CH", {
                    useGrouping: true,
                    maximumFractionDigits: 2
                })}</Col>
            </Row>
        </div>;
    }
}
