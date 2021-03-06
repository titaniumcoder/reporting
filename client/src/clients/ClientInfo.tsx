import React, {Fragment} from "react";
import {useSelector} from "react-redux";
import {RootState} from "../rootReducer";
import ShowRate from "../components/ShowRate";
import ShowHours from "../components/ShowHours";
import {Table} from "reactstrap";

const ClientInfo = () => {
    const clientInfo = useSelector((state: RootState) => state.client.clientInfo);

    if (clientInfo.length === 0) {
        return <div/>;
    }

    const divs = clientInfo.map(ci => {
        const projs = ci.projects && ci.projects.length > 0;

        return (
            <Fragment key={ci.id}>
                <tr className="row bg-light">
                    <td className="col">{ci.name}</td>
                    <td className="col-2 text-center d-none d-md-block"><ShowHours minutes={ci.maxMinutes}/>{!!ci.rateInCentsPerHour && <br/>}<ShowRate
                        rate={ci.rateInCentsPerHour}/></td>
                    <td className="col-2 text-center d-none d-md-block"><ShowHours minutes={ci.billedMinutes}/>{!!ci.billedAmount && <br/>}<ShowRate
                        rate={ci.billedAmount}/></td>
                    <td className="col-2 text-center"><ShowHours minutes={ci.openMinutes}/>{!!ci.openAmount && <br/>}<ShowRate rate={ci.openAmount}/>
                    </td>
                    <td className="col-2 text-center d-none d-md-block"><ShowHours minutes={ci.remainingMinutes}/>{!!ci.remainingAmount && <br/>}<ShowRate
                        rate={ci.remainingAmount}/></td>
                </tr>

                {projs && ci.projects.map(p =>
                    <tr key={p.projectId} className="row">
                        <td className="pl-4 col">{p.name}</td>
                        <td className="col-2 text-center d-none d-md-block"><ShowHours minutes={p.maxMinutes}/>{!!p.rateInCentsPerHour && <br/>}<ShowRate
                            rate={p.rateInCentsPerHour}/></td>
                        <td className="col-2 text-center d-none d-md-block"><ShowHours minutes={p.billedMinutes}/>{!!p.billedAmount && <br/>}<ShowRate
                            rate={ci.billedAmount}/></td>
                        <td className="col-2 text-center"><ShowHours minutes={p.openMinutes}/>{!!p.openAmount && <br/>}<ShowRate rate={p.openAmount}/>
                        </td>
                        <td className="col-2 text-center d-none d-md-block"><ShowHours minutes={p.remainingMinutes}/>{!!p.remainingAmount && <br/>}<ShowRate
                            rate={p.remainingAmount}/></td>
                    </tr>
                )}
            </Fragment>
        );
    });


    return (
        <Table>
            <thead>
            <tr className="row">
                <th className="col">Name</th>
                <th className="col-2 text-center d-none d-md-block">Max / Rate</th>
                <th className="col-2 text-center d-none d-md-block">Billed</th>
                <th className="col-2 text-center">Next Billing</th>
                <th className="col-2 text-center d-none d-md-block">Remaining</th>
            </tr>
            </thead>
            <tbody>
            {divs}
            </tbody>
        </Table>
    );
};

export default ClientInfo;
