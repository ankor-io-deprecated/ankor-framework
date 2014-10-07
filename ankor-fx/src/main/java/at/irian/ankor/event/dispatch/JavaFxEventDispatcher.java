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

package at.irian.ankor.event.dispatch;

import at.irian.ankor.event.Event;
import javafx.application.Platform;

/**
 * EventDispatcher that hands over the actual dispatching to the JavaFX Application Thread.
 *
 * @author Manfred Geiler
 */
public class JavaFxEventDispatcher implements EventDispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JavaFxEventDispatcher.class);

    private final EventDispatcher delegateEventDispatcher;

    public JavaFxEventDispatcher(EventDispatcher delegateEventDispatcher) {
        this.delegateEventDispatcher = delegateEventDispatcher;
    }

    @Override
    public void dispatch(final Event event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                delegateEventDispatcher.dispatch(event);
            }
        });
    }

    @Override
    public void close() {
        delegateEventDispatcher.close();
    }
}
