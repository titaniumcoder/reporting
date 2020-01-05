import React, {useEffect, useMemo} from "react";
import {Button, ButtonGroup, Col, Row} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../rootReducer";
import reportingApi, {TimeEntry} from "../api/reportingApi";
import {currentTimeEntrySuccess} from "./timeentrySlice";

const CurrentTimeEntry = () => {
    const {loggedIn, admin, canBook, authToken} = useSelector((root: RootState) => root.auth);

    const isAllowed = useMemo(() => loggedIn && (admin || canBook), [loggedIn, admin, canBook]);

    const currentTimeEntry: TimeEntry | undefined = useSelector((root: RootState) => root.timeentry.currentTimeEntry);

    const dispatch = useDispatch();

    useEffect(() => {
        if (isAllowed && authToken) {
            const sse = new EventSource('/sse/current-timeentry?token=' + authToken);
            sse.onmessage = (e) => {
                dispatch(currentTimeEntrySuccess(JSON.parse(e.data)));
            };
            return () => {
                sse.close()
            };
        } else {

            return () => {
            }
        }
    }, [isAllowed, authToken, dispatch]);

    const executeUpdateTimeEntry = async (api) => {
        if (currentTimeEntry) {
            const {starting, billable, billed, description, ending, id, projectId, username} = currentTimeEntry;
            await api({
                billable,
                billed,
                description,
                ending,
                id,
                projectId,
                starting,
                username
            });
        }
    };

    const stopCurrentEntry = async () => {
        await executeUpdateTimeEntry(reportingApi.stopTimeEntry);
    };
    const updateCurrentEntry = async () => {
        await executeUpdateTimeEntry(reportingApi.updateTimeEntry);
    };
    const startNewEntry = async () => {
        await reportingApi.startTimeEntry(undefined);
    };

    if (currentTimeEntry) {
        return (
            <Row className="mt-3 mb-2 bg-info">
                <Col xs={2} className="my-auto">
                    {currentTimeEntry.starting} - {currentTimeEntry.ending}
                </Col>
                <Col xs="auto" className="my-auto">
                    Projekt:
                </Col>
                <Col xs={2} className="my-auto">
                    {currentTimeEntry.projectName}
                </Col>

                <Col className="my-auto">
                    {currentTimeEntry.description}
                </Col>

                <Col xs="auto" className="my-auto text-white text-right">({currentTimeEntry.timeUsed || 'aa'})</Col>
                <Col xs="auto">
                    <ButtonGroup>
                        <Button size="lg" onClick={stopCurrentEntry} color="info">
                            <FontAwesomeIcon icon="stop-circle"/>
                        </Button>
                    </ButtonGroup>
                </Col>
            </Row>
        );
    } else {
        return (
            <Row className="bg-light mt-2 mb-2">
                <Col/>
                <Col xs={1}><Button size="lg" onClick={startNewEntry} color="info"><FontAwesomeIcon
                    icon="play-circle"/></Button></Col>
            </Row>
        );
    }
};

export default CurrentTimeEntry;
