/**
 * 
 */
package net.floodlightcontroller.isolation;

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
import net.floodlightcontroller.packet.Ethernet;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFType;
import org.openflow.util.HexString;
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
	protected static Logger logger;
	
	
	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.IListener#getName()
	 */
	@Override
	public String getName()
	{
		return IsolationController.class.getSimpleName();
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.IListener#isCallbackOrderingPrereq(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.IListener#isCallbackOrderingPostreq(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name)
	{
		//make sure we're before forwarding things.
		return name.equalsIgnoreCase("forwarding");
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.module.IFloodlightModule#getModuleServices()
	 */
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.module.IFloodlightModule#getServiceImpls()
	 */
	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService>
			getServiceImpls()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.module.IFloodlightModule#getModuleDependencies()
	 */
	@Override
	public Collection<Class<? extends IFloodlightService>>
			getModuleDependencies()
	{
		Collection<Class<? extends IFloodlightService>> deps = new ArrayList<Class<? extends IFloodlightService>>();
		//IfloodlightProviderService is for listening to OF messages. 
		deps.add(IFloodlightProviderService.class);
		return deps;
	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.module.IFloodlightModule#init(net.floodlightcontroller.core.module.FloodlightModuleContext)
	 */
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException
	{
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(IsolationController.class);

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.module.IFloodlightModule#startUp(net.floodlightcontroller.core.module.FloodlightModuleContext)
	 */
	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException
	{
		//all other modules are initialised by now...
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);

	}

	/* (non-Javadoc)
	 * @see net.floodlightcontroller.core.IOFMessageListener#receive(net.floodlightcontroller.core.IOFSwitch, org.openflow.protocol.OFMessage, net.floodlightcontroller.core.FloodlightContext)
	 */
	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx)
	{
//		logger.info(getName() + " : " + msg.toString());
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		Long src = Ethernet.toLong(eth.getSourceMACAddress());
		Long dst = Ethernet.toLong(eth.getDestinationMACAddress());
		logger.info("Source: " + HexString.toHexString(src) + " Dest: " + HexString.toHexString(dst));
		if (src.longValue() == 1L)
		{
			logger.info("Dropping packet from source " + HexString.toHexString(src));
			return Command.STOP;
			
		}
		return Command.CONTINUE;
	}

}
