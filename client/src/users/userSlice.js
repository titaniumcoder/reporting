import {createSlice} from "@reduxjs/toolkit";

const userSlice = createSlice({
    name: 'auth',
    initialState: {
        users: [],
        loading: false,
        error: undefined
    },
    reducers: {
        loadUsers: {
            reducer(state, action) {
                const {users} = action.payload;
                state.users = users;
                state.loading = false;
                state.error = undefined;
            },
            prepare(users) {
                return {
                    payload: {users}
                }
            }
        },
        loadingUsers(state) {
            state.loading = true;
            state.error = undefined;
        },
        loadUsersFailed: {
            reducer(state, action) {
                // TODO what do we do here?
                const {error} = action.payload;
                state.error = error;
                state.users = [];
                state.loading = false;
            },
            prepare(error) {
                return {
                    payload: {
                        error
                    }
                }
            }
        }
    }
});

export const {loadUsers, loadUsersFailed, loadingUsers} = userSlice.actions;
export default userSlice.reducer;
