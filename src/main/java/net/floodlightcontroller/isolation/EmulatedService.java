/**
 * 
 */
package net.floodlightcontroller.isolation;

import java.util.List;

import org.openflow.protocol.OFFlowMod;
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

	/**
	 * Returns a OFFlowMod message that contains the actions to execute the
	 * policy defined by this EmulatedService. Implementing classes should
	 * return a valid OFFlowMod instance, that can be sent to the switch to
	 * reroute the packets.
	 * 
	 * @param in
	 *            the packet that is to be rerouted.
	 * @return an OFFlowMod message that implements this Emulated Service.
	 */
	public OFFlowMod getFlowMod();
	
	
	/**
	 * 
	 * @return the actions that go in the FlowMod. 
	 */
	public List<OFAction> getActions();
	

	/**
	 * query whether the packet belongs to this service.
	 * 
	 * @param in
	 *            the packet that needs to be routed.
	 * @return true if this emulated service is relevant to the incoming packet.
	 */
	// public boolean appliesToPacket(OFPacketIn in);

	/**
	 * Gets the priority of this rerouting. Lower priorities are processed
	 * first.
	 * 
	 * @return the priority for this service.
	 */
	// public int getPriority();

}
