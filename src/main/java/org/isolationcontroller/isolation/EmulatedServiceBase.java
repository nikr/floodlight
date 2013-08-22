/**
 *
 */
package org.isolationcontroller.isolation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import net.floodlightcontroller.packet.IPv4;

import org.openflow.protocol.OFMatch;

/**
 * Base implementation of common parts of a EmulatedService.
 *
 * @author Niklas Rehfeld
 *
 */
public abstract class EmulatedServiceBase implements EmulatedService
{

	protected String name;
	protected int destIp;
	protected OFMatch match;
	protected static final String IP_MATCH = "(\\d{1,3}\\.){3}\\d{1,3}";

	/**
	 * This should be called by any subclasses.
	 */
	public EmulatedServiceBase()
	{
		match = new OFMatch();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.isolationcontroller.isolation.EmulatedService#setName(java.lang.String
	 * )
	 */
	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.isolationcontroller.isolation.EmulatedService#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.isolationcontroller.isolation.EmulatedService#setDestinationIP(int)
	 */
	@Override
	public void setDestinationIP(int IP)
	{
		destIp = IP;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.isolationcontroller.isolation.EmulatedService#getDestinationIP()
	 */
	@Override
	public int getDestinationIP()
	{
		return destIp;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.isolationcontroller.isolation.EmulatedService#readFromFile(java.io
	 * .File)
	 */
	@Override
	public void readFromFile(File f) throws FileNotFoundException, IOException
	{
		if (f == null)
		{
			throw new IOException("File is null");
		}
		Properties p = new Properties();
		p.load(new FileInputStream(f));

		String ip = p.getProperty(DEST_IP);
		if (ip.matches(IP_MATCH))
		{
			destIp = IPv4.toIPv4Address(ip);
		}
		else if (ip.matches("\\d"))
		{
			destIp = Integer.parseInt(ip);
		}

		String m = p.getProperty("match");
		if (m != null)
		{
			match.fromString(m);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.isolationcontroller.isolation.EmulatedService#writeToFile(java.io
	 * .File)
	 */
	@Override
	public void writeToFile(File f)
	{


	}

}
