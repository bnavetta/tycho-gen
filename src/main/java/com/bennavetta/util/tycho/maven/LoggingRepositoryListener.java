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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sonatype.aether.RepositoryEvent;
import org.sonatype.aether.RepositoryListener;

/**
 * Send events to Log4j.<br>
 * Missing/Invalid Artifact/Metadata -> warn<br>
 * *ing Artifact/Metadata -> debug<br>
 * *ed Artifact/Metadata -> info<br>
 * @author ben
 *
 */
public class LoggingRepositoryListener implements RepositoryListener
{
	private Logger log = LogManager.getLogger(getClass());
	
	@Override
	public void artifactDescriptorInvalid(RepositoryEvent event)
	{
		log.warn("Invalid artifact descriptor for {}: {}", event.getArtifact(), event.getException().getMessage());
	}

	@Override
	public void artifactDescriptorMissing(RepositoryEvent event)
	{
		log.warn("Missing descriptor for {}", event.getArtifact());
	}

	@Override
	public void metadataInvalid(RepositoryEvent event)
	{
		log.warn("Invalid metadata: {}", event.getMetadata());
	}

	@Override
	public void artifactResolving(RepositoryEvent event)
	{
		log.debug("Resolving artifact {}", event.getArtifact());
	}

	@Override
	public void artifactResolved(RepositoryEvent event)
	{
		log.info("Resolved artifact {}", event.getArtifact());
	}

	@Override
	public void metadataResolving(RepositoryEvent event)
	{
		log.debug("Resolving metadata {}", event.getMetadata());
	}

	@Override
	public void metadataResolved(RepositoryEvent event)
	{
		log.info("Resolved metadata {}", event.getMetadata());
	}

	@Override
	public void artifactDownloading(RepositoryEvent event)
	{
		log.debug("Downloading artifact {}", event.getArtifact());
	}

	@Override
	public void artifactDownloaded(RepositoryEvent event)
	{
		log.info("Downloaded artifact {}", event.getArtifact());
	}

	@Override
	public void metadataDownloading(RepositoryEvent event)
	{
		log.debug("Downloading metadata {}", event.getMetadata());
	}

	@Override
	public void metadataDownloaded(RepositoryEvent event)
	{
		log.info("Downloaded metadata {}", event.getMetadata());
	}

	@Override
	public void artifactInstalling(RepositoryEvent event)
	{
		log.debug("Installing artifact {}", event.getArtifact());
	}

	@Override
	public void artifactInstalled(RepositoryEvent event)
	{
		log.info("Installed artifact {}", event.getArtifact());
	}

	@Override
	public void metadataInstalling(RepositoryEvent event)
	{
		log.debug("Installing metadata {}", event.getMetadata());
	}

	@Override
	public void metadataInstalled(RepositoryEvent event)
	{
		log.info("Installed metadata {}", event.getMetadata());
	}

	@Override
	public void artifactDeploying(RepositoryEvent event)
	{
		log.debug("Deploying artifact {}", event.getArtifact());
	}

	@Override
	public void artifactDeployed(RepositoryEvent event)
	{
		log.info("Deployed artifact {}", event.getArtifact());
	}

	@Override
	public void metadataDeploying(RepositoryEvent event)
	{
		log.debug("Deploying metadata {}", event.getMetadata());
	}

	@Override
	public void metadataDeployed(RepositoryEvent event)
	{
		log.info("Deployed metadata {}", event.getMetadata());
	}

}
