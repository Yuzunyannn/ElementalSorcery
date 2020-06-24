package yuzunyannn.elementalsorcery;

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import yuzunyannn.elementalsorcery.init.ESInit;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) throws Throwable {
		super.preInit(event);
		ESInit.initClient(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		ESInit.postInitClinet(event);
	}
}
