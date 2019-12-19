import React from 'react';
import { Col, Row } from 'reactstrap';
import { formatCurrency } from './utils';
import {IHeaderInfo} from "./models/models";

interface IHeaderInfoProps {
    headerInfo: IHeaderInfo
}

const HeaderInfo: React.FC<IHeaderInfoProps> = ({ headerInfo }) =>
    (
        headerInfo ?
            <Row>
                <Col style={{ paddingLeft: '0.4rem', marginTop: '0.2rem' }}>
                    <Row>
                        {headerInfo.cashouts.map((client) => (
                            <React.Fragment key={client.client}>
                                <Col xs={2}><b>{client.client}</b>:</Col>
                                <Col xs={2}>{formatCurrency(client.amount)}</Col>
                            </React.Fragment>
                        ))}
                    </Row>
                    <Row>
                        <Col xs={{ size: 2, offset: 4 }}><b>Total:</b></Col>
                        <Col xs={2}>{formatCurrency(headerInfo.totalCashout)}</Col>
                    </Row>
                </Col>
            </Row> : null
    );

export default HeaderInfo;
