package com.bennavetta.util.tycho.impl;

import org.sonatype.aether.artifact.Artifact;

import com.bennavetta.util.tycho.ArtifactInfo;

public class DefaultArtifactInfo implements ArtifactInfo
{
	private Artifact artifact;
	
	private String symbolicName;
	
	public DefaultArtifactInfo(Artifact artifact)
	{
		this.artifact = artifact;
	}
	
	public DefaultArtifactInfo(Artifact artifact, String symbolicName)
	{
		this.artifact = artifact;
		this.symbolicName = symbolicName;
	}
	
	@Override
	public Artifact getArtifact()
	{
		return artifact;
	}

	@Override
	public String getSymbolicName()
	{
		return symbolicName;
	}

}
