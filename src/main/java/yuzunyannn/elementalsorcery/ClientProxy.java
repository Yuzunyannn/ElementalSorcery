package yuzunyannn.elementalsorcery;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import yuzunyannn.elementalsorcery.init.ESInit;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) throws Throwable {
		super.preInit(event);
		ESInit.preInitClient(event);
	}

	@Override
	public void init(FMLInitializationEvent event) throws Throwable {
		super.init(event);
		ESInit.initClinet(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) throws Throwable {
		super.postInit(event);
		ESInit.postInitClinet(event);
	}
}
