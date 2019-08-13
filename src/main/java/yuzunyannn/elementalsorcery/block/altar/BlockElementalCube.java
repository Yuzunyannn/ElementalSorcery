package yuzunyannn.elementalsorcery.block.altar;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ability.IElementInventory;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.util.ElementHelper;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.init.registries.ElementRegister;
import yuzunyannn.elementalsorcery.tile.TileElementalCube;

public class BlockElementalCube extends BlockContainer {

	// 获取带有能力的物品
	public ItemBlock getItemBlock() {
		ItemBlock item = new ItemBlock(this) {
			@Override
			@Nullable
			public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack,
					@Nullable NBTTagCompound nbt) {
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
	public EnumBlockRenderType getRenderType(IBlockState state) {
		// 不进行绘画，直接使用Tile的绘图
		return EnumBlockRenderType.INVISIBLE;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (stack.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null)) {
			IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
			ElementHelper.addElementInformation(inventory, worldIn, tooltip, flagIn);
		}
	}

	// 不是完成方块哟
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	// 是透明方块哟
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AXIS_BOX;
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
		yes = yes && state.getBlock().isFullCube(state) && state.getBlock().isOpaqueCube(state);

		return yes;
	}

	private TileEntity temp_tile;

	// 破坏方块，因为掉落物即将要删除tile_entity，暂时存储下
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		temp_tile = worldIn.getTileEntity(pos);
		super.breakBlock(worldIn, pos, state);
	}

	// 掉落物
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		ItemStack stack = new ItemStack(this);
		TileEntity tile = temp_tile;
		temp_tile = null;
		if (!(tile instanceof TileElementalCube)) {
			drops.add(stack);
			return;
		}
		TileElementalCube tile_ec = (TileElementalCube) tile;
		tile_ec.toElementInventory(stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null));
		drops.add(stack);

		IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		inventory.getStackInSlot(0).grow(-1);
		inventory.saveState(stack);
	}

	// 当被放置
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase player,
			ItemStack stack) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (!(tile instanceof TileElementalCube))
			return;
		TileElementalCube tile_ec = (TileElementalCube) tile;
		if (player instanceof EntityPlayer) {
			if (((EntityPlayer) player).isCreative()) {
				stack = stack.copy();
			}
		}
		tile_ec.setElementInventory(ElementHelper.getElementInventory(stack));
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
		List<Element> list = ElementRegister.instance.getValues();
		for (Element e : list) {
			ItemStack stack = new ItemStack(this);
			ElementStack estack = new ElementStack(e, 1000, 1000);
			if (estack.isMagic())
				continue;
			IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
			inventory.setStackInSlot(0, estack);
			inventory.saveState(stack);
			items.add(stack);
		}
	}
}
