/*******************************************************************************
 * Copyright (c) 2013 GigaSpaces Technologies Ltd. All rights reserved
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
package org.cloudifysource.rest.validators;

import org.cloudifysource.dsl.internal.DSLErrorMessageException;
import org.cloudifysource.dsl.internal.debug.DebugUtils;
import org.cloudifysource.dsl.rest.request.InstallServiceRequest;
import org.cloudifysource.rest.controllers.RestErrorException;
import org.springframework.stereotype.Component;

/**
 * Validate all fields of {@link InstallServiceRequest}.
 * @author yael
 *
 */
@Component
public class ValidateInstallServiceRequest implements InstallServiceValidator {

	@Override
	public void validate(final InstallServiceValidationContext validationContext) throws RestErrorException {
//		String absolutePuName = validationContext.getAbsolutePuName();
		InstallServiceRequest request = validationContext.getRequest();
		if (request == null) {
			return;
		}
		//this validation throws exception on application install. already validated in getFromRepo
//		String uploadKey = request.getServiceFolderUploadKey();
//		if (StringUtils.isBlank(uploadKey)) {
//			throw new RestErrorException(CloudifyMessageKeys.UPLOAD_KEY_PARAMETER_MISSING.getName(), absolutePuName);
//		}

		boolean debugAll = request.isDebugAll();
		String debugEvents = request.getDebugEvents();
		try {
			DebugUtils.validateDebugSettings(debugAll, debugEvents, request.getDebugMode());
			} catch (DSLErrorMessageException e) {
				throw new RestErrorException(e.getErrorMessage().getName(), e.getArgs());
			}
	}

}
