package yuzunyannn.elementalsorcery.item;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class ItemParcel extends Item {

	public ItemParcel() {
		this.setHasSubtypes(true);
		this.setUnlocalizedName("parcel");
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int meta = stack.getMetadata();
		if (meta != 0) return;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return;
		String sender = nbt.getString("sender");
		TextFormatting color = TextFormatting.DARK_AQUA;
		if (sender.isEmpty()) tooltip.add(color + I18n.format("info.from", I18n.format("info.unknow")));
		else tooltip.add(color + I18n.format("info.from", sender));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		int meta = stack.getMetadata();
		if (meta == 0) {
			stack.setItemDamage(1);
			NBTTagCompound nbt = stack.getTagCompound();
			stack.setTagCompound(null);
			if (nbt == null) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			LinkedList<ItemStack> list = NBTHelper.getItemList(nbt, "goods");
			for (ItemStack item : list) playerIn.inventory.addItemStackToInventory(item);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}

}
