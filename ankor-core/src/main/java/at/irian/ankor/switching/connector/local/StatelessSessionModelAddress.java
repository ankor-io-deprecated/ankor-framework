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

package at.irian.ankor.switching.connector.local;

import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Manfred Geiler
 */
public class StatelessSessionModelAddress implements ModelAddress {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatelessSessionModelAddress.class);

    private final ModelAddress clientAddress;

    public StatelessSessionModelAddress(ModelAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    @Override
    public String getModelName() {
        return clientAddress.getModelName();
    }

    @Override
    public String consistentHashKey() {
        return clientAddress.consistentHashKey();
    }

    public ModelAddress getClientAddress() {
        return clientAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatelessSessionModelAddress that = (StatelessSessionModelAddress) o;
        return clientAddress.equals(that.clientAddress);
    }

    @Override
    public int hashCode() {
        return clientAddress.hashCode();
    }

    @Override
    public String toString() {
        return "StatelessSessionModelAddress{" +
               "clientAddress=" + clientAddress +
               '}';
    }


}
