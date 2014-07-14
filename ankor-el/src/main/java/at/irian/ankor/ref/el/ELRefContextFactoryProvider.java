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

package at.irian.ankor.ref.el;

import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.RefContextFactoryProvider;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.factory.BeanFactory;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;

import java.util.List;

/**
* @author Manfred Geiler
*/
public class ELRefContextFactoryProvider extends RefContextFactoryProvider {
    @Override
    public RefContextFactory createRefContextFactory(BeanResolver beanResolver,
                                                     List<ViewModelPostProcessor> viewModelPostProcessors,
                                                     Scheduler scheduler,
                                                     BeanMetadataProvider metadataProvider,
                                                     BeanFactory beanFactory,
                                                     Switchboard switchboard) {
        return new ELRefContextFactory(beanResolver,
                                       viewModelPostProcessors,
                                       scheduler,
                                       metadataProvider,
                                       beanFactory,
                                       switchboard);
    }
}
