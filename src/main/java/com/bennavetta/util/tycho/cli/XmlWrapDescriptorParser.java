package com.bennavetta.util.tycho.cli;

import java.io.File;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.jdom2.Document;
import org.jdom2.Element;

import com.bennavetta.util.tycho.WrapRequest;
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
