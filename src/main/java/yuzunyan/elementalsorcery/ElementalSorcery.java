package yuzunyan.elementalsorcery;

import java.io.File;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ContainerType;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ElementalSorcery.MODID, name = ElementalSorcery.NAME, version = ElementalSorcery.VERSION)
public class ElementalSorcery {
	public static final String MODID = "elementalsorcery";
	public static final String NAME = "Elemental Sorcery";
	public static final String VERSION = "0.1.0";

	public static Logger logger;


	@Instance(ElementalSorcery.MODID)
	public static ElementalSorcery instance;

	@SidedProxy(clientSide = "yuzunyan.elementalsorcery.ClientProxy", serverSide = "yuzunyan.elementalsorcery.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		proxy.serverStarting(event);
	}

}
