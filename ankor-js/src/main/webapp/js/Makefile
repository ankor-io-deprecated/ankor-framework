TARGET=ankor.js
TARGET_MIN=ankor.min.js

TARGET_REACT=ankor-react.js
TARGET_REACT_MIN=ankor-react.min.js

SRC_FILES=$(shell find ankor -type f -name "*.js")

BUILD_OPTIONS=build.js
BUILD_OPTIONS_JQUERY=build-jquery.js
BUILD_OPTIONS_REACT=build-react.js

RJS=node_modules/requirejs/bin/r.js

all: build min react react-min

build: $(TARGET)

clean:
	rm -rf node_modules $(TARGET) $(TARGET_MIN) $(TARGET_JQUERY) $(TARGET_JQUERY_MIN) $(TARGET_DOJO) $(TARGET_DOJO_MIN) $(TARGET_REACT) $(TARGET_REACT_MIN)

$(TARGET): $(RJS)
	$(RJS) -o $(BUILD_OPTIONS) out=$(TARGET)

$(RJS): 
	npm install
	
min: $(RJS)
	$(RJS) -o $(BUILD_OPTIONS) out=$(TARGET_MIN) optimize=uglify
	
react: 
	$(RJS) -o $(BUILD_OPTIONS_REACT) out=$(TARGET_REACT)

react-min:
	$(RJS) -o $(BUILD_OPTIONS_REACT) out=$(TARGET_REACT_MIN) optimize=uglify
