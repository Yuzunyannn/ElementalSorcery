package yuzunyannn.elementalsorcery.api.util.target;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class CORInvalid extends CapabilityObjectRef {

	@Override
	public void restore(World world) {
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public int tagId() {
		return TAG_INVALID;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return null;
	}

}
