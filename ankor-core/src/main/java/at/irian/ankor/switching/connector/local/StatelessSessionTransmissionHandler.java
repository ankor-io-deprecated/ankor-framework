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

import at.irian.ankor.application.Application;
import at.irian.ankor.serialization.modify.Modifier;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionFactory;
import at.irian.ankor.switching.connector.HandlerScopeContext;
import at.irian.ankor.switching.connector.TransmissionHandler;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Collections;

/**
 * Delivers received EventMessages to the according local ModelSessions.
 *
 * @author Manfred Geiler
 */
public class StatelessSessionTransmissionHandler implements TransmissionHandler<StatelessSessionModelAddress> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatelessSessionTransmissionHandler.class);

    private final ModelSessionFactory modelSessionFactory;
    private final Application application;
    private final DeliverHelper deliverHelper;
    private final SessionModelAddressBinding sessionModelAddressBinding;

    public StatelessSessionTransmissionHandler(ModelSessionFactory modelSessionFactory,
                                               Application application,
                                               Modifier modifier) {
        this.modelSessionFactory = modelSessionFactory;
        this.application = application;
        this.deliverHelper = new DeliverHelper(modifier);
        this.sessionModelAddressBinding = new SessionModelAddressBinding();
    }

    @Override
    public void transmitEventMessage(ModelAddress sender,
                                     StatelessSessionModelAddress receiver,
                                     EventMessage message,
                                     HandlerScopeContext context) {
        LOG.debug("delivering {} from {} to {}", message, sender, receiver);
        String modelName = receiver.getModelName();
        ModelSession modelSession = createModelSession(modelName);
        sessionModelAddressBinding.associatedModelAddress(modelSession, modelName, receiver);
        deliverHelper.deliver(sender, modelSession, message);
    }

    private ModelSession createModelSession(String modelName) {
        ModelSession modelSession = modelSessionFactory.createModelSession();
        Object modelRoot = application.createModel(modelName, Collections.<String,Object>emptyMap(), modelSession.getRefContext());
        modelSession.setModelRoot(modelName, modelRoot);
        return modelSession;
    }

}
