package yuzunyannn.elementalsorcery.api.computer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.target.CapabilityObjectRef;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;

public interface IDeviceEnv {

	@Nonnull
	World getWorld();

	@Nullable
	EntityLivingBase getEntityLiving();

	@Nonnull
	BlockPos getBlockPos();

	@Nonnull
	CapabilityObjectRef createRef();

	@Nonnull
	IWorldObject createWorldObj();

	boolean isRemote();

	void markDirty();

}
