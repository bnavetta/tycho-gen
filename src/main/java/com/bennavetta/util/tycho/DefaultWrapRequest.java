package com.bennavetta.util.tycho;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;

public class DefaultWrapRequest implements WrapRequest
{
	private Model parent;
	
	private String groupId;
	private String artifactId;
	private String version;
	private List<Repository> repositories;
	private String tychoVersion;
	
	public Model getParent()
	{
		return parent;
	}
	public void setParent(Model parent)
	{
		this.parent = parent;
	}
	public String getGroupId()
	{
		return groupId;
	}
	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
	}
	public String getArtifactId()
	{
		return artifactId;
	}
	public void setArtifactId(String artifactId)
	{
		this.artifactId = artifactId;
	}
	public String getVersion()
	{
		return version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
	public List<Repository> getRepositories()
	{
		return repositories;
	}
	public void setRepositories(List<Repository> repositories)
	{
		this.repositories = repositories;
	}
	
	public void addRepository(Repository repository)
	{
		if(repositories == null)
			repositories = new ArrayList<>();
		repositories.add(repository);
	}
	
	public String getTychoVersion()
	{
		return tychoVersion;
	}
	public void setTychoVersion(String tychoVersion)
	{
		this.tychoVersion = tychoVersion;
	}
}
