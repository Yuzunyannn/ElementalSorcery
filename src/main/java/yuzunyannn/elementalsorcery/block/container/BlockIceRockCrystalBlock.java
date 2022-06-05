package yuzunyannn.elementalsorcery.block.container;

import java.util.List;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockCrystalBlock;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockStand;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockIceRockCrystalBlock extends BlockIceRockSendRecv {

	public static final PropertyEnum<EnumStatus> STATUS = PropertyEnum.create("status", EnumStatus.class);

	public BlockIceRockCrystalBlock() {
		super(Material.GLASS, "iceRockCrystalBlock", 5.5f, MapColor.LIGHT_BLUE_STAINED_HARDENED_CLAY);
		setDefaultState(blockState.getBaseState().withProperty(STATUS, EnumStatus.NORMAL));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileIceRockCrystalBlock();
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(Item.getItemFromBlock(this), 1, 0);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return state.getValue(STATUS) == EnumStatus.ACTIVE ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(STATUS) == EnumStatus.ACTIVE ? 12 : 1;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return state.getValue(STATUS) == EnumStatus.ACTIVE ? false : true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return state.getValue(STATUS) == EnumStatus.ACTIVE ? false : true;
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, STATUS);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(STATUS, EnumStatus.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(STATUS).ordinal();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this));
		ItemStack itemStack = new ItemStack(this);
		NBTTagCompound tag;
		itemStack.setTagCompound(tag = new NBTTagCompound());
		tag.setDouble("magicFragment", Math.pow(10, 12));
		items.add(itemStack);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileIceRockCrystalBlock tileCrystalBlock = BlockHelper.getTileEntity(worldIn, pos,
				TileIceRockCrystalBlock.class);
		tileCrystalBlock.canNotLinkMark = true;
		TileIceRockStand stand = null;
		for (int i = 0; i < BlockIceRockStand.TOWER_MAX_HEIGHT; i++) {
			BlockPos at = pos.down(i + 1);
			if (worldIn.isOutsideBuildHeight(at)) break;
			TileEntity tile = worldIn.getTileEntity(at);
			if (tile == null) break;
			if (tile instanceof TileIceRockCrystalBlock) continue;
			if (tile instanceof TileIceRockStand) {
				stand = (TileIceRockStand) tile;
				stand.checkAndBreakStructure();
			}
			break;
		}
		super.breakBlock(worldIn, pos, state);
		if (stand != null) {
			stand.checkAndBuildStructure();
			stand.updateToClient();
		}
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return false;
	}

	@Override
	public void writeTileDataToItemStack(IBlockAccess world, BlockPos pos, EntityLivingBase user, TileEntity tile,
			ItemStack stack) {
		super.writeTileDataToItemStack(world, pos, user, tile, stack);
		if (tile instanceof TileIceRockCrystalBlock) {
			double fragment = ((TileIceRockCrystalBlock) tile).getMagicFragmentOwn();
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
			nbt.setDouble("magicFragment", fragment);
		}
	}

	@Override
	public void readTileDataFromItemStack(IBlockAccess world, BlockPos pos, EntityLivingBase user, TileEntity tile,
			ItemStack stack) {
		super.readTileDataFromItemStack(world, pos, user, tile, stack);
		if (tile instanceof TileIceRockCrystalBlock) {
			TileIceRockCrystalBlock tileCrystalBlock = (TileIceRockCrystalBlock) tile;
			NBTTagCompound nbt = stack.getTagCompound();
			double fragment = nbt != null ? nbt.getDouble("magicFragment") : 0;
			tileCrystalBlock.setMagicFragmentOwn(fragment);
		}
	}

	@Override
	public void onTileBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack, TileEntity te) {
		for (int i = 0; i < BlockIceRockStand.TOWER_MAX_HEIGHT; i++) {
			BlockPos at = pos.down(i + 1);
			if (worldIn.isOutsideBuildHeight(at)) break;
			TileEntity tile = worldIn.getTileEntity(at);
			if (tile == null) break;
			if (tile instanceof TileIceRockCrystalBlock) continue;
			if (tile instanceof TileIceRockStand) {
				((TileIceRockStand) tile).checkAndBuildStructure();
				((TileIceRockStand) tile).updateToClient();
			}
			break;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		double fragment = nbt != null ? nbt.getDouble("magicFragment") : 0;
		tooltip.add(I18n.format("info.magic.fragment") + ": " + TextHelper.toAbbreviatedNumber(fragment));
	}

	public static enum EnumStatus implements IStringSerializable {
		NORMAL("normal"),
		ACTIVE("active");

		private String name;

		private EnumStatus(String material) {
			this.name = material;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}
}
