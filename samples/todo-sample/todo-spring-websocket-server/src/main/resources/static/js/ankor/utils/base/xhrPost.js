define(function() {
    return function(url, data, cb) {
        var dataString = "";
        for (var key in data) {
            if (!data.hasOwnProperty(key)) {
                continue;
            }
            if (dataString.length != 0) {
                dataString += "&";
            }
            dataString += encodeURIComponent(key);
            dataString += "=";
            dataString += encodeURIComponent(data[key]);
        }

        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState != 4) {
                return;
            }
            if (!xhr.response) {
                cb(new Error("xhr error"));
            }
            else {
                cb(null, xhr.response);
            }
        };
        xhr.open("POST", url);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.send(dataString);
    };
});