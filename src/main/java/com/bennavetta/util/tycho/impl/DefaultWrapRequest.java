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
package com.bennavetta.util.tycho.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import com.bennavetta.util.tycho.ArtifactInfo;
import com.bennavetta.util.tycho.WrapRequest;

public class DefaultWrapRequest implements WrapRequest
{
	private Model parent;
	private Set<ArtifactInfo> artifacts;
	private List<Repository> repositories;
	private File bndDirectory;
	
	public Model getParent()
	{
		return parent;
	}
	public void setParent(Model parent)
	{
		this.parent = parent;
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
	
	@Override
	public Set<ArtifactInfo> getArtifacts()
	{
		return artifacts;
	}
	
	public void setArtifacts(Set<ArtifactInfo> artifacts)
	{
		this.artifacts = artifacts;
	}
	
	public void addArtifact(ArtifactInfo info)
	{
		if(artifacts == null)
			artifacts = new HashSet<>();
		artifacts.add(info);
	}
	
	public void addArtifact(Artifact artifact)
	{
		if(artifacts == null)
			artifacts = new HashSet<>();
		artifacts.add(new DefaultArtifactInfo(artifact));
	}
	
	public void addArtifact(String groupId, String artifactId, String version)
	{
		addArtifact(new DefaultArtifact(groupId + ":" + artifactId + ":" + version));
	}
	
	public File getBndDirectory()
	{
		return bndDirectory;
	}
	public void setBndDirectory(File bndDirectory)
	{
		this.bndDirectory = bndDirectory;
	}
}
