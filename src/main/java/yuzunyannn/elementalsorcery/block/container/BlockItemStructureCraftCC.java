package yuzunyannn.elementalsorcery.block.container;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.element.IISCraftHanlder;
import yuzunyannn.elementalsorcery.block.BlockInvalidEnchantmentTable;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftCC;

public class BlockItemStructureCraftCC extends BlockContainerNormal {

	public BlockItemStructureCraftCC() {
		super(Material.ROCK, "ISCraftCC", 3.5F, MapColor.QUARTZ);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileItemStructureCraftCC();
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BlockInvalidEnchantmentTable.AABB;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return true;
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_IS_CRAFT_CC, worldIn, pos.getX(), pos.getY(),
				pos.getZ());
		return true;
	}

	@SideOnly(Side.CLIENT)
	public WeakReference<List<ItemStack>> canMarkStacks;

	@SideOnly(Side.CLIENT)
	public List<ItemStack> getMarkList() {
		List<ItemStack> list = canMarkStacks == null ? null : canMarkStacks.get();
		if (list != null) return list;
		list = new ArrayList<ItemStack>();
		canMarkStacks = new WeakReference<>(list);
		for (Item item : Item.REGISTRY) {
			NonNullList<ItemStack> getList = NonNullList.create();
			item.getSubItems(CreativeTabs.SEARCH, getList);
			for (ItemStack stack : getList) next: {
				for (IISCraftHanlder handler : TileItemStructureCraftCC.handlerMap.values()) {
					if (handler.isKeyItem(stack)) {
						list.add(stack);
						break next;
					}
				}
			}
		}
		return list;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		List<ItemStack> list = getMarkList();
		if (list.isEmpty()) return;
		int index = Math.abs(EventClient.tick / 20);
		for (int i = 0; i < Math.min(4, list.size()); i++) {
			ItemStack itemStack = list.get((i + index) % list.size());
			String namespace = itemStack.getItem().getRegistryName().getNamespace();
			tooltip.add(namespace + ":" + itemStack.getDisplayName());
		}
	}

}
