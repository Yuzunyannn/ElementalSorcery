package yuzunyannn.elementalsorcery.item.crystal;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.BlockItemTypeInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class ItemArchitectureCrystal extends ItemCrystal {

	public ItemArchitectureCrystal() {
		super("architectureCrystal", 32.6f, 0x2e7539);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		ArcInfo info = new ArcInfo(stack, Side.CLIENT);
		if (info.isMiss()) {
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.arcCrystal.miss"));
			return;
		}
		if (!info.isValid()) return;
		tooltip.add(TextFormatting.GOLD + I18n.format("info.arcCrystal.choice"));
		tooltip.add(I18n.format("info.select.axis", info.pos.getX(), info.pos.getY(), info.pos.getZ()));
		tooltip.add(I18n.format("info.arcCrystal.face", info.facing.getName()));
		if (info.building.getBlockTypeInfos().isEmpty()) {
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.arcCrystal.wait"));
			return;
		}
		String author = info.building.getAuthor();
		if (!author.isEmpty()) tooltip.add(I18n.format("info.arcCrystal.author", author));
		tooltip.add(I18n.format("info.arcCrystal.name", info.building.getName()));
		List<BlockItemTypeInfo> list = info.building.getBlockTypeInfos();
		if (list.size() > 10) {
			StringBuilder builder = new StringBuilder();
			for (BlockItemTypeInfo tinfo : list) {
				String str = I18n.format("info.arcCrystal.count", tinfo.getDisplayName(), tinfo.getCount());
				builder.append(str).append(' ');
			}
			tooltip.add(builder.toString());
		} else {
			for (BlockItemTypeInfo tinfo : list) {
				tooltip.add(I18n.format("info.arcCrystal.count", tinfo.getDisplayName(), tinfo.getCount()));
			}
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!player.isSneaking()) return EnumActionResult.PASS;
		ItemStack stack = player.getHeldItem(hand);
		NBTTagCompound nbt = stack.getSubCompound("building");
		if (nbt == null) {
			if (worldIn.isRemote) {
				player.sendMessage(new TextComponentTranslation("info.arcCrystal.blank"));
			}
			return EnumActionResult.PASS;
		}
		pos = pos.up();
		NBTHelper.setBlockPos(nbt, "pos", pos);
		nbt.setByte("face", (byte) player.getHorizontalFacing().ordinal());
		if (worldIn.isRemote) {
			player.sendMessage(
					new TextComponentString(I18n.format("info.posSet.success", pos.getX(), pos.getY(), pos.getZ())));
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;

		items.add(new ItemStack(this, 1, 0));
		ItemStack stack;

		Collection<Building> bs = BuildingLib.instance.getBuildingsFromLib();
		for (Building building : bs) {
			if (building.getKeyName().startsWith("dungeon")) continue;
			stack = new ItemStack(this, 1, 0);
			ArcInfo.initArcInfoToItem(stack, building.getKeyName());
			items.add(stack);
		}
	}

}
