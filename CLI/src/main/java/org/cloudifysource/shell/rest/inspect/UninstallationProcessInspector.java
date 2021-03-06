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
package org.cloudifysource.shell.rest.inspect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.cloudifysource.dsl.internal.CloudifyConstants;
import org.cloudifysource.restclient.RestClient;
import org.cloudifysource.restclient.exceptions.RestClientException;
import org.cloudifysource.shell.ConditionLatch;
import org.cloudifysource.shell.exceptions.CLIException;
import org.cloudifysource.shell.installer.CLIEventsDisplayer;

/**
 * Created with IntelliJ IDEA.
 * User: elip
 * Date: 6/4/13
 * Time: 7:51 PM
 */
public abstract class UninstallationProcessInspector extends InstallationProcessInspector {

    private final CLIEventsDisplayer displayer = new CLIEventsDisplayer();

    private boolean waitForCloudResourcesRelease = true;

    public UninstallationProcessInspector(final RestClient restClient,
                                          final String deploymentId,
                                          final boolean verbose,
                                          final Map<String, Integer> plannedNumberOfInstancesPerService,
                                          final Map<String, Integer> currentRunningInstancesPerService) {
        super(restClient,
              deploymentId,
              verbose,
              plannedNumberOfInstancesPerService,
              currentRunningInstancesPerService);
    }

    @Override
    public void waitForLifeCycleToEnd(long timeout) throws InterruptedException, CLIException, TimeoutException {


        ConditionLatch conditionLatch = createConditionLatch(timeout);

        conditionLatch.waitFor(new ConditionLatch.Predicate() {

            boolean lifeCycleEnded = false;
            boolean undeployEnded = false;

            @Override
            public boolean isDone() throws CLIException, InterruptedException {
                try {
                    List<String> latestEvents;
                    boolean ended = false;
                    if (!lifeCycleEnded) {
                        lifeCycleEnded = lifeCycleEnded();
                        latestEvents = getLatestEvents();
                        if (latestEvents.contains(CloudifyConstants.UNDEPLOYED_SUCCESSFULLY_EVENT)) {
                            undeployEnded = true;
                        }
                        if (!latestEvents.isEmpty()) {
                            if (latestEvents.contains(CloudifyConstants.UNDEPLOYED_SUCCESSFULLY_EVENT)) {
                                ended = true;
                            }
                            displayer.printEvents(latestEvents);
                        } else {
                            if (!lifeCycleEnded) {
                                displayer.printNoChange();
                            }
                        }
                        printUnInstalledInstances();
                        if (lifeCycleEnded && waitForCloudResourcesRelease) {
                            displayer.printEvent("releasing cloud resources...");
                            return ended;
                        }
                    }

                    if (lifeCycleEnded) {
                    	if (waitForCloudResourcesRelease) {
	                        // wait for cloud resources
	                        latestEvents = getLatestEvents();

                            undeployEnded = false;
                            if (latestEvents.contains(CloudifyConstants.UNDEPLOYED_SUCCESSFULLY_EVENT)) {
                                undeployEnded = true;
                            } else {
                                displayer.printNoChange();
                            }
                            ended = lifeCycleEnded && undeployEnded;
                    	} else {
                    		ended = true;
                    	}
                    }
                    return ended;
                } catch (final RestClientException e) {
                    throw new CLIException(e.getMessage(), e, e.getVerbose());
                }
            }

            private void printUnInstalledInstances() throws RestClientException {
                for (Map.Entry<String, Integer> entry : plannedNumberOfInstancesPerService.entrySet()) {
                    int runningInstances = getNumberOfRunningInstances(entry.getKey());
                    Integer current = currentRunningInstancesPerService.get(entry.getKey());
                    if (runningInstances < current) {
                        // a new instance is now running
                        displayer.printEvent("Installed " + runningInstances + " planned " + entry.getValue());
                        currentRunningInstancesPerService.put(entry.getKey(), runningInstances);
                    }
                }
            }
        });


    }

	public void setWaitForCloudResourcesRelease(boolean waitForCloudResourcesRelease) {
		this.waitForCloudResourcesRelease = waitForCloudResourcesRelease;
	}
}
