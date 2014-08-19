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

/**
 * Base type for global event listeners.
 * A global event listener is not bound to a certain model property.
 *
 * todo: change concept and distinguish between global and property based event listeners
 *
 * @author Manfred Geiler
 */
public abstract class GlobalEventListener implements EventListener {

    protected GlobalEventListener() {
    }

    /**
     * @return always false (GlobalEventListeners are never to be discarded)
     */
    @Override
    public boolean isDiscardable() {
        return false;
    }

}
