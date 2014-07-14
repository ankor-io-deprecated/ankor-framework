/*
 * Copyright (C) 2013-2014  Irian Solutions  (http://www.irian.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.irian.ankor.system;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketFxClientBuilder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketFxClientBuilder.class);

    private static final String DEFAULT_APPLICATION_NAME = "My Socket Fx Client";
    private static final String DEFAULT_MODEL_NAME = "root";
    private static final String DEFAULT_SERVER_ADDRESS = "//localhost:8080";
    private static final String DEFAULT_CLIENT_ADDRESS = "//localhost:9090";

    private String applicationName = DEFAULT_APPLICATION_NAME;
    private String modelName = DEFAULT_MODEL_NAME;
    private Map<String, Object> connectParams = new HashMap<String, Object>();
    private String clientAddress = DEFAULT_CLIENT_ADDRESS;
    private String serverAddress = DEFAULT_SERVER_ADDRESS;

    public SocketFxClientBuilder withApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public SocketFxClientBuilder withModelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    public SocketFxClientBuilder withConnectParam(String key, Object value) {
        connectParams.put(key, value);
        return this;
    }

    public SocketFxClientBuilder withClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
        return this;
    }

    public SocketFxClientBuilder withServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
        return this;
    }

    public AnkorClient build() {
        return SocketFxClient.create(applicationName,
                                     modelName,
                                     connectParams,
                                     clientAddress,
                                     serverAddress);
    }
}
