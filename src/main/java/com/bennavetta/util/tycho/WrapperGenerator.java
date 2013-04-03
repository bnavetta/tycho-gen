package com.bennavetta.util.tycho;

public interface WrapperGenerator
{
	public void generate(WrapRequest request) throws WrapException;
	
	public void setBundleGenerator(BundleGenerator generator);
}
