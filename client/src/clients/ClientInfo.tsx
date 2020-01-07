import React, {useEffect} from "react";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../rootReducer";
import {selectClient} from "./clientSlice";
import {useParams} from 'react-router-dom';

const ClientInfo = () => {
    const canViewMoney = useSelector((state: RootState) => state.auth.canViewMoney);

    const dispatch = useDispatch();

    let { client } = useParams();

    useEffect(() => {
        dispatch(selectClient(client));
        return () => {
            dispatch(selectClient(undefined));
        }
    }, [client, dispatch]);

    return (
        <div>
            <div><h4>For Current Client ({client}):</h4></div>
            <div>
                <ul>
                    <li>Time Status</li>
                    {canViewMoney &&
                    <li>Money</li>
                    }
                    <li>Project List:
                        <ul>
                            <li>Name</li>
                            <li>Time Status</li>
                            {canViewMoney &&
                            <li>Money</li>
                            }
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    );
};

export default ClientInfo;
