package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class DDItemHandler implements IDataDetectable<IItemHandlerModifiable, NBTTagList> {

	protected final Consumer<Integer> resetter;
	protected final Supplier<IItemHandlerModifiable> getter;
	protected IItemHandler checkHandler;

	public DDItemHandler(Consumer<Integer> resetter, Supplier<IItemHandlerModifiable> getter) {
		this.resetter = resetter;
		this.getter = getter;
	}

	public void setCheckHandler(IItemHandler checkHandler) {
		this.checkHandler = checkHandler;
	}

	public IItemHandler getCheckHandler() {
		if (checkHandler != null) return checkHandler;
		return getHanlder();
	}

	public IItemHandlerModifiable getHanlder() {
		return this.getter.get();
	}

	public IItemHandlerModifiable resetHandler(int count) {
		this.resetter.accept(count);
		return this.getter.get();
	}

	@Override
	public NBTTagList detectChanges(IDataRef<IItemHandlerModifiable> templateRef) {
		IItemHandler handler = this.getCheckHandler();
		IItemHandlerModifiable template = templateRef.get();
		NBTTagList tagList = null;
		boolean isPassCheck = false;
		if (template == null || template.getSlots() != handler.getSlots()) {
			tagList = new NBTTagList();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("cap", handler.getSlots());
			tagList.appendTag(nbt);
			templateRef.set(template = new ItemStackHandler(handler.getSlots()));
			isPassCheck = true;
		}
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack itemstack = handler.getStackInSlot(i);
			boolean clientStackChanged = true;

			if (!isPassCheck) {
				ItemStack itemstack1 = template.getStackInSlot(i);
				if (ItemHelper.areItemsEqual(itemstack1, itemstack)) continue;
				clientStackChanged = !ItemStack.areItemStacksEqualUsingNBTShareTag(itemstack1, itemstack);
			}

			ItemStack itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
			template.setStackInSlot(i, itemstack1);

			if (!clientStackChanged) continue;

			if (tagList == null) tagList = new NBTTagList();
			NBTTagCompound nbt = NBTHelper.serializeItemStackForSend(itemstack);
			nbt.setShort("slot", (short) i);
			tagList.appendTag(nbt);
		}

		return tagList;
	}

	@Override
	public void mergeChanges(NBTTagList list) {
		IItemHandlerModifiable handler = getHanlder();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			if (nbt.hasKey("cap")) {
				handler = this.resetHandler(nbt.getInteger("cap"));
				continue;
			}
			int slot = nbt.getInteger("slot");
			ItemStack stack = NBTHelper.deserializeItemStackFromSend(nbt);
			try {
				handler.setStackInSlot(slot, stack);
			} catch (IndexOutOfBoundsException e) {}
		}
	}

}
