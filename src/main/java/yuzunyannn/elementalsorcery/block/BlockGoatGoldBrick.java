package yuzunyannn.elementalsorcery.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IBlockJumpModify;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class BlockGoatGoldBrick extends Block implements Mapper, IBlockJumpModify {

	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);

	public BlockGoatGoldBrick() {
		super(Material.ROCK, MapColor.GOLD);
		this.setTranslationKey("goatGoldBrick");
		this.setHarvestLevel("pickaxe", 4);
		this.setHardness(Float.MAX_VALUE / 10);
		this.setTickRandomly(true);
		this.setDefaultState(this.getDefaultState().withProperty(VARIANT, EnumType.NORMAL));
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT });
	}

	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (EnumType enumtype : EnumType.values()) items.add(new ItemStack(this, 1, enumtype.ordinal()));
	}

	@Override
	public String apply(ItemStack var1) {
		return "common";
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumType) state.getValue(VARIANT)).ordinal();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumType) state.getValue(VARIANT)).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumType.values()[meta]);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(VARIANT) == EnumType.GLOW) return 15;
		return 0;
	}

	// 屏蔽精准采集
	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return ItemStack.EMPTY;
	}

	// 平步普通掉落
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {

	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof EntityLivingBase) {
			IBlockState state = worldIn.getBlockState(pos);
			EnumType type = state.getValue(VARIANT);
			if (type == EnumType.JUMP || type == EnumType.MOVE) {
				((EntityLivingBase) entityIn).removeActivePotionEffect(MobEffects.JUMP_BOOST);
			}
		}
	}

	static public void onScarletCrystalUpdate(EntityItem eItem) {
		if (eItem.ticksExisted % 100 != 0) return;
		BlockPos pos = new BlockPos(eItem.posX, eItem.posY - 0.2, eItem.posZ);
		IBlockState state = eItem.world.getBlockState(pos);
		if (state.getBlock() != ESInit.BLOCKS.GOAT_GOLD_BRICK) return;
		EnumType type = state.getValue(VARIANT);
		if (type != EnumType.NORMAL) {
			eItem.motionY = 0.2;
			eItem.motionX += RandomHelper.rand.nextGaussian() * 0.2;
			eItem.motionZ += RandomHelper.rand.nextGaussian() * 0.2;
			return;
		}

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				BlockPos at = pos.add(x, 0, z);
				state = eItem.world.getBlockState(at);
				if (state.getBlock() != ESInit.BLOCKS.GOAT_GOLD_BRICK) continue;
				type = state.getValue(VARIANT);
				if (type == EnumType.NORMAL && x == 0 && z == 0) {
					FireworkEffect.spawn(eItem.world, at.up(), 0, 1, 0.1f, new int[] { 0x760e05, 0xd4584d },
							new int[] { 0xf1c301, 0xfbd903 });
					eItem.world.setBlockState(at, state.withProperty(VARIANT, EnumType.JUMP));
				} else if (type == EnumType.JUMP || type == EnumType.GLOW) {
					FireworkEffect.spawn(eItem.world, at.up(), 0, 1, 0.1f, new int[] { 0xf1c301, 0xfbd903 },
							new int[] { 0x760e05, 0xd4584d });
					eItem.world.setBlockState(at, state.withProperty(VARIANT, EnumType.NORMAL));
				}
			}
		}
		eItem.getItem().shrink(1);
	}

	public static boolean tryWitherGoatGoldBrick(World worldIn, BlockPos at) {
		IBlockState state = worldIn.getBlockState(at);
		if (state.getBlock() != ESInit.BLOCKS.GOAT_GOLD_BRICK) return false;
		EnumType type = state.getValue(VARIANT);
		if (type == EnumType.WITHER) return true;
		worldIn.setBlockState(at, state.withProperty(VARIANT, EnumType.WITHER));
		return true;
	}

	public static void tryWitherGoatGoldBrickArea(World worldIn, BlockPos pos, boolean passFastFind) {
		if (worldIn.isRemote) return;
		int size = 4;

		if (!passFastFind) {
			Random rand = worldIn.rand;
			boolean isFind = false;
			for (int i = 0; i < 8; i++) {
				BlockPos at = pos.add(rand.nextGaussian() * size, rand.nextGaussian() * size,
						rand.nextGaussian() * size);
				if (tryWitherGoatGoldBrick(worldIn, at)) isFind = true;
			}
			if (isFind) size = 1;
		}

		for (int x = -size; x <= size; x++) {
			for (int y = -size; y <= size; y++) {
				for (int z = -size; z <= size; z++) {
					tryWitherGoatGoldBrick(worldIn, pos.add(x, y, z));
				}
			}
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
		if (worldIn.isRemote) return;
		EnumType type = state.getValue(VARIANT);
		if (type == EnumType.WITHER) {
			tryWitherGoatGoldBrickArea(worldIn, pos, false);
			worldIn.destroyBlock(pos, false);
			return;
		}
		if (type == EnumType.MOVE) {
			List<BlockPos> moveList = new ArrayList<>();
			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				if (BlockHelper.isReplaceBlock(worldIn, pos.offset(facing))) moveList.add(pos.offset(facing));
			}
			if (moveList.isEmpty()) return;
			Collections.shuffle(moveList);
			BlockPos at = moveList.get(1);
			worldIn.destroyBlock(pos, false);
			worldIn.setBlockState(at, state);
			if (random.nextFloat() > 0.25f) worldIn.scheduleBlockUpdate(at, state.getBlock(), 20, 0);
		}
	}

	@Override
	public void onPlayerJump(World world, BlockPos pos, IBlockState state, EntityLivingBase entity) {
		EnumType type = state.getValue(VARIANT);
		if (type == EnumType.WITHER) return;
		if (type == EnumType.JUMP || type == EnumType.MOVE) {
			if (entity.motionY < 2) {
				entity.motionY += 0.25f;
				entity.motionY *= 1.2f;
			}
			if (world.isRemote) onPlayerJumpEffect(world, entity, true);
		} else {
			if (!EntityHelper.isCreative(entity)) entity.motionY *= 0.2f;
			if (world.isRemote) onPlayerJumpEffect(world, entity, false);
		}

	}

	@SideOnly(Side.CLIENT)
	public void onPlayerJumpEffect(World world, EntityLivingBase player, boolean isUp) {
		Random rand = player.getRNG();
		for (int i = 0; i < 7; i++) {
			Vec3d vec = player.getPositionVector();
			vec = vec.add(rand.nextGaussian() * 0.5, isUp ? 0 : player.height, rand.nextGaussian() * 0.5);
			EffectElementMove em = new EffectElementMove(world, vec);
			em.yDecay = 0.8;
			em.motionY = 0.2 + rand.nextFloat() * 0.2;
			if (!isUp) em.motionY = -em.motionY;
			em.setColor(0xf3a016);
			Effect.addEffect(em);
		}
	}

	public static enum EnumType implements IStringSerializable {
		NORMAL("normal"),
		GLOW("glow"),
		MOVE("move"),
		JUMP("jump"),
		WITHER("wither");

		final String name;

		EnumType(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

	}
}
