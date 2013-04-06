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

import java.util.HashSet;
import java.util.Set;

import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.DependencyVisitor;
import org.sonatype.aether.version.Version;

public class VersionResolver implements DependencyVisitor
{	
	private static class Node
	{
		private String groupId;
		private String artifactId;
		private Version version;
	}
	
	private Set<Node> nodes = new HashSet<>();
	
	public void resolve(DependencyNode root)
	{
		root.accept(this);
	}
	
	public Version getVersion(DependencyNode node)
	{
		return nodeFor(node).version;
	}
	
	private Node nodeFor(DependencyNode dn)
	{
		for(Node node : nodes)
		{
			if(node.groupId.equals(groupId(dn)) && node.artifactId.equals(artifactId(dn)))
				return node;
		}
		Node node = new Node();
		node.groupId = groupId(dn);
		node.artifactId = artifactId(dn);
		node.version = dn.getVersion();
		nodes.add(node);
		return node;
	}
	
	private String groupId(DependencyNode node)
	{
		if(node.getDependency() != null && node.getDependency().getArtifact() != null)
			return node.getDependency().getArtifact().getGroupId();
		return "";
	}
	
	private String artifactId(DependencyNode node)
	{
		if(node.getDependency() != null && node.getDependency().getArtifact() != null)
			return node.getDependency().getArtifact().getArtifactId();
		return "";
	}

	@Override
	public boolean visitEnter(DependencyNode dn)
	{
		if(dn.getVersion() == null) // fake nodes for requests with multiple dependencies
			return true;
		Node node = nodeFor(dn);
		if(dn.getVersion().compareTo(node.version) > 0)
			node.version = dn.getVersion();
		return true;
	}

	@Override
	public boolean visitLeave(DependencyNode node)
	{
		return true;
	}
}
