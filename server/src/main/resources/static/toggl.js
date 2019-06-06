const startOfMonth = moment().startOf('month');
const endOfMonth = moment().endOf('month');

Vue.filter('currency', function (value) {
    if (typeof value !== "number") {
        return value;
    }
    const formatter = new Intl.NumberFormat('de-CH', {
        style: 'currency',
        currency: 'CHF',
        minimumFractionDigits: 0
    });
    return formatter.format(value);
});

const app = new Vue({
    el: '#app',
    data: {
        dateFrom: startOfMonth.format('YYYY-MM-DD'),
        dateTo: endOfMonth.format('YYYY-MM-DD'),
        message: 'Hello Spring!',
        excel: null,
        clients: [{id: 1, name: 'A'}, {id: 2, name: 'B'}],
        activeClient: null,
        timesheet: null,
        projects: null,
        cashout: null,
        totalCashout: 0,
        casher: null
    },
    created() {
        fetch('/api/clients')
            .then(res => res.json())
            .then(res => {
                this.clients = res;
            });
        this.updatecash();
    },
    destroyed() {
        clearInterval(this.casher);
    },
    methods: {
        handleFetchError: function (res) {
            if (!res.ok) {
                throw Error(res.statusText);
            }
            return res;
        },
        updatecash: function () {
            this.fetchCash();

            this.casher = setInterval(() => this.fetchCash(), 30000);
        },
        fetchCash: function () {
            const s = this;

            fetch('/api/cash')
                .then(this.handleFetchError)
                .then(res => res.json())
                .then(res => {
                    if (res) {
                        s.cashout = res;
                        s.totalCashout = res.map(x => x.amount).reduce((acc, r) => acc + r)
                    } else {
                        s.cashout = null;
                        s.totalCashout = 0;
                    }
                })
        },
        prepare: function () {
            const s = this;

            if (this.activeClient) {
                const selectedClient = this.clients[this.activeClient];
                fetch(`/api/timesheet/${selectedClient.id}?from=${this.dateFrom}&to=${this.dateTo}`)
                    .then(this.handleFetchError)
                    .then(res => res.blob())
                    .then(res => {
                        s.excel = window.URL.createObjectURL(res);
                    });
            }

        },
        download: function () {
            this.excel = null;
        },
        tagRange: function () {
            if (this.activeClient !== null) {
                const selectedClient = this.clients[this.activeClient];
                fetch(`/api/client/${selectedClient.id}/billed?from=${this.dateFrom}&to=${this.dateTo}`, {
                    method: 'PUT',
                    credentials: 'include'

                })
                    .then(this.handleFetchError)
                    .then(() => {
                        this.updateDates();
                    });
            }
        },
        untagRange: function () {
            if (this.activeClient !== null) {
                const selectedClient = this.clients[this.activeClient];
                fetch(`/api/client/${selectedClient.id}/billed?from=${this.dateFrom}&to=${this.dateTo}`, {
                    method: 'DELETE',
                    credentials: 'include'

                })
                    .then(this.handleFetchError)
                    .then(() => {
                        this.updateDates();
                    });
            }
        },
        tagBilled: function (id) {
            fetch(`/api/tag/${id}`, {method: 'PUT', credentials: 'include'})
                .then(this.handleFetchError)
                .then(() => {
                    this.updateDates();
                })
        },
        untagBilled: function (id) {
            fetch(`/api/tag/${id}`, {method: 'DELETE', credentials: 'include'})
                .then(this.handleFetchError)
                .then(() => {
                    this.updateDates();
                })
        },
        switchClient: function (id) {
            this.activeClient = id;
            const selectedClient = this.clients[id];
            const s = this;

            fetch(`/api/client/${selectedClient.id}?from=${this.dateFrom}&to=${this.dateTo}`)
                .then(this.handleFetchError)
                .then(res => res.json())
                .then(res => {
                    s.timesheet = res.timeEntries;
                    s.projects = res.projects;
                });
        },
        updateDates: function () {
            if (this.activeClient !== null) {
                const selectedClient = this.clients[this.activeClient];
                fetch(`/api/client/${selectedClient.id}?from=${this.dateFrom}&to=${this.dateTo}`)
                    .then(this.handleFetchError)
                    .then(res => res.json())
                    .then(res => {
                        this.timesheet = res.timeEntries;
                        this.projects = res.projects;
                    });
            }
        },
        formatMinutes: function (minutes) {
            const hours = Math.floor(minutes / 60);
            const remaining = minutes % 60;
            return hours + ':' + remaining.toLocaleString('de-CH', {minimumIntegerDigits: 2, maximumFractionDigits: 0})
        },
        formatDecimal: function (minutes) {
            return (minutes / 60.0).toLocaleString('de-CH', {
                maximumFractionDigits: 2,
                minimumFractionDigits: 2
            })
        },
        formatDate: function (d) {
            return moment(d).format('DD.MM.YYYY');
        },
        formatTime: function (d) {
            return moment(d).format('HH:mm');
        },
        formatDayMinutes: function (day) {
            const sum = day.map(x => x.minutes).reduce((acc, c) => acc + c);
            return this.formatMinutes(sum)
        },
        formatDayDecimal: function (day) {
            const sum = day.map(x => x.minutes).reduce((acc, c) => acc + c);
            return this.formatDecimal(sum)
        }
    }
});
