package yuzunyannn.elementalsorcery.grimoire.remote;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;
import yuzunyannn.elementalsorcery.util.world.WorldLocation;

public class FMantraEnderTeleportTo extends FMantraBase {

	public FMantraEnderTeleportTo() {
		addCanUseElement(ESInit.ELEMENTS.ENDER);
		setMaxCharge(10000);
		setChargetSpeed(10);
		setIconRes("textures/mantras/teleport_f_goto.png");
	}

	@Override
	public void cast(World world, BlockPos pos, WorldLocation to, VariableSet content) {
		double charge = content.get(CHARGE);
		int count = (int) Math.pow(1.1, charge / 100);
		if (count == 0) return;
		float range = (float) ((charge / 10000) * 4 + 4);
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, range, range, range);
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, aabb, e -> {
			return e instanceof EntityLivingBase || e instanceof EntityItem;
		});
		Vec3d toVec = new Vec3d(to.getPos());
		for (Entity e : list) {
			Vec3d at = e.getPositionVector().subtract(new Vec3d(pos)).add(toVec);
			EntityPortal.moveTo(e, at, to.getDimension());
		}
	}

}
