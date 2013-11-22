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
        return '{"senderId":"' + id + '","modelId":"' + id + '","messageId":"' + id + '#4","property":"root.model.tasks[0].numCompleted","change":{"type":"value","key":null,"value":true}}';
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

var hasProperty = function (res, path) {
    return res.hasOwnProperty("property") && res.property == path
};

var checkResponse = function (res, paths) {
    for (var i = 0; i < paths.length; i++) {
        if (hasProperty(res, paths[i])) {
            return true
        }
    }
    return false;
};

var numCompleted;
function test(socket, responseTimes) {
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
                postMessage("Client " + numCompleted + " completed step " + step);
                step++;
                if (step < requests.length) {
                    countdown = responseRefs[step].length; // expecting 5 return messages
                    start = new Date().getTime();
                    socket.send(requests[step](id));
                } else {
                    postMessage("Client " + numCompleted + " completed sequence");
                    numCompleted++;
                    socket.close();
                }
            }
        }
    };
}

var average = function (responseTimes, n) {
    var sum = 0;
    for (var i = 0; i < n; i++) {
        sum += responseTimes[i];
    }
    return sum / n;
};

var variance = function (responseTimes, n, avg) {
    if (n == 1) return 0;

    var sum = 0;
    var term;
    for (var i = 0; i < n; i++) {
        term = responseTimes[i] - avg;
        sum += term * term; // ^2
    }
    return sum / (n - 1);
};

onmessage = function (e) {
    //console.log("Received command from master");
    var data = e.data;

    var i;
    var n = data.n; // number of simulated clients
    var rampUpRate = data.rampUpRate;

    var sockets = [];
    var responseTimes = [];

    i = 0;
    var rampUp = function () {
        if (i < n) {
            sockets.push(new WebSocket(testUrl));
            test(sockets[i], responseTimes);
            postMessage("Start client nr " + i);
            setTimeout(rampUp, rampUpRate);
            i++;
        }
    };

    numCompleted = 0;
    rampUp();

    setTimeout(function () {
        var res = {
            type: "Report",
            failures: n - numCompleted,
            avg: 0,
            std: 0,
            responseTimes: responseTimes
        };

        res.avg = average(responseTimes, responseTimes.length);
        res.std = Math.sqrt(variance(responseTimes, responseTimes.length, res.avg));

        postMessage(res);

    }, rampUpRate * n + 5000);
};

