import React from 'react';
import {render} from '@testing-library/react';
import App from './App';
import {Provider} from "react-redux";
import configureStore from 'redux-mock-store';
import {MemoryRouter} from "react-router-dom";

const mockStore = configureStore([]);

describe('App should render something', () => {

    let store;
    let component;

    beforeEach(() => {
        store = mockStore({
            auth: {admin: false, loggedIn: false, canBook: false, canViewMoney: false}
        });

        component = render(
            <Provider store={store}>
                <MemoryRouter>
                    <App/>
                </MemoryRouter>
            </Provider>
        );
    });

    it('should show a login button', () => {
        const {getByText} = component;
        const linkElement = getByText(/Login/i);
        expect(linkElement).toBeInTheDocument();
    });
});
