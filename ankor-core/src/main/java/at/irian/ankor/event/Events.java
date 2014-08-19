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

package at.irian.ankor.event;

import at.irian.ankor.event.source.ModelSource;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.session.ModelSession;

/**
 * Utilities for dealing with {@link Event Events}.
 */
public final class Events {

    private Events() {}

    public static boolean isLocalModelEvent(ModelPropertyEvent event) {
        Source source = event.getSource();
        if (source instanceof ModelSource) {
            String sourceModelSessionId = ((ModelSource) source).getModelSessionId();
            ModelSession currentModelSession = event.getProperty().context().modelSession();
            return sourceModelSessionId.equals(currentModelSession.getId());
        } else {
            return false;
        }
    }

}
