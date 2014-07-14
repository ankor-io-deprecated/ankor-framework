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
import at.irian.ankor.change.Change;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.session.ModelSessionFactory;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.ConnectionHandler;
import at.irian.ankor.switching.connector.HandlerScopeContext;
import at.irian.ankor.switching.msg.ChangeEventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Collections;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class StatelessSessionConnectionHandler implements ConnectionHandler<StatelessSessionModelAddress> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatelessSessionConnectionHandler.class);

    private final ModelSessionFactory modelSessionFactory;
    private final Application application;
    private final Switchboard switchboard;
    private final SessionModelAddressBinding sessionModelAddressBinding;

    public StatelessSessionConnectionHandler(ModelSessionFactory modelSessionFactory,
                                             Application application,
                                             Switchboard switchboard) {
        this.modelSessionFactory = modelSessionFactory;
        this.application = application;
        this.switchboard = switchboard;
        this.sessionModelAddressBinding = new SessionModelAddressBinding();
    }

    @Override
    public void openConnection(ModelAddress sender,
                               StatelessSessionModelAddress receiver,
                               Map<String, Object> connectParameters,
                               HandlerScopeContext context) {
        LOG.debug("open connection from {} to {}", sender, receiver);

        String modelName = receiver.getModelName();
        ModelSession modelSession = createModelSession(connectParameters, modelName);

        sessionModelAddressBinding.associatedModelAddress(modelSession, modelName, receiver);

        // send an initial change event for the model root back to the sender
        Object modelRoot = modelSession.getModelRoot(modelName);
        Map<String, Object> state = null; // todo
        switchboard.send(receiver, new ChangeEventMessage(modelName,
                                                          Change.valueChange(modelRoot),
                                                          state,
                                                          modelSession.getStateHolderDefinition().getPaths()),
                         sender);
    }

    @Override
    public void closeConnection(ModelAddress sender,
                                StatelessSessionModelAddress receiver,
                                boolean lastRoute,
                                HandlerScopeContext context) {
    }

    private ModelSession createModelSession(Map<String, Object> connectParameters, String modelName) {
        ModelSession modelSession = modelSessionFactory.createModelSession();
        if (connectParameters == null) {
            connectParameters = Collections.emptyMap();
        }
        Object modelRoot = application.createModel(modelName, connectParameters, modelSession.getRefContext());
        modelSession.setModelRoot(modelName, modelRoot);
        return modelSession;
    }

}
