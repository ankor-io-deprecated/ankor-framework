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

package at.irian.ankor.viewmodel.factory;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;
import org.springframework.context.ApplicationContext;

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
public class SpringBasedBeanFactory extends AbstractBeanFactory {

    private final AbstractBeanFactory delegateBeanFactory;
    private final ApplicationContext springApplicationContext;

    public SpringBasedBeanFactory(BeanMetadataProvider beanMetadataProvider,
                                  AbstractBeanFactory baseBeanFactory,
                                  ApplicationContext springApplicationContext) {
        super(beanMetadataProvider);
        this.delegateBeanFactory = baseBeanFactory;
        this.springApplicationContext = springApplicationContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T createRawInstance(Class<T> type, Ref ref, Object[] constructorArgs) {
        T bean = delegateBeanFactory.createRawInstance(type, ref, constructorArgs);
        springApplicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
        return bean;

    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public Object initializeInstance(Object instance, Ref ref, BeanMetadata metadata) {
        Object ankorInitializedBean = delegateBeanFactory.initializeInstance(instance, ref, metadata);
        Object springInitializedBean = springApplicationContext.getAutowireCapableBeanFactory()
                                                         .initializeBean(ankorInitializedBean, ref.propertyName());
        return springInitializedBean;
    }
}
