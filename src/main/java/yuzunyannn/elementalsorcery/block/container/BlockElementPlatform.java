package yuzunyannn.elementalsorcery.block.container;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import yuzunyannn.elementalsorcery.block.altar.BlockElementContainer;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.tile.TileElementPlatform;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryOnlyInsert;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockElementPlatform extends BlockElementContainer {

	public ItemBlock getItemBlock() {
		ItemBlock item = new ItemBlock(this) {
			@Override
			public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
				return new CapabilityProvider.ElementInventoryUseProvider(stack, new ElementInventoryOnlyInsert(4));
			}
		};
		item.setMaxStackSize(1);
		return item;
	}

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 15.0 / 16.0, 1.0D);

	public BlockElementPlatform() {
		super(Material.ROCK, MapColor.QUARTZ);
		this.setHarvestLevel("pickaxe", 1);
		this.setUnlocalizedName("elementPlatform");
		this.setHardness(1.5f);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileElementPlatform();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return BlockHelper.onBlockActivatedWithIGetItemStack(worldIn, pos, state, playerIn, hand, true);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		BlockHelper.dropWithGetItemStack(worldIn, pos, state);
		super.breakBlock(worldIn, pos, state);
	}
}
