/*******************************************************************************
 * Copyright (c) 2011 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.cloudifysource.shell.rest;

import org.cloudifysource.dsl.internal.CloudifyConstants;
import org.cloudifysource.dsl.rest.ApplicationDescription;
import org.cloudifysource.dsl.rest.ServiceDescription;
import org.cloudifysource.restclient.RestClient;
import org.cloudifysource.restclient.exceptions.RestClientException;
import org.cloudifysource.restclient.exceptions.RestClientResponseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: elip
 * Date: 5/29/13
 * Time: 1:54 PM
 * <br></br>
 *
 * Provides functionality for inspecting the installation process of applications.
 */
public class ApplicationInstallationProcessInspector extends InstallationProcessInspector {

    private static final String TIMEOUT_ERROR_MESSAGE = "Application installation timed out. "
            + "Configure the timeout using the -timeout flag.";

    private String applicationName;

    public ApplicationInstallationProcessInspector(final RestClient restClient,
                                                   final String deploymentId,
                                                   final String applicationName,
                                                   final boolean verbose,
                                                   final Map<String, Integer> plannedNumberOfInstancesPerService) {
        super(restClient, deploymentId, verbose, plannedNumberOfInstancesPerService);
        this.applicationName = applicationName;
    }

    @Override
    public Map<String, Integer> initNumberOfCurrentRunningInstancesPerService(Set<String> serviceNames) {
        Map<String, Integer> currentRunningInstancesPerService = new HashMap<String, Integer>();
        for (String service : serviceNames) {
            currentRunningInstancesPerService.put(service, 0);
        }
        return currentRunningInstancesPerService;
    }

    @Override
    public boolean lifeCycleEnded() throws RestClientException {
        ApplicationDescription applicationDescription = restClient.getApplicationDescription(applicationName);
        return applicationDescription.getApplicationState().equals(CloudifyConstants.DeploymentState.STARTED);
    }


    @Override
    public int getNumberOfRunningInstances(final String serviceName) throws RestClientException {
    	int instanceCount;
    	try {
    		ServiceDescription serviceDescription = restClient.getServiceDescription(applicationName, serviceName);
    		instanceCount = serviceDescription.getInstanceCount();
    	} catch (RestClientResponseException e) {
    		if (e.getStatusCode() == RESOURCE_NOT_FOUND_EXCEPTION_CODE) {
    			//if we got here - the service is not installed yet
    			instanceCount = 0;
        	} else {
        		throw e;
        	}
    	}
    	
    	return instanceCount;

    }

    @Override
    public String getTimeoutErrorMessage() {
        return TIMEOUT_ERROR_MESSAGE;
    }
}
