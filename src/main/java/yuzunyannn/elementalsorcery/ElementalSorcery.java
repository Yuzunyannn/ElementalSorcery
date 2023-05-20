package yuzunyannn.elementalsorcery;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
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
import yuzunyannn.elementalsorcery.api.APIObject;
import yuzunyannn.elementalsorcery.api.ESAPI;

@Mod(modid = ESAPI.MODID, name = ESAPI.NAME, version = ElementalSorcery.VERSION)
public class ElementalSorcery {

	public static final String VERSION = "0.17.0";

	public static Side side;
	public static ESData data;

	@Instance(ESAPI.MODID)
	public static ElementalSorcery instance;

	@SidedProxy(clientSide = "yuzunyannn.elementalsorcery.ClientProxy", serverSide = "yuzunyannn.elementalsorcery.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws Throwable {
		try {

//			LaunchClassLoader classLoader = (LaunchClassLoader) proxy.getClass().getClassLoader();
//			classLoader.loadClass("yuzunyannn.elementalsorcery.mods.ModRequire");
//			classLoader.loadClass("yuzunyannn.elementalsorcery.mods.LambdaGatherer");
//			classLoader.registerTransformer("yuzunyannn.elementalsorcery.mods.ModCheckClassTransformer");

			ESAPI.logger = event.getModLog();
			side = event.getSide();
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
		for (ModContainer mod : mods) if (mod.getModId().equals(ESAPI.MODID)) return mod;
		return null;
	}

	public static void setAPIField(Object obj) {
		try {
			Class<ESAPI> cls = ESAPI.class;
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			Field fields[] = cls.getDeclaredFields();
			for (Field field : fields) {
				APIObject apiObj = field.getAnnotation(APIObject.class);
				if (apiObj == null) continue;
				Class<?> type = field.getType();
				if (type.isAssignableFrom(obj.getClass())) {
					modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
					field.set(cls, obj);
					modifiers.setAccessible(false);
					return;
				}
			}
			throw new RuntimeException("Cannot find field of " + obj.getClass());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
