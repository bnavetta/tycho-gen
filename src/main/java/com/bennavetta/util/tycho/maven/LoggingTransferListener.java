package com.bennavetta.util.tycho.maven;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sonatype.aether.transfer.AbstractTransferListener;
import org.sonatype.aether.transfer.TransferEvent;
import org.sonatype.aether.transfer.TransferResource;

/**
 * Based on ConsoleTransferListener from the Sonatype Aether Demos
 * @author ben
 *
 */
public class LoggingTransferListener extends AbstractTransferListener
{
	private Logger log = LogManager.getLogger(getClass());

	private Map<TransferResource, Long> downloads = new ConcurrentHashMap<TransferResource, Long>();

	private int lastLength;


	@Override
	public void transferInitiated( TransferEvent event )
	{
		String message = event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploading" : "Downloading";
		log.info("{}: {}{}", message, event.getResource().getRepositoryUrl(), event.getResource().getResourceName());
	}

	@Override
	public void transferProgressed( TransferEvent event )
	{
		TransferResource resource = event.getResource();
		downloads.put( resource, Long.valueOf( event.getTransferredBytes() ) );

		StringBuilder buffer = new StringBuilder( 64 );

		for ( Map.Entry<TransferResource, Long> entry : downloads.entrySet() )
		{
			long total = entry.getKey().getContentLength();
			long complete = entry.getValue().longValue();

			buffer.append( getStatus( complete, total ) ).append( " " );
		}

		int pad = lastLength - buffer.length();
		lastLength = buffer.length();
		pad( buffer, pad );

		log.debug(buffer);
	}

	private String getStatus( long complete, long total )
	{
		if ( total >= 1024 )
		{
			return toKB( complete ) + "/" + toKB( total ) + " KB ";
		}
		else if ( total >= 0 )
		{
			return complete + "/" + total + " B ";
		}
		else if ( complete >= 1024 )
		{
			return toKB( complete ) + " KB ";
		}
		else
		{
			return complete + " B ";
		}
	}

	private void pad( StringBuilder buffer, int spaces )
	{
		String block = " ";
		while ( spaces > 0 )
		{
			int n = Math.min( spaces, block.length() );
			buffer.append( block, 0, n );
			spaces -= n;
		}
	}

	@Override
	public void transferSucceeded( TransferEvent event )
	{
		transferCompleted( event );

		TransferResource resource = event.getResource();
		long contentLength = event.getTransferredBytes();
		if ( contentLength >= 0 )
		{
			String type = ( event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploaded" : "Downloaded" );
			String len = contentLength >= 1024 ? toKB( contentLength ) + " KB" : contentLength + " B";

			String throughput = "";
			long duration = System.currentTimeMillis() - resource.getTransferStartTime();
			if ( duration > 0 )
			{
				DecimalFormat format = new DecimalFormat( "0.0", new DecimalFormatSymbols( Locale.ENGLISH ) );
				double kbPerSec = ( contentLength / 1024.0 ) / ( duration / 1000.0 );
				throughput = " at " + format.format( kbPerSec ) + " KB/sec";
			}

			log.info("{}: {}{} ({} {})", type, resource.getRepositoryUrl(), resource.getResourceName(), len, throughput);
		}
	}

	@Override
	public void transferFailed( TransferEvent event )
	{
		transferCompleted( event );

		log.error("Transfer error", event.getException());
	}

	private void transferCompleted( TransferEvent event )
	{
		downloads.remove( event.getResource() );

		StringBuilder buffer = new StringBuilder( 64 );
		pad( buffer, lastLength );
		buffer.append( '\r' );
		log.debug(buffer);
	}

	public void transferCorrupted( TransferEvent event )
	{
		log.error("Corrupted transfer", event.getException());
	}

	protected long toKB( long bytes )
	{
		return ( bytes + 1023 ) / 1024;
	}

}