package yuzunyan.elementalsorcery.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;

public class ESCapability {
	@CapabilityInject(IElementInventory.class)
	public static Capability<IElementInventory> ELEMENTINVENTORY_CAPABILITY;
}
