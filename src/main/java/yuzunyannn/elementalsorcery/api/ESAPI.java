package yuzunyannn.elementalsorcery.api;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.apache.logging.log4j.Logger;

import yuzunyannn.elementalsorcery.api.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.api.element.IElementMap;
import yuzunyannn.elementalsorcery.api.element.IISCraftHanlderMap;
import yuzunyannn.elementalsorcery.api.mantra.ISilentWorld;

public class ESAPI {

	public static final boolean isDevelop;
	public static final String MODID = "elementalsorcery";
	public static final String NAME = "Elemental Sorcery";
	public static Logger logger;

	static {
		boolean debugOpen = false;
		try {
			List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
			for (String arg : args) {
				if (arg.startsWith("-agentlib:jdwp")) {
					debugOpen = true;
					break;
				}
			}
		} catch (Throwable e) {}
		isDevelop = debugOpen;
//		isDevelop = false;
	}

	@APIObject
	public final static IElementMap elementMap = null;
	@APIObject
	public final static IISCraftHanlderMap ISCraftMap = null;
	@APIObject
	public final static RecipeManagement recipeMgr = null;
	@APIObject
	public final static ISilentWorld silent = null;

}
