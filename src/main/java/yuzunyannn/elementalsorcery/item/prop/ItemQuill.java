package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemQuill extends Item {

	public ItemQuill() {
		this.setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		for (EnumType type : EnumType.values()) items.add(new ItemStack(this, 1, type.getMeta()));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.quill." + EnumType.fromId(stack.getMetadata()).getUnlocalizedName();
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	public static enum EnumType {
		NORMAL("normal"),
		MAGIC("magic"),
		FIRE("fire"),
		WATER("water"),
		ELEMENT("element");

		final String unlocalizedName;

		EnumType(String unlocalizedName) {
			this.unlocalizedName = unlocalizedName;
		}

		public int getMeta() {
			return this.ordinal();
		}

		public String getUnlocalizedName() {
			return unlocalizedName;
		}

		public String getName() {
			return this.name().toLowerCase();
		}

		public static EnumType fromId(int id) {
			EnumType[] types = EnumType.values();
			return types[id % types.length];
		}
	}

	public void showParticle(World world, BlockPos pos) {
		Vec3d vPos = new Vec3d(pos);
		for (int k = 0; k < 8; ++k) {
			world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, vPos.x + Math.random(), vPos.y + Math.random(),
					vPos.z + Math.random(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		ItemStack stack = entityItem.getItem();
		World world = entityItem.world;
		BlockPos pos = entityItem.getPosition();
		entityItem.setEntityInvulnerable(false);
		boolean inLava = entityItem.isInLava();
		switch (stack.getMetadata()) {
		case 1: {
			entityItem.setNoDespawn();
			if (entityItem.isBurning() || inLava) {
				entityItem.setEntityInvulnerable(true);
				if (inLava) {
					entityItem.motionX = 0;
					entityItem.motionZ = 0;
					entityItem.motionY = -0.05;
					if (world.isRemote) break;
					if (entityItem.ticksExisted % 20 == 0) {
						BlockPos lava = BlockHelper.tryFind(world, Blocks.LAVA.getDefaultState(), pos, 3, 2, 1);
						if (lava == null) break;
						world.setBlockToAir(lava);
						if (world.rand.nextFloat() < 0.75) break;
						ItemHelper.dropItem(world, pos, new ItemStack(this, 1, 2));
						stack.shrink(1);
					}
				}
			} else if (entityItem.isInWater()) {
				if (world.isRemote) break;
				if (entityItem.ticksExisted % 20 == 0) {
					BlockPos water = BlockHelper.tryFind(world, Blocks.WATER.getDefaultState(), pos, 3, 2, 1);
					if (water == null) break;
					world.setBlockToAir(water);
					if (world.rand.nextFloat() < 0.9375) break;
					ItemHelper.dropItem(world, pos, new ItemStack(this, 1, 3));
					stack.shrink(1);
				}
			}
			break;
		}
		case 2: {
			if (entityItem.isInWater()) {
				IBlockState state = world.getBlockState(pos);
				if (state.getBlock() != Blocks.WATER) break;
				if (world.isRemote) {
					showParticle(world, pos);
					break;
				}
				world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
				entityItem.setDead();
				world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
						2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			} else if (entityItem.isBurning() || inLava) {
				entityItem.setEntityInvulnerable(true);
				if (inLava) entityItem.motionY += 0.05;
			}
			break;
		}

		case 3: {
			if (inLava) {
				entityItem.setEntityInvulnerable(true);
				entityItem.motionX = 0;
				entityItem.motionZ = 0;
				entityItem.motionY = -0.2;
				IBlockState state = world.getBlockState(pos);
				if (state.getBlock() != Blocks.LAVA) break;
				if (world.isRemote) {
					showParticle(world, pos);
					break;
				}
				if (state == Blocks.LAVA.getDefaultState()) world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
				else world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
				entityItem.setDead();
				world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
						2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			} else if (entityItem.isBurning()) {
				entityItem.setEntityInvulnerable(true);
				IBlockState state = world.getBlockState(pos);
				if (state.getBlock() != Blocks.FIRE) break;
				if (world.isRemote) break;
				world.setBlockToAir(pos);
				world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
						2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			} else if (entityItem.isInWater()) {
				entityItem.motionY += 0.045f;
			}
			break;
		}
		case 4: {
			if (inLava || entityItem.isInWater() || entityItem.isBurning()) {
				entityItem.setEntityInvulnerable(true);
				if (inLava || entityItem.isInWater()) entityItem.motionY += 0.05;
			}
			break;
		}
		}
		return false;
	}

	public boolean onPutBlock(EntityPlayer player, IBlockState state) {
		World world = player.world;
		RayTraceResult ray = WorldHelper.getLookAtBlock(world, player, 8);
		if (ray == null) return false;
		BlockPos pos = ray.getBlockPos();
		boolean flag = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
		if (!flag) pos = pos.offset(ray.sideHit);
		if (!flag && !world.getBlockState(pos).getBlock().isReplaceable(world, pos)) return false;
		// 蒸发
		if (world.provider.doesWaterVaporize() && state == Blocks.FLOWING_WATER.getDefaultState()) {
			int l = pos.getX();
			int i = pos.getY();
			int j = pos.getZ();
			world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
					2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			for (int k = 0; k < 8; ++k) {
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double) l + Math.random(),
						(double) i + Math.random(), (double) j + Math.random(), 0.0D, 0.0D, 0.0D);
			}
			return true;
		}
		// 放置
		if (flag) world.destroyBlock(pos, true);
		world.setBlockState(pos, state, 11);
		SoundEvent soundevent = state == Blocks.FLOWING_LAVA ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA
				: SoundEvents.ITEM_BUCKET_EMPTY;
		world.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
		player.addStat(StatList.getObjectUseStats(this));
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		switch (stack.getMetadata()) {
		case 2:
			if (onPutBlock(player, Blocks.FLOWING_LAVA.getDefaultState())) {
				if (!player.isCreative()) stack.shrink(1);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
			break;
		case 3:
			if (onPutBlock(player, Blocks.FLOWING_WATER.getDefaultState())) {
				if (!player.isCreative()) stack.shrink(1);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
			break;
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
	}

}
