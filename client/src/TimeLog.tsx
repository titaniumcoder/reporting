import React from "react";

const TimeLog = () => {
    return (
        <div>
            <h1>Time Entries</h1>
            <div>Selection for Time Entries (Time Range, billed or not billed), default: year, not
                billed
            </div>
            <div>Some selection for TimeRanges: Last Year, Current Year, Last Month, Current Month</div>
            <div>Time Entries (not billed per default - with ops if allowed: billed, not billed, edit,
                delete)
            </div>
        </div>
    );
};

export default TimeLog;
