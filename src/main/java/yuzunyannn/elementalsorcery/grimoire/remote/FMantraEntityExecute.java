package yuzunyannn.elementalsorcery.grimoire.remote;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.target.WorldLocation;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class FMantraEntityExecute extends FMantraBase {

	protected float fragmentPreRange;
	protected float minRange = 2;

	public void setFragmentPreRange(float fragmentPreDistance) {
		this.fragmentPreRange = fragmentPreDistance;
	}

	public void setMaxRangeWithMaxFragment(float maxDistance) {
		fragmentPreRange = (float) (maxDistance / this.maxCharge);
	}

	@Override
	public void cast(World world, BlockPos pos, WorldLocation to, VariableSet content) {
		double charge = content.get(CHARGE);
		double range = MathHelper.clamp(charge / fragmentPreRange, minRange, 32);
		int count = (int) (range * range * 0.5f);
		World toWorld = to.getWorld(world);
		AxisAlignedBB aabb = WorldHelper.createAABB(to.getPos(), range, range / 2, range / 2);
		List<EntityLivingBase> list = toWorld.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		for (EntityLivingBase e : list) {
			if (count-- < 0) return;
			executeEntityAction(world, to.getPos(), e, charge);
		}
	}

	protected void executeEntityAction(World world, BlockPos pos, EntityLivingBase target, double charge) {

	}

}
