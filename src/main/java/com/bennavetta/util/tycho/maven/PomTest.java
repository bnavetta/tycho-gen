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
