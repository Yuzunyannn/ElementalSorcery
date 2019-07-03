package yuzunyan.elementalsorcery;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import yuzunyan.elementalsorcery.event.CommandES;
import yuzunyan.elementalsorcery.event.ESTestAndDebug;
import yuzunyan.elementalsorcery.init.ESInit;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ESInit.preInit(event);
	}

	public void init(FMLInitializationEvent event) {
		ESInit.init(event);
	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandES());
		event.registerServerCommand(new ESTestAndDebug.DebugCmd());
	}
}
