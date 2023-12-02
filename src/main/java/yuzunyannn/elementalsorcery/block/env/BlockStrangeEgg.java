package yuzunyannn.elementalsorcery.block.env;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class BlockStrangeEgg extends Block {

	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.3, 0.0, 0.3, 0.7, 0.5, 0.7);
	public static final PropertyInteger STATE = PropertyInteger.create("state", 0, 2);
	public static final int STATE_CONT = 3;

	final public boolean isDead;

	public BlockStrangeEgg(boolean isDead) {
		super(Material.ROCK);
		this.isDead = isDead;
		this.setSoundType(SoundType.SLIME);
		this.setTranslationKey("strangeEgg" + (isDead ? "Deading" : ""));
		this.setTickRandomly(true);
	}

	public ItemBlock getItemBlock() {
		return new ItemBlock(this) {
			@Override
			public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
					EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
				if (newState.getBlock() == this.block) {
					newState = newState.withProperty(STATE, Math.abs(pos.hashCode()) % STATE_CONT);
				}
				return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
			}
		};
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { STATE });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(STATE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(STATE);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB.offset(state.getOffset(source, pos));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return AABB.offset(blockState.getOffset(worldIn, pos));
	}

	@Override
	public EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.isSideSolid(pos.down(), EnumFacing.UP) && super.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (fromPos.getY() + 1 == pos.getY()) {
			if (!worldIn.isSideSolid(pos.up(), EnumFacing.UP)) {
				worldIn.destroyBlock(pos, false);
				if (worldIn.rand.nextFloat() < 0.8) return;
				onDestroy(worldIn, fromPos, null);
			}
		}
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te,
			ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		onDestroy(worldIn, pos, player);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		super.onEntityCollision(worldIn, pos, state, entityIn);
		if (entityIn instanceof EntityLivingBase) {
			//这个函数有坑，这个pos传入的是BlockPos.MutableBlockPos
			//在单机模式下，destroyBlock发送消息会直接引用pos，并直接把消息引用透传client线程
			//这就导致了client处理使用了BlockPos.MutableBlockPos，同时server线程也在使用，就G了
			pos = new BlockPos(pos); 
			worldIn.destroyBlock(pos, true);
			onDestroy(worldIn, pos, (EntityLivingBase) entityIn);
		}
	}

	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
		if (worldIn.rand.nextFloat() < 0.25) return;
		onDestroy(worldIn, pos, null);
	}

	public void onDestroy(World worldIn, BlockPos pos, @Nullable EntityLivingBase destroyer) {
		if (worldIn.isRemote) return;
		if (this.isDead) return;

		Random rand = worldIn.rand;
		if (rand.nextDouble() > 0.75) {
			ItemHelper.dropItem(worldIn, pos, new ItemStack(ESObjects.ITEMS.ELF_CRYSTAL, rand.nextInt(4) + 1));
		} else {
			int power = rand.nextInt(200) + 100;
			int n = rand.nextInt(6);
			ElementStack eStack;
			switch (n) {
			case 0:
				eStack = new ElementStack(ESObjects.ELEMENTS.AIR, 800, power);
				break;
			case 1:
				eStack = new ElementStack(ESObjects.ELEMENTS.EARTH, 800, power);
				break;
			case 2:
				eStack = new ElementStack(ESObjects.ELEMENTS.METAL, 800, power);
				break;
			case 3:
				eStack = new ElementStack(ESObjects.ELEMENTS.FIRE, 400, power);
				break;
			default:
				eStack = new ElementStack(ESObjects.ELEMENTS.WOOD, 800, power);
				break;
			}
			ElementExplosion.doExplosion(worldIn, new Vec3d(pos).add(0.5, 0.25, 0.5), eStack, null);
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (this.isDead) {
			IBlockState myState = state;
			int range = rand.nextFloat() < 0.25 ? 3 : 2;
			for (int x = -range; x <= range; x++) {
				for (int z = -range; z <= range; z++) {
					for (int y = -1; y <= 1; y++) {
						BlockPos at = pos.add(x, y, z);
						IBlockState atState = worldIn.getBlockState(at);
						if (atState.getBlock() == ESObjects.BLOCKS.STRANGE_EGG) {
							worldIn.setBlockState(at, myState.withProperty(STATE, atState.getValue(STATE)));
						}
					}
				}
			}
			worldIn.destroyBlock(pos, false);
		} else {
			pos = pos.add(rand.nextInt(5) - 2, 0, rand.nextInt(5) - 2);
			if (!BlockHelper.isReplaceBlock(worldIn, pos)) {
				pos = pos.up();
				if (!BlockHelper.isReplaceBlock(worldIn, pos)) return;
			} else if (BlockHelper.isReplaceBlock(worldIn, pos.down())) pos = pos.down();

			if (worldIn.isSideSolid(pos.down(), EnumFacing.UP)) {
				worldIn.setBlockState(pos, state.withProperty(STATE, Math.abs(pos.hashCode()) % STATE_CONT));
			}
		}
	}

	public static void handlePoison(World world, BlockPos pos, ItemStack potion) {
		if (world.rand.nextInt(10) != 0) return;
		for (int x = -1; x <= 0; x++) {
			for (int z = -1; z <= 0; z++) {
				BlockPos at = pos.add(x, 0, z);
				IBlockState state = world.getBlockState(at);
				if (state.getBlock() == ESObjects.BLOCKS.STRANGE_EGG) {
					IBlockState newState = ESObjects.BLOCKS.STRANGE_EGG_DEAD.getDefaultState();
					newState = newState.withProperty(BlockStrangeEgg.STATE, state.getValue(BlockStrangeEgg.STATE));
					world.setBlockState(at, newState);
				}
			}
		}
	}

}
