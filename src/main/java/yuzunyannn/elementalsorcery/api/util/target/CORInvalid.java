package yuzunyannn.elementalsorcery.api.util.target;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class CORInvalid extends CapabilityObjectRef {

	public static class Storage implements ICapabilityRefStorage<CORInvalid> {
		@Override
		public void write(ByteBuf buf, CORInvalid obj) {
		}

		@Override
		public CORInvalid read(ByteBuf buf) {
			return new CORInvalid();
		}
	}

	@Override
	public boolean equals(CapabilityObjectRef other) {
		return false;
	}

	@Override
	public boolean isInvalid() {
		return true;
	}

	@Override
	public IWorldObject toWorldObject() {
		return null;
	}

	@Override
	public void restore(World world) {
	}

	@Override
	public boolean checkReference() {
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
