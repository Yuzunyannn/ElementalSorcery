package yuzunyannn.elementalsorcery.grimoire.remote;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
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
			BlockPos atPos = new BlockPos(at);
			for (int i = 0; i < 10; i++) {
				int y = i / 2 + 1;
				if (i % 2 == 0) y = -y;
				BlockPos checkPos = atPos.up(y);
				if (BlockHelper.isPassableBlock(world, checkPos) && BlockHelper.isPassableBlock(world, checkPos.up())) {
					at = at.add(0, y, 0);
					break;
				}
			}
			EntityPortal.moveTo(e, at, to.getDimension());
		}
		world.playSound(null, toVec.x, toVec.y, toVec.z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 2,
				1);

	}

}
