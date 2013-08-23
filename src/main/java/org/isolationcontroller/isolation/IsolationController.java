/**
 *
 */
package org.isolationcontroller.isolation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.BasePacket;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.PacketParsingException;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.topology.TopologyManager;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFType;
import org.openflow.protocol.Wildcards;
import org.openflow.protocol.Wildcards.Flag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sand
 * 
 */
public class IsolationController implements IOFMessageListener,
		IFloodlightModule
{

	protected IFloodlightProviderService floodlightProvider;
	protected IRoutingService routingService;
	protected ITopologyService topoService;
	protected static Logger logger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.floodlightcontroller.core.IListener#getName()
	 */
	@Override
	public String getName()
	{
		return IsolationController.class.getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.floodlightcontroller.core.IListener#isCallbackOrderingPrereq(java
	 * .lang.Object, java.lang.String)
	 */
	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.floodlightcontroller.core.IListener#isCallbackOrderingPostreq(java
	 * .lang.Object, java.lang.String)
	 */
	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name)
	{
		// make sure we're before forwarding things.
		return name.equalsIgnoreCase("forwarding");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.floodlightcontroller.core.module.IFloodlightModule#getModuleServices
	 * ()
	 */
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.floodlightcontroller.core.module.IFloodlightModule#getServiceImpls()
	 */
	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.floodlightcontroller.core.module.IFloodlightModule#getModuleDependencies
	 * ()
	 */
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies()
	{
		Collection<Class<? extends IFloodlightService>> deps = new ArrayList<Class<? extends IFloodlightService>>();
		// IfloodlightProviderService is for listening to OF messages.
		deps.add(IFloodlightProviderService.class);
		deps.add(ITopologyService.class);
		deps.add(IRoutingService.class);
		return deps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.floodlightcontroller.core.module.IFloodlightModule#init(net.
	 * floodlightcontroller.core.module.FloodlightModuleContext)
	 */
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException
	{
		floodlightProvider = context
				.getServiceImpl(IFloodlightProviderService.class);
		routingService = context.getServiceImpl(IRoutingService.class);
		topoService = context.getServiceImpl(ITopologyService.class);
		logger = LoggerFactory.getLogger(IsolationController.class);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.floodlightcontroller.core.module.IFloodlightModule#startUp(net.
	 * floodlightcontroller.core.module.FloodlightModuleContext)
	 */
	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException
	{
		// all other modules are initialised by now...
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.floodlightcontroller.core.IOFMessageListener#receive(net.
	 * floodlightcontroller.core.IOFSwitch, org.openflow.protocol.OFMessage,
	 * net.floodlightcontroller.core.FloodlightContext)
	 */
	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx)
	{
		// Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
		// IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

		// eth.

		// byte[] b = OFMessage.getData(sw, msg, cntx);

		// if it's a PACKET_IN message we can check the incoming port.
		OFPacketIn pktIn = null;
		if (msg.getType() == OFType.PACKET_IN)
		{
			pktIn = (OFPacketIn) msg;
			OFMatch match = new OFMatch();
			match.loadFromPacket(pktIn.getPacketData(), pktIn.getInPort());
			logger.info("src IP: "
					+ IPv4.fromIPv4Address(match.getNetworkSource())
					+ " dest IP : "
					+ IPv4.fromIPv4Address(match.getNetworkDestination()));
			// logger.info(OFMessage.getDataAsString(sw, msg, cntx));
			// Data i = new IPv4();

			// byte[] b = pktIn.getPacketData();
			// Data i = new Data(b);
			// logger.info(i.toString());

			// try
			// {
			//
			// i.deserialize(b, 0, b.length);
			// } catch (PacketParsingException e)
			// {
			// logger.error("BAH!" + e.getLocalizedMessage());
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// logger.info("packet dest: " + i.getDestinationAddress());
			// return blockPort(sw, pktIn, cntx);
		}

		// Long src = Ethernet.toLong(eth.getSourceMACAddress());
		// Long dst = Ethernet.toLong(eth.getDestinationMACAddress());
		// logger.info("Source: " + HexString.toHexString(src) + " Dest: "
		// + HexString.toHexString(dst));
		// if (src.longValue() == 1L)
		// {
		// logger.info("Dropping packet from source "
		// + HexString.toHexString(src));
		// return Command.STOP;
		//
		// }

		return Command.CONTINUE;
	}

	/**
	 * Adds a drop flow for the port that this packet came in on.
	 * 
	 * @param sw
	 * @param pktIn
	 * @param cntxt
	 * @return
	 */
	private Command blockPort(IOFSwitch sw, OFPacketIn pktIn,
			FloodlightContext cntxt)
	{
		short port = pktIn.getInPort();
		if (sw.getId() == 2L && port == 1)
		{
			logger.info("Dropping packet from port " + sw.getStringId() + "/"
					+ port);
			OFFlowMod flow = (OFFlowMod) floodlightProvider
					.getOFMessageFactory().getMessage(OFType.FLOW_MOD);
			OFMatch m = new OFMatch().setWildcards(Wildcards.FULL
					.matchOn(Flag.IN_PORT));
			m.setInputPort(port);
			flow.setMatch(m);
			flow.setCommand(OFFlowMod.OFPFC_ADD);
			// flow.setActions()
			flow.setActions(null);// no actions is equivalent to DROP according
									// to s. 4.9 of OF spec.
			// flow.setBufferId(sw.getBuffers()); //NOT SURE ABOUT THIS.
			flow.setBufferId(pktIn.getBufferId());
			try
			{
				logger.info("Writing flowmod: " + flow.toString());
				sw.write(flow, cntxt);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Command.STOP;
	}

}
