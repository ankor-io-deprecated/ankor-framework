(function () {
    var path = location.host + "/performance/master";
    if (window.location.protocol == 'http:') {
        path = 'ws://' + path;
    } else {
        path = 'wss://' + path;
    }
    var master = new WebSocket(path);

    var button = document.getElementById("button");

    var startHeartbeat = function (socket) {
        var heartbeat = function () {
            console.log("\u2665-beat");
            socket.send("");
            setTimeout(heartbeat, 5000);
        };

        console.log("Starting heartbeat");
        heartbeat();
    };

    master.onopen = function () {
        console.log("Minion waiting for command");

        button.addEventListener("click", function () {
            console.log("Requesting test from master");
            button.setAttribute("disabled", "true");
            var n = document.getElementById("num-simulated-clients").value;
            var rampUpRate = document.getElementById("ramp-up-rate").value;
            master.send(JSON.stringify({type: "WorkLoad", n: n, rampUpRate: rampUpRate}));

        });

        window.onbeforeunload = function () {
            master.close();
        };

        startHeartbeat(master);
    };

    master.onmessage = function (e) {
        var data = JSON.parse(e.data);
        if (data.hasOwnProperty("type") && data.type == "WorkLoad") {
            button.setAttribute("disabled", "true");
            document.getElementById("report").innerHTML = "";
            startTest(data);
        } else if (data.hasOwnProperty("type") && data.type == "OverallReport") {
            button.removeAttribute("disabled");
            document.getElementById("report").innerHTML = data.numClients + '  clients (' + data.numSimulatedClients +
                ' simulated): Avg: ' + data.avg + 'ms, Std: ' + data.std + 'ms, 90% of requests are below: ' +
                data.quartile90 + 'ms, Max: ' + data.max + 'ms, Failures: ' + data.failures;
        } else if (data.hasOwnProperty("numClients")) {
            // num clients update
            var span = document.getElementById("num-clients");
            span.innerHTML = data["numClients"];
        }
    };

    var testUrl = "wss://ankor-todo-sample.irian.at/websocket/ankor";

    var requests = [
        function (id) {
            return '{"senderId":"' + id + '","modelId":"' + id + '","messageId":"' + id + '#0","property":"root","action":"init"}';
        },
        function (id) {
            return '{"senderId":"' + id + '","modelId":"' + id + '","messageId":"' + id + '#1","property":"root.model","action":{"name":"newTask","params":{"title":"1"}}}';
        },
        function (id) {
            return '{"senderId":"' + id + '","modelId":"' + id + '","messageId":"' + id + '#2","property":"root.model","action":{"name":"newTask","params":{"title":"2"}}}';
        },
        function (id) {
            return '{"senderId":"' + id + '","modelId":"' + id + '","messageId":"' + id + '#3","property":"root.model","action":{"name":"newTask","params":{"title":"3"}}}';
        },
        function (id) {
            return '{"senderId":"' + id + '","modelId":"' + id + '","messageId":"' + id + '#4","property":"root.model.tasks[0].completed","change":{"type":"value","key":null,"value":true}}';
        },
        function (id) {
            return '{"senderId":"' + id + '","modelId":"' + id + '","messageId":"' + id + '#5","property":"root.model","action":"clearTasks"}';
        }
    ];

    var responseRefs = [
        ["root"],
        ["root.model.itemsLeft", "root.model.tasks", "root.model.footerVisibility", "root.model.itemsLeftText"],
        ["root.model.itemsLeft", "root.model.tasks", "root.model.itemsLeftText"],
        ["root.model.itemsLeft", "root.model.tasks"],
        ["root.model.itemsLeft", "root.model.itemsComplete", "root.model.clearButtonVisibility", "root.model.itemsCompleteText"],
        ["root.model.tasks", "root.model.itemsComplete", "root.model.clearButtonVisibility", "root.model.itemsCompleteText"]
    ];

    function hasProperty(res, path) {
        return res.hasOwnProperty("property") && res.property == path
    }

    function checkResponse(res, paths) {
        for (var i = 0; i < paths.length; i++) {
            if (hasProperty(res, paths[i])) {
                return true
            }
        }
        return false;
    }

    var completed;

    function runTest(socket, responseTimes) {
        var id = null;
        var step = 0;
        var countdown = 0;
        var start;
        socket.onmessage = function (e) {
            if (id == null) {
                id = e.data;
                // postMessage("id received " + id)
                countdown = responseRefs[0].length;
                start = new Date().getTime();
                socket.send(requests[0](id));
            } else {
                var responseTime = new Date().getTime() - start;
                var res = JSON.parse(e.data);
                if (checkResponse(res, responseRefs[step])) {
                    countdown--;
                    responseTimes.push(responseTime);
                }
                if (countdown == 0) {
                    console.log("Number " + completed + " completed step " + step);
                    step++;
                    if (step < requests.length) {
                        countdown = responseRefs[step].length; // expecting 5 return messages
                        start = new Date().getTime();
                        socket.send(requests[step](id));
                    } else {
                        console.log("Number " + completed + " completed sequence");
                        completed++;
                        socket.close();
                    }
                }
            }
        };
    }

    function average(responseTimes, n) {
        var sum = 0;
        for (var i = 0; i < n; i++) {
            sum += responseTimes[i];
        }
        return sum / n;
    }

    function variance(responseTimes, n, avg) {
        if (n == 1) return 0;

        var sum = 0;
        var term;
        for (var i = 0; i < n; i++) {
            term = responseTimes[i] - avg;
            sum += term * term; // ^2
        }
        return sum / (n - 1);
    }

    function startTest(data) {
        var i;
        var n = data.n; // number of simulated clients
        var rampUpRate = data.rampUpRate;

        var sockets = [];
        var responseTimes = [];

        i = 0;
        var rampUp = function () {
            if (i < n) {
                sockets.push(new WebSocket(testUrl));
                runTest(sockets[i], responseTimes);
                console.log("Start client nr " + i);
                setTimeout(rampUp, rampUpRate);
                i++;
            }
        };

        completed = 0;
        rampUp();

        setTimeout(function () {
            var res = {
                type: "Report",
                failures: n - completed,
                avg: 0,
                std: 0,
                responseTimes: responseTimes
            };

            res.avg = average(responseTimes, responseTimes.length);
            res.std = Math.sqrt(variance(responseTimes, responseTimes.length, res.avg));

            //postMessage(res);
            console.log(res);
            master.send(JSON.stringify(res));

        }, (rampUpRate + 500) * n); // XXX
    }
})();
