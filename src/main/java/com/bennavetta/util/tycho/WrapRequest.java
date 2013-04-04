package com.bennavetta.util.tycho;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.sonatype.aether.artifact.Artifact;

public interface WrapRequest
{
	/**
	 * The parent project that the wrappers will be modules of. The wrapper modules will be created
	 * in a subdirectory of the parent.
	 * @return a valid {@code Model}
	 */
	public Model getParent();
	
	/**
	 * The artifacts to wrap. This does not need to include transitive dependencies.
	 * @return a list of artifacts. Must not be {@code null}
	 */
	public Iterable<Artifact> getArtifacts();
	
	/**
	 * Any repositories needed to locate the wrapped projects.
	 * @return a list of configured repositories, or {@code null}
	 */
	public Iterable<Repository> getRepositories();
}
