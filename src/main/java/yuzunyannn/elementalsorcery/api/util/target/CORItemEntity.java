package yuzunyannn.elementalsorcery.api.util.target;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.IGetItemStack;

public class CORItemEntity extends COREntity {

	public CORItemEntity(EntityItem itemEntity) {
		super(itemEntity);
	}

	public <T extends Entity & IGetItemStack> CORItemEntity(T entity) {
		super(entity);
	}

	@Override
	public int tagId() {
		return TAG_ENTITY_ITEM;
	}

	@Override
	public ItemStack toItemStack() {
		Entity entity = toEntity();
		if (entity == null) return ItemStack.EMPTY;
		if (entity instanceof EntityItem) return ((EntityItem) entity).getItem();
		if (entity instanceof IGetItemStack) return ((IGetItemStack) entity).getStack();
		return ItemStack.EMPTY;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		ItemStack stack = toItemStack();
		return stack.isEmpty() ? false : stack.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		ItemStack stack = toItemStack();
		return stack.isEmpty() ? null : stack.getCapability(capability, facing);
	}

}
