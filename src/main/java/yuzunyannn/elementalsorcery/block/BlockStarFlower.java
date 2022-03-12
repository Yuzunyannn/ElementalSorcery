package yuzunyannn.elementalsorcery.block;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IStarPray;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.explore.StarPrays;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.FireworkEffect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.tile.TileStarFlower;

public class BlockStarFlower extends BlockBush implements IGrowable, Mapper, ITileEntityProvider {

	public static final int MAX_STAGE = 2;
	public static final int STAGE_WITHERED = MAX_STAGE + 1;
	public static final int STAGE_ELEMENT = MAX_STAGE + 2;
	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, MAX_STAGE + 2);

	public BlockStarFlower() {
		super(Material.PLANTS, MapColor.GREEN);
		this.setSoundType(SoundType.PLANT);
		this.setTranslationKey("starFlower");
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, MAX_STAGE));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { STAGE });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(STAGE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(STAGE);
	}

	@Override
	public boolean canSustainBush(IBlockState state) {
		return super.canSustainBush(state) || state.getBlock() == Blocks.SAND
				|| state.getBlock() == ESInit.BLOCKS.STAR_SAND;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return state.getValue(STAGE) < MAX_STAGE;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return false;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		int stage = state.getValue(STAGE);
		if (stage < MAX_STAGE) worldIn.setBlockState(pos, state.withProperty(STAGE, stage + 1));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		int stage = state.getValue(STAGE);
		if (stage != MAX_STAGE) return false;
		worldIn.setBlockState(pos, state.withProperty(STAGE, STAGE_WITHERED));

		// 获取种子
		ElfTime time = new ElfTime(worldIn);
		if (time.at(ElfTime.Period.MIDNIGHT)) {
			if (playerIn.getHeldItemMainhand().isEmpty()) {
				int n = 1;
				if (worldIn.rand.nextFloat() < 0.01) n = 2;
				playerIn.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ESInit.BLOCKS.STAR_FLOWER, n));
				return true;
			}
		}

		bene(playerIn, pos);
		if (worldIn.isRemote) return true;

		FireworkEffect.spawn(worldIn, pos, 0, 1, 0.1f, new int[] { 0xe5eef5, 0xf1f8ff, 0xd0e3ee },
				new int[] { 0xf1f8ff });
		return true;
	}

	public static void bene(EntityLivingBase player, BlockPos flowerPos) {
		World world = player.world;
		float max = 0;
		IStarPray starPray = null;
		for (IStarPray sp : StarPrays.prays) {
			float n = sp.hopeDegree(world, flowerPos, player);
			n = n * sp.priority(world, flowerPos, player);
			if (n > max) {
				max = n;
				starPray = sp;
			}
		}
		if (starPray == null) return;
		if (world.isRemote) starPray.doPrayClient(world, flowerPos, player);
		else starPray.doPray(world, flowerPos, player);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		int stage = state.getValue(STAGE);
		if (stage >= MAX_STAGE) return;
		if (!worldIn.canBlockSeeSky(pos)) return;
		ElfTime time = new ElfTime(worldIn);
		if (time.at(ElfTime.Period.MIDNIGHT)) {
			if (rand.nextInt(3) != 0) return;
			this.grow(worldIn, rand, pos, state);
		} else if (time.at(ElfTime.Period.DAY)) {
			worldIn.setBlockState(pos, state.withProperty(STAGE, STAGE_WITHERED));
		}

	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (worldIn.isRemote) return;
		int stage = state.getValue(STAGE);
		if (stage != MAX_STAGE) return;
		if (entityIn instanceof EntityItem) {
			ItemStack stack = ((EntityItem) entityIn).getItem();
			if (stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null) != null) {
				stack.shrink(1);
				ElfTime time = new ElfTime(worldIn);
				if (worldIn.rand.nextFloat() < 0.75 && time.at(ElfTime.Period.MIDNIGHT))
					worldIn.setBlockState(pos, state.withProperty(STAGE, STAGE_ELEMENT));
				FireworkEffect.spawn(worldIn, pos, 0, 1, 0.1f, new int[] { 0xe5eef5, 0xd0e3ee, 0x2bb9b0 },
						new int[] { 0xf1f8ff });
			}
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		int stage = state.getValue(STAGE);
		if (stage == STAGE_ELEMENT) drops.add(new ItemStack(ESInit.BLOCKS.STAR_FLOWER));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
		int stage = stateIn.getValue(STAGE);
		if (stage == MAX_STAGE) {
			if (rand.nextInt(3) != 0) return;
			double x = pos.getX() + 0.5 + rand.nextGaussian() * 0.2;
			double y = pos.getY() + 0.7 + rand.nextFloat() * 0.15;
			double z = pos.getZ() + 0.5 + rand.nextGaussian() * 0.2;
			EffectElementMove effect = new EffectElementMove(world, new Vec3d(x, y, z));
			effect.yAccelerate = -0.0005;
			effect.setColor(229 / 255f, 238 / 255f, 245 / 255f);
			Effect.addEffect(effect);
		}
	}

	@Override
	public String apply(ItemStack var1) {
		int stage = var1.getMetadata();
		if (stage == 0) return "seed";
		return "normal";
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, MAX_STAGE));
		items.add(new ItemStack(this, 1, STAGE_ELEMENT));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return meta == STAGE_ELEMENT ? new TileStarFlower() : null;
	}
}
