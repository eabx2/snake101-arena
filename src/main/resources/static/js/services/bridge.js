app.factory('bridge', [function() {

    var data = {};

    var addData = function (property,value) {
        data[property] = value;
    }

    var getData = function (property) {
        return data[property];
    }

    return {
        addData: addData,
        getData: getData
    }

}]);