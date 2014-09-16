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

package at.irian.ankor.ref;

import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.PropertyMetadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 */
public class RefInjectionViewModelPostProcessor implements ViewModelPostProcessor {
    @Override
    public void postProcess(Object viewModelObject, Ref viewModelRef, BeanMetadata metadata) {
        for (PropertyMetadata propertyMetadata : metadata.getPropertiesMetadata()) {
            if (propertyMetadata.isInjectedRef()) {
                Method setterMethod = propertyMetadata.getSetterMethod();
                if (setterMethod != null) {
                    doSetterInjection(viewModelObject, setterMethod, viewModelRef);
                    continue;
                }
                Field field = propertyMetadata.getField();
                if (field != null) {
                    doFieldInjection(viewModelObject, field, viewModelRef);
                    continue;
                }
                throw new IllegalStateException("Cannot inject Ref to bean of type " + viewModelObject.getClass().getName() + ": no setter method and no field metadata found");
            }
        }
    }

    private void doSetterInjection(Object bean, Method setterMethod, Object value) {
        try {
            setterMethod.invoke(bean, value);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot inject Ref to bean of type " + bean.getClass().getName());
        }
    }

    private void doFieldInjection(Object bean, Field field, Object value) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(bean, value);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot inject Ref to bean of type " + bean.getClass().getName());
        }
    }

}
