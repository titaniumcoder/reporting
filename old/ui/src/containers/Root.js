import React from 'react'
import PropTypes from 'prop-types'
import {Provider} from 'react-redux'
import {Route} from 'react-router-dom'
import App from './App'
import ClientPage from './ClientPage'

const Root = ({store}) => (
    <Provider store={store}>
        <div>
            <Route path="/" component={App}/>
            <Route path="/:client" component={ClientPage}/>
        </div>
    </Provider>
);

Root.propTypes = {
    store: PropTypes.object.isRequired,
};
export default Root
