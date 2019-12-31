import React from 'react';
import {Button, ButtonGroup, Col, Form, FormGroup, Input, Label, Row} from 'reactstrap';
import moment, {Moment} from 'moment';
import {Formik} from "formik";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

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
                  handleChange,
                  handleBlur,
                  handleSubmit,
                  isSubmitting,
                  /* and other goodies */
              }) => (
                <Row style={{paddingTop: "0.5rem"}}>
                    <Col>
                        <Form onSubmit={handleSubmit} inline>
                            <FormGroup className="mr-1">
                                <Label for="dateFrom" className="pr-1">Von:</Label>
                                <Input
                                    type="date"
                                    name="dateFrom"
                                    value={values.dateFrom}
                                    onBlur={handleBlur}
                                    onChange={handleChange}/>
                            </FormGroup>
                            <FormGroup className="mr-1">
                                <Label for="dateTo" className="pr-1">Bis: </Label>
                                <Input
                                    type="date"
                                    name="dateTo"
                                    value={values.dateTo}
                                    onBlur={handleBlur}
                                    onChange={handleChange}/>
                            </FormGroup>
                            <Button type="submit" color="primary" disabled={isSubmitting} className="mr-2"><FontAwesomeIcon
                                icon="cog"/></Button>
                        </Form>
                    </Col>
                    <Col xs="auto" className="text-right">
                        <ButtonGroup>
                            <Button disabled={!enabled} color="secondary"
                                    onClick={createExcel}><FontAwesomeIcon
                                icon="file-excel"/></Button>
                            <Button disabled={!enabled} color="danger" onClick={setBilled}><FontAwesomeIcon
                                icon="check"/></Button>
                            <Button disabled={!enabled} color="danger"
                                    onClick={setUnbilled}><FontAwesomeIcon
                                icon="undo"/></Button>
                        </ButtonGroup>
                        <Button className="ml-2" color="info" onClick={logout}><FontAwesomeIcon
                            icon="sign-out-alt"/></Button>
                    </Col>
                </Row>
            )}
        </Formik>
    );
};

export default Header;
