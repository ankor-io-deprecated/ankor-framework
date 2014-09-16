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

package at.irian.ankor.spring;

import at.irian.ankor.annotation.AnnotationBeanMetadataProvider;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.factory.ReflectionBeanFactory;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Implementation of a {@link at.irian.ankor.viewmodel.factory.BeanFactory BeanFactory} providing basic Spring
 * IoC support.
 * This factory creates new bean instances exactly like the {@link at.irian.ankor.viewmodel.factory.ReflectionBeanFactory}.
 * Right after the raw instance is created (and before it is initialized) Spring's auto-wiring is invoked, so that
 * Spring beans may get injected into the newly created view model bean.
 * After the Ankor initialization (i.e. view model post processing, see {@link at.irian.ankor.viewmodel.ViewModelPostProcessor})
 * Spring's bean initialization is invoked, so that Spring specific
 * {@link org.springframework.beans.factory.config.BeanPostProcessor BeanPostProcessors} are called.
 *
 */
@Component
public class SpringBasedBeanFactory extends ReflectionBeanFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public SpringBasedBeanFactory() {
        super(new AnnotationBeanMetadataProvider());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T createRawInstance(Class<T> type, Ref ref, Object[] constructorArgs) {
        T bean = super.createRawInstance(type, ref, constructorArgs);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
        return bean;

    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public Object initializeInstance(Object instance, Ref ref, BeanMetadata metadata) {
        Object ankorInitializedBean = super.initializeInstance(instance, ref, metadata);
        Object springInitializedBean = applicationContext.getAutowireCapableBeanFactory()
                                                         .initializeBean(ankorInitializedBean, ref.propertyName());
        return springInitializedBean;
    }
}
