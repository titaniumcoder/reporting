import {useEffect, useMemo, useState} from "react";

export interface FormErrors {
    [field: string]: string;
}

export type SaveFunc<T> = (T) => Promise<void>

export type ValidatorFunc<T> = (T) => FormErrors;

export function FormHandler<T>(instance: T, validator: ValidatorFunc<T>, save: SaveFunc<T>) {
    const [values, setValues] = useState(instance);
    const [submitting, setSubmitting] = useState(false);
    const [submitDisabled, setSubmitDisabled] = useState(true);

    const errors = useMemo(() => validator(values), [values, validator]);

    useEffect(() => {
        setSubmitDisabled(submitting || errors === {});
    }, [submitting, errors]);

    const handleChange = (evt) => {
        setValues({
            ...values,
            [evt.target.name]: evt.target.value
        });
    };

    const handleChecked = (evt) => {
        setValues({
            ...values,
            [evt.target.name]: evt.target.checked
        });
    };

    const handleMultiple = (evt) => {
        setValues({
            ...values,
            [evt.target.name]: Array.from<HTMLOptionElement>(evt.target.selectedOptions).map(i => i.value)
        });
    };

    const doSubmit = async (evt) => {
        evt.preventDefault();

        if (!submitDisabled) {
            // submit the form
            try {
                setSubmitting(true);
                await save(values);
            } finally {
                setSubmitting(false);
            }
        }
    };

    return {
        values,
        errors,
        submitDisabled,
        setSubmitting,
        handleChange,
        handleChecked,
        handleMultiple,
        doSubmit
    }
}
