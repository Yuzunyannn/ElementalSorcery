package yuzunyannn.elementalsorcery.mods;

import ic2.api.energy.tile.IEnergySink;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ModCapabilities {

	@CapabilityInject(IEnergySink.class)
	public static Capability<IEnergySink> ENERGY_SINK;

}
