import React from "react";

interface ShowMinutesInput {
    minutes?: number;
    decimal?: boolean;
}

const ShowMinutes: React.FC<ShowMinutesInput> = ({minutes, decimal}) => {
    if (!minutes) {
        return null;
    }

    const dec = !!decimal;

    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;
    const remaining = dec ? remainingMinutes * 100 / 60 : remainingMinutes;

    return (
        <span>{hours}{dec ? '.' : ':'}{remaining.toLocaleString('de-CH', {minimumIntegerDigits: 2, maximumFractionDigits: 0})}</span>
    );
};

export default ShowMinutes;
