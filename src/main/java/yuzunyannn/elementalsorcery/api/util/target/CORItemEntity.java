package yuzunyannn.elementalsorcery.api.util.target;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import yuzunyannn.elementalsorcery.api.IGetItemStack;

public class CORItemEntity extends COREntity {

	public static class Storage implements ICapabilityRefStorage<CORItemEntity> {
		@Override
		public void write(ByteBuf buf, CORItemEntity obj) {
			buf.writeInt(obj.id);
		}

		@Override
		public CORItemEntity read(ByteBuf buf) {
			return new CORItemEntity(buf.readInt());
		}
	}

	public CORItemEntity(EntityItem itemEntity) {
		super(itemEntity);
	}

	public <T extends Entity & IGetItemStack> CORItemEntity(T entity) {
		super(entity);
	}

	protected CORItemEntity(int id) {
		super(id);
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
