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
package com.bennavetta.util.tycho.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Repository;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

public class RepoModelResolver implements ModelResolver
{
	private List<RemoteRepository> repos;
	private RepositorySystem system;
	private RepositorySystemSession session;
	
	public RepoModelResolver()
	{
		this.repos = new ArrayList<>();
		this.system = Maven.repositorySystem();
		this.session = Maven.repositorySystemSession(system);
	}
	
	@Override
	public ModelSource resolveModel(String groupId, String artifactId, String version) throws UnresolvableModelException
	{
		Artifact artifact = new DefaultArtifact(groupId + ":" + artifactId + ":" + version);
		return new FileModelSource(Maven.getMetadata(system, session, artifact).getFile());
	}

	@Override
	public void addRepository(Repository repository) throws InvalidRepositoryException
	{
		RemoteRepository remote = new RemoteRepository(repository.getId(), repository.getLayout(), repository.getUrl());
		repos.add(remote);
	}

	@Override
	public ModelResolver newCopy()
	{
		return this;
	}

}
