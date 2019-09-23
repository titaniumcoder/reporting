import moment from 'moment';

export const formatMinutes = (minutes) => {
    const hours = Math.floor(minutes / 60);
    const remaining = minutes % 60;
    return hours + ':' + remaining.toLocaleString('de-CH', { minimumIntegerDigits: 2, maximumFractionDigits: 0 })
};

export const formatDecimal = (minutes) => {
    return (minutes / 60.0).toLocaleString('de-CH', {
        maximumFractionDigits: 2,
        minimumFractionDigits: 2
    })
};

export const formatDate = (d) => {
    return moment(d).format('DD.MM.YYYY');
};

export const formatTime = (d) => {
    return moment(d).format('HH:mm');
};

export const formatDayMinutes = (day) => {
    const sum = day.map(x => x.minutes).reduce((acc, c) => acc + c);
    return formatMinutes(sum)
};

export const formatDayDecimal = (day) => {
    const sum = day.map(x => x.minutes).reduce((acc, c) => acc + c);
    return formatDecimal(sum)
};

export const formatCurrency = (value) => {
    return new Intl.NumberFormat('de-CH', {
        style: 'currency',
        currency: 'CHF'
    }).format(value)
};
