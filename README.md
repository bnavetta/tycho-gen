tycho-gen
========

Currently, [Tycho](http://eclipse.org/tycho/) is one of the better ways of building OSGi applications with Maven (and the only way of building Eclipse plugins and RCP apps that I know of). However, Tycho uses Eclipse P2 repositories, which makes using regular Maven dependencies a pain. The suggested workaround was to use the [maven-bundle-plugin](http://felix.apache.org/site/apache-felix-maven-bundle-plugin-bnd.html) in a separate build to generate OSGi bundles and install them to the local repository. tycho-gen uses Tycho to wrap Maven dependencies so they can be built along with your code.

tycho-gen essentially does what the PDE "Plug-in from Existing JAR Archives" wizard does, but from the command line and with Tycho/Maven. It uses [Sonatype Aether](http://www.sonatype.org/aether) to resolve dependencies and [BND](http://www.aqute.biz/Bnd/Bnd) to generate the manifest file.

Usage
========

For a summary of options, run `java -jar tycho-gen-$version-dist.jar` (see below). Rather than specifying all of the artifact and repository information on the command line, tycho-gen uses an XML configuration file.

Configuration File Syntax

	<?xml version="1.0" encoding="UTF-8"?>
	<configuration>
		<repositories>
			<repository id="jboss-releases" name="JBoss Maven Releases" url="https://repository.jboss.org/nexus/content/repositories/releases/"/>
		</repositories>
		<bndDir>thirdparty/bnd-config</bndDir>
		<parent>thirdparty/pom.xml</parent>
		<artifacts>
			<artifact groupId="org.apache.logging.log4j" artifactId="log4j-api" version="2.0-beta4"/>
		</artifacts>
	</configuration>

The `<repositories>` element is optional. `<repository>` elements can also have a `layout` attribute that specifies the layout of the Maven repository (default or legacy). The `<parent>` element should point to a `pom.xml` file for a project with a packaging of "pom". The generated wrappers will be created under the parent project as modules with a packagin type of "eclipse-plugin". It is recommended that this project be a module of your main project because tycho-gen has to modify the POM file. The `<bndDir>` element specifies a directory containing [bnd configuration files](http://www.aqute.biz/Bnd/Format). Files are tested in this order within the directory: `*symbolicName*`, `*symbolicName*.bnd`, `*symbolicName*.properties`. Do not set the `Bundle-SymbolicName` header because it will be ignored when generating the POM. Defaults are applied for `Bundle-Version`, `Bundle-Name`, `Import-Package`, and `Export-Package`, so those headers do not be need to set unless you want to override them.
	
**NOTE:** Because tycho-gen has to delete and recreate each wrapper project, the Eclipse metadata gets destroyed. After generating the wrappers, you will need to run `mvn eclipse:eclipse` or reimport the projects. This will get fixed at some point.

Building
========

tycho-gen is built with [Gradle](http://www.gradle.org), but it can be built without having Gradle installed. To build, run `./gradlew build` on Linux/OS X and `gradlew.bat build` on Windows. This will generate `tycho-gen-$version-dist.jar` in `build/libs`.

TODO
========

* Unit tests
* Integration tests against Tycho/Maven
* Remove the assorted test classes cluttering up the main source directory
* Have an option to print out the information that needs to be added to the parent pom instead of overwriting it.