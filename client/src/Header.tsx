import React from 'react';
import {Button, ButtonGroup, Col, Form, FormGroup, Input, Label, Row} from 'reactstrap';
import moment, {Moment} from 'moment';
import {Formik} from "formik";

interface IHeaderProps {
    dateFrom: Moment;
    dateTo: Moment;
    enabled: boolean;

    loadFromTo: (from: Moment, to: Moment) => void;
    createExcel: () => void;
    setBilled: () => void;
    setUnbilled: () => void;

    logout: () => void;
}

const Header: React.FC<IHeaderProps> = ({enabled, dateFrom, dateTo, loadFromTo, createExcel, setBilled, setUnbilled, logout}) => {
    const defaultFromTo = {
        dateFrom: dateFrom.format('YYYY-MM-DD'),
        dateTo: dateTo.format('YYYY-MM-DD')
    };

    const handleSubmit = (event, values) => {
        loadFromTo(moment(values.dateFrom, 'YYYY-MM-DD'), moment(values.dateTo, 'YYYY-MM-DD'));
    };

    return (
        <Row style={{paddingTop: "0.5rem"}}>
            <Col>
                <Formik
                    initialValues={{dateFrom: defaultFromTo.dateFrom, dateTo: defaultFromTo.dateTo}}
                    validate={values => {
                        let errors: any = {};
                        if (!values.dateFrom) {
                            errors.dateFrom = 'Date From is required';
                        }
                        if (!values.dateTo) {
                            errors.dateTo = 'Date To is required';
                        }
                        if (values.dateFrom && values.dateTo && values.dateTo < values.dateFrom) {
                            errors.dateTo = 'Date To must be after from';
                        }
                        return errors;
                    }}
                    onSubmit={(values, {setSubmitting}) => {
                        setSubmitting(true);
                        handleSubmit(null, values);
                        setSubmitting(false);
                    }}>
                    {({
                          values,
                          errors,
                          touched,
                          handleChange,
                          handleBlur,
                          handleSubmit,
                          isSubmitting,
                          /* and other goodies */
                      }) => (
                        <Form onSubmit={handleSubmit}>
                            <Row>
                                <Col xs="4">
                                    <FormGroup>
                                        <Label for="dateFrom">Von</Label>
                                        <Input
                                            type="date"
                                            name="dateFrom"
                                            value={values.dateFrom}
                                            onBlur={handleBlur}
                                            onChange={handleChange}/>
                                    </FormGroup>
                                </Col>
                                <Col xs="4">
                                    <FormGroup>
                                        <Label for="dateTo">Bis</Label>
                                        <Input
                                            type="date"
                                            name="dateTo"
                                            value={values.dateTo}
                                            onBlur={handleBlur}
                                            onChange={handleChange}/>
                                    </FormGroup>
                                </Col>
                                <Col xs="auto">
                                    <Button type="submit" color="primary" disabled={isSubmitting}>Laden</Button>
                                </Col>
                            </Row>
                        </Form>
                    )}
                </Formik>
            </Col>
            <Col xs="auto">
                <ButtonGroup>
                    <Button disabled={!enabled} color="secondary" onClick={createExcel}>Excel</Button>
                    <Button disabled={!enabled} color="danger" onClick={setBilled}>Abgerechnet</Button>
                    <Button disabled={!enabled} color="danger" onClick={setUnbilled}>Zurücksetzen</Button>
                </ButtonGroup>
            </Col>
            <Col xs="auto" style={{paddingLeft: "0.4rem"}}>
                <Button color="info" onClick={logout}>Ausloggen</Button>
            </Col>
        </Row>
    );
};

export default Header;
