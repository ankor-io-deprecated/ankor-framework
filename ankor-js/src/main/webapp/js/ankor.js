(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        //Allow using this built library as an AMD module
        //in another project. That other project will only
        //see this AMD call, not the internal modules in
        //the closure below.
        define([], factory);
    } else {
        //Browser globals case. Just assign the
        //result to a property on the global.
        root.ankor = factory();
    }
}(this, function () {
//almond, and your modules will be inlined here
/**
 * @license almond 0.3.0 Copyright (c) 2011-2014, The Dojo Foundation All Rights Reserved.
 * Available via the MIT or new BSD license.
 * see: http://github.com/jrburke/almond for details
 */
//Going sloppy to avoid 'use strict' string cost, but strict practices should
//be followed.
/*jslint sloppy: true */
/*global setTimeout: false */

var requirejs, require, define;
(function (undef) {
    var main, req, makeMap, handlers,
        defined = {},
        waiting = {},
        config = {},
        defining = {},
        hasOwn = Object.prototype.hasOwnProperty,
        aps = [].slice,
        jsSuffixRegExp = /\.js$/;

    function hasProp(obj, prop) {
        return hasOwn.call(obj, prop);
    }

    /**
     * Given a relative module name, like ./something, normalize it to
     * a real name that can be mapped to a path.
     * @param {String} name the relative name
     * @param {String} baseName a real name that the name arg is relative
     * to.
     * @returns {String} normalized name
     */
    function normalize(name, baseName) {
        var nameParts, nameSegment, mapValue, foundMap, lastIndex,
            foundI, foundStarMap, starI, i, j, part,
            baseParts = baseName && baseName.split("/"),
            map = config.map,
            starMap = (map && map['*']) || {};

        //Adjust any relative paths.
        if (name && name.charAt(0) === ".") {
            //If have a base name, try to normalize against it,
            //otherwise, assume it is a top-level require that will
            //be relative to baseUrl in the end.
            if (baseName) {
                //Convert baseName to array, and lop off the last part,
                //so that . matches that "directory" and not name of the baseName's
                //module. For instance, baseName of "one/two/three", maps to
                //"one/two/three.js", but we want the directory, "one/two" for
                //this normalization.
                baseParts = baseParts.slice(0, baseParts.length - 1);
                name = name.split('/');
                lastIndex = name.length - 1;

                // Node .js allowance:
                if (config.nodeIdCompat && jsSuffixRegExp.test(name[lastIndex])) {
                    name[lastIndex] = name[lastIndex].replace(jsSuffixRegExp, '');
                }

                name = baseParts.concat(name);

                //start trimDots
                for (i = 0; i < name.length; i += 1) {
                    part = name[i];
                    if (part === ".") {
                        name.splice(i, 1);
                        i -= 1;
                    } else if (part === "..") {
                        if (i === 1 && (name[2] === '..' || name[0] === '..')) {
                            //End of the line. Keep at least one non-dot
                            //path segment at the front so it can be mapped
                            //correctly to disk. Otherwise, there is likely
                            //no path mapping for a path starting with '..'.
                            //This can still fail, but catches the most reasonable
                            //uses of ..
                            break;
                        } else if (i > 0) {
                            name.splice(i - 1, 2);
                            i -= 2;
                        }
                    }
                }
                //end trimDots

                name = name.join("/");
            } else if (name.indexOf('./') === 0) {
                // No baseName, so this is ID is resolved relative
                // to baseUrl, pull off the leading dot.
                name = name.substring(2);
            }
        }

        //Apply map config if available.
        if ((baseParts || starMap) && map) {
            nameParts = name.split('/');

            for (i = nameParts.length; i > 0; i -= 1) {
                nameSegment = nameParts.slice(0, i).join("/");

                if (baseParts) {
                    //Find the longest baseName segment match in the config.
                    //So, do joins on the biggest to smallest lengths of baseParts.
                    for (j = baseParts.length; j > 0; j -= 1) {
                        mapValue = map[baseParts.slice(0, j).join('/')];

                        //baseName segment has  config, find if it has one for
                        //this name.
                        if (mapValue) {
                            mapValue = mapValue[nameSegment];
                            if (mapValue) {
                                //Match, update name to the new value.
                                foundMap = mapValue;
                                foundI = i;
                                break;
                            }
                        }
                    }
                }

                if (foundMap) {
                    break;
                }

                //Check for a star map match, but just hold on to it,
                //if there is a shorter segment match later in a matching
                //config, then favor over this star map.
                if (!foundStarMap && starMap && starMap[nameSegment]) {
                    foundStarMap = starMap[nameSegment];
                    starI = i;
                }
            }

            if (!foundMap && foundStarMap) {
                foundMap = foundStarMap;
                foundI = starI;
            }

            if (foundMap) {
                nameParts.splice(0, foundI, foundMap);
                name = nameParts.join('/');
            }
        }

        return name;
    }

    function makeRequire(relName, forceSync) {
        return function () {
            //A version of a require function that passes a moduleName
            //value for items that may need to
            //look up paths relative to the moduleName
            var args = aps.call(arguments, 0);

            //If first arg is not require('string'), and there is only
            //one arg, it is the array form without a callback. Insert
            //a null so that the following concat is correct.
            if (typeof args[0] !== 'string' && args.length === 1) {
                args.push(null);
            }
            return req.apply(undef, args.concat([relName, forceSync]));
        };
    }

    function makeNormalize(relName) {
        return function (name) {
            return normalize(name, relName);
        };
    }

    function makeLoad(depName) {
        return function (value) {
            defined[depName] = value;
        };
    }

    function callDep(name) {
        if (hasProp(waiting, name)) {
            var args = waiting[name];
            delete waiting[name];
            defining[name] = true;
            main.apply(undef, args);
        }

        if (!hasProp(defined, name) && !hasProp(defining, name)) {
            throw new Error('No ' + name);
        }
        return defined[name];
    }

    //Turns a plugin!resource to [plugin, resource]
    //with the plugin being undefined if the name
    //did not have a plugin prefix.
    function splitPrefix(name) {
        var prefix,
            index = name ? name.indexOf('!') : -1;
        if (index > -1) {
            prefix = name.substring(0, index);
            name = name.substring(index + 1, name.length);
        }
        return [prefix, name];
    }

    /**
     * Makes a name map, normalizing the name, and using a plugin
     * for normalization if necessary. Grabs a ref to plugin
     * too, as an optimization.
     */
    makeMap = function (name, relName) {
        var plugin,
            parts = splitPrefix(name),
            prefix = parts[0];

        name = parts[1];

        if (prefix) {
            prefix = normalize(prefix, relName);
            plugin = callDep(prefix);
        }

        //Normalize according
        if (prefix) {
            if (plugin && plugin.normalize) {
                name = plugin.normalize(name, makeNormalize(relName));
            } else {
                name = normalize(name, relName);
            }
        } else {
            name = normalize(name, relName);
            parts = splitPrefix(name);
            prefix = parts[0];
            name = parts[1];
            if (prefix) {
                plugin = callDep(prefix);
            }
        }

        //Using ridiculous property names for space reasons
        return {
            f: prefix ? prefix + '!' + name : name, //fullName
            n: name,
            pr: prefix,
            p: plugin
        };
    };

    function makeConfig(name) {
        return function () {
            return (config && config.config && config.config[name]) || {};
        };
    }

    handlers = {
        require: function (name) {
            return makeRequire(name);
        },
        exports: function (name) {
            var e = defined[name];
            if (typeof e !== 'undefined') {
                return e;
            } else {
                return (defined[name] = {});
            }
        },
        module: function (name) {
            return {
                id: name,
                uri: '',
                exports: defined[name],
                config: makeConfig(name)
            };
        }
    };

    main = function (name, deps, callback, relName) {
        var cjsModule, depName, ret, map, i,
            args = [],
            callbackType = typeof callback,
            usingExports;

        //Use name if no relName
        relName = relName || name;

        //Call the callback to define the module, if necessary.
        if (callbackType === 'undefined' || callbackType === 'function') {
            //Pull out the defined dependencies and pass the ordered
            //values to the callback.
            //Default to [require, exports, module] if no deps
            deps = !deps.length && callback.length ? ['require', 'exports', 'module'] : deps;
            for (i = 0; i < deps.length; i += 1) {
                map = makeMap(deps[i], relName);
                depName = map.f;

                //Fast path CommonJS standard dependencies.
                if (depName === "require") {
                    args[i] = handlers.require(name);
                } else if (depName === "exports") {
                    //CommonJS module spec 1.1
                    args[i] = handlers.exports(name);
                    usingExports = true;
                } else if (depName === "module") {
                    //CommonJS module spec 1.1
                    cjsModule = args[i] = handlers.module(name);
                } else if (hasProp(defined, depName) ||
                           hasProp(waiting, depName) ||
                           hasProp(defining, depName)) {
                    args[i] = callDep(depName);
                } else if (map.p) {
                    map.p.load(map.n, makeRequire(relName, true), makeLoad(depName), {});
                    args[i] = defined[depName];
                } else {
                    throw new Error(name + ' missing ' + depName);
                }
            }

            ret = callback ? callback.apply(defined[name], args) : undefined;

            if (name) {
                //If setting exports via "module" is in play,
                //favor that over return value and exports. After that,
                //favor a non-undefined return value over exports use.
                if (cjsModule && cjsModule.exports !== undef &&
                        cjsModule.exports !== defined[name]) {
                    defined[name] = cjsModule.exports;
                } else if (ret !== undef || !usingExports) {
                    //Use the return value from the function.
                    defined[name] = ret;
                }
            }
        } else if (name) {
            //May just be an object definition for the module. Only
            //worry about defining if have a module name.
            defined[name] = callback;
        }
    };

    requirejs = require = req = function (deps, callback, relName, forceSync, alt) {
        if (typeof deps === "string") {
            if (handlers[deps]) {
                //callback in this case is really relName
                return handlers[deps](callback);
            }
            //Just return the module wanted. In this scenario, the
            //deps arg is the module name, and second arg (if passed)
            //is just the relName.
            //Normalize module name, if it contains . or ..
            return callDep(makeMap(deps, callback).f);
        } else if (!deps.splice) {
            //deps is a config object, not an array.
            config = deps;
            if (config.deps) {
                req(config.deps, config.callback);
            }
            if (!callback) {
                return;
            }

            if (callback.splice) {
                //callback is an array, which means it is a dependency list.
                //Adjust args if there are dependencies
                deps = callback;
                callback = relName;
                relName = null;
            } else {
                deps = undef;
            }
        }

        //Support require(['a'])
        callback = callback || function () {};

        //If relName is a function, it is an errback handler,
        //so remove it.
        if (typeof relName === 'function') {
            relName = forceSync;
            forceSync = alt;
        }

        //Simulate async callback;
        if (forceSync) {
            main(undef, deps, callback, relName);
        } else {
            //Using a non-zero value because of concern for what old browsers
            //do, and latest browsers "upgrade" to 4 if lower value is used:
            //http://www.whatwg.org/specs/web-apps/current-work/multipage/timers.html#dom-windowtimers-settimeout:
            //If want a value immediately, use require('id') instead -- something
            //that works in almond on the global level, but not guaranteed and
            //unlikely to work in other AMD implementations.
            setTimeout(function () {
                main(undef, deps, callback, relName);
            }, 4);
        }

        return req;
    };

    /**
     * Just drops the config on the floor, but returns req in case
     * the config return value is used.
     */
    req.config = function (cfg) {
        return req(cfg);
    };

    /**
     * Expose module registry for debugging and tooling
     */
    requirejs._defined = defined;

    define = function (name, deps, callback) {

        //This module may not have dependencies
        if (!deps.splice) {
            //deps is not an array, so probably means
            //an object literal or factory function for
            //the value. Adjust args.
            callback = deps;
            deps = [];
        }

        if (!hasProp(defined, name) && !hasProp(waiting, name)) {
            waiting[name] = [name, deps, callback];
        }
    };

    define.amd = {
        jQuery: true
    };
}());

define("almond.js", function(){});

define('ankor/events/BaseEvent',[],function() {
    var BaseEvent = function(path, eventSource) {
        this.path = path;
        this.eventSource = eventSource;
    };

    return BaseEvent;
});
define('ankor/events/ActionEvent',[
    "./BaseEvent"
], function(BaseEvent) {
    var ActionEvent = function(path, eventSource, actionName, params) {
        BaseEvent.call(this, path, eventSource);

        this.actionName = actionName;
        this.params = params;
    };

    ActionEvent.prototype = new BaseEvent();

    return ActionEvent;
});
define('ankor/events/ChangeEvent',[
    "./BaseEvent"
], function(BaseEvent) {
    var ChangeEvent = function(path, eventSource, type, key, value) {
        BaseEvent.call(this, path, eventSource);

        this.type = type;
        this.key = key;
        this.value = value;
    };

    ChangeEvent.prototype = new BaseEvent();

    ChangeEvent.prototype.TYPE = ChangeEvent.TYPE = {
        VALUE: "value",
        INSERT: "insert",
        DEL: "delete",
        REPLACE: "replace"
    };

    return ChangeEvent;
});
define('ankor/transport/Message',[],function() {
    var Message = function(event) {
        this.event = event || null;
    };

    return Message;
});
define('ankor/PathSegment',[],function() {
    var PathSegment = function(key, type) {
        //Validate type
        var typeValid = false;
        for (var t in PathSegment.TYPE) {
            if (PathSegment.TYPE.hasOwnProperty(t) && type === PathSegment.TYPE[t]) {
                typeValid = true;
                break;
            }
        }
        if (!typeValid) {
            throw new Error("Illegal PathSegment type");
        }

        //Validate key
        if ((this.isProperty() && (!key || typeof key != "string")) || (this.isIndex() && typeof key != "number")) {
            throw new Error("Illegal PathSegment key");
        }

        //Init
        this.key = key;
        this.type = type;
    };

    PathSegment.TYPE = {
        PROPERTY: 0,
        INDEX: 1
    };

    PathSegment.prototype.isProperty = function() {
        return this.type === PathSegment.TYPE.PROPERTY;
    };

    PathSegment.prototype.isIndex = function() {
        return this.type === PathSegment.TYPE.INDEX;
    };

    return PathSegment;
});

define('ankor/Path',[
    "./PathSegment"
], function (PathSegment) {
    var parseSegments = function (pathString) {
        var segments = [];
        var pathSegments = pathString.split(".");
        for (var i = 0, pathSegment; (pathSegment = pathSegments[i]); i++) {
            var keyIndex = pathSegment.indexOf("[");
            if (keyIndex == -1) {
                segments.push(new PathSegment(pathSegment, PathSegment.TYPE.PROPERTY));
            }
            else {
                var propertyName = pathSegment.substr(0, keyIndex);
                var keys = pathSegment.substring(keyIndex + 1, pathSegment.length - 1).split("][");

                segments.push(new PathSegment(propertyName, PathSegment.TYPE.PROPERTY));

                for (var j = 0, key; (key = keys[j]); j++) {
                    if (key.indexOf("'") != -1 || key.indexOf('"') != -1) {
                        segments.push(new PathSegment(key.substring(1, key.length - 1), PathSegment.TYPE.PROPERTY));
                    }
                    else {
                        segments.push(new PathSegment(parseInt(key), PathSegment.TYPE.INDEX));
                    }
                }
            }
        }
        return segments;
    };

    var Path = function (pathOrSegments) {
        this.segments = [];

        if (pathOrSegments instanceof Array) {
            this.segments = pathOrSegments;
        }
        else if (typeof pathOrSegments == "string") {
            this.segments = parseSegments(pathOrSegments);
        }
    };

    Path.prototype.toString = function () {
        var path = "";
        for (var i = 0, segment; (segment = this.segments[i]); i++) {
            if (segment.type === PathSegment.TYPE.PROPERTY) {
                if (i != 0) {
                    path += ".";
                }
                path += segment.key;
            }
            else if (segment.type === PathSegment.TYPE.INDEX) {
                path += "[" + segment.key + "]";
            }
        }
        return path;
    };

    Path.prototype.append = function (path) {
        var segments = [];

        if (path instanceof Path) {
            segments = path.segments;
        }
        else if (path instanceof Array) {
            segments = path;
        }
        else if (typeof path == "string") {
            segments = parseSegments(path);
        }

        return new Path(this.segments.concat(segments));
    };

    Path.prototype.appendIndex = function (index) {
        var segments = this.segments.slice(0);
        segments.push(new PathSegment(index, PathSegment.TYPE.INDEX));
        return new Path(segments);
    };

    Path.prototype.parent = function () {
        return new Path(this.segments.slice(0, -1));
    };

    Path.prototype.root = function () {
        return new Path([this.segments[0]]);
    };

    Path.prototype.propertyName = function () {
        return this.segments[this.segments.length - 1].key;
    };

    Path.prototype.equals = function (path) {
        return this.toString() == path.toString();
    };

    Path.prototype.slice = function (startIndex, lastIndex) {
        return new Path(this.segments.slice(startIndex, lastIndex));
    };

    return Path;
});

define('ankor/transport/BaseTransport',[
    "./Message",
    "../Path",
    "../events/BaseEvent",
    "../events/ChangeEvent",
    "../events/ActionEvent"
], function(Message, Path, BaseEvent, ChangeEvent, ActionEvent) {
    var BaseTransport = function() {
        this.outgoingMessages = [];
        this.connected = false;
    };

    BaseTransport.prototype.onConnectionChange = function(connected) {};

    BaseTransport.prototype.init = function(ankorSystem) {
        this.ankorSystem = ankorSystem;
        this.utils = ankorSystem.utils;

        if (this.onConnectionChange) this.onConnectionChange(this.connected);
    };

    BaseTransport.prototype.sendEvent = function(event) {
        var message = new Message(event);
        this.sendMessage(message);
    };

    BaseTransport.prototype.sendMessage = function(message) {

        var stateProps = this.ankorSystem.stateProps;
        if (stateProps) {
            message.stateValues = {};
            for (var i = 0, stateProp; (stateProp = stateProps[i]); i++) {
                message.stateValues[stateProp] = this.ankorSystem.model.getValue(new Path(stateProp));
            }
        }

        this.outgoingMessages.push(message);
        if (this.ankorSystem.debug) {
            message.event.pathString = message.event.path.toString();
            console.log("OUT", message);
        }
    };

    BaseTransport.prototype.receiveMessage = function(message) {
        if (this.ankorSystem.debug) {
            message.event.pathString = message.event.path.toString();
            console.log("IN", message);
        }
        this.ankorSystem.onIncomingEvent(message.event);
    };

    BaseTransport.prototype.decodeMessage = function(parsedJson) {
        var event = null;
        var path = new Path(parsedJson.property);
        
        if (parsedJson.change) {
            event = new ChangeEvent(path, "ankorRemoteEvent", parsedJson.change.type, parsedJson.change.key, parsedJson.change.value);
        }
        else if (parsedJson.action) {
            if (parsedJson.action instanceof Object) {
                event = new ActionEvent(path, "ankorRemoteEvent", parsedJson.action.name, parsedJson.action.params);
            }
            else {
                event = new ActionEvent(path, "ankorRemoteEvent", parsedJson.action, null);
            }
        }
        else {
            event = new BaseEvent(path, "ankorRemoteEvent");
        }
        
        var message = new Message(event);
        
        // unexpected side effects
        if (parsedJson.stateProps && true /* TODO: Check if message is newer */ ) {
            this.ankorSystem.stateProps = message.stateProps = parsedJson.stateProps;
        }

        return message;
    };

    BaseTransport.prototype.encodeMessage = function(message) {
        var event = message.event;
        var jsonMessage = {
            property: event.path.toString()
        };
        
        if (message.stateValues) {
            jsonMessage.stateValues = message.stateValues;
        }
        
        if (event instanceof ActionEvent) {
            jsonMessage.action = {
                name: event.actionName,
                params: event.params
            };
        }
        else if (event instanceof ChangeEvent) {
            jsonMessage.change = {
                type: event.type,
                key: event.key,
                value: event.value
            };
        }
        else if (message.connectParams) {
            jsonMessage.connectParams = message.connectParams;
        }
        return jsonMessage;
    };

    return BaseTransport;
});

define('ankor/transport/HttpPollingTransport',[
    "./BaseTransport"
], function(BaseTransport) {
    var HttpPollingTransport = function(endpoint, options) {
        BaseTransport.call(this);

        this.endpoint = endpoint;
        this.inFlight = null;
        this.pollingInterval = 100;
        if (options && options.pollingInterval != undefined) {
            this.pollingInterval = options.pollingInterval;
        }
    };
    HttpPollingTransport.prototype = new BaseTransport();

    HttpPollingTransport.prototype.init = function(ankorSystem) {
        BaseTransport.prototype.init(ankorSystem);
        this.sendTimer = setTimeout(this.utils.hitch(this, "processOutgoingMessages"), this.pollingInterval);
    };

    HttpPollingTransport.prototype.sendMessage = function(message) {
        BaseTransport.prototype.sendMessage.call(this, message);

        if (!this.inFlight) {
            this.processOutgoingMessages();
        }
    };

    HttpPollingTransport.prototype.processOutgoingMessages = function() {
        clearTimeout(this.sendTimer);
        this.inFlight = this.outgoingMessages;
        this.outgoingMessages = [];

        //Build JSON of messages
        var jsonMessages = [];
        for (var i = 0, message; (message = this.inFlight[i]); i++) {
            jsonMessages.push(this.encodeMessage(message));
        }

        //Ajax request
        this.utils.xhrPost(this.endpoint, {
            clientId: this.ankorSystem.senderId,
            messages: this.utils.jsonStringify(jsonMessages)
        }, this.utils.hitch(this, function(err, response) {
            try {
                var parsedMessages = this.utils.jsonParse(response);
            }
            catch (e) {
                err = e;
            }
            if (err) {
                if (this.ankorSystem.debug) {
                    console.log("Ankor HttpPollingTransport Error", err);
                }
                this.outgoingMessages = this.inFlight.concat(this.outgoingMessages);

                if (this.connected) {
                    this.connected = false;
                    if (this.onConnectionChange) this.onConnectionChange(this.connected);
                }
            }
            else {
                for (var i = 0, parsedMessage; (parsedMessage = parsedMessages[i]); i++) {
                    var message = this.decodeMessage(parsedMessage);
                    this.receiveMessage(message);
                }

                if (!this.connected) {
                    this.connected = true;
                    if (this.onConnectionChange) this.onConnectionChange(this.connected);
                }
            }
            this.inFlight = null;
            this.sendTimer = setTimeout(this.utils.hitch(this, "processOutgoingMessages"), this.pollingInterval);
        }));
    };

    return HttpPollingTransport;
});

define('ankor/transport/WebSocketTransport',[
    "./BaseTransport",
    "./Message"
], function (BaseTransport, Message) {
    var WebSocketTransport = function (endpointUri, options) {
        BaseTransport.call(this);

        this.options = options || {};
        this.endpointUri = endpointUri;
        this.endpoint = null;
        this.socket = null;
        this.reconnecting = false;

        this._heartbeatInterval = this.options.heartbeatInterval || 5000;
        this._heartbeatTimer = null;
        this._reconnectBackoff = this.options.reconnectBackoff || 250;
        this._reconnectDelay = this.reconnectBackoff;
        this._reconnectMaxDelay = this.options.reconnectMaxDelay || 5000;
    };

    WebSocketTransport.prototype = new BaseTransport();

    WebSocketTransport.prototype.init = function (ankorSystem) {
        BaseTransport.prototype.init(ankorSystem);

        this.endpoint = this.endpointUri + "/" + ankorSystem.senderId;
        if (this.ankorSystem.debug) {
            console.log("endpoint:", this.endpoint);
        }

        this._connect();
        window.onbeforeunload = this.utils.hitch(this, "_disconnect");
    };

    WebSocketTransport.prototype.sendMessage = function (message) {
        BaseTransport.prototype.sendMessage.call(this, message);

        if (this.connected) {
            this._sendPendingMessages();
        }
    };

    WebSocketTransport.prototype._host = function() {
        var path = window.location.host + this.endpoint;

        if (window.location.protocol == 'http:') {
            path = 'ws://' + path;
        } else {
            path = 'wss://' + path;
        }

        return path;
    };

    WebSocketTransport.prototype._webSocket = function(host) {
        if ("WebSocket" in window) {
            return new WebSocket(host);
        }
        return null;
    };

    WebSocketTransport.prototype._connect = function() {
        this._disconnect();

        this.socket = this._webSocket(this._host(this.endpoint));

        if (this.socket) {
            this.socket.onopen = this.utils.hitch(this, "_onSocketOpen");
            this.socket.onclose = this.utils.hitch(this, "_onSocketClose");
            this.socket.onmessage = this.utils.hitch(this, "_onSocketMessage");
            this.socket.onerror = this.utils.hitch(this, "_onSocketError");
        }
    };

    WebSocketTransport.prototype._reconnect = function() {
        this._disconnect();

        if (this.reconnecting) {
            this._reconnectDelay = Math.min(this._reconnectDelay + this._reconnectBackoff, this._reconnectMaxDelay);
        }
        else {
            this.reconnecting = true;
            this._reconnectDelay = this._reconnectBackoff;
        }

        setTimeout(this.utils.hitch(this, function() {
            if (this.ankorSystem.debug) {
                console.log("WebSocket trying to reconnect (Delay = " + this._reconnectDelay + ")");
            }
            this._connect();
        }), this._reconnectDelay);
    };

    WebSocketTransport.prototype._disconnect = function() {
        this._stopHeartbeat();
        this.connected = false;
        if (this.onConnectionChange) this.onConnectionChange(this.connected);

        if (this.socket) {
            this.socket.onopen = null;
            this.socket.onclose = null;
            this.socket.onmessage = null;
            this.socket.onerror = null;
            this.socket.close();
            this.socket = null;
        }
    };

    WebSocketTransport.prototype._onSocketOpen = function() {
        this.connected = true;
        this.reconnecting = false;
        this._startHeartbeat();
        if (this.onConnectionChange) this.onConnectionChange(this.connected);

        var connectMsg = new Message({
            path: this.options.connectProperty
        });
        connectMsg.connectParams = this.options.connectParams;
        this._sendMessage(connectMsg);
        this._sendPendingMessages();
    };

    WebSocketTransport.prototype._onSocketClose = function() {
        this._reconnect();
    };

    WebSocketTransport.prototype._onSocketMessage = function(msg) {
        var message = this.decodeMessage(this.utils.jsonParse(msg.data));
        this.receiveMessage(message);
    };

    WebSocketTransport.prototype._onSocketError = function(err) {
        if (this.ankorSystem.debug) {
            console.log("WebSocketTransport onError", err);
        }

        this._reconnect();
    };

    WebSocketTransport.prototype._sendPendingMessages = function() {
        if (this.connected) {
            for (var i = 0, message; (message = this.outgoingMessages[i]); i++) {
                this._sendMessage(message);
            }
            this.outgoingMessages = [];
        }
    };

    WebSocketTransport.prototype._sendMessage = function(message) {
        var jsonMessage = this.utils.jsonStringify(this.encodeMessage(message));
        this.socket.send(jsonMessage);
    };

    WebSocketTransport.prototype._startHeartbeat = function() {
        if (!this._heartbeatTimer) {
            this._heartbeatTimer = setTimeout(this.utils.hitch(this, "_onHeartbeat"), this._heartbeatInterval);
        }
    };

    WebSocketTransport.prototype._stopHeartbeat = function() {
        if (this._heartbeatTimer) {
            clearTimeout(this._heartbeatTimer);
            this._heartbeatTimer = null;
        }
    };

    WebSocketTransport.prototype._onHeartbeat = function() {
        if (this.socket) {
            if (this.ankorSystem.debug) {
                console.log("\u2665-beat");
            }
            this.socket.send("");
            this._heartbeatTimer = setTimeout(this.utils.hitch(this, "_onHeartbeat"), this._heartbeatInterval);
        }
    };

    return WebSocketTransport;
});

define('ankor/utils/base/uuid',[],function() {
    return function() {
        //UUIDv4 generator from dojox/uuid
        var HEX_RADIX = 16;

        function _generateRandomEightCharacterHexString(){
            // Make random32bitNumber be a randomly generated floating point number
            // between 0 and (4,294,967,296 - 1), inclusive.
            var random32bitNumber = Math.floor( (Math.random() % 1) * Math.pow(2, 32) );
            var eightCharacterHexString = random32bitNumber.toString(HEX_RADIX);
            while(eightCharacterHexString.length < 8){
                eightCharacterHexString = "0" + eightCharacterHexString;
            }
            return eightCharacterHexString; // for example: "3B12F1DF"
        }

        var hyphen = "-";
        var versionCodeForRandomlyGeneratedUuids = "4"; // 8 == binary2hex("0100")
        var variantCodeForDCEUuids = "8"; // 8 == binary2hex("1000")
        var a = _generateRandomEightCharacterHexString();
        var b = _generateRandomEightCharacterHexString();
        b = b.substring(0, 4) + hyphen + versionCodeForRandomlyGeneratedUuids + b.substring(5, 8);
        var c = _generateRandomEightCharacterHexString();
        c = variantCodeForDCEUuids + c.substring(1, 4) + hyphen + c.substring(4, 8);
        var d = _generateRandomEightCharacterHexString();
        var returnValue = a + hyphen + b + hyphen + c + d;
        returnValue = returnValue.toLowerCase();
        return returnValue; // String
    };
});
define('ankor/utils/base/hitch',[],function() {
    return function(scope, method) {
        if (typeof method == "string") {
            method = scope[method];
        }
        return function() {
            return method.apply(scope, arguments || []);
        };
    };
});
define('ankor/utils/base/jsonParse',[],function() {
    return function(jsonString) {
        if (typeof JSON != "undefined") {
            return JSON.parse(jsonString)
        }
        else {
            return eval("(" + jsonString + ")");
        }
    };
});

define('ankor/utils/base/jsonStringify',[],function() {
    return function(jsonObject) {
        if (typeof JSON != "undefined") {
            return JSON.stringify((jsonObject));
        }
        else {
            var escapeString = function(str){
                return ('"' + str.replace(/(["\\])/g, '\\$1') + '"').
                    replace(/[\f]/g, "\\f").replace(/[\b]/g, "\\b").replace(/[\n]/g, "\\n").
                    replace(/[\t]/g, "\\t").replace(/[\r]/g, "\\r"); // string
            };
            var stringify = function(it){
                var val, objtype = typeof it;
                if(objtype == "number"){
                    return isFinite(it) ? it + "" : "null";
                }
                if(objtype == "boolean"){
                    return it + "";
                }
                if(it === null){
                    return "null";
                }
                if(typeof it == "string"){
                    return escapeString(it);
                }
                if(it instanceof Array){
                    var itl = it.length, res = [];
                    for(var key = 0; key < itl; key++){
                        var obj = it[key];
                        val = stringify(obj, key);
                        if(typeof val != "string"){
                            val = "null";
                        }
                        res.push(val);
                    }
                    return "[" + res.join(",") + "]";
                }
                var output = [];
                for(var key in it){
                    var keyStr;
                    if(it.hasOwnProperty(key)){
                        if(typeof key == "number"){
                            keyStr = '"' + key + '"';
                        }else if(typeof key == "string"){
                            keyStr = escapeString(key);
                        }else{
                            // skip non-string or number keys
                            continue;
                        }
                        val = stringify(it[key], key);
                        if(typeof val != "string"){
                            // skip non-serializable values
                            continue;
                        }
                        // At this point, the most non-IE browsers don't get in this branch
                        // (they have native JSON), so push is definitely the way to
                        output.push(keyStr + ":" + val);
                    }
                }
                return "{" + output.join(",") + "}"; // String
            };
            return stringify(jsonObject);
        }
    };
});
define('ankor/utils/base/xhrPost',[],function() {
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
define('ankor/utils/BaseUtils',[
    "./base/uuid",
    "./base/hitch",
    "./base/jsonParse",
    "./base/jsonStringify",
    "./base/xhrPost",
], function(uuid, hitch, jsonParse, jsonStringify, xhrPost) {
    return function() {
        this.uuid = uuid;
        this.hitch = hitch;
        this.jsonParse = jsonParse;
        this.jsonStringify = jsonStringify;
        this.xhrPost = xhrPost;
    };
});

define('ankor/ListenerRegistry',[],function() {
    var listenerCounter = 0;

    var ListenerRegistry = function(ankorSystem) {
        this.ankorSystem = ankorSystem;
        this.propListeners = {
            children: {},
            listeners: {},
            ref: this.ankorSystem.getRef("")
        };
        this.treeListeners = {
            children: {},
            listeners: {},
            ref: this.ankorSystem.getRef("")
        };
        this.actionListeners = {
            children: {},
            listeners: {},
            ref: this.ankorSystem.getRef("")
        };
    };

    ListenerRegistry.prototype.addListener = function(type, path, cb) {
        var listeners = null;
        if (type == "propChange") {
            listeners = this._resolveListenersForPath(this.propListeners, path);
        } else if (type == "treeChange") {
            listeners = this._resolveListenersForPath(this.treeListeners, path);
        } else if (type == "action") {
            listeners = this._resolveListenersForPath(this.actionListeners, path);
        }

        var listenerId = "#" + listenerCounter++;
        listeners.listeners[listenerId] = cb;
        return {
            remove: function() {
                delete listeners.listeners[listenerId];
                //Todo: Maybe clean up listener trees that no longer have any listeners...
            }
        };
    };

    ListenerRegistry.prototype.triggerListeners = function(path, event) {
        //Trigger treeChangeListeners
        var id, listeners, listener;

        listeners = this._resolveListenersForPath(this.treeListeners, path);
        while (listeners) {
            //Trigger listeners on this level
            //console.log("Firing TREE ", listeners.ref.path());
            for (id in listeners.listeners) {
                if (!listeners.listeners.hasOwnProperty(id)) {
                    continue;
                }
                listener = listeners.listeners[id];
                listener(listeners.ref, event);
            }

            //Go up one level
            var currentPath = listeners.ref.path;
            listeners = null;
            if (currentPath.segments.length > 0) {
                listeners = this._resolveListenersForPath(this.treeListeners, currentPath.parent())
            }
        }

        //Trigger propChangeListeners
        var listenersToTrigger = [
            this._resolveListenersForPath(this.propListeners, path)
        ];
        var pathsToCleanup = {};
        var first = true;
        while (listenersToTrigger.length > 0) {
            listeners = listenersToTrigger.shift();
            if (!listeners.ref.isValid()) {
                var refToCleanup = listeners.ref.parent();
                while (!refToCleanup.isValid()) {
                    refToCleanup = refToCleanup.parent();
                }
                pathsToCleanup[refToCleanup.path.toString()] = refToCleanup.path;
                continue;
            }

            //Trigger listeners on this level
            //console.log("Firing PROP ", listeners.ref.path());
            for (id in listeners.listeners) {
                if (!listeners.listeners.hasOwnProperty(id)) {
                    continue;
                }
                listener = listeners.listeners[id];
                listener(listeners.ref, event);
            }

            //Add child listeners to the listenersToTriggerList
            if (!first || event.type == event.TYPE.VALUE) {
                //Propagate to all children if it's a VALUE change event or if it's not the first level
                for (var childName in listeners.children) {
                    if (!listeners.children.hasOwnProperty(childName)) {
                        continue;
                    }
                    listenersToTrigger.push(listeners.children[childName]);
                }
            }
            else {
                //Otherwise (if first) only notify affected children for INSERT and REPLACE, and nobody for DEL (invalid ref anyways)
                var key;
                if (event.type == event.TYPE.INSERT) {
                    key = event.key.toString();
                    if (key in listeners.children) {
                        listenersToTrigger.push(listeners.children[key]);
                    }
                }
                else if (event.type == event.TYPE.REPLACE) {
                    var index = event.key;
                    for (var i = 0; i < event.value.length; i++) {
                        key = (index + i).toString();
                        if (key in listeners.children) {
                            listenersToTrigger.push(listeners.children[key]);
                        }
                    }
                }
            }

            first = false;
        }

        //Cleanup found invalid listeners
        for (var pathString in pathsToCleanup) {
            if (!pathsToCleanup.hasOwnProperty(pathString)) {
                continue;
            }
            this.removeInvalidListeners(pathsToCleanup[pathString]);
        }
    };

    ListenerRegistry.prototype.triggerActionListeners = function(path, event) {
        var id, listeners, listener;
        listeners = this._resolveListenersForPath(this.actionListeners, path);
        for (id in listeners.listeners) {
            listener = listeners.listeners[id];
            listener(listeners.ref, event);
        }
    };

    ListenerRegistry.prototype.removeInvalidListeners = function(path) {
        //Removes all listeners that are descendants of the given path that are no longer valid (for a ref that points nowhere)
        var filterObsoleteListeners = function(listeners) {
            //Check every child if it's still valid
            var segmentString,
                segmentStrings = [];
            for (segmentString in listeners.children) {
                if (!listeners.children.hasOwnProperty(segmentString)) {
                    continue;
                }
                segmentStrings.push(segmentString);
            }
            for (var i = 0; (segmentString = segmentStrings[i]); i++) {
                var childListeners = listeners.children[segmentString];
                if (childListeners.ref.isValid()) {
                    filterObsoleteListeners(childListeners);
                }
                else {
                    //console.log("Invalidating Listener", childListeners.ref.path());
                    delete listeners.children[segmentString];
                }
            }
        };

        filterObsoleteListeners(this._resolveListenersForPath(this.propListeners, path));
        filterObsoleteListeners(this._resolveListenersForPath(this.treeListeners, path));
    };
    ListenerRegistry.prototype._resolveListenersForPath = function(listeners, path) {
        var resolvedListeners = listeners;
        var currentRef = listeners.ref;
        for (var i = 0, segment; (segment = path.segments[i]); i++) {
            if (segment.isProperty()) {
                currentRef = currentRef.append(segment.key);
            }
            else if (segment.isIndex()) {
                currentRef = currentRef.appendIndex(segment.key);
            }

            var key = segment.key.toString();
            if (!(key in resolvedListeners.children)) {
                resolvedListeners.children[key] = {
                    children: {},
                    listeners: {},
                    ref: currentRef
                }
            }
            resolvedListeners = resolvedListeners.children[key];
        }
        return resolvedListeners;
    };

    return ListenerRegistry;
});

define('ankor/ModelInterface',[],function() {
    var ModelInterface = function() {};

    ModelInterface.prototype.getValue = function(path) {
        throw new Error("ModelWrapper.getValue not implemented");
    };

    ModelInterface.prototype.setValue = function(path, value) {
        throw new Error("ModelWrapper.setValue not implemented");
    };

    ModelInterface.prototype.isValid = function(path) {
        throw new Error("ModelWrapper.isValid not implemented");
    };

    ModelInterface.prototype.del = function(path) {
        throw new Error("ModelWrapper.del not implemented");
    };

    ModelInterface.prototype.insert = function(path, index, value) {
        throw new Error("ModelWrapper.insert not implemented");
    };

    ModelInterface.prototype.size = function(path) {
        throw new Error("ModelWrapper.size not implemented");
    };

    return ModelInterface;
});

define('ankor/BigCacheController',[],function() {
    var BigCacheController = function(model, options) {
        this.model = model;
        this.size = 1000;
        this.order = [];
        this.indexMode = false; //Set to true if cache keys are numeric indices (different treatment)

        if (options && "size" in options) {
            this.size = options.size;
        }
        if (options && options.indexMode) {
            this.indexMode = true;
        }
    };

    BigCacheController.prototype._indexOf = function(key) {
        var index = -1;
        if (Array.prototype.indexOf) {
            index = this.order.indexOf(key);
        }
        else {
            for (var i = 0; i < this.order.length; i++) {
                if (this.order[i] == key) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    };

    BigCacheController.prototype.add = function(key) {
        if (this._indexOf(key) == -1) {
            this.order.push(key);
        }
    };

    BigCacheController.prototype.insert = function(key) {
        if (!this.indexMode) {
            throw new Error("BigCacheController.insert only available when in indexMode");
        }

        var newOrder = [];
        for (var i = 0; i < this.order.length; i++) {
            var cacheKey = this.order[i];
            if (cacheKey < key) {
                newOrder.push(cacheKey);
            }
            else {
                newOrder.push(cacheKey + 1);
            }
        }
        this.order = newOrder;
        this.add(key);
    };

    BigCacheController.prototype.remove = function(key) {
        var newOrder = [];
        for (var i = 0; i < this.order.length; i++) {
            var cacheKey = this.order[i];

            //INDEX MODE
            if (this.indexMode) {
                if (cacheKey < key) {
                    newOrder.push(cacheKey);
                }
                else if (cacheKey > key) {
                    newOrder.push(cacheKey - 1); //Decrement all indexes by one that are bigger than the removed key
                }
            }
            //KEY MODE
            else {
                if (cacheKey != key) {
                    newOrder.push(cacheKey);
                }
            }
        }
        this.order = newOrder;
    };

    BigCacheController.prototype.touch = function(key) {
        //Find index of key in order array
        var index = this._indexOf(key);
        if (index == -1) {
            return;
        }

        //Remove index from order array
        this.order.splice(index, 1);

        //Re-add at back
        this.order.push(key);
    };

    BigCacheController.prototype.cleanup = function() {
        while (this.order.length > this.size) {
            var key = this.order.shift();
            delete this.model.model[key];
        }
    };

    return BigCacheController;
});

define('ankor/BigList',[
    "./ModelInterface",
    "./Path",
    "./BigCacheController"
], function(ModelInterface, Path, BigCacheController) {
    var BigList = function(config, model) {
        this.model = model;
        this.cacheController = new BigCacheController(model, {
            indexMode: true
        });

        this._loadTimer = null;
        this._loadQueue = [];
        this._loadPending = {};

        //Parse/Init config
        this._size = config["@size"];
        this._chunk = config["@chunk"];
        this._substitute = config["@subst"];

        for (var i = 0; i < config["@init"].length; i++) {
            this.setValue(new Path("").appendIndex(i), config["@init"][i]);
        }
    };

    BigList.prototype = new ModelInterface();

    BigList.prototype.getValue = function(path) {
        //console.log("BIGLIST getValue", this.model.baseRef.path.append(path).toString());

        if (path.segments.length == 0) {
            var tempArray = [];
            for (var i = 0; i < this._size; i++) {
                tempArray.push(this.getValue([{ type: "index", key: i }]));
            }
            return tempArray;
        }
        else {
            var key = path.segments[0].key;

            if (key >= this._size) {
                throw new Error("Index out of bounds");
            }

            //Put substitute into requested model place if not loaded yet... (and remove it after the check)
            var tempSubstitute = false;
            if (!(key in this.model.model)) {
                this.model.model[key] = this._substitute;
                tempSubstitute = true;
                this._requestMissing(key);
            }
            else {
                this.cacheController.touch(key);
            }

            //Run model.getValue
            var value = this.model.getValue(path);

            //Delete substitute isValid was run against subst
            if (tempSubstitute) {
                delete this.model.model[key];
            }

            //Return result
            return value;
        }
    };

    BigList.prototype.setValue = function(path, value) {
        //console.log("BIGLIST setValue", this.model.baseRef.path.append(path).toString(), value);

        //Throw an error if this is a set on a subproperty for not loaded elements
        var key = path.segments[0].key;
        if (path.segments.length > 1 && !(key in this.model.model)) {
            //throw  new Error("Can't set on BigList when item not loaded");
            return; //Silently ignore
        }

        //Update internal state
        if (path.segments.length == 1) {
            //Update cache
            if (!(key in this.model.model)) {
                this.cacheController.add(key);
            }
            else {
                this.cacheController.touch(key);
            }

            //Update _loadPending
            delete this._loadPending[key];
        }

        //Set value in model
        this.model.setValue(path, value);

        //Clean up cache
        this.cacheController.cleanup();
    };

    BigList.prototype.isValid = function(path) {
        //console.log("BIGLIST isValid", this.model.baseRef.append(pathSegments).path());

        if (path.segments.length == 0) {
            //isValid for BigList itself -> obviously true
            return true;
        }
        else {
            //Put substitute into requested model place if not loaded yet... (and remove it after the check)
            var key = path.segments[0].key;
            var tempSubstitute = false;
            if (!(key in this.model.model)) {
                this.model.model[key] = this._substitute;
                tempSubstitute = true;
            }

            //Run model.isValid
            var valid = this.model.isValid(path);

            //Delete substitute isValid was run against subst
            if (tempSubstitute) {
                delete this.model.model[key];
            }

            //Return result
            return valid;
        }
    };

    BigList.prototype.del = function(path) {
        //console.log("BIGLIST del", this.model.baseRef.append(pathSegments).path());

        var key = parseInt(path.segments[0].key);

        if (path.segments.length == 1) {
            //Remove item from model if currently cached
            if (key in this.model.model) {
                delete this.model.model[key];
            }

            //Update size
            this._size--;

            //Update indices of @model
            var newModel = {};
            for (var modelKey in this.model.model) {
                if (!this.model.model.hasOwnProperty(modelKey)) {
                    continue;
                }

                var newKey = parseInt(modelKey);
                if (newKey > key) {
                    newKey--;
                }
                newModel[newKey] = this.model.model[modelKey];
            }
            this.model.model = newModel;

            //Update indices of @_loadQueue
            var newLoadQueue = [];
            for (var i = 0; i < this._loadQueue.length; i++) {
                var loadKey = this._loadQueue[i];
                if (loadKey < key) {
                    newLoadQueue.push(loadKey);
                }
                else if (loadKey > key) {
                    newLoadQueue.push(loadKey - 1);
                }
            }
            this._loadQueue = newLoadQueue;

            //Update indices of cache
            this.cacheController.remove(key);
        }
        else if (key in this.model.model) {
            //If del for a subpath and item is currently cached -> delegate to model
            this.model.del(path);
        }
    };

    BigList.prototype.insert = function(path, index, value) {
        //console.log("BIGLIST insert", this.model.baseRef.append(pathSegments).path(), index, value);

        index = parseInt(index);

        if (path.segments.length == 0) {
            //Update size
            this._size++;

            //Update indices of @model
            var newModel = {};
            for (var modelKey in this.model.model) {
                if (!this.model.model.hasOwnProperty(modelKey)) {
                    continue;
                }

                var newKey = parseInt(modelKey);
                if (newKey >= index) {
                    newKey++;
                }
                newModel[newKey] = this.model.model[modelKey];
            }
            this.model.model = newModel;

            //Update indices of @_loadQueue
            var newLoadQueue = [];
            for (var i = 0; i < this._loadQueue.length; i++) {
                var loadKey = this._loadQueue[i];
                if (loadKey < index) {
                    newLoadQueue.push(loadKey);
                }
                else if (loadKey >= index) {
                    newLoadQueue.push(loadKey + 1);
                }
            }
            this._loadQueue = newLoadQueue;

            //Update indices of cache
            this.cacheController.insert(index);

            //Insert new item
            this.setValue(new Path("").appendIndex(index), value);
        }
        else if (path.segments[0].key in this.model.model) {
            this.model.insert(path, index, value);
        }
    };

    BigList.prototype.size = function(path) {
        if (path.segments.length == 0) {
            return this._size;
        }
        else {
            return this.model.size(path);
        }
    };

    BigList.prototype._requestMissing = function(index) {
        if (this._loadPending[index]) {
            return;
        }

        this._loadQueue.push(index);
        this._loadPending[index] = true;
        if (this._loadTimer) {
            clearTimeout(this._loadTimer);
        }

        var self = this;
        this._loadTimer = setTimeout(function() {
            //Calc indices that really have to be loaded based on chunk size
            var i;
            var indicesToLoad = [];
            var lastAddedIndex = null;

            //Sort by numerical index
            self._loadQueue.sort(function(lhs, rhs) {
                return lhs - rhs;
            });

            //Step through queued indices from min to max and check if last indicesToLoad entry covers current index with given chunk size
            for (i = 0; i < self._loadQueue.length; i++) {
                var index = self._loadQueue[i];
                if (lastAddedIndex == null || index > lastAddedIndex + self._chunk - 1) {
                    lastAddedIndex = index;
                    indicesToLoad.push(index);
                    for (var j = 0; j < self._chunk; j++) {
                        self._loadPending[index + j] = true;
                    }
                }
            }

            //Send actions for indicesToLoad
            for (i = 0; i < indicesToLoad.length; i++) {
                self.model.baseRef.appendIndex(indicesToLoad[i]).fire("@missingProperty");
            }

            //Reset loadQueue & timer
            self._loadQueue = [];
            self._loadTimer = null;
        }, 100);
    };

    return BigList;
});

define('ankor/BigMap',[
    "./ModelInterface",
    "./Path",
    "./BigCacheController"
], function(ModelInterface, Path, BigCacheController) {
    var BigMap = function(init, model) {
        this.model = model;
        this.cacheController = new BigCacheController(model);

        this._loadTimer = null;
        this._loadPending = {};

        //Parse/Init config
        this._size = init["@size"];
        this._substitute = init["@subst"];

        for (var key in init) {
            if (!init.hasOwnProperty(key) || key == "@size" || key == "@subst") {
                continue;
            }
            this.setValue(new Path("").append(key), init[key]);
        }
    };

    BigMap.prototype = new ModelInterface();

    BigMap.prototype.getValue = function(path) {
        //console.log("BIGMAP getValue", this.model.baseRef.path.append(path).toString());

        if (path.segments.length == 0) {
            return this.model.getValue(path);
        }
        else {
            var key = path.segments[0].key;

            //Put substitute into requested model place if not loaded yet... (and remove it after the check)
            var tempSubstitute = false;
            if (!(key in this.model.model)) {
                this.model.model[key] = this._substitute;
                tempSubstitute = true;
                this._requestMissing(key);
            }
            else {
                this.cacheController.touch(key);
            }

            //Run model.getValue
            var value = this.model.getValue(path);

            //Delete substitute if getValue was run against subst
            if (tempSubstitute) {
                delete this.model.model[key];
            }

            //Return result
            return value;
        }
    };

    BigMap.prototype.setValue = function(path, value) {
        //console.log("BIGMAP setValue", this.model.baseRef.path.append(path).toString(), value);

        //Throw an error if this is a set on a subproperty for not loaded elements
        var key = path.segments[0].key;
        if (path.segments.length > 1 && !(key in this.model.model)) {
            //throw  new Error("Can't set on BigMap when item not loaded");
            return; //Silently ignore
        }

        //Update internal state
        if (path.segments.length == 1) {
            //Update cache
            if (!(key in this.model.model)) {
                this.cacheController.add(key);
            }
            else {
                this.cacheController.touch(key);
            }

            //Update _loadPending
            delete this._loadPending[key];
        }

        //Set value in model
        this.model.setValue(path, value);

        //Clean up cache
        this.cacheController.cleanup();
    };

    BigMap.prototype.isValid = function(path) {
        //console.log("BIGMAP isValid", this.model.baseRef.path.append(path).toString());

        if (path.segments.length == 0 || path.segments.length == 1) {
            //segments.length == 0 -> isValid for BigMap itself is obviously true
            //segments.length == 1 -> Potentially any key could be contained in the BigMap, so return true as well...
            return true;
        }
        else {
            //Put substitute into requested model place if not loaded yet... (and remove it after the check)
            var key = path.segments[0].key;
            var tempSubstitute = false;
            if (!(key in this.model.model)) {
                this.model.model[key] = this._substitute;
                tempSubstitute = true;
            }

            //Run model.isValid
            var valid = this.model.isValid(path);

            //Delete substitute isValid was run against subst
            if (tempSubstitute) {
                delete this.model.model[key];
            }

            //Return result
            return valid;
        }
    };

    BigMap.prototype.del = function(path) {
        //console.log("BIGMAP del", this.model.baseRef.path.append(path).toString());

        var key = path.segments[0].key;

        if (path.segments.length == 1) {
            //Remove item from model if currently cached
            if (key in this.model.model) {
                delete this.model.model[key];
            }

            //Update size
            this._size--;

            //Remove from cache
            this.cacheController.remove(key);
        }
        else if (key in this.model.model) {
            //If del for a subpath and item is currently cached -> delegate to model
            this.model.del(path);
        }
    };

    BigMap.prototype.insert = function(path, index, value) {
        //console.log("BIGMAP insert", this.model.baseRef.path.append(path).toString(), index, value);

        if (path.segments.length == 0) {
            throw new Error("Insert only works for Arrays");
        }
        else if (path.segments[0].key in this.model.model) {
            this.model.insert(path, index, value);
        }
    };

    BigMap.prototype.size = function(path) {
        if (path.segments.length == 0) {
            return this._size;
        }
        else {
            return this.model.size(path);
        }
    };

    BigMap.prototype._requestMissing = function(key) {
        if (this._loadPending[key]) {
            return;
        }

        this._loadPending[key] = true;
        this.model.baseRef.append(key).fire("@missingProperty");
    };

    return BigMap;
});

define('ankor/Model',[
  "./ModelInterface",
  "./BigList",
  "./BigMap"
], function (ModelInterface, BigList, BigMap) {

  //Class that holds a model, and applies basic operations with a given path (in segments as used in Ref)
  var Model = function (baseRef) {
    this.baseRef = baseRef;
    this.model = {};
  };

  Model.prototype = new ModelInterface();

  Model.prototype.getValue = function (path) {
    //Resolve value in model (or delegate to ModelInterface if encountered during path resolving)
    var value = this.model;
    for (var i = 0, segment; (segment = path.segments[i]); i++) {
      if (value != undefined) {
        value = value[segment.key];
      }

      //Check for embedded ModelInterface and delegate
      if (value instanceof ModelInterface) {
        return value.getValue(path.slice(i + 1));
      }
    }
    return value;
  };

  Model.prototype.setValue = function (path, value) {
    //Resolve parent (and delegate set if ModelInterface is found along the way)
    var parentPath = path.parent();
    var parentModel = this.model;
    for (var i = 0, segment; (segment = parentPath.segments[i]); i++) {
      parentModel = parentModel[segment.key];

      //Check current parentValue
      if (parentModel === undefined || parentModel === null) {
        throw new Error("setValue encountered undefined or null in path");
      }
      //Check for embedded ModelInterface and delegate
      else if (parentModel instanceof ModelInterface) {
        parentModel.setValue(path.slice(i + 1), value);
        return;
      }
    }

    //We now have a parentModel (and haven't delegated the set), so apply the value (recursively)
    applyValue.call(this, parentModel, path.propertyName(), value, path);
  };

  Model.prototype.isValid = function (path) {
    var valid = true;
    var value = this.model;

    for (var i = 0, segment; (segment = path.segments[i]); i++) {
      //If parent is null (and therefore the current child can't exist) or child is undefined, then set valid to false
      if (value === null || value[segment.key] === undefined) {
        valid = false;
        break;
      }

      //Set new (current) value
      value = value[segment.key];

      //Check for embedded ModelInterface and delegate
      if (value instanceof ModelInterface) {
        return value.isValid(path.slice(i + 1));
      }
    }

    return valid;
  };

  Model.prototype.del = function (path) {
    //Resolve parent (and delegate del if ModelInterface is found along the way)
    var parentPath = path.parent();
    var parentModel = this.model;
    for (var i = 0, segment; (segment = parentPath.segments[i]); i++) {
      parentModel = parentModel[segment.key];

      //Check current parentValue
      if (parentModel === undefined || parentModel === null) {
        throw new Error("del encountered undefined or null in path");
      }
      //Check for embedded ModelInterface and delegate
      else if (parentModel instanceof ModelInterface) {
        parentModel.del(path.slice(i + 1));
        return;
      }
    }

    //We haven't delegated to ModelInterface, so now remove the value
    var lastSegment = path.segments[path.segments.length - 1];
    if (parentModel instanceof Array) {
      parentModel.splice(lastSegment.key, 1);
    }
    else {
      delete parentModel[lastSegment.key];
    }
  };

  Model.prototype.insert = function (path, index, insertValue) {
    var value = this.model;
    for (var i = 0, segment; (segment = path.segments[i]); i++) {
      if (value != undefined) {
        value = value[segment.key];
      }

      //Check for embedded ModelInterface and delegate
      if (value instanceof ModelInterface) {
        value.insert(path.slice(i + 1), index, insertValue);
        return;
      }
    }

    if (!(value instanceof Array)) {
      throw new Error("Insert only works for Arrays");
    }

    value.splice(index, 0, insertValue);
  };

  Model.prototype.size = function (path) {
    var value = this.model;

    //Resolve value and delegate if BigList
    for (var i = 0, segment; (segment = path.segments[i]); i++) {
      if (value != undefined) {
        value = value[segment.key];
      }

      //Check for embedded ModelInterface and delegate
      if (value instanceof ModelInterface) {
        return value.size(path.slice(i + 1));
      }
    }

    //Check size of resolved value if not delegated
    if (value instanceof Array) {
      return value.length;
    }
    else if (value instanceof Object) {
      var count = 0;
      for (var key in value) {
        count++;
      }
      return count;
    }
    else {
      return -1;
    }
  };
  
  // private helper for setValue to recursively apply values to the model that instantiates BigLists as needed
  function applyValue(model, name, value, currentPath) {
    if (value instanceof Array) {
      if (value.length == 1 && (value[0] instanceof Object) && "@chunk" in value[0] && "@init" in value[0] && "@size" in value[0] && "@subst" in value[0]) {
        model[name] = new BigList(value[0], new Model(this.baseRef.append(currentPath)));
      }
      else {
        model[name] = [];
        for (var i = 0; i < value.length; i++) {
          applyValue.call(this, model[name], i, value[i], currentPath.appendIndex(i));
        }
      }
    }
    else if (value instanceof Object) {
      if ("@subst" in value && "@size" in value) {
        model[name] = new BigMap(value, new Model(this.baseRef.append(currentPath)));
      }
      else {
        model[name] = {};
        for (var key in value) {
          if (!value.hasOwnProperty(key)) {
            continue;
          }
          applyValue.call(this, model[name], key, value[key], currentPath.append(key));
        }
      }
    }
    else {
      model[name] = value;
    }
  }

  return Model;
});
define('ankor/Ref',[
    "./events/BaseEvent",
    "./events/ChangeEvent",
    "./events/ActionEvent"
], function(BaseEvent, ChangeEvent, ActionEvent) {
    var Ref = function(ankorSystem, path) {
        this.ankorSystem = ankorSystem;
        this.path = path;
    };

    //////////////////
    // PATH METHODS //
    //////////////////
    Ref.prototype.append = function(path) {
        return new Ref(this.ankorSystem, this.path.append(path));
    };
  
    Ref.prototype.appendPath = Ref.prototype.append;

    Ref.prototype.appendIndex = function(index) {
        return new Ref(this.ankorSystem, this.path.appendIndex(index));
    };

    Ref.prototype.parent = function() {
        return new Ref(this.ankorSystem, this.path.parent());
    };
    
    Ref.prototype.root = function () {
        return new Ref(this.ankorSystem, this.path.root());
    };

    Ref.prototype.propertyName = function() {
        return this.path.propertyName();
    };

    Ref.prototype.equals = function(ref) {
        return this.path.equals(ref.path);
    };

    ///////////////////
    // MODEL METHODS //
    ///////////////////

    Ref.prototype.getValue = function() {
        return this.ankorSystem.model.getValue(this.path);
    };

    Ref.prototype.setValue = function(value, source) {
        //Apply value to model
        this.ankorSystem.model.setValue(this.path, value);

        //Cleanup listeners
        if (value === null) {
            this.ankorSystem.removeInvalidListeners(this.path.parent());
        }

        //Build event
        var event = new ChangeEvent(this.path, source, ChangeEvent.TYPE.VALUE, null, value);

        //Trigger listeners
        this.ankorSystem.triggerListeners(this.path, event);

        //Send message
        this.ankorSystem.transport.sendEvent(event);
    };

    //Removes this ref from the parent (regardless of map or array)
    Ref.prototype.del = function(source) {
        //Apply change to model
        this.ankorSystem.model.del(this.path);

        //Cleanup listeners
        var parentPath = this.path.parent();
        this.ankorSystem.removeInvalidListeners(parentPath);

        //Build event
        var event = new ChangeEvent(parentPath, source, ChangeEvent.TYPE.DEL, this.propertyName(), null);

        //Trigger listeners
        this.ankorSystem.triggerListeners(parentPath, event);

        //Send message
        this.ankorSystem.transport.sendEvent(event);
    };

    Ref.prototype.insert = function(index, value, source) {
        this.ankorSystem.model.insert(this.path, index, value);

        //Build event
        var event = new ChangeEvent(this.path, source, ChangeEvent.TYPE.INSERT, index, value);

        //Trigger listeners
        this.ankorSystem.triggerListeners(this.path, event);

        //Send message
        this.ankorSystem.transport.sendEvent(event);
    };

    Ref.prototype.size = function() {
        return this.ankorSystem.model.size(this.path);
    };

    Ref.prototype.isValid = function() {
        return this.ankorSystem.model.isValid(this.path);
    };

    ///////////////////
    // EVENT METHODS //
    ///////////////////

    Ref.prototype._handleEvent = function(event) {
        if (event instanceof ChangeEvent) {
            if (event.type === ChangeEvent.TYPE.VALUE) {
                //Apply value to model
                this.ankorSystem.model.setValue(this.path, event.value);

                //Cleanup listeners
                if (value === null) {
                    this.ankorSystem.removeInvalidListeners(this.path.parent());
                }

                //Trigger listeners
                this.ankorSystem.triggerListeners(this.path, event);
            }
            else if (event.type === ChangeEvent.TYPE.DEL) {
                //Apply change to model
                this.ankorSystem.model.del(this.path.append(event.key.toString()));

                //Cleanup listeners
                this.ankorSystem.removeInvalidListeners(this.path);

                //Trigger listeners
                this.ankorSystem.triggerListeners(this.path, event);
            }
            else if (event.type === ChangeEvent.TYPE.INSERT) {
                //Apply change to model
                this.ankorSystem.model.insert(this.path, event.key.toString(), event.value);

                //Trigger listeners
                this.ankorSystem.triggerListeners(this.path, event);
            }
            else if (event.type === ChangeEvent.TYPE.REPLACE) {
                //Apply change to model
                for(var i = 0; i < event.value.length; i++) {
                    var path = this.path.appendIndex(event.key + i);
                    var value = event.value[i];
                    this.ankorSystem.model.setValue(path, value);
                }

                //Cleanup listeners
                this.ankorSystem.removeInvalidListeners(this.path);

                //Trigger listeners
                this.ankorSystem.triggerListeners(this.path, event);
            }
        } else if (event instanceof ActionEvent) {
            this.ankorSystem.triggerListeners(this.path, event);
        }
    };

    Ref.prototype.fire = function(actionName, params) {
        var event = new ActionEvent(this.path, null, actionName, params);
        this.ankorSystem.transport.sendEvent(event);
    };

    Ref.prototype.addPropChangeListener = function(cb) {
        return this.ankorSystem.addListener("propChange", this.path, cb);
    };

    Ref.prototype.addTreeChangeListener = function(cb) {
        return this.ankorSystem.addListener("treeChange", this.path, cb);
    };

    Ref.prototype.addActionListener = function(cb) {
        return this.ankorSystem.addListener("action", this.path, cb);
    };

    return Ref;
});

define('ankor/AnkorSystem',[
    "./ListenerRegistry",
    "./Model",
    "./Path",
    "./Ref",
    "./events/BaseEvent",
    "./events/ChangeEvent",
    "./events/ActionEvent"
], function(ListenerRegistry, Model, Path, Ref, BaseEvent, ChangeEvent, ActionEvent) {
    var AnkorSystem = function(options) {
        if (!options) {
          throw new Error("AnkorSystem missing options");
        }
        if (!options.utils) {
            throw new Error("AnkorSystem missing utils");
        }
        if (!options.transport) {
            throw new Error("AnkorSystem missing transport");
        }

        this.debug = options.debug || false;
        this.utils = options.utils;
        this.senderId = this.utils.uuid();
        this.modelId = options.modelId || this.utils.uuid();
        this.transport = options.transport;
        this.model = new Model(this.getRef(""));
        this.listenerRegistry = new ListenerRegistry(this);

        this.transport.init(this);
    };

    AnkorSystem.prototype.getRef = function(pathOrString) {
        if (!(pathOrString instanceof Path)) {
            pathOrString = new Path(pathOrString);
        }
        return new Ref(this, pathOrString);
    };

    AnkorSystem.prototype.addListener = function(type, path, cb) {
        return this.listenerRegistry.addListener(type, path, cb);
    };

    AnkorSystem.prototype.triggerListeners = function(path, event) {
        if (event instanceof ChangeEvent) {
            this.listenerRegistry.triggerListeners(path, event);
        } else if (event instanceof ActionEvent) {
            this.listenerRegistry.triggerActionListeners(path, event);
        }
    };

    AnkorSystem.prototype.removeInvalidListeners = function(path) {
        this.listenerRegistry.removeInvalidListeners(path);
    };

    AnkorSystem.prototype.onIncomingEvent = function(event) {
        var ref = this.getRef(event.path);
        ref._handleEvent(event);
    };

    return AnkorSystem;
});

/**
 * This is the API endpoint of the Ankor UMD library.
 * 
 * It exposes a namespace object containing all Ankor classes. (TODO: Remove internal classes).
 * The structure of the object is equal of the folder structure of the ankor-js source.
 * 
 * This file is used as an entry point by the r.js optimizer to generate the distribution files (bower).
 * See `Makefile` and `build.js` for more details.
 * 
 * If you add classes to ankor-js that should be available to Ankor users you have to add them here,
 * otherwise they will be missing from the distribution files.
 * 
 * This version does not contain any library specific utils or adapters.
 * See `ankor-jquery.js`, `ankor-dodo.js` and `ankor-react.js`.
 */
define('ankor/ankor',[
    './events/ActionEvent',
    './events/BaseEvent',
    './events/ChangeEvent',
    './transport/BaseTransport',
    './transport/HttpPollingTransport',
    './transport/Message',
    './transport/WebSocketTransport',
    './utils/BaseUtils',
    './AnkorSystem',
    './BigCacheController',
    './BigList',
    './BigMap',
    './ListenerRegistry',
    './Model',
    './ModelInterface',
    './Path',
    './PathSegment',
    './Ref'
], function (ActionEvent,
             BaseEvent,
             ChangeEvent, 
             BaseTransport, 
             HttpPollingTransport, 
             Message, 
             WebSocketTransport, 
             BaseUtils,
             AnkorSystem, 
             BigCacheController, 
             BigList,
             BigMap, 
             ListenerRegistry, 
             Model, 
             ModelInterface, 
             Path, 
             PathSegment, 
             Ref) {
    
    var ankor = {
        adapters: {},
        events: {
            ActionEvent: ActionEvent,
            BaseEvent: BaseEvent,
            ChangeEvent: ChangeEvent
        },
        transport: {
            BaseTransport: BaseTransport,
            HttpPollingTransport: HttpPollingTransport, 
            Message: Message, 
            WebSocketTransport: WebSocketTransport
        },
        utils: {
            BaseUtils: BaseUtils
        },
        AnkorSystem: AnkorSystem,
        BigCacheController: BigCacheController,
        BigList: BigList,
        BigMap: BigMap,
        ListenerRegistry: ListenerRegistry, 
        Model: Model, 
        ModelInterface: ModelInterface, 
        Path: Path, 
        PathSegment: PathSegment, 
        Ref: Ref        
    };
    
    return ankor;
});

    //The modules for your project will be inlined above
    //this snippet. Ask almond to synchronously require the
    //module value for 'main' here and return it as the
    //value to use for the public API for the built file.
    return require('ankor/ankor');
}));
