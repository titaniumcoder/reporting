export interface IProjectLimit {
    id: number;
    project: string;
    startdate: string;
    enddate: string;
    maxHours: number;
    usagePerMonth: {
        month: number;
        usage: number;
    }[];
    totalHoursUsed: number;
    percentage: number;
}
