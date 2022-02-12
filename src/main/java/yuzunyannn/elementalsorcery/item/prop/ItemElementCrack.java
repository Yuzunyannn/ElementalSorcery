package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;

public class ItemElementCrack extends Item {

	public ItemElementCrack() {
		this.setUnlocalizedName("elementCrack");
		this.setMaxStackSize(1);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		World world = entityItem.world;
		entityItem.setEntityInvulnerable(true);
		entityItem.setNoGravity(true);
		entityItem.motionY = 0.001D;
		if (entityItem.motionX > 0.75) entityItem.motionX = 0.75;
		if (entityItem.motionZ > 0.75) entityItem.motionZ = 0.75;
//		if (entityItem.ticksExisted % 3 == 0) {
//			BlockPos center = entityItem.getPosition();
//			for (int x = -1; x <= 1; x++) {
//				for (int y = -1; y <= 1; y++) {
//					for (int z = -1; z <= 1; z++) {
//						world.setBlockState(center.add(x, y, z), Blocks.AIR.getDefaultState());
//					}
//				}
//			}
//		}
		if (!world.isRemote) return super.onEntityItemUpdate(entityItem);
		int tick = entityItem.ticksExisted;
		Vec3d vec = entityItem.getPositionVector();
		playTickEffect(world, vec, tick);
		return super.onEntityItemUpdate(entityItem);
	}

	final static int[] color = new int[] { 0xFFBFBF, 0xBFFFBF, 0xBFBFFF };

	@SideOnly(Side.CLIENT)
	public void playTickEffect(World world, Vec3d vec, int tick) {
		for (int i = 0; i < 3; i++) {
			EffectElementMove effect = new EffectElementMove(world, vec.addVector(0, 0.3, 0));
			effect.isGlow = true;
			effect.prevScale = effect.scale = 0.12f;
			effect.setColor(color[i]);
			float sin = MathHelper.sin(tick * 3.1415926f / 20 + i * 3.1415926f * 2 / 3);
			float cos = MathHelper.cos(tick * 3.1415926f / 20 + i * 3.1415926f * 2 / 3);
			Vec3d speed = new Vec3d(sin, Effect.rand.nextGaussian() * 0.125f, cos).scale(0.2);
			effect.setVelocity(speed);
			effect.setAccelerate(speed.scale(-0.01));
			effect.xDecay = effect.zDecay = effect.yDecay = 0.8;
			Effect.addEffect(effect);
		}
	}
}
