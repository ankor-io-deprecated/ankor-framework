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

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.ModelAddressSource;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.impl.RefImplementor;
import at.irian.ankor.serialization.modify.Modifier;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.state.SimpleSendStateDefinition;
import at.irian.ankor.switching.msg.ActionEventMessage;
import at.irian.ankor.switching.msg.ChangeEventMessage;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
class DeliverHelper {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DeliverHelper.class);

    private final Modifier modifier;

    DeliverHelper(Modifier modifier) {
        this.modifier = modifier;
    }

    public void deliver(final ModelAddress sender, final ModelSession modelSession, final EventMessage msg) {

        if (msg instanceof ActionEventMessage) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    RefFactory refFactory = refContext.refFactory();
                    Ref actionProperty = refFactory.ref(((ActionEventMessage) msg).getProperty());
                    Action action = modifier.modifyAfterReceive(((ActionEventMessage) msg).getAction(), actionProperty);
                    applyStateToModelSession(modelSession, refFactory, ((ActionEventMessage) msg).getStateValues());
                    updateSendStateDefinition(modelSession, ((ActionEventMessage) msg).getStateHolderProperties());
                    Source source = new ModelAddressSource(sender, DeliverHelper.this);
                    ((RefImplementor) actionProperty).fire(source, action);
                }

                @Override
                public String toString() {
                    return "AsyncTask{msg=" + msg + ", modelSession=" + modelSession + "}";
                }
            });

        } else if (msg instanceof ChangeEventMessage) {
            AnkorPatterns.runLater(modelSession, new Runnable() {
                @Override
                public void run() {
                    RefContext refContext = modelSession.getRefContext();
                    RefFactory refFactory = refContext.refFactory();
                    Ref changedProperty = refFactory.ref(((ChangeEventMessage) msg).getProperty());
                    Change change = modifier.modifyAfterReceive(((ChangeEventMessage) msg).getChange(), changedProperty);
                    applyStateToModelSession(modelSession, refFactory, ((ChangeEventMessage) msg).getStateValues());
                    updateSendStateDefinition(modelSession, ((ChangeEventMessage) msg).getStateHolderProperties());
                    Source source = new ModelAddressSource(sender, DeliverHelper.this);
                    ((RefImplementor)changedProperty).apply(source, change);
                }

                @Override
                public String toString() {
                    return "AsyncTask{msg=" + msg + ", modelSession=" + modelSession + "}";
                }
            });

        } else {

            throw new IllegalArgumentException("Unsupported message type " + msg.getClass().getName());

        }
    }

    private void applyStateToModelSession(ModelSession modelSession,
                                          RefFactory refFactory,
                                          Map<String, Object> stateValues) {
        if (stateValues != null) {

            if (!modelSession.getApplication().isStateless()) {
                LOG.warn("Application {} is stateful, got unexpected state values: {}", modelSession.getApplication(), stateValues);
                return;
            }

            for (Map.Entry<String, Object> entry : stateValues.entrySet()) {
                //todo we should only allow this if a ref is marked as @StateHolder
                Ref ref = refFactory.ref(entry.getKey());
                ((RefImplementor) ref).internalSetValue(entry.getValue());
            }
        }
    }

    private void updateSendStateDefinition(ModelSession modelSession, Set<String> stateHolderProperties) {
        modelSession.setSendStateDefinition(SimpleSendStateDefinition.of(stateHolderProperties));
    }

}
