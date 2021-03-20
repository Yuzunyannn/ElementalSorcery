package yuzunyannn.elementalsorcery.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileLifeDirt extends TileEntity {

	//give @p elementalsorcery:life_dirt 1 0 {"lifePlant":{"id":"elementalsorcery:order_crystal","Count":1,"Damage":0}}
	
	public static ItemStack getPlant(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return ItemStack.EMPTY;
		nbt = nbt.getCompoundTag("lifePlant");
		return new ItemStack(nbt);
	}

	public static boolean hasPlant(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return false;
		return nbt.hasKey("lifePlant", 10);
	}
	

	public static void setPlant(ItemStack stack, ItemStack plant) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		nbt.setTag("lifePlant", plant.serializeNBT());
	}

	public static TileLifeDirt checkAndCreate(ItemStack stack) {
		stack = getPlant(stack);
		if (stack.isEmpty()) return null;
		return new TileLifeDirt(stack);
	}

	protected ItemStack plant = ItemStack.EMPTY;

	protected TileLifeDirt(ItemStack stack) {
		plant = stack;
	}

	public TileLifeDirt() {
	}

	public ItemStack getPlant() {
		return plant;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("plant", plant.serializeNBT());
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		plant = new ItemStack(compound.getCompoundTag("plant"));
		super.readFromNBT(compound);
	}
}
