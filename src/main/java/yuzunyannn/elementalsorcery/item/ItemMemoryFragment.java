package yuzunyannn.elementalsorcery.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.ESObjects;
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
	public String getItemStackDisplayName(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return super.getItemStackDisplayName(stack);
		int cmeta = nbt.getInteger("cmeta");
		EnumDyeColor color = EnumDyeColor.byMetadata(cmeta);
		String colorName = ColorHelper.toTextFormatting(color)
				+ I18n.format("item.fireworksCharge." + color.getTranslationKey());
		return super.getItemStackDisplayName(stack) + String.format("(%s)", colorName);
	}

	public static ItemStack getMemoryFragment(int dimId, int areadId, EnumDyeColor color) {
		ItemStack itemstack = new ItemStack(ESObjects.ITEMS.MEMORY_FRAGMENT);
		NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(itemstack);
		nbt.setByte("cmeta", (byte) color.getMetadata());
		nbt.setInteger("areaId", areadId);
		nbt.setInteger("dimId", dimId);
		return itemstack;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.ticksExisted == 60) entityItem.setNoDespawn();
		return super.onEntityItemUpdate(entityItem);
	}

}
