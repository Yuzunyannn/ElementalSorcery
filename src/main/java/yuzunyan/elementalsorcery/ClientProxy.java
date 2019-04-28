package yuzunyan.elementalsorcery;

import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import yuzunyan.elementalsorcery.init.ESInit;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		ESInit.initClient(event);
	}
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		ESInit.postInitClinet(event);
	}
}
