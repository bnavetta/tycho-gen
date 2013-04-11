package com.bennavetta.util.tycho;

import org.sonatype.aether.artifact.Artifact;

public interface ArtifactInfo
{
	/**
	 * The actual artifact to wrap
	 * @return an artifact, must not be {@code null}
	 */
	public Artifact getArtifact();
	
	/**
	 * The requested symbolic name. This can be used to override the generated one.
	 * @return a valid symbolic name, or {@code null}
	 */
	public String getSymbolicName();
}
