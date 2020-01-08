import React from "react";

interface HoursInput {
    minutes?: number;
    decimal?: boolean;
}

type ToHoursFunc = (minutes: number, decimal: boolean | undefined) => (undefined | string);

export const toHours: ToHoursFunc = (minutes, decimal) => {
    const dec = !!decimal;

    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;
    const remaining = dec ? remainingMinutes * 100 / 60 : remainingMinutes;

    return '' + hours + (dec ? '.' : ':') + remaining.toLocaleString('de-CH', {
        minimumIntegerDigits: 2,
        maximumSignificantDigits: 2,
        maximumFractionDigits: 0
    });
};

type ToMinutesFunc = (hours: string | undefined) => (number | undefined);

export const toMinutes: ToMinutesFunc = (hours) => {
    if (!hours) {
        return undefined
    }

    const dec = hours.indexOf('.') > 0;
    if (dec) {
        return Number.parseFloat(hours) * 60;
    } else {
        const parts = hours.split(':');

        if (parts.length !== 2) {
            throw new Error('Illegal time type, its either HH:MM or hh.mm');
        }

        const hoursDec = Number.parseInt(parts[0]);
        const minutesDec = Number.parseInt(parts[1]);

        const minutes = dec ? (minutesDec * 60 / 100) : minutesDec;

        return hoursDec * 60 + minutes;
    }
};

const ShowHours = ({minutes, decimal}: HoursInput) => {
    if (!minutes || minutes < 0) {
        return <span>{decimal ? '0.00' : '0:00'}</span>;
    }
    const hours = toHours(minutes, decimal);

    return (
        <span>{hours}</span>
    );
};

export default ShowHours;
