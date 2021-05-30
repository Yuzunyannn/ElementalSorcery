package yuzunyannn.elementalsorcery.util;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class ESFakePlayer {

	static public FakePlayer get(WorldServer world) {
		return FakePlayerFactory.getMinecraft(world);
	}

}
