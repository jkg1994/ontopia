/*
 * #!
 * Ontopia Rest
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.rest.v1.variant;

import java.util.Collection;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.model.VariantName;
import net.ontopia.topicmaps.rest.resources.AbstractTransactionalResource;
import net.ontopia.topicmaps.rest.resources.Parameters;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class VariantsResource extends AbstractTransactionalResource {
	
	@Get
	public Collection<VariantNameIF> getVariantNames() {
		TopicNameIF name = Parameters.TOPICNAME.required(this);
		return name.getVariants();
	}
	
	@Put
	public void addVariantName(VariantName variant) {

		if (variant == null) {
			throw OntopiaRestErrors.MANDATORY_OBJECT_IS_NULL.build("VariantName");
		}

		VariantNameIF result = getController(VariantNameController.class).add(
				getTopicMap(), 
				Parameters.TOPICNAME.required(this),
				variant);
		store.commit();
		
		// todo: maybe this should be '302 Found' instead
		redirectSeeOther("../../variants/" + result.getObjectId()); // todo: how to make this stable?
	}
}
