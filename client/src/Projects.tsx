import React from 'react';
import {formatDecimal, formatMinutes} from './utils';

interface IProjectsProp {
    projects: {
        name: string;
        minutesInRange: number;
        minutesOpen: number;
        minutesTotal: number;
        minutesBilled: number;
        percentage: number;
    }[];
}

const Projects: React.FC<IProjectsProp> = ({projects}) => {
    const total =
        projects && projects.length > 0 ? projects.map(x => x.minutesInRange).reduce((acc, x) => acc + x) : 0;
    return (
        projects && projects.length > 0 ?
            <table className="table table-bordered">{ /* v-if="projects"> */}
                <thead>
                <tr>
                    <th>Projekt</th>
                    <th className="text-right">in Range</th>
                    <th className="text-right">Open</th>
                    <th className="text-right">Billed</th>
                    <th className="text-right">Total</th>
                </tr>
                </thead>
                <tbody>
                {projects.map((project, idx) =>
                    <tr key={idx}>
                        <td>{project.name}</td>
                        <td className="text-right">
                            {formatMinutes(project.minutesInRange)}<br/>
                            {formatDecimal(project.minutesInRange)}
                        </td>
                        <td className="text-right">
                            {formatMinutes(project.minutesOpen)}<br/>
                            {formatDecimal(project.minutesOpen)}
                        </td>
                        <td className="text-right">
                            {formatMinutes(project.minutesBilled)}<br/>
                            {formatDecimal(project.minutesBilled)}
                        </td>
                        <td className="text-right">
                            {formatMinutes(project.minutesTotal)}<br/>
                            {formatDecimal(project.percentage)} %
                        </td>
                    </tr>
                )}
                </tbody>
                <tfoot>
                <tr>
                    <th className="text-right">Total:</th>
                    <td className="text-right">{formatMinutes(total)}</td>
                    <td className="text-right">{formatDecimal(total)}</td>
                </tr>
                </tfoot>
            </table> : null
    );
};

export default Projects;
