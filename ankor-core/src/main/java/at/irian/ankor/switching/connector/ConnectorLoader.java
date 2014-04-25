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

package at.irian.ankor.switching.connector;

import at.irian.ankor.system.AnkorSystem;

/**
 * The ConnectorLoader is responsible for locating and loading
 * pluggable {@link at.irian.ankor.switching.connector.Connector connectors}.
 *
 * @author Manfred Geiler
 */
public interface ConnectorLoader {

    /**
     * Look for pluggable {@link at.irian.ankor.switching.connector.Connector connectors}, load and initialize them.
     */
    void loadAndInitConnectors(AnkorSystem ankorSystem);

    /**
     * Unload all loaded {@link at.irian.ankor.switching.connector.Connector connectors}.
     */
    void unloadConnectors();

    /**
     * Start all loaded {@link at.irian.ankor.switching.connector.Connector connectors}.
     */
    void startAllConnectors();

    /**
     * Stop all loaded {@link at.irian.ankor.switching.connector.Connector connectors}.
     */
    void stopAllConnectors();
}
