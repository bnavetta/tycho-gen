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
	 * @param artifact the Maven artifact
	 * @return a valid OSGi version
	 * @see <a href="http://wiki.osgi.org/wiki/Bundle-Version">Bundle-Version<a>
	 */
	public String getVersion(Artifact artifact);

	/**
	 * Generate the bundle's name
	 * @param artifact the Maven artifact
	 * @return an OSGi Bundle-Name
	 * @see <a href="http://wiki.osgi.org/wiki/Bundle-Name">Bundle-Name</a>
	 */
	public String getBundleName(Artifact artifact);
}
