/**
 *
 */
package org.isolationcontroller.isolation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.action.OFAction;

/**
 * This interface represents an emulated service on the isolated network.
 *
 * @author Niklas Rehfeld
 *
 */
public interface EmulatedService
{

	public final static String DEST_IP = "dest_ip";
	public final static String NAME = "name";
	public final static String MATCH = "match";

	/**
	 * Reads a definition of the EmulatedService from a java properties file.
	 *
	 * keys that are accepted are:
	 * <ul>
	 * <li>name [String]
	 * <li>dest_ip [either an integer or xxx.xxx.xxx.xxx]
	 * <li>match [see {@link OFMatch#fromString(String)}
	 * </ul>
	 *
	 * @param f
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void readFromFile(File f) throws FileNotFoundException, IOException;

	/**
	 * Writes the definition of this file out to a properties file.
	 * @see #readFromFile(File)
	 * @param f
	 */
	public void writeToFile(File f);

	/**
	 * Sets the name of the Emulated Service.
	 * @param name a nique name for this service.
	 */
	public void setName(String name);

	/**
	 *
	 * @return the unique name for this service.
	 */
	public String getName();

	/**
	 * Sets the IP address that all traffic to this service should be redirected to.
	 * @param IP the new IP address that the service should be redirected to.
	 */
	public void setDestinationIP(int IP);

	/**
	 *
	 * @return The IP that all traffic to this service will be redirected to.
	 */
	public int getDestinationIP();

	/**
	 *
	 *
	 * @return the actions that go in the FlowMod.
	 */
	public List<OFAction> getActions();

	/**
	 * Set the fields that this emulated service matches on.
	 * This should not include the switch port or any other fields that relate to the actual host that this is being applied to.
	 *
	 *   Typically this will consist of Transport-layer ports and suchlike.
	 * @param m an OFMatch structure that captures the type of traffic that is dealt with by this service.
	 */
	public void setMatch(OFMatch m);

	public OFMatch getMatch();

}
