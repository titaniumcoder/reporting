export interface IHeaderInfo {
    cashouts: {
        client: string;
        amount: number;
    }[];
    projectLimits: {
        id: number;
        project: string;
        startdate: string;
        enddate: string;
        maxHours: number;
        usagePerMonth: {
            month: number;
            usage: number;
        }[];
        totalHoursBilled: number;
        totalHoursNonBilled: number;
        totalHours: number;
        percentage: number;
    }[];
    clientLimits: {
        id: number;
        client: string;
        startdate: string;
        enddate: string;
        maxHours: number;
        usagePerMonth: {
            month: number;
            usage: number;
        }[];
        totalHoursBilled: number;
        totalHoursNonBilled: number;
        totalHours: number;
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
    minutes: number;
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
