export interface IClientLimit {
    id: number;
    client: string;
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
