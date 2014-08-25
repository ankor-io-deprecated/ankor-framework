define(function() {
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
