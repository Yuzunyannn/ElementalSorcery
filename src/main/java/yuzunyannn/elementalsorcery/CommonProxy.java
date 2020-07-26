package yuzunyannn.elementalsorcery;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import yuzunyannn.elementalsorcery.event.CommandES;
import yuzunyannn.elementalsorcery.event.ESTestAndDebug;
import yuzunyannn.elementalsorcery.init.ESInit;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) throws Throwable {
		ESInit.preInit(event);
	}

	public void init(FMLInitializationEvent event) throws Throwable {
		ESInit.init(event);
	}

	public void postInit(FMLPostInitializationEvent event) throws Throwable {
		ESInit.postInit(event);
	}

	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandES());
		event.registerServerCommand(new ESTestAndDebug.DebugCmd());
	}
}
