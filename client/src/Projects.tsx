import React from 'react';
import {formatDecimal, formatMinutes} from './utils';

interface IProjectsProp {
    projects: {
        name: string;
        minutes: number;
    }[];
}

const Projects: React.FC<IProjectsProp> = ({projects}) => {
    const total =
        projects && projects.length > 0 ? projects.map(x => x.minutes).reduce((acc, x) => acc + x) : 0;
    return (
        projects && projects.length > 0 ?
            <table className="table table-bordered">{ /* v-if="projects"> */}
                <thead>
                <tr>
                    <th>Projekt</th>
                    <th className="text-right">h:m</th>
                    <th className="text-right">h.m</th>
                </tr>
                </thead>
                <tbody>
                {projects.map((project, idx) =>
                    <tr key={idx}>
                        <td>{project.name}</td>
                        <td className="text-right">{formatMinutes(project.minutes)}</td>
                        <td className="text-right">{formatDecimal(project.minutes)}</td>
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
