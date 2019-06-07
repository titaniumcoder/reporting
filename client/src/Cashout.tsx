import React from 'react';
import './App.css';
import { Col, Row } from 'reactstrap';
import { formatCurrency } from './utils';

interface ICashoutProps {
    cashout: {
        client: string;
        amount: number | null;
    }[],
    totalCashout: number | null;
}

const Cashout: React.FC<ICashoutProps> = ({ cashout, totalCashout }) =>
    (
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
    );

export default Cashout;
