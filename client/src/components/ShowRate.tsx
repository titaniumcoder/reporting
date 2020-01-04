import React from "react";

const ShowRate: React.FC<{rate: number | undefined}> = ({rate}) => {
    if (!rate) {
        return null;
    }

    return (
        <span>
            {(rate / 100).toLocaleString('de-CH', {minimumIntegerDigits: 1, maximumFractionDigits: 2})}
        </span>
    );
};

export default ShowRate;
