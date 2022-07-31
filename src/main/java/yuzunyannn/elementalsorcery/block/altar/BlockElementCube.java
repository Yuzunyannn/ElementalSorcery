package yuzunyannn.elementalsorcery.block.altar;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.particle.ESParticleDigging;
import yuzunyannn.elementalsorcery.tile.altar.TileElementalCube;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryStronger;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;

public class BlockElementCube extends BlockElementContainer {

	// 获取带有能力的物品，规定函数用于反射
	public ItemBlock getItemBlock() {
		ItemBlock item = new ItemBlock(this) {
			@Override
			public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
				return new CapabilityProvider.ElementInventoryUseProvider(stack, new ElementInventoryStronger(1));
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

	public BlockElementCube() {
		super(Material.GLASS, "elementalCube", 0, MapColor.CYAN);
		this.setLightLevel(0.5F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileElementalCube();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return super.getBoundingBox(state, source, pos);
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
	@Override
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
		TileElementalCube cube = BlockHelper.getTileEntity(worldIn, pos, TileElementalCube.class);
		if (cube == null) return true;
		if (worldIn.isRemote) {
			if (cube.wake <= 0) cube.colorRate = 1;
//			if (ElementalSorcery.isDevelop) cube.wake(0, pos);
			return true;
		}
		return cube.getElementInventory().openTerminal(worldIn, pos, playerIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
		TileElementalCube tile = BlockHelper.getTileEntity(world, pos, TileElementalCube.class);
		Vec3d color = tile.getBaseColor();
		IBlockState state = world.getBlockState(pos);
		for (int j = 0; j < 4; ++j) {
			for (int k = 0; k < 4; ++k) {
				for (int l = 0; l < 4; ++l) {
					double d0 = ((double) j + 0.5D) / 4.0D;
					double d1 = ((double) k + 0.5D) / 4.0D;
					double d2 = ((double) l + 0.5D) / 4.0D;
					ESParticleDigging effect = new ESParticleDigging(world, pos.getX() + d0, pos.getY() + d1,
							pos.getZ() + d2, d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, state);
					effect.setBlockPos(pos);
					if (Effect.rand.nextInt(10) == 1 && tile.color != Vec3d.ZERO)
						effect.setRBGColorF((float) tile.color.x, (float) tile.color.y, (float) tile.color.z);
					else effect.setRBGColorF((float) color.x, (float) color.y, (float) color.z);
					manager.addEffect(effect);
				}
			}
		}
		return true;
	}

	@Override
	public void readTileDataFromItemStack(IBlockAccess world, BlockPos pos, EntityLivingBase user, TileEntity tile,
			ItemStack stack) {
		super.readTileDataFromItemStack(world, pos, user, tile, stack);
		if (tile instanceof TileElementalCube) ((TileElementalCube) tile).setDyeColor(getDyeColor(stack));
	}

	@Override
	public void writeTileDataToItemStack(IBlockAccess world, BlockPos pos, EntityLivingBase user, TileEntity tile,
			ItemStack stack) {
		super.writeTileDataToItemStack(world, pos, user, tile, stack);
		if (tile instanceof TileElementalCube) setDyeColor(stack, ((TileElementalCube) tile).getDyeColor());
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
