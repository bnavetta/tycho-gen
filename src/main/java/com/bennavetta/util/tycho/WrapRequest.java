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
package com.bennavetta.util.tycho;

import java.io.File;

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
	
	/**
	 * If the generated manifests need to be customized, 
	 * @return
	 */
	public File getBndDirectory();
}
