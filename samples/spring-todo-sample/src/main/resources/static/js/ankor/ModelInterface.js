define(function() {
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
