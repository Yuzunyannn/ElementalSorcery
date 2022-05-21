package yuzunyannn.elementalsorcery.util.helper;

import java.util.function.BiFunction;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.util.ESFakePlayer;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class BlockHelper {
	/** 获取tile实体 */
	public static <T> T getTileEntity(IBlockAccess world, BlockPos pos, Class<T> type) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && type.isAssignableFrom(tile.getClass())) return (T) tile;
		return null;
	}

	/** 掉落 */
	public static void drop(IItemHandler itemHandler, World worldIn, BlockPos pos) {
		if (itemHandler == null) return;
		for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
			if (!itemHandler.getStackInSlot(i).isEmpty()) {
				Block.spawnAsEntity(worldIn, pos, itemHandler.getStackInSlot(i));
				if (itemHandler instanceof IItemHandlerModifiable)
					((IItemHandlerModifiable) itemHandler).setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}

	/** 掉落，只进行EnumFace中null的获取 */
	public static void drop(World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile == null) return;
		IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		BlockHelper.drop(itemHandler, worldIn, pos);
	}

	public static void destroyBlock(World world, BlockPos pos, boolean silkHarvest, int fortune) {
		if (world.isAirBlock(pos)) return;
		ItemStack silkTool = new ItemStack(Items.WOODEN_PICKAXE);
		FakePlayer fakePlayer = ESFakePlayer.get((WorldServer) world);
		if (silkHarvest) silkTool.addEnchantment(Enchantments.SILK_TOUCH, 1);
		else ItemHelper.addEnchantment(silkTool, Enchantments.FORTUNE, fortune);
		IBlockState iblockstate = world.getBlockState(pos);
		Block block = iblockstate.getBlock();
		block.harvestBlock(world, fakePlayer, pos, iblockstate, null, silkTool);
		world.setBlockToAir(pos);
		world.playEvent(2001, pos, Block.getStateId(iblockstate));
	}

	/** 方块激活的时候，处理继承IGetItemStack的物品栈 */
	public static boolean onBlockActivatedWithIGetItemStack(World worldIn, BlockPos pos, IBlockState state,
			EntityPlayer playerIn, EnumHand hand, boolean justOne) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (stack.isEmpty()) {
			if (hand != EnumHand.MAIN_HAND) return false;
			IGetItemStack tile = (IGetItemStack) worldIn.getTileEntity(pos);
			stack = tile.getStack();
			if (stack.isEmpty()) return false;
			if (!worldIn.isRemote) {
				if (justOne || playerIn.isSneaking()) tile.setStack(ItemStack.EMPTY);
				else {
					ItemStack get = stack.splitStack(1);
					tile.setStack(stack);
					stack = get;
				}
				boolean ok = false;
				if (playerIn.inventory.getStackInSlot(playerIn.inventory.currentItem).isEmpty())
					ok = playerIn.inventory.add(playerIn.inventory.currentItem, stack);
				else ok = playerIn.inventory.addItemStackToInventory(stack);
				if (!ok) Block.spawnAsEntity(worldIn, pos, stack);
			}
			return true;
		}
		IGetItemStack tile = (IGetItemStack) worldIn.getTileEntity(pos);
		if (!tile.canSetStack(stack)) return false;
		if (tile.getStack().isEmpty()) {
			if (!worldIn.isRemote) {
				if (justOne) {
					ItemStack inStack = ItemStack.EMPTY;
					inStack = stack.copy();
					inStack.setCount(1);
					stack.shrink(1);
					tile.setStack(inStack);
				} else {
					playerIn.setHeldItem(hand, ItemStack.EMPTY);
					tile.setStack(stack);
				}
			}
		} else {
			if (justOne) return false;
			ItemStack inStack = tile.getStack();
			if (!ItemHandlerHelper.canItemStacksStack(inStack, stack)) return false;
			int count = inStack.getCount();
			count++;
			if (count > inStack.getMaxStackSize()) return false;
			if (!worldIn.isRemote) {
				inStack.setCount(count);
				stack.shrink(1);
				tile.setStack(inStack);
			}
		}
		return true;
	}

	/** 方块破碎的时候，掉落继承IGetItemStack的物品 */
	public static void dropWithGetItemStack(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof IGetItemStack && !worldIn.isRemote) {
			IGetItemStack tile = (IGetItemStack) tileentity;
			ItemStack stack = tile.getStack();
			if (!stack.isEmpty()) Block.spawnAsEntity(worldIn, pos, stack);
		}
	}

	/** 向指定方块插入物品 */
	static public ItemStack insertInto(World world, BlockPos pos, EnumFacing face, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) return stack;
		IItemHandler heandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
		return insertInto(heandler, stack);
	}

	static public ItemStack insertInto(IItemHandler heandler, ItemStack stack) {
		if (heandler == null) return stack;
		for (int i = 0; i < heandler.getSlots(); i++) {
			stack = heandler.insertItem(i, stack, false);
			if (stack.isEmpty()) return stack;
		}
		return stack;
	}

	static public IItemHandler getItemHandler(World world, BlockPos pos, EnumFacing face) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) return null;
		return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
	}

	static public IElementInventory getElementInventory(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return ElementHelper.getElementInventory(world.getTileEntity(pos));
	}

	/** 尝试寻找某个方块 */
	static public BlockPos tryFind(World world, BiFunction<World, BlockPos, Boolean> testFun, BlockPos origin,
			int tryTimes, int width, int height) {
		int w = 2;
		int h = 2;
		for (int t = 0; t < tryTimes; t++) {
			int x = Math.round((w * 0.5f - RandomHelper.rand.nextFloat() * w));
			int z = Math.round((w * 0.5f - RandomHelper.rand.nextFloat() * w));
			int y = Math.round((h * 0.5f - RandomHelper.rand.nextFloat() * h));
			BlockPos pos = origin.add(x, y, z);
			if (testFun.apply(world, pos)) return pos;
			w = Math.min(MathHelper.ceil(w * 1.5f), width * 2);
			h = Math.min(MathHelper.ceil(h * 1.5f), height * 2);
		}
		return null;
	}

	static public BlockPos tryFind(World world, IBlockState needState, BlockPos origin, int tryTimes, int width,
			int height) {
		return tryFind(world, (worldIn, pos) -> {
			return worldIn.getBlockState(pos) == needState;
		}, origin, tryTimes, width, height);
	}

	static public BlockPos tryFindAnySolid(World world, BlockPos origin, int tryTimes, int width, int height) {
		return tryFind(world, (worldIn, pos) -> {
			IBlockState state = worldIn.getBlockState(pos);
			return state.isFullBlock();
		}, origin, tryTimes, width, height);
	}

	static public boolean hasKeyInOreDictionary(ItemStack woodStack, String key) {
		return getOreName(woodStack).toLowerCase().lastIndexOf(key) != -1;
	}

	static public String getOreName(ItemStack oreStack) {
		if (oreStack.isEmpty()) return "";
		int[] ore = OreDictionary.getOreIDs(oreStack);
		if (ore == null || ore.length == 0) return "";
		String name = OreDictionary.getOreName(ore[0]);
		return name == null ? "" : name;
	}

	public static boolean isSolidBlock(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state.isFullBlock() && state.isFullCube() && state.isOpaqueCube();
	}

	public static boolean isReplaceBlock(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock().isReplaceable(world, pos);
	}

	public static boolean isPassableBlock(World world, BlockPos pos) {
		if (world.isAirBlock(pos)) return true;
		return isReplaceBlock(world, pos);
	}

	public static boolean isFluid(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state.getMaterial().isLiquid();
	}

	public static boolean isBedrock(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial().isLiquid()) return false;
		return state.getBlockHardness(world, pos) < 0;
	}

	public static boolean isHardnessLower(World world, BlockPos pos, float n) {
		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial().isLiquid()) return true;
		float h = state.getBlockHardness(world, pos);
		if (h < 0) return false;
		return h < n;
	}

}
