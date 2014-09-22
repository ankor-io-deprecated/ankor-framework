TARGET=ankor.js
TARGET_MIN=ankor.min.js

TARGET_JQUERY=ankor-jquery.js
TARGET_JQUERY_MIN=ankor-jquery.min.js

TARGET_DOJO=ankor-dojo.js
TARGET_DOJO_MIN=ankor-dojo.min.js

TARGET_REACT=ankor-react.js
TARGET_REACT_MIN=ankor-react.min.js

SRC_FILES=$(shell find ankor -type f -name "*.js")
BUILD_OPTIONS=build.js
RJS=node_modules/requirejs/bin/r.js

all: build min jquery jquery-min react react-min

build: $(TARGET)

clean:
	rm -rf node_modules $(TARGET) $(TARGET_MIN) $(TARGET_JQUERY) $(TARGET_JQUERY_MIN) $(TARGET_DOJO) $(TARGET_DOJO_MIN) $(TARGET_REACT) $(TARGET_REACT_MIN)

$(TARGET): $(RJS)
	$(RJS) -o $(BUILD_OPTIONS) out=$(TARGET)

$(RJS): 
	npm install
	
min: $(RJS)
	$(RJS) -o $(BUILD_OPTIONS) out=$(TARGET_MIN) optimize=uglify
	
jquery: 
	$(RJS) -o $(BUILD_OPTIONS) out=$(TARGET_JQUERY)

jquery-min:
	$(RJS) -o $(BUILD_OPTIONS) out=$(TARGET_JQUERY_MIN) optimize=uglify

dojo: 
	$(RJS) -o $(BUILD_OPTIONS) out=$(TARGET_DOJO)

dojo-min:
	$(RJS) -o $(BUILD_OPTIONS) out=$(TARGET_DOJO_MIN) optimize=uglify

react: 
	$(RJS) -o $(BUILD_OPTIONS) out=$(TARGET_REACT)

react-min:
	$(RJS) -o $(BUILD_OPTIONS) out=$(TARGET_REACT_MIN) optimize=uglify
