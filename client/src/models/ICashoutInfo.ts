import {ICashout} from "./ICashout";
import {IProjectLimit} from "./IProjectLimit";
import {IClientLimit} from "./IClientLimit";

export interface ICashoutInfo {
    cashouts: ICashout[];
    projectLimits: IProjectLimit[];
    clientLimits: IClientLimit[];
}
