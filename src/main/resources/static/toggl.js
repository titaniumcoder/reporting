function formatMinutes(minutes) {
    const hours = Math.floor(minutes / 60);
    const remaining = minutes % 60;
    return hours + ':' + remaining.toLocaleString('de-CH', {minimumIntegerDigits: 2, maximumFractionDigits: 0})
}

const formatDecimal = (minutes) => (minutes / 60.0).toLocaleString('de-CH', {
    maximumFractionDigits: 2,
    minimumFractionDigits: 2
});

const app = new Vue({
    el: '#app',
    data: {
        message: 'Hello Spring!',
        excelAvailable: false
    },
    methods: {
        prepare: function () {
            console.log('Preparing Excel Sheet');

            this.excelAvailable = true;
        },
        download: function () {
            console.log('Downloading Excel Sheet');

            this.excelAvailable = false;
        },
        tagRange: function () {
            console.log('Flagging current service and date range to billed');
        },
        untagRange: function () {
            console.log('Flagging current service and date range to non-billed');
        },
        switchClient: function(id) {
            console.log('Switching to Client: ' + id);
        }
    }
});
