import React from 'react';
import './App.css';
import { formatDecimal, formatMinutes } from './utils';

interface IProjectsProp {
    projects: {
        name: string;
        minutes: number;
    }[];
}

const Projects: React.FC<IProjectsProp> = ({ projects }) =>
    (
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
        </table>
    );

export default Projects;
