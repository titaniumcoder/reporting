import React from 'react';
import {formatDecimal, formatMinutes} from './utils';

interface IProjectsProp {
    projects: {
        name?: string;
        minutesWorked: number;
        minutesTotal?: number;
        percentage?: number;
    }[];
    total: {
        name?: string;
        minutesWorked: number;
        minutesTotal?: number;
        percentage?: number;
    }
}

const Projects: React.FC<IProjectsProp> = ({projects, total}) => {
    return (
        projects && projects.length > 0 ?
            <table className="table table-bordered">{ /* v-if="projects"> */}
                <thead>
                <tr>
                    <th>Projekt</th>
                    <th className="text-right">Worked</th>
                    <th className="text-right">Total</th>
                </tr>
                </thead>
                <tbody>
                {projects.map((project, idx) =>
                    <tr key={idx}>
                        <td>{project.name}</td>
                        <td className="text-right">
                            {formatMinutes(project.minutesWorked)}<br/>
                            {formatDecimal(project.minutesWorked)}
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
                    <th className="text-right">
                        {formatMinutes(total.minutesWorked)}<br/>
                        {formatDecimal(total.minutesWorked)}
                    </th>
                    <th className="text-right">
                        {formatMinutes(total.minutesTotal)}<br/>
                        {formatDecimal(total.percentage)} %
                    </th>
                </tr>
                </tfoot>
            </table> : null
    );
};

export default Projects;
