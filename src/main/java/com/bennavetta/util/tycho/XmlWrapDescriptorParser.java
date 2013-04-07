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

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.jdom2.Document;
import org.jdom2.Element;

import com.bennavetta.util.tycho.impl.DefaultWrapRequest;
import com.bennavetta.util.tycho.maven.Maven;

public class XmlWrapDescriptorParser
{
	public WrapRequest createRequest(Document doc) throws Exception
	{
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
				req.addRepository(repo);
			}
		}
		
		if(Boolean.parseBoolean(doc.getRootElement().getChildText("optionalImports")))
		{
			req.setOptionalImports(true);
		}
		
		Model parent = Maven.createModel(new File(doc.getRootElement().getChildText("parent")));
		req.setParent(parent);
		for(Element artifactDescriptor : doc.getRootElement().getChild("artifacts").getChildren("artifact"))
		{
			req.addArtifact(
					artifactDescriptor.getAttributeValue("groupId"),
					artifactDescriptor.getAttributeValue("artifactId"),
					artifactDescriptor.getAttributeValue("version"));
		}
		return req;
	}
}
