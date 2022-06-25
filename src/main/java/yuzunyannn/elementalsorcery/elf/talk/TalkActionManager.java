package yuzunyannn.elementalsorcery.elf.talk;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class TalkActionManager {

	static public final TalkActionManager instance = new TalkActionManager();

	private Map<ResourceLocation, ActionFactory> map = new HashMap<>();

	public static interface ActionFactory {
		ITalkAction craete(JsonObject json);
	}

	public void register(ResourceLocation id, ActionFactory action) {
		map.put(id, action);
	}

	public void register(String id, ActionFactory action) {
		ResourceLocation rid;
		if (id.indexOf(":") == -1) {
			ModContainer mod = Loader.instance().activeModContainer();
			rid = mod == null ? new ResourceLocation(ElementalSorcery.MODID, id)
					: new ResourceLocation(mod.getModId(), id);
		} else rid = new ResourceLocation(id);
		register(rid, action);
	}

	public static void init() {
		Loader.instance().setActiveModContainer(ElementalSorcery.getModeContainer());
		instance.register("end", TalkActionEnd::new);
		instance.register("goto", TalkActionGoTo::new);
	}

}
