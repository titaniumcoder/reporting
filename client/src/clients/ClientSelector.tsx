import React, {useEffect} from "react";
import {useDispatch} from "react-redux";
import {selectClientWithInfo} from "./clientSlice";
import {useRouteMatch} from 'react-router-dom';

const ClientSelector = () => {
    const dispatch = useDispatch();

    let routeMatch = useRouteMatch("/client/:client");
    const match = routeMatch && routeMatch.params.client;

    useEffect(() => {
        dispatch(selectClientWithInfo(match));
    }, [match, dispatch]);

    return <span></span>;
};

export default ClientSelector;
