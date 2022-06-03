package yuzunyannn.elementalsorcery.potion;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class PotionPoundWalker extends PotionCommon {

	public PotionPoundWalker() {
		super(false, 0x7b562b, "poundWalker");
		iconIndex = 5;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(EntityLivingBase owner, int amplifier) {
		if (owner.world.isRemote) return;
		double v = PotionWindWalker.getEntitySpeed(owner);
		Random rand = owner.getRNG();
		if (v * (amplifier * 0.3f + 1) < rand.nextFloat()) return;

		World world = owner.world;

		Vec3d center = owner.getPositionVector();

		Vec3d look = owner.getLookVec();
		Vec3d vertical = new Vec3d(-look.z, 0, look.x);
		if (vertical.lengthSquared() < 0.1) return;

		look = new Vec3d(look.x, 0, look.z).normalize();
		vertical = vertical.normalize();

		int vmax = Math.min(2, (amplifier / 2) + 1);
		int lmax = Math.min(3, (amplifier / 2) + 1);
		vertical = vertical.scale((rand.nextFloat() * vmax + 1) * (rand.nextBoolean() ? -1 : 1));
		look = look.scale(rand.nextGaussian() * lmax);

		BlockPos randomPos = new BlockPos(center.add(vertical).add(look)).down();
		pound(world, randomPos, amplifier, owner);
	}

	public static void pound(World world, BlockPos pos, float amplifier, @Nullable EntityLivingBase owner) {
		IBlockState state = world.getBlockState(pos);
		BlockPos upPos = pos.up();

		if (BlockHelper.isBedrock(world, pos)) return;
		if (!canPound(state) || !BlockHelper.isReplaceBlock(world, upPos)) return;

		world.destroyBlock(upPos, true);

		EntityFallingBlock falling = new EntityFallingBlock(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
				state);
		falling.motionY = Math.min(0.2 + 0.04 * Math.min(8, amplifier), 0.4);
		world.spawnEntity(falling);

		AxisAlignedBB aabb = new AxisAlignedBB(pos.up());
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);
		for (Entity entity : entities) {
			if (entity == owner) continue;
			if (entity instanceof EntityItem) entity.motionY = falling.motionY * 3.5;
			else if (entity instanceof EntityLivingBase) {
				entity.motionY = 0.2 + 0.14 * amplifier;
				EntityLivingBase living = (EntityLivingBase) entity;
				living.velocityChanged = true;
				if (entity.motionY > 0.8f)
					living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) (entity.motionY * 20 * 10)));
			}
		}
	}

	public static boolean canPound(IBlockState state) {
		Block block = state.getBlock();
		if (state.getMaterial() == Material.AIR) return false;
		if (block.hasTileEntity(state)) return false;
		if (!state.isFullCube()) return false;
		if (!state.isFullBlock()) return false;
		if (!state.isOpaqueCube()) return false;
		return true;
	}

}
