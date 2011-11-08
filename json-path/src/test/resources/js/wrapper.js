var WRAPPER;
if (!WRAPPER) {
    WRAPPER = {
        jsonPath: function(json, path) {
            var jsonObj = JSON.parse(json);

            return JSON.stringify(jsonPath(jsonObj, path), null, 4);
        }
    };
}


