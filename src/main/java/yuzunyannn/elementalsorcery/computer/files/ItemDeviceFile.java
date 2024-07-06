package yuzunyannn.elementalsorcery.computer.files;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceFile;
import yuzunyannn.elementalsorcery.api.item.ESItemStorageEnum;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.util.render.GameDisplayCast;
import yuzunyannn.elementalsorcery.computer.DiskItem;
import yuzunyannn.elementalsorcery.item.prop.ItemMantraGem;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemDeviceFile extends DeviceFileAdapter {

	static public IDeviceFile getFile(DeviceFilePath path, final Supplier<ItemStack> stackGetter) {
		return new DynDeviceFile(path, new Function<IDeviceFile, IDeviceFile>() {

			private ItemStack holdStack = ItemStack.EMPTY;

			@Override
			public IDeviceFile apply(IDeviceFile origin) {
				ItemStack curStack = stackGetter.get();
				if (holdStack == curStack) return origin;
				holdStack = curStack;
				if (curStack.isEmpty()) return null;
				return getFile(path, curStack);
			}

		});
	}

	static protected IDeviceFile getFile(DeviceFilePath path, ItemStack stack) {

		if (stack.getItem() == ESObjects.ITEMS.DISK || ItemHelper.hasSubCompound(stack, ESItemStorageEnum.DISK_DATA)) {
			DiskItem disk = new DiskItem(stack);
			return new DiskDeviceFile(path, disk.getContext(), path.length());
		}

		Mantra mantra = ItemMantraGem.getMantraFromMantraGem(stack);
		if (mantra != null) return new MantraDeviceFile(path, mantra);

		return new ItemDeviceFile(path, stack);
	}

	protected final ItemStack stack;

	public ItemDeviceFile(DeviceFilePath path, ItemStack stack) {
		super(path);
		this.stack = stack;
	}

	protected ItemStack getItemStack() {
		return this.stack;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public Object toDisplayObject() {
		return new Object[] { "filedata:", GameDisplayCast.cast(stack) };
	}

}
