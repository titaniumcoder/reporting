import React from "react";
import {FormFeedback, FormGroup, Input, Label} from "reactstrap";
import {ProjectList, UpdatingTimeEntry} from "../api/reportingApi";
import {FormErrors} from "../components/FormHandler";

export interface SavingTimeEntry {
    id: number;
    starting: string;
    ending: string;

    projectId: number;

    description: string;

    username: string;

    billed: boolean;
    billable: boolean;
}

export const validateTimeEntry = (te: SavingTimeEntry) => {
    let errors: FormErrors = {};

    if (!te.starting) {
        errors['starting'] = 'Starting is required';
    }
    if (!te.username) {
        errors['username'] = 'User is required';
    }
    return errors;
};

export const toUpdatingTimeEntry = (timeEntry: SavingTimeEntry) => ({
        username: timeEntry.username,
        starting: timeEntry.starting,
        projectId: timeEntry.projectId === -1 ? undefined : timeEntry.projectId,
        id: timeEntry.id,
        ending: timeEntry.ending ? timeEntry.ending : undefined,
        description: timeEntry.description ? timeEntry.description : undefined,
        billed: false,
        billable: false
    } as UpdatingTimeEntry);

interface TimeEntryFormProps {
    errors: FormErrors,
    values: SavingTimeEntry,
    handleChange: (evt) => void;
    handleChecked: (evt) => void;
    projectList: ProjectList[];
}

export const TimeEntryForm = ({errors, values, handleChange, handleChecked, projectList}: TimeEntryFormProps) => (
    <div>
        <FormGroup>
            <Label for="starting">Starting:</Label>
            <Input
                type="datetime-local"
                name="starting"
                valid={!errors['starting']}
                invalid={!!errors['starting']}
                value={values.starting}
                onChange={handleChange}
            />
            <FormFeedback>{errors['starting']}</FormFeedback>
        </FormGroup>
        <FormGroup>
            <Label for="ending">Ending:</Label>
            <Input
                type="datetime-local"
                name="ending"
                valid={!errors['ending']}
                invalid={!!errors['ending']}
                value={values.ending}
                onChange={handleChange}
            />
            <FormFeedback>{errors['ending']}</FormFeedback>
        </FormGroup>
        <FormGroup>
            <Label for="projectId">Project:</Label>
            <Input
                autoFocus={true}
                type="select"
                name="projectId"
                valid={!errors['projectId']}
                invalid={!!errors['projectId']}
                value={values.projectId}
                onChange={handleChange}>
                <option value={-1}>- No Project -</option>
                {projectList.map(p =>
                    <option key={p.id} value={p.id}>{p.name} ({p.clientName})</option>
                )}
                <option value={1}>Project 1</option>
            </Input>
            <FormFeedback>{errors['projectId']}</FormFeedback>
        </FormGroup>
        <FormGroup>
            <Label for="description">Description:</Label>
            <Input
                type="text"
                name="description"
                valid={!errors['description']}
                invalid={!!errors['description']}
                value={values.description}
                onChange={handleChange}
            />
            <FormFeedback>{errors['description']}</FormFeedback>
        </FormGroup>
        <FormGroup>
            <Label for="username">User:</Label>
            <Input
                type="text"
                name="username"
                valid={!errors['username']}
                invalid={!!errors['username']}
                value={values.username}
                onChange={handleChange}
            />
            <FormFeedback>{errors['username']}</FormFeedback>
        </FormGroup>
        <FormGroup check>
            <Label check>
                <Input
                    type="checkbox"
                    checked={values.billable}
                    onChange={handleChecked}
                    name="billable"
                />
                {' '}Billable?</Label>
        </FormGroup>
        <FormGroup check>
            <Label check>
                <Input
                    type="checkbox"
                    checked={values.billed}
                    onChange={handleChecked}
                    name="billed"
                />
                {' '}Billed?</Label>
        </FormGroup>
    </div>
);
