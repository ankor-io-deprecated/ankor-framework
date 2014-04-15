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

package at.irian.ankor.switching.connector.local;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Manfred Geiler
 */
public class SessionModelAddressBinding {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SessionModelAddressBinding.class);

    private static final String KEY = SessionModelAddressBinding.class.getName() + ".KEY";

    public void associatedModelAddress(ModelSession modelSession, String modelName, ModelAddress address) {
        if (modelSession.getApplication().isStateless()) {
            modelSession.setAttribute(getSessionAttributeKey(modelName), address);
        }
    }

    public ModelAddress getAssociatedModelAddress(ModelSession modelSession, String modelName) {
        if (modelSession.getApplication().isStateless()) {
            ModelAddress clientAddress = modelSession.getAttribute(getSessionAttributeKey(modelName));
            if (clientAddress == null) {
                throw new IllegalStateException("No clientAddress?!");
            }
            return clientAddress;
        } else {
            return new StatefulSessionModelAddress(modelSession, modelName);
        }
    }

    private String getSessionAttributeKey(String modelName) {
        return KEY + '_' + modelName;
    }

}
