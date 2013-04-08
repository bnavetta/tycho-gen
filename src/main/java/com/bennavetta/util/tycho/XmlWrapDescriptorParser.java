/**
 * Copyright 2013 Ben Navetta <ben.navetta@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bennavetta.util.tycho;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.jdom2.Document;
import org.jdom2.Element;

import com.bennavetta.util.tycho.impl.DefaultWrapRequest;
import com.bennavetta.util.tycho.maven.Maven;

public class XmlWrapDescriptorParser
{
	private Logger log = LogManager.getLogger(getClass());
	
	public WrapRequest createRequest(Document doc) throws Exception
	{
		log.info("Parsing wrapper configuration {} from {}", doc, doc.getBaseURI());
		
		DefaultWrapRequest req = new DefaultWrapRequest();
		
		if(doc.getRootElement().getChild("repositories") != null)
		{
			for(Element repoDescriptor : doc.getRootElement().getChild("repositories").getChildren("repository"))
			{
				Repository repo = new Repository();
				repo.setId(repoDescriptor.getAttributeValue("id"));
				repo.setLayout(repoDescriptor.getAttributeValue("layout", "default"));
				repo.setName(repoDescriptor.getAttributeValue("name"));
				repo.setUrl(repoDescriptor.getAttributeValue("url"));
				log.debug("Adding repository {}", repo);
				req.addRepository(repo);
			}
		}
		
		String bndDir = doc.getRootElement().getChildText("bndDir");
		if(bndDir != null)
		{
			req.setBndDirectory(new File(bndDir));
			log.debug("Bnd directory: {}", bndDir);
		}
		
		Model parent = Maven.createModel(new File(doc.getRootElement().getChildText("parent")));
		req.setParent(parent);
		log.debug("Parent: {}", parent);
		for(Element artifactDescriptor : doc.getRootElement().getChild("artifacts").getChildren("artifact"))
		{
			String groupId = artifactDescriptor.getAttributeValue("groupId");
			String artifactId = artifactDescriptor.getAttributeValue("artifactId");
			String version = artifactDescriptor.getAttributeValue("version");
			log.debug("Adding artifact {}:{}:{}", groupId, artifactId, version);
			req.addArtifact(groupId, artifactId, version);
		}
		return req;
	}
}
