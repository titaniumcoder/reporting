const startOfMonth = moment().startOf('month');
const endOfMonth = moment().endOf('month');

const app = new Vue({
        el: '#app',
        data: {
            dateFrom: startOfMonth.format('YYYY-MM-DD'),
            dateTo: endOfMonth.format('YYYY-MM-DD'),
            message: 'Hello Spring!',
            excelAvailable: false,
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
            updatecash: function () {
                fetch('/api/cash')
                    .then(res => res.json())
                    .then(res => {
                        this.cashout = res.filter(x => x.amount !== 0);
                        this.totalCashout = res.map(x => x.amount).reduce((acc, r) => acc + r)
                    });

                this.casher = setInterval(() => {
                    fetch('/api/cash')
                        .then(res => res.json())
                        .then(res => {
                            this.cashout = res;
                            this.totalCashout = res.map(x => x.amount).reduce((acc, r) => acc + r)
                        })
                }, 30000);
            },
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
            tagBilled: function (id) {
                console.log(`Flagging ${id} to billed`);
            },
            untagBilled: function () {
                console.log(`Flagging ${id} to unbilled`);
            },
            switchClient: function (id) {
                this.activeClient = id;
                const selectedClient = this.clients[id];
                fetch(`/api/client/${selectedClient.id}?from=${this.dateFrom}&to=${this.dateTo}`)
                    .then(res => res.json())
                    .then(res => {
                        this.timesheet = res.timeEntries;
                        this.projects = res.projects;
                    });
            },
            updateDates: function() {
                if (this.activeClient !== null) {
                    fetch(`/api/client/${selectedClient.id}?from=${this.dateFrom}&to=${this.dateTo}`)
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
    })
;
