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
