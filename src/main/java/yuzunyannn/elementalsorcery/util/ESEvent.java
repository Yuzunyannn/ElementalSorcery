package yuzunyannn.elementalsorcery.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ESEvent extends Event {

	public static <T extends ESEvent> T post(T event) {
		MinecraftForge.EVENT_BUS.post(event);
		return event;
	}

}
