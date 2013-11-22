(function () {
    var path = location.host + "/performance/master";
    if (window.location.protocol == 'http:') {
        path = 'ws://' + path;
    } else {
        path = 'wss://' + path;
    }
    var master = new WebSocket(path);
    var worker = new Worker("/performance/js/worker.js");
    var button = document.getElementById("button");

    var startHeartbeat = function(socket) {
        var heartbeat = function () {
            console.log("\u2665-beat");
            socket.send("");

            setTimeout(heartbeat, 25000);
        };

        console.log("Starting heartbeat");
        setTimeout(heartbeat, self.heartbeatInterval);
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

    worker.onmessage = function (e) {
        console.log(e.data);
        if (typeof(e.data) == "object") {
            button.removeAttribute("disabled");
            master.send(JSON.stringify(e.data));
        }
    };

    master.onmessage = function (e) {
        var data = JSON.parse(e.data);
        if (data.hasOwnProperty("type") && data.type == "WorkLoad") {
            button.setAttribute("disabled", "true");
            document.getElementById("report").innerHTML = "";
            worker.postMessage(data);
        } else if (data.hasOwnProperty("type") && data.type == "OverallReport") {
            document.getElementById("report").innerHTML = data.numClients + '  clients (' + data.numSimulatedClients +
                ' simulated): Avg: ' + data.avg + 'ms, Std: ' + data.std + 'ms, 90% of requests are below: ' +
                data.quartile90 + 'ms, Max: ' + data.max + 'ms, Failures: ' + data.failures;
        } else if (data.hasOwnProperty("numClients")) {
            // num clients update
            var span = document.getElementById("num-clients");
            span.innerHTML = data["numClients"];
        }
    };
})();
