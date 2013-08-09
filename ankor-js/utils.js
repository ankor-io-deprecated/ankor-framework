define(function() {
    return {
        uuid: function() {
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
        },
        hitch: function(scope, method) {
            if (typeof method == "string") {
                method = scope[method];
            }
            return function() {
                return method.apply(scope, arguments || []);
            };
        }
    }
});
