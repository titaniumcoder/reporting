import {createAction, createReducer} from 'redux-starter-kit';

export const clientsRequest = createAction('clientsRequest');
export const clientsSuccess = createAction('clientsSuccess');
export const clientsError = createAction('clientsError');

function requestClientsFromServer(state, action) {
    // TODO implement this
    return state;
}

export default createReducer({ok: false}, {
    [clientsRequest]: requestClientsFromServer,
    [clientsSuccess]: (state) => [...state, {ok: false}],
    [clientsError]: (state, action) => [...state, {ok: action.status === 200}]
});
