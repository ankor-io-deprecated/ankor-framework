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

package at.irian.ankor.application;

import at.irian.ankor.ref.RefContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Convenient {@link Application} implementation, that is easily configurable and implements reasonable default behaviour.
 * This implementation is ideal for those who want to configure their {@link Application} in Bean style by creating a
 * {@link GenericApplication} object and then setting several properties.
 * <p>
 * Defaults are:
 * <ul>
 *     <li>Name: "unnamed"</li>
 *     <li>Known model names: "root"</li>
 *     <li>stateless: no</li>
 * </ul>
 * </p>
 */
@SuppressWarnings("UnusedDeclaration")
public class GenericApplication implements Application {

    public static final String DEFAULT_APPLICATION_NAME = "unnamed";
    public static final String DEFAULT_MODEL_ROOT_NAME = "root";
    public static final String DEFAULT_MODEL_INSTANCE_ID_PARAM = CollaborationSingleRootApplication.MODEL_INSTANCE_ID_PARAM;
    private static final Object[] EMPTY_CONSTRUCTOR_ARGS = new Object[0];

    private String name = DEFAULT_APPLICATION_NAME;
    private boolean stateless = false;
    private String modelInstanceIdParam = DEFAULT_MODEL_INSTANCE_ID_PARAM;
    private Map<String, Class<?>> modelTypes = new HashMap<String, Class<?>>();
    private Map<String, Map<String, Object>> modelMap = new HashMap<String, Map<String, Object>>();
    private Map<String, Object[]> modelRootConstructorArgs = null;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setStateless(boolean stateless) {
        this.stateless = stateless;
    }

    @Override
    public boolean isStateless() {
        return stateless;
    }

    public String getModelInstanceIdParam() {
        return modelInstanceIdParam;
    }

    public void setModelInstanceIdParam(String modelInstanceIdParam) {
        this.modelInstanceIdParam = modelInstanceIdParam;
    }

    public Map<String, Class<?>> getModelTypes() {
        return modelTypes;
    }

    public void setModelTypes(Map<String, Class<?>> modelTypes) {
        this.modelTypes = modelTypes;
        initModelMap();
    }

    public void setDefaultModelType(Class modelType) {
        modelTypes.put(DEFAULT_MODEL_ROOT_NAME, modelType);
        initModelMap();
    }

    private void initModelMap() {
        for (String modelName : modelTypes.keySet()) {
            modelMap.put(modelName, new ConcurrentHashMap<String, Object>());
        }
    }

    public void setModelConstructorArgs(Map<String, Object[]> modelRootConstructorArgs) {
        this.modelRootConstructorArgs = modelRootConstructorArgs;
    }

    public void setDefaultModelConstructorArgs(Object... args) {
        if (modelRootConstructorArgs == null) {
            modelRootConstructorArgs = new HashMap<String, Object[]>();
        }
        modelRootConstructorArgs.put(DEFAULT_MODEL_ROOT_NAME, args);
    }

    @Override
    public Set<String> getKnownModelNames() {
        return modelTypes.keySet();
    }

    @Override
    public Object lookupModel(String modelName, Map<String, Object> connectParameters) {
        String instanceId = null;
        if (connectParameters != null) {
            instanceId = (String) connectParameters.get(modelInstanceIdParam);
        }
        if (instanceId != null) {
            Map<String, Object> instanceMap = getInstanceMap(modelName);
            return instanceMap.get(instanceId);
        } else {
            return null;
        }
    }

    @Override
    public Object createModel(String modelName, Map<String, Object> connectParameters, RefContext refContext) {

        Class<?> modelType = modelTypes.get(modelName);
        if (modelType == null) {
            StringBuilder sb = getKnownModelNamesString();
            throw new IllegalArgumentException("Unexpected model name " + modelName + " - expected one of: " + sb.toString());
        }

        Object[] constructorArgs = null;
        if (modelRootConstructorArgs != null) {
            constructorArgs = modelRootConstructorArgs.get(modelName);
        }
        if (constructorArgs == null) {
            constructorArgs = EMPTY_CONSTRUCTOR_ARGS;
        }

        Object modelRoot = refContext.beanFactory().createAndInitializeInstance(modelType,
                                                                                refContext.refFactory().ref(modelName),
                                                                                constructorArgs);

        String instanceId = getInstanceId(connectParameters);

        getInstanceMap(modelName).put(instanceId, modelRoot);

        return modelRoot;
    }


    @Override
    public void releaseModel(String modelName, Object modelRoot) {
        Map<String, Object> instanceMap = getInstanceMap(modelName);
        Iterator<Object> iterator = instanceMap.values().iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next == modelRoot) {
                iterator.remove();
            }
        }
    }

    @Override
    public void shutdown() {
        for (Map.Entry<String, Map<String, Object>> entry : modelMap.entrySet()) {
            String modelName = entry.getKey();
            Map<String, Object> instanceMap = entry.getValue();
            for (Object modelRoot : instanceMap.values()) {
                releaseModel(modelName, modelRoot);
            }
            instanceMap.clear();
        }
    }


    private Map<String, Object> getInstanceMap(String modelName) {
        Map<String, Object> instanceMap = modelMap.get(modelName);
        if (instanceMap == null) {
            StringBuilder sb = getKnownModelNamesString();
            throw new IllegalArgumentException("Unexpected model name " + modelName + " - expected one of: " + sb.toString());
        }
        return instanceMap;
    }

    private StringBuilder getKnownModelNamesString() {
        StringBuilder sb = new StringBuilder();
        for (String n : getKnownModelNames()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(n);
        }
        return sb;
    }

    private String getInstanceId(Map<String, Object> connectParameters) {
        String instanceId = null;
        if (connectParameters != null) {
            instanceId = (String) connectParameters.get(modelInstanceIdParam);
        }
        if (instanceId == null) {
            instanceId = UUID.randomUUID().toString();
        }
        return instanceId;
    }

}
