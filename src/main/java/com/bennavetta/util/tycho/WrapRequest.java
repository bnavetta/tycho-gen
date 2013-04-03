package com.bennavetta.util.tycho;

import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;

public interface WrapRequest
{
	/**
	 * The parent project that the wrapper will be a module of. The wrapper module will be created
	 * in a subdirectory of the parent.
	 * @return a valid {@code Model}
	 */
	public Model getParent();
	
	/**
	 * The group ID of the project to wrap
	 * @return a valid group ID (there must be an existing groupId/artifactId/version combination in Maven Central or one of the given repositories)
	 */
	public String getGroupId();
	
	/**
	 * The artifact Id of the project to wrap
	 * @return a valid artifact ID (there must be an existing groupId/artifactId/version combination in Maven Central or one of the given repositories)
	 */
	public String getArtifactId();
	
	/**
	 * The version of the project to wrap
	 * @return a valid version (there must be an existing groupId/artifactId/version combination in Maven Central or one of the given repositories)
	 */
	public String getVersion();
	
	/**
	 * Any repositories needed to locate the wrapped project.
	 * @return a list of configured repositories, or {@code null}
	 */
	public List<Repository> getRepositories();
	
	/**
	 * The version of Tycho to use
	 * @return a valid Eclipse Tycho version
	 */
	public String getTychoVersion();
}
