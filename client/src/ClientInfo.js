import React from "react";

const ClientInfo = ({canViewMoney}) => {
    return (
        <div>
            <div><h4>For Current Client:</h4></div>
            <div>
                <ul>
                    <li>Time Status</li>
                    <li>Money (only if allowed to see)</li>
                    <li>Project List:
                        <ul>
                            <li>Name</li>
                            <li>Time Status</li>
                            <li>Money (if allowed)</li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    );
};

export default ClientInfo;
