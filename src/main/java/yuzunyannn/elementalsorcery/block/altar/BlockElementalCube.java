package yuzunyannn.elementalsorcery.block.altar;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.tile.altar.TileElementalCube;

public class BlockElementalCube extends BlockElementContainer {

	// 获取带有能力的物品，规定函数用于反射
	public ItemBlock getItemBlock() {
		ItemBlock item = new ItemBlock(this) {
			@Override
			public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
				return new CapabilityProvider.ElementInventoryUseProvider(stack);
			}
		};
		item.setMaxStackSize(1);
		return item;
	}

	// 方块一半的边长
	public static final double BLOCK_HALF_SIZE = 0.70710678118 * 0.5;
	protected static final AxisAlignedBB AXIS_BOX = new AxisAlignedBB(0.5F - BLOCK_HALF_SIZE, 0.0F,
			0.5F - BLOCK_HALF_SIZE, 0.5F + BLOCK_HALF_SIZE, 1.0D + (0.5F - BLOCK_HALF_SIZE) * 0.5,
			0.5F + BLOCK_HALF_SIZE);

	public BlockElementalCube() {
		super(Material.GLASS);
		this.setUnlocalizedName("elementalCube");
		this.setLightLevel(0.75F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileElementalCube();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	// 放置判定
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		boolean yes = super.canPlaceBlockAt(worldIn, pos);
		yes = yes && !worldIn.isAirBlock(pos.down());
		new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ() - 1);
		yes = yes && worldIn.isAirBlock(pos.north());
		yes = yes && worldIn.isAirBlock(pos.south());
		yes = yes && worldIn.isAirBlock(pos.west());
		yes = yes && worldIn.isAirBlock(pos.east());
		yes = yes && worldIn.isAirBlock(pos.up());

		IBlockState state = worldIn.getBlockState(pos.down());
		yes = yes && state.isFullCube() && state.isOpaqueCube();

		return yes;
	}

	// 周围改变
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!this.canPlaceBlockAt(worldIn, pos)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		List<Element> list = Element.REGISTRY.getValues();
		for (Element e : list) {
			ItemStack stack = new ItemStack(this);
			ElementStack estack = new ElementStack(e, 10000, 1000);
			IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
			inventory.setStackInSlot(0, estack);
			inventory.saveState(stack);
			items.add(stack);
		}
	}
}
