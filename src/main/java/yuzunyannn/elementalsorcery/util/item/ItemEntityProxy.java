package yuzunyannn.elementalsorcery.util.item;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.entity.EntityItemGoods;

public abstract class ItemEntityProxy {

	public static boolean isItemEntity(Entity e) {
		return e instanceof EntityItem || e instanceof EntityItemGoods;
	}

	@Nullable
	public static ItemEntityProxy proxy(Entity e) {
		if (e instanceof EntityItem) return new OfEntityItem((EntityItem) e);
		else if (e instanceof EntityItemGoods) return new OfEntityItemGoods((EntityItemGoods) e);
		return null;
	}

	public abstract ItemStack getItemStack();

	public abstract void setItemStack(ItemStack itemStack);

	protected static class OfEntityItem extends ItemEntityProxy {

		public final EntityItem proxy;

		public OfEntityItem(EntityItem entity) {
			this.proxy = entity;
		}

		@Override
		public ItemStack getItemStack() {
			return proxy.getItem();
		}

		@Override
		public void setItemStack(ItemStack itemStack) {
			proxy.setItem(itemStack);
		}

	}

	protected static class OfEntityItemGoods extends ItemEntityProxy {

		public final EntityItemGoods proxy;

		public OfEntityItemGoods(EntityItemGoods entity) {
			this.proxy = entity;
		}

		@Override
		public ItemStack getItemStack() {
			return proxy.getItem();
		}

		@Override
		public void setItemStack(ItemStack itemStack) {
			proxy.setItem(itemStack);
		}

	}

}
