export interface IHeaderInfo {
    cashouts: {
        client: string;
        amount: number;
        minutesTotal?: number;
        minutesWorked: number;
        percentage?: number;
    }[];
    totalCashout: number;
}

export interface IClient {
    name: string;
    id: number;

    minutesTotal?: number;
    minutesWorked: number;

    percentage?: number;
}

export interface IProject {
    name?: string;

    minutesWorked: number;
    minutesTotal?: number;

    percentage?: number;
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
