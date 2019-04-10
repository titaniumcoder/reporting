import {camelizeKeys} from 'humps';

// Extracts the next page URL from Github API response.
const getNextPageUrl = response => {
    const link = response.headers.get('link');
    if (!link) {
        return null
    }

    const nextLink = link.split(',').find(s => s.indexOf('rel="next"') > -1);
    if (!nextLink) {
        return null
    }

    return nextLink.trim().split(';')[0].slice(1, -1)
};

// Fetches an API response and normalizes the result JSON according to schema.
// This makes every API response have the same shape, regardless of how nested it was.
export const callApi = (endpoint, username, password) => {
    const headers = new Headers();
    if (username || password)
        headers.set('Authorization', 'Basic ' + Buffer.from(username + ":" + password).toString('base64'));

    return fetch(endpoint, {redirect: "follow", headers: headers})
        .then(response =>
            response.json().then(json => {
                if (!response.ok) {
                    return Promise.reject('invalid response from server: ' + response.error)
                }

                const camelizedJson = camelizeKeys(json);
                const nextPageUrl = getNextPageUrl(response);

                return Object.assign({},
                    camelizedJson,
                    {nextPageUrl}
                )
            })
        )
};

export const generalApi = (endpoint, method, body, username, password, blob = false, empty = false) => {
        let init;
        if (body) {
            init = {
                redirect: "follow", headers: {
                    'Authorization': 'Basic ' + Buffer.from(username + ":" + password).toString('base64'),
                    'Content-type': 'application/json'

                },
                method,
                body
            };
        } else {
            init = {
                redirect: "follow", headers: {
                    'Authorization': 'Basic ' + Buffer.from(username + ":" + password).toString('base64'),
                },
                method
            };
        }

        return fetch(endpoint, init)
            .then(response => {
                if (!response.ok) {
                    return Promise.reject('invalid response from server: ' + response.error)
                }

                if (empty) {
                    return Promise.resolve(null);
                }

                if (blob)
                    return response.blob();
                else
                    return response.json();
            });
    }
;
