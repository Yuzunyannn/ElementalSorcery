package yuzunyannn.elementalsorcery.mods.ae2;

import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.mods.Mods;

public class ESAE2Core {

	public final static IAE2EngeryToElement engeryToElementMap = new IAE2EngeryToElement();

	static public void postInit() {
		ElementMap.instance.add(1, engeryToElementMap);
		ESAPI.ISCraftMap.put(Mods.AE2 + ":" + "inscriber", new ISIAE2InscriberCraftHandler());
	}

}
