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
import at.irian.ankor.viewmodel.factory.AbstractBeanFactory;
import at.irian.ankor.viewmodel.factory.ReflectionBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.beans.Introspector;

/**
 */
@SuppressWarnings({"SpringFacetCodeInspection", "UnusedDeclaration"})
@Configuration
public class SpringBasedBeanFactory extends ReflectionBeanFactory implements ApplicationContextAware {

    private static final ThreadLocal<Ref> TEMPORARY_REF_HOLDER = new ThreadLocal<Ref>();

    private ApplicationContext applicationContext;

    public SpringBasedBeanFactory() {
        super(new AnnotationBeanMetadataProvider());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @Scope("prototype")
    public Ref ref() {
        return TEMPORARY_REF_HOLDER.get();
    }


    @SuppressWarnings("unchecked")
    @Override
    protected <T> T createInstance(Class<T> type, Ref ref, Object[] constructorArgs) {
        try {
            TEMPORARY_REF_HOLDER.set(ref);
            if (constructorArgs != null && constructorArgs.length > 0) {
                String beanName = Introspector.decapitalize(type.getSimpleName());
                return (T)applicationContext.getBean(beanName, constructorArgs);
            } else {
                return applicationContext.getBean(type);
            }
        } catch (NoSuchBeanDefinitionException e) {
            return super.createInstance(type, ref, constructorArgs);
        } finally {
            TEMPORARY_REF_HOLDER.remove();
        }
    }
}
