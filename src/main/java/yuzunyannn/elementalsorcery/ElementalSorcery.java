package yuzunyannn.elementalsorcery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ElementalSorcery.MODID, name = ElementalSorcery.NAME, version = ElementalSorcery.VERSION)
public class ElementalSorcery {
	public static final String MODID = "elementalsorcery";
	public static final String NAME = "Elemental Sorcery";
	public static final String VERSION = "0.3.0";

	public static Logger logger;
	public static Side side;
	public static ESConfig config;
	public static ESData data;

	@Instance(ElementalSorcery.MODID)
	public static ElementalSorcery instance;

	@SidedProxy(clientSide = "yuzunyannn.elementalsorcery.ClientProxy", serverSide = "yuzunyannn.elementalsorcery.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws Throwable {
		try {
			logger = event.getModLog();
			side = event.getSide();
			config = new ESConfig(event);
			data = new ESData(event);
			proxy.preInit(event);
		} catch (Throwable e) {
			CrashReport report = CrashReport.makeCrashReport(e, "Elementalsorcery初始化异常！");
			if (event.getSide() == Side.CLIENT) {
				Minecraft.getMinecraft().crashed(report);
				Minecraft.getMinecraft().displayCrashReport(report);
			} else {
				event.getModLog().error("Elementalsorcery初始化异常！", e);
				throw e;
			}
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) throws Throwable {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) throws Throwable {
		proxy.postInit(event);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		proxy.serverStarting(event);
	}

	public static ModContainer getModeContainer() {
		List<ModContainer> mods = Loader.instance().getModList();
		for (ModContainer mod : mods) 
			if (mod.getModId().equals(MODID)) return mod;
		return null;
	}

	/** 记录ES玩家动态数据 */
	private static final Map<String, NBTTagCompound> userData = new HashMap<String, NBTTagCompound>();

	/** 获取玩家动态数据，不会储存 */
	public static NBTTagCompound getPlayerData(EntityLivingBase player) {
		if (player instanceof EntityPlayer) return getPlayerData(player.getName());
		return new NBTTagCompound();
	}

	public static void removePlayerData(EntityPlayer player) {
		removePlayerData(player.getName());
	}

	public static NBTTagCompound getPlayerData(String username) {
		if (!userData.containsKey(username)) userData.put(username, new NBTTagCompound());
		return userData.get(username);
	}

	public static void removePlayerData(String username) {
		userData.remove(username);
	}

}
