import {configureStore} from '@reduxjs/toolkit'
import rootReducer from './rootReducer'

const store = configureStore({
    reducer: rootReducer
});

// @ts-ignore
if (process.env.NODE_ENV === 'development' && module.hot) {
    // @ts-ignore
    module.hot.accept('./rootReducer', () => {
        const newRootReducer = require('./rootReducer').default;
        store.replaceReducer(newRootReducer)
    })
}
export type AppDispatch = typeof store.dispatch

export default store
