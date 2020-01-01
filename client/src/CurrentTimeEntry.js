import React, {useState} from "react";
import {Button} from "reactstrap";
import Row from "reactstrap/es/Row";
import Col from "reactstrap/es/Col";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

const CurrentTimeEntry = ({running}) => {
    // TODO replace this with the reality

    // TODO handle start button: send to server
    // TODO handle stop button: show dialog
    const [isRunning, setIsRunning] = useState(false);

    const toggle = () => setIsRunning(!isRunning);

    if (isRunning) {
        return (
            <Row className="bg-info mt-2 mb-2">
                <Col xs={3} className="my-auto text-white"><b>Timer läuft!</b></Col>
                <Col className="my-auto text-white"><b>Startzeit: hh:mm (seit hh:mm)</b></Col>
                <Col xs={1}><Button size="lg" onClick={toggle} color="info"><FontAwesomeIcon icon="stop-circle"/></Button></Col>
            </Row>
        );
    } else {
        return (
            <Row className="bg-info mt-2 mb-2">
                <Col/>
                <Col xs={1}><Button size="lg" onClick={toggle} color="info"><FontAwesomeIcon icon="play-circle"/></Button></Col>
            </Row>
        );
    }
};

export default CurrentTimeEntry;
