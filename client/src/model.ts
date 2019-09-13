import { Moment } from 'moment';

export interface IClient {
    name: string;
    id: number;
}

export interface IProject {
    name: string;
    minutes: number;
}

export interface ICashout {
    client: string;
    amount: number;
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

