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

import org.sonatype.aether.artifact.Artifact;

/**
 * A strategy for generating OSGi metadata from an {@link Artifact}. Metadata generation takes place after
 * resolution, so the artifact's file is available.
 * @author ben
 *
 */
public interface BundleGenerator
{
	/**
	 * Generate the bundle's symbolic name.
	 * @param artifact the Maven artifact
	 * @return a valid symbolic name
	 * @see <a href="http://wiki.osgi.org/wiki/Bundle-SymbolicName">Bundle-SymbolicName</a>
	 */
	public String getSymbolicName(Artifact artifact);
	
	/**
	 * Generate the bundle's version
	 * @param mavenVersion a Maven version string
	 * @return a valid OSGi version
	 * @see <a href="http://wiki.osgi.org/wiki/Bundle-Version">Bundle-Version<a>
	 */
	public String getVersion(String mavenVersion);

	/**
	 * Generate the bundle's name
	 * @param artifact the Maven artifact
	 * @return an OSGi Bundle-Name
	 * @see <a href="http://wiki.osgi.org/wiki/Bundle-Name">Bundle-Name</a>
	 */
	public String getBundleName(Artifact artifact);
}
