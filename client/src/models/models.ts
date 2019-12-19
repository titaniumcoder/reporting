export interface IHeaderInfo {
    cashouts: {
        client: string;
        amount: number;
    }[];
    projectLimits: {
        id: number;
        project: string;
        year: number;
        maxHours: number;
        totalHoursBilled: number;
        totalHoursOpen: number;
        totalHoursUsed: number;
        percentage: number;
    }[];
    clientLimits: {
        id: number;
        client: string;
        year: number;
        maxHours: number;
        totalHoursBilled: number;
        totalHoursOpen: number;
        totalHoursUsed: number;
        percentage: number;
    }[];
    totalCashout: number;
}

export interface IClient {
    name: string;
    id: number;
}

export interface IProject {
    name: string;
    minutesInRange: number;

    minutesTotal: number;
    minutesBilled: number;
    minutesOpen: number;

    percentage: number;
}

export interface ITimeEntry {
    id: number;
    day: string;
    project: string;
    startdate: string;
    enddate: string;
    minutes: number;
    description: string;
    tags: string[];
}

export interface IAuthentication {
    username?: string;
    password?: string;
    accessToken?: string;
    refreshToken?: string;
    expiration?: number; // TODO may be timestamp?
    refreshExpiration?: number; // TODO may be timestamp?
}
