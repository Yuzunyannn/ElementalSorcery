package yuzunyannn.elementalsorcery.item;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemMemoryFragment extends Item {

	public static class MemoryFragment {
		EnumDyeColor color;
		int count;

		public MemoryFragment(EnumDyeColor color, int count) {
			this.color = color;
			this.count = count;
		}

		public MemoryFragment(int meta) {
			this.fromMeta(meta);
		}

		public int toMeta() {
			return color.getMetadata() << 16 | (count & 0xffff);
		}

		public void fromMeta(int meta) {
			color = EnumDyeColor.byMetadata((meta >> 16) & 0xff);
			count = (short) (meta & 0xffff);
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public EnumDyeColor getColor() {
			return color;
		}
	}

	public ItemMemoryFragment() {
		this.setTranslationKey("memoryFragment");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			ItemStack stack = new ItemStack(this);
			items.add(stack);
			NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
			nbt.setByte("cmeta", (byte) -1);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return super.getItemStackDisplayName(stack);
		int cmeta = nbt.getInteger("cmeta");
		String colorName = "";
		if (cmeta < 0) {
			EnumDyeColor[] colors = EnumDyeColor.values();
			colorName = ColorHelper.toTextFormatting(colors[(EventClient.tick / 4) % colors.length]) + "⭐";
		} else {
			EnumDyeColor color = EnumDyeColor.byMetadata(cmeta);
			colorName = ColorHelper.toTextFormatting(color)
					+ I18n.format("item.fireworksCharge." + color.getTranslationKey());
		}
		return super.getItemStackDisplayName(stack) + String.format("(%s)", colorName + TextFormatting.RESET);
	}

	public static ItemStack getMemoryFragment(int dimId, int areadId, @Nullable EnumDyeColor color) {
		ItemStack itemstack = new ItemStack(ESObjects.ITEMS.MEMORY_FRAGMENT);
		NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(itemstack);
		nbt.setByte("cmeta", color == null ? (byte) -1 : (byte) color.getMetadata());
		nbt.setInteger("areaId", areadId);
		nbt.setInteger("dimId", dimId);
		return itemstack;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		entityItem.setEntityInvulnerable(true);
		if (entityItem.ticksExisted == 60) entityItem.setNoDespawn();
		return super.onEntityItemUpdate(entityItem);
	}

}
