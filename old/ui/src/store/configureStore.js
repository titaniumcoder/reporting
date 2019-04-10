if (process.env.NODE_ENV === 'production-once-ready') {
    module.exports = require('./configureStore.prod');
} else {
    module.exports = require('./configureStore.dev');
}
