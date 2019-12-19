import React from 'react';
import { Col, Row } from 'reactstrap';
import { formatCurrency } from './utils';
import {ICashoutInfo} from "./models/models";

interface ICashoutProps {
    cashout: ICashoutInfo
}

const Cashout: React.FC<ICashoutProps> = ({ cashout }) =>
    (
        cashout ?
            <Row>
                <Col style={{ paddingLeft: '0.4rem', marginTop: '0.2rem' }}>
                    <Row>
                        {cashout.cashouts.map((client) => (
                            <React.Fragment key={client.client}>
                                <Col xs={2}><b>{client.client}</b>:</Col>
                                <Col xs={2}>{formatCurrency(client.amount)}</Col>
                            </React.Fragment>
                        ))}
                    </Row>
                    <Row>
                        <Col xs={{ size: 2, offset: 4 }}><b>Total:</b></Col>
                        <Col xs={2}>{formatCurrency(cashout.totalCashout)}</Col>
                    </Row>
                </Col>
            </Row> : null
    );

export default Cashout;
