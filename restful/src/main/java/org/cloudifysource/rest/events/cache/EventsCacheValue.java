/*******************************************************************************
 * Copyright (c) 2013 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package org.cloudifysource.rest.events.cache;

import org.cloudifysource.dsl.rest.response.ServiceDeploymentEvents;
import org.openspaces.admin.pu.ProcessingUnit;

/**
 * Created with IntelliJ IDEA.
 * User: elip
 * Date: 5/20/13
 * Time: 2:00 PM
 * <br/><br/>
 *
 * Value for the events cache. containing the actual events plus some implementation specific information.
 */
public class EventsCacheValue {

    private ServiceDeploymentEvents events = new ServiceDeploymentEvents();
    private long lastRefreshedTimestamp;
    private int lastEventIndex;
    private volatile Object mutex = new Object();
    private ProcessingUnit processingUnit;

    public ProcessingUnit getProcessingUnit() {
        return processingUnit;
    }

    public void setProcessingUnit(final ProcessingUnit processingUnit) {
        this.processingUnit = processingUnit;
    }

    public ServiceDeploymentEvents getEvents() {
        return events;
    }

    public void setEvents(final ServiceDeploymentEvents events) {
        this.events = events;
    }

    public long getLastRefreshedTimestamp() {
        return lastRefreshedTimestamp;
    }

    public void setLastRefreshedTimestamp(final long lastRefreshedTimestamp) {
        this.lastRefreshedTimestamp = lastRefreshedTimestamp;
    }

    public int getLastEventIndex() {
        return lastEventIndex;
    }

    public void setLastEventIndex(final int lastEventIndex) {
        this.lastEventIndex = lastEventIndex;
    }

    public Object getMutex() {
        return mutex;
    }

    @Override
    public String toString() {
        return "EventsCacheValue{" + "events=" + events
                + ", lastRefreshedTimestamp=" + lastRefreshedTimestamp
                + ", lastEventIndex=" + lastEventIndex + ", mutex=" + mutex + '}';
    }
}
