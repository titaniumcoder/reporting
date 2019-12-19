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
