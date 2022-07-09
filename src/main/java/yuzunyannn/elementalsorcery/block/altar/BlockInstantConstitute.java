package yuzunyannn.elementalsorcery.block.altar;

import java.util.List;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.tile.altar.TileInstantConstitute;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class BlockInstantConstitute extends BlockContainerNormal {

	public BlockInstantConstitute() {
		super(Material.ROCK, "instantConstitute", 5.5F, MapColor.QUARTZ);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileInstantConstitute();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		TileInstantConstitute tile = BlockHelper.getTileEntity(worldIn, pos, TileInstantConstitute.class);
		if (tile == null) return false;
		if (tile.doConstitute(stack)) {
			if (player instanceof EntityPlayerMP) {
				ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "use:instantConstitute");
				ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "construct:start");
			}
			return true;
		}
		return false;
	}

	@Override
	public void writeTileDataToItemStack(IBlockAccess world, BlockPos pos, EntityLivingBase user, TileEntity tile,
			ItemStack stack) {
		super.writeTileDataToItemStack(world, pos, user, tile, stack);
		if (tile instanceof TileInstantConstitute) {
			TileInstantConstitute ic = (TileInstantConstitute) tile;
			NBTTagCompound nbt = stack.getOrCreateSubCompound("IConsDat");
			nbt.setInteger("oVal", ic.getOrderVal());
			nbt.setInteger("omVal", ic.getMaxOrderVal());
		}
	}

	@Override
	public void readTileDataFromItemStack(IBlockAccess world, BlockPos pos, EntityLivingBase user, TileEntity tile,
			ItemStack stack) {
		super.readTileDataFromItemStack(world, pos, user, tile, stack);
		if (tile instanceof TileInstantConstitute) {
			TileInstantConstitute ic = (TileInstantConstitute) tile;
			NBTTagCompound nbt = stack.getOrCreateSubCompound("IConsDat");
			ic.setOrderVal(nbt.getInteger("oVal"));
			ic.setMaxOrderVal(nbt.getInteger("omVal"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getOrCreateSubCompound("IConsDat");
		int orderVal = nbt.getInteger("oVal");
		int orderMaxVal = Math.max(64, nbt.getInteger("omVal"));
		String str = String.format("|↕| %s/%s", TextHelper.toAbbreviatedNumber(orderVal),
				TextHelper.toAbbreviatedNumber(orderMaxVal));
		tooltip.add(TextFormatting.BLUE + str);
	}

}
