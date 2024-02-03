package yuzunyannn.elementalsorcery.api.util.detecter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class DDItemStack extends BaseDataDetectable<ItemStack, NBTTagCompound> {

	public DDItemStack(Consumer<ItemStack> setter, Supplier<ItemStack> getter) {
		super(setter, getter);
	}

	@Override
	public NBTTagCompound detectChanges(IDataRef<ItemStack> templateRef) {
		ItemStack temp = templateRef.get(ItemStack.EMPTY);
		ItemStack stack = get();
		if (!ItemHelper.areItemsEqual(temp, stack)) {
			templateRef.set(stack.copy());
			return NBTHelper.serializeItemStackForSend(stack);
		}
		return null;
	}

	@Override
	public void mergeChanges(NBTTagCompound nbt) {
		set(NBTHelper.deserializeItemStackFromSend(nbt));
	}

}
