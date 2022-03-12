package yuzunyannn.elementalsorcery.element.explosion;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraEnderTeleport;

public class EEEnder extends ElementExplosion {

	public EEEnder(World world, Vec3d position, float strength, ElementStack estack) {
		super(world, position, strength, estack);
	}

	@Override
	protected void doExplosionBlockAt(BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial() == Material.AIR) return;
		float size = this.size * 1.5f;
		BlockPos at = new BlockPos(new Vec3d(pos).add(size * rand.nextGaussian(), size * rand.nextGaussian(),
				size * rand.nextGaussian()));
		if (world.isRemote) {
			spawnEffectFromBlock(pos);
			return;
		}
		// 有tile的或者格子相等 直接炸毁
		if (world.getTileEntity(pos) != null || pos.equals(at)) {
			super.doExplosionBlockAt(pos);
			return;
		}
		// 没tile的尝试移动
		super.doExplosionBlockAt(at);
		if (world.isAirBlock(at)) {
			world.setBlockState(at, state);
			world.setBlockToAir(pos);
		}
	}

	@Override
	protected void doExplosionEntityAt(Entity entity, Vec3d orient, double strength, double damage, double pound) {
		String name = EntityList.getEntityString(entity);
		boolean isEnder = name != null && name.toLowerCase().indexOf("ender") != -1;
		if (isEnder) return;

		doDamageSource(entity, damage * 0.05);

		if (!entity.isNonBoss()) return;
		float sizeModifier = 1;
		if (entity instanceof EntityLivingBase) {
			float power = eStack.getPower();
			EntityLivingBase living = (EntityLivingBase) entity;
			if (power / 12 < living.getMaxHealth()) return;
		} else if (entity instanceof EntityItem) {
			
		} else return;

		float size = this.size * 2f * sizeModifier;
		Vec3d at = entity.getPositionVector().add(size * rand.nextGaussian(), size * rand.nextGaussian(),
				size * rand.nextGaussian());
		if (world.isOutsideBuildHeight(new BlockPos(at))) {
			at = new Vec3d(at.x, entity.posY, at.z);
		}
		MantraEnderTeleport.doEnderTeleportWithDrown(world, entity, at);

	}

}
