(function () {
    var master = new WebSocket("ws://localhost:8080/master");
    var worker = new Worker("/js/worker.js");
    var button = document.getElementById("button");

    master.onopen = function (e) {
        console.log("Minion waiting for command");

        button.addEventListener("click", function () {
            console.log("Requesting test from master");
            button.setAttribute("disabled", "true");
            master.send("test 1");

        });

        window.onbeforeunload = function () {
            master.close();
        }
    };

    worker.onmessage = function (e) {
        button.removeAttribute("disabled");
        master.send(e.data);
    };

    master.onmessage = function (e) {
        console.log("Received command from master");
        button.setAttribute("disabled", "true");
        worker.postMessage(e.data);
    };
})();
