package yuzunyannn.elementalsorcery.building;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.util.ESFakePlayer;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class PutBlock {

	protected World world;
	protected boolean forcePut;
	protected ItemStack stack = ItemStack.EMPTY;
	protected EnumFacing facing = EnumFacing.NORTH;
	protected IBlockState state;
	protected EntityPlayer player;
	protected NBTTagCompound tileSave;

	public PutBlock(World world) {
		this.world = world;
	}

	public PutBlock setBlockState(IBlockState state) {
		this.state = state;
		return this;
	}

	public PutBlock setItemStack(ItemStack stack) {
		this.stack = stack;
		return this;
	}

	public PutBlock setFacing(EnumFacing facing) {
		this.facing = facing;
		return this;
	}

	public PutBlock setForce(boolean forcePut) {
		this.forcePut = forcePut;
		return this;
	}

	public PutBlock setPlayer(EntityPlayer player) {
		this.player = player;
		return this;
	}

	public PutBlock setTileSave(NBTTagCompound tileSave) {
		this.tileSave = tileSave;
		return this;
	}

	public ItemStack execute(BlockPos to) {
		if (!BlockHelper.isReplaceBlock(world, to) && !forcePut) return stack;
		facing = facing == null ? getFacingFromState(state) : facing;

		Item item = stack.getItem();
		if (item instanceof ItemBlock) return putItemBlock(to, (ItemBlock) item);
		else if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) != null)
			return putFluid(to);

		if (state == null) return stack;

		if (item == Items.BED) return putBed(to);
		else if (item instanceof ItemDoor) {
			ItemDoor.placeDoor(world, to, facing, state.getBlock(), false);
			return ItemStack.EMPTY;
		} else {
			world.setBlockState(to, state);
			loadTileSave(to, true);
			return ItemStack.EMPTY;
		}
	}

	protected ItemStack putFluid(BlockPos to) {
		IFluidHandlerItem fhi = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		FluidStack fstack = fhi.drain(1000, true);
		if (fstack != null) {
			IBlockState fluidState = fstack.getFluid().getBlock().getDefaultState();
			if (fluidState.getBlock() instanceof BlockStaticLiquid) {
				fluidState = BlockStaticLiquid.getFlowingBlock(fluidState.getMaterial()).getDefaultState();
				world.setBlockState(to, fluidState);
			} else world.setBlockState(to, fluidState);
		}
		return fhi.getContainer();
	}

	protected ItemStack putItemBlock(BlockPos to, ItemBlock itemBlock) {
		IBlockState toState = itemBlock.getBlock().getStateFromMeta(stack.getItemDamage());

		if (world.isRemote) {
			world.setBlockState(to, toState);
			return ItemStack.EMPTY;
		}

		Block block = toState.getBlock();

		if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
			// 活塞
			if (state != null) toState = state.withProperty(BlockPistonBase.EXTENDED, false);
			else toState = toState.withProperty(BlockPistonBase.FACING, facing);
			world.setBlockState(to, toState);
			return ItemStack.EMPTY;
		}

		if (state != null) {
			if (state.getBlock() == Blocks.UNLIT_REDSTONE_TORCH) {
				// 红石火把
				facing = state.getValue(BlockRedstoneTorch.FACING);
				toState = toState.withProperty(BlockRedstoneTorch.FACING, facing);
			} else {
				if (itemBlock instanceof ItemSlab) toState = state;
				else if (itemBlock.getBlock() == state.getBlock()) toState = state;
			}
		}

		boolean needPlace = false;
		needPlace = needPlace || block instanceof BlockDoublePlant || block instanceof ITileEntityProvider;

		if (needPlace) {
			if (player == null) player = ESFakePlayer.get((WorldServer) world);
			itemBlock.placeBlockAt(stack, player, world, to, EnumFacing.UP, 0, 0, 0, toState);
			state = world.getBlockState(to);
			if (state != toState && state.getBlock() == toState.getBlock()) world.setBlockState(to, toState, 2);
			loadTileSave(to, (player == null) || player.isCreative() || player instanceof FakePlayer);
			return ItemStack.EMPTY;
		}

		world.setBlockState(to, toState);
		loadTileSave(to, true);
		return ItemStack.EMPTY;
	}

	protected ItemStack putBed(BlockPos to) {
		facing = state.getValue(BlockBed.FACING);
		world.setBlockState(to, state);
		world.setBlockState(to.offset(facing), state.cycleProperty(BlockBed.PART));
		TileEntityBed bed = BlockHelper.getTileEntity(world, to, TileEntityBed.class);
		if (bed != null) bed.setColor(EnumDyeColor.byMetadata(stack.getMetadata()));
		bed = BlockHelper.getTileEntity(world, to.offset(facing), TileEntityBed.class);
		if (bed != null) bed.setColor(EnumDyeColor.byMetadata(stack.getMetadata()));
		return ItemStack.EMPTY;
	}

	protected void loadTileSave(BlockPos to, boolean byInit) {
		if (tileSave == null) return;
		TileEntity tile = world.getTileEntity(to);
		if (tile == null) return;
		tileSave = tileSave.copy();
		if (byInit) tileSave.setBoolean("_template_", true);
		NBTTagCompound nbt = EntityBlockMove.handleTileSave(tileSave, tile);
		tile.deserializeNBT(nbt);
	}

	/** 根据blockState获取面向 */
	public static EnumFacing getFacingFromState(IBlockState state) {
		if (state == null) return EnumFacing.NORTH;
		Collection<IProperty<?>> properties = state.getPropertyKeys();
		for (IProperty<?> property : properties) {
			if (property instanceof PropertyDirection) {
				EnumFacing facing = state.getValue((PropertyDirection) property);
				if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) return EnumFacing.NORTH;
				return facing == null ? EnumFacing.NORTH : facing;
			}
		}
		return EnumFacing.NORTH;
	}

}
