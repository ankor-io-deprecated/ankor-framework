var testUrl = "wss://ankor-todo-sample.irian.at/websocket/ankor";

function avg(responseTimes, n) {
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

function test1(socket, start, responseTimes) {
    //console.log("Test..");

    var id = null;
    socket.onmessage = function (e) {
        var responseTime = new Date().getTime() - start;
        if (id == null) {
            id = e.data;
            socket.send('{"senderId":"' + id + '","modelId":"' + id + '","messageId":"' + id + '#0","property":"root","action":"init"}');
        } else {
            var res = JSON.parse(e.data);
            if (res.hasOwnProperty("property") && res.property == "root") {
                // successfully initialized
                responseTimes.push(responseTime);
            }
        }
    };
}

onmessage = function (e) {
    //console.log("Received command from master");
    //button.setAttribute("disabled", "true");
    var data = JSON.parse(e.data);

    var i;
    var n = data.n; // number of simulated clients

    var sockets = [];
    for (i = 0; i < n; i++) {
        sockets.push(new WebSocket(testUrl));
    }

    // Test 1
    var responseTimes = [];
    var start = new Date().getTime();
    for (i = 0; i < n; i++) {
        test1(sockets[i], start, responseTimes);
    }

    setTimeout(function () {
        //console.log("Time over, sending report to master");

        var res = {
            type: "report",
            failures: 0,
            avg: 0,
            std: 0
        };

        res.failures = n - responseTimes.length;
        res.avg = avg(responseTimes, responseTimes.length);
        res.std = Math.sqrt(variance(responseTimes, responseTimes.length, res.avg));

        for (var i = 0; i < n; i++) {
            sockets[i].close();
        }

        postMessage(JSON.stringify(res));

    }, 5000);
};

