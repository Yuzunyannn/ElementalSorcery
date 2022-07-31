package yuzunyannn.elementalsorcery.mods.ae2;

import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.mods.Mods;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraft;

public class ESAE2Core {

	public final static IAE2EngeryToElement engeryToElementMap = new IAE2EngeryToElement();

	static public void postInit() {
		ElementMap.instance.addFront(engeryToElementMap);
		TileItemStructureCraft.handlerMap.put(Mods.AE2 + ":" + "inscriber", new ISIAE2InscriberCraftHandler());
	}

}
