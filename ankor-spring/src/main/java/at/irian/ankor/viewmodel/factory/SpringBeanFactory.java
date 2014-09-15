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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 */
@SuppressWarnings({"SpringFacetCodeInspection", "UnusedDeclaration"})
@Configuration
@Component
public class SpringBeanFactory implements BeanFactory, ApplicationContextAware {

    private static final ThreadLocal<Ref> TEMPORARY_REF_HOLDER = new ThreadLocal<Ref>();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @Scope("prototype")
    public Ref getRef() {
        return TEMPORARY_REF_HOLDER.get();
    }

    @Override
    public <T> T createNewInstance(Class<T> type, Ref ref, Object[] constructorArgs) {
        if (constructorArgs != null && constructorArgs.length > 0) {
            throw new IllegalArgumentException("Constructor args not supported by Ankor Spring Support");
        }
        try {
            TEMPORARY_REF_HOLDER.set(ref);
            return applicationContext.getBean(type);
        } finally {
            TEMPORARY_REF_HOLDER.remove();
        }
    }
}
