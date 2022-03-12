package yuzunyannn.elementalsorcery.block.altar;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.altar.TileElementalCube;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryLimit;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;

public class BlockElementalCube extends BlockElementContainer {

	// 获取带有能力的物品，规定函数用于反射
	public ItemBlock getItemBlock() {
		ItemBlock item = new ItemBlock(this) {
			@Override
			public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
				return new CapabilityProvider.ElementInventoryUseProvider(stack, new ElementInventoryLimit(1));
			}

			@Override
			public String getItemStackDisplayName(ItemStack stack) {
				String name = super.getItemStackDisplayName(stack);
				EnumDyeColor color = getDyeColor(stack);
				if (color != null) {
					String colorName = I18n.translateToLocal("item.fireworksCharge." + color.getTranslationKey());
					name = name + ColorHelper.toTextFormatting(color) + " " + colorName;
				}
				return name;
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
		super(Material.GLASS, MapColor.CYAN);
		this.setTranslationKey("elementalCube");
		this.setLightLevel(0.75F);
		this.setHardness(0.5f);
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
		yes = yes && canPlaceBlockAtAround(worldIn, pos);
//		IBlockState state = worldIn.getBlockState(pos.down());
//		yes = yes && state.isFullCube() && state.isOpaqueCube();
		return yes;
	}

	public boolean canPlaceBlockAtAround(World worldIn, BlockPos pos) {
		boolean yes = true;
		yes = yes && worldIn.isAirBlock(pos.north());
		yes = yes && worldIn.isAirBlock(pos.south());
		yes = yes && worldIn.isAirBlock(pos.west());
		yes = yes && worldIn.isAirBlock(pos.east());
		yes = yes && worldIn.isAirBlock(pos.up());
		return yes;
	}

	// 周围改变
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!this.canPlaceBlockAtAround(worldIn, pos)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		List<Element> list = Element.REGISTRY.getValues();
		for (Element e : list) {
			if (e == ESInit.ELEMENTS.VOID) {
				for (EnumDyeColor dye : EnumDyeColor.values()) {
					ItemStack stack = new ItemStack(this);
					items.add(setDyeColor(stack, dye));
				}
			}
			ItemStack stack = new ItemStack(this);
			ElementStack estack = new ElementStack(e, 10000, 1000);
			IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
			inventory.setStackInSlot(0, estack);
			inventory.saveState(stack);
			items.add(stack);

		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			TileElementalCube cube = BlockHelper.getTileEntity(worldIn, pos, TileElementalCube.class);
			if (cube != null && cube.wake <= 0) cube.colorRate = 1;
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase player,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, player, stack);
		TileElementalCube cube = BlockHelper.getTileEntity(worldIn, pos, TileElementalCube.class);
		if (cube == null) return;
		cube.setDyeColor(getDyeColor(stack));
	}

	@Override
	protected void modifyDropStack(IBlockAccess world, BlockPos pos, ItemStack stack, TileEntity originTile) {
		super.modifyDropStack(world, pos, stack, originTile);
		if (originTile instanceof TileElementalCube) setDyeColor(stack, ((TileElementalCube) originTile).getDyeColor());
	}

	@Nullable
	public static EnumDyeColor getDyeColor(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return null;
		if (!nbt.hasKey("colorMeta")) return null;
		return EnumDyeColor.byMetadata(nbt.getByte("colorMeta"));
	}

	public static ItemStack setDyeColor(ItemStack stack, EnumDyeColor color) {
		if (color == null) return stack;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		nbt.setByte("colorMeta", (byte) color.getMetadata());
		return stack;
	}

	public static Color toColorType(EnumDyeColor dye) {
		if (dye == null) return Color.defaultColor;
		Vec3d color = ColorHelper.color(dye.getColorValue());
		color = new Vec3d(color.x * 0.5, color.y * 0.5, color.z * 0.5);
		return new Color(dye.getColorValue(), ColorHelper.color(color));
	}

	public static class Color {

		public static final Color defaultColor = new Color(0x48fff9, 0x1d736d);

		final Vec3d base;
		final Vec3d cover;

		public Color(int base, int cover) {
			this.base = ColorHelper.color(base);
			this.cover = ColorHelper.color(cover);
		}

		public Vec3d getBaseColor() {
			return base;
		}

		public Vec3d getCoverColor() {
			return cover;
		}
	}
}
