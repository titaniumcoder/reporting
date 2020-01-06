import React from 'react';

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

const Checkbox = ({value}: {value: boolean}) => {
    if (value) {
        return <FontAwesomeIcon icon="check"/>;
    } else {
        return <FontAwesomeIcon icon="times"/>
    }
};

export default Checkbox;
