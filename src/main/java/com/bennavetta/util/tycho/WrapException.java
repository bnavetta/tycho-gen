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

public class WrapException extends Exception
{
	private static final long serialVersionUID = 1055857404634636252L;

	private WrapRequest request;
	
	public WrapException(WrapRequest request)
	{
		this.request = request;
	}
	
	public WrapException(String message, WrapRequest request)
	{
		super(message);
		this.request = request;
	}
	
	public WrapException(String message, Throwable cause, WrapRequest request)
	{
		super(message, cause);
		this.request = request;
	}
	
	public WrapRequest getRequest()
	{
		return request;
	}
}
