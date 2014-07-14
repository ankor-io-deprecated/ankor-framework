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

package at.irian.ankor.console;

import at.irian.ankor.application.Application;
import at.irian.ankor.monitor.stats.AnkorSystemStats;
import at.irian.ankor.ref.RefContext;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public class ConsoleApplication implements Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConsoleApplication.class);

    public static final String MODEL_NAME = "ankorConsole";

    private final AnkorSystemStats stats;
    private ConsoleModelRoot consoleModelRoot;

    public ConsoleApplication(AnkorSystemStats stats) {
        this.stats = stats;
    }

    @Override
    public String getName() {
        return "Ankor Console";
    }

    @Override
    public boolean isStateless() {
        return false;
    }

    @Override
    public Set<String> getKnownModelNames() {
        return Collections.singleton(MODEL_NAME);
    }

    @Override
    public Object lookupModel(String modelName, Map<String, Object> connectParameters) {
        if (MODEL_NAME.equals(modelName)) {
            return consoleModelRoot;
        } else {
            throw new IllegalArgumentException("Unknown model " + modelName);
        }
    }

    @Override
    public Object createModel(String modelName, Map<String, Object> connectParameters, RefContext refContext) {
        if (MODEL_NAME.equals(modelName)) {
            consoleModelRoot = new ConsoleModelRoot(refContext.refFactory().ref(MODEL_NAME), stats);
            return consoleModelRoot;
        } else {
            throw new IllegalArgumentException("Unknown model " + modelName);
        }
    }

    @Override
    public void releaseModel(String modelName, Object modelRoot) {
        if (MODEL_NAME.equals(modelName)) {
            if (modelRoot == consoleModelRoot) {
                consoleModelRoot = null;
            }
        } else {
            throw new IllegalArgumentException("Unknown model " + modelName);
        }
    }

    @Override
    public void shutdown() {
        consoleModelRoot = null;
    }
}
