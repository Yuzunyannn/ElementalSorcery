package yuzunyan.elementalsorcery.item;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
import yuzunyan.elementalsorcery.building.ArcInfo;
import yuzunyan.elementalsorcery.building.Building;
import yuzunyan.elementalsorcery.building.BuildingLib;
import yuzunyan.elementalsorcery.util.NBTHelper;

public class ItemArchitectureCrystal extends Item {

	public ItemArchitectureCrystal() {
		this.setUnlocalizedName("architectureCrystal");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ArcInfo info = new ArcInfo(stack, Side.CLIENT);
		if (!info.isValid())
			return;
		if (info.isMiss()) {
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.arcCrystal.miss"));
			return;
		}
		tooltip.add("§e" + I18n.format("info.arcCrystal.choice"));
		tooltip.add(I18n.format("info.arcCrystal.name", info.keyName));
		tooltip.add(I18n.format("info.arcCrystal.axis", info.pos.getX(), info.pos.getY(), info.pos.getZ()));

		if (info.building == null) {
			tooltip.add("You don't get this builing info from server!");
			return;
		}
		List<Building.BlockItemTypeInfo> list = info.building.getBlockTypeInfos();
		for (Building.BlockItemTypeInfo tinfo : list) {
			tooltip.add(I18n.format("info.arcCrystal.count", I18n.format(tinfo.getUnlocalizedName() + ".name"),
					tinfo.getCount()));
		}

	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!player.isSneaking())
			return EnumActionResult.PASS;
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
		if (worldIn.isRemote) {
			player.sendMessage(
					new TextComponentString(I18n.format("info.posSet.success", pos.getX(), pos.getY(), pos.getZ())));
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab))
			return;

		items.add(new ItemStack(this, 1, 0));
		ItemStack stack;

		Collection<Building> bs = BuildingLib.instance.getBuildingsFromLib();
		for (Building building : bs) {
			stack = new ItemStack(this, 1, 0);
			ArcInfo.initArcInfoToItem(stack, building.getKeyName());
			items.add(stack);
		}
	}

}
