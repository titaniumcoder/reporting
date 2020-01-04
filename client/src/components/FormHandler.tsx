import {useEffect, useMemo, useState} from "react";

export function FormHandler<T>(instance: T, validator: (T) => Map<string, string | undefined>, save: (T) => Promise<void>) {
    const [values, setValues] = useState(instance);
    const [submitting, setSubmitting] = useState(false);
    const [submitDisabled, setSubmitDisabled] = useState(true);

    const errors = useMemo(() => validator(values), [values, validator]);

    useEffect(() => {
        setSubmitDisabled(submitting || errors.size > 0);
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
        console.log('Submit event: ', evt);
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
