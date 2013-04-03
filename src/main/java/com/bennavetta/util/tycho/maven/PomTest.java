package com.bennavetta.util.tycho.maven;

import java.io.File;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public class PomTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws ModelBuildingException, ComponentLookupException
	{
		File pomFile = new File("/Users/ben/workspaces/gae-website/appsite-client/thirdparty/pom.xml");
		
		Model model = Maven.createModel(pomFile);
		System.out.println(model.getModules());
		
		
	}

}
