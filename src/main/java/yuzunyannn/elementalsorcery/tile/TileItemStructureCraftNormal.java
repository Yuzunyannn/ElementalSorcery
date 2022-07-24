package yuzunyannn.elementalsorcery.tile;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.tile.IItemStructureCraft;
import yuzunyannn.elementalsorcery.container.ContainerItemStructureCraftNormal;
import yuzunyannn.elementalsorcery.container.gui.GuiItemStructureCraftNormal;
import yuzunyannn.elementalsorcery.util.item.InventoryCraftingUseInventory;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class TileItemStructureCraftNormal extends TileEntityNetwork implements IItemStructureCraft {

	/** 需求 */
	protected ItemStackHandlerInventory inputInv = new ItemStackHandlerInventory(this.initItemStackCount());
	/** 产出 */
	protected ItemStackHandler ouputInv = new ItemStackHandler(1) {
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		};

		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return stack;
		};
	};

	@Override
	public void onLoad() {
		super.onLoad();
		this.onSlotChange(ContainerItemStructureCraftNormal.vest());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		this.onSlotChange(ContainerItemStructureCraftNormal.vest());
	}

	protected int initItemStackCount() {
		return 9;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (inputInv != null) nbt.setTag("inputInv", this.inputInv.serializeNBT());
		if (ouputInv != null) nbt.setTag("outputInv", this.ouputInv.serializeNBT());
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.inputInv.deserializeNBT(nbt.getCompoundTag("inputInv"));
		this.ouputInv.deserializeNBT(nbt.getCompoundTag("outputInv"));
		super.readFromNBT(nbt);
	}

	public ItemStackHandler getInputInv() {
		return inputInv;
	}

	public ItemStackHandler getOuputInv() {
		return ouputInv;
	}

	@Override
	public ItemStack getOutput() {
		return ouputInv.getStackInSlot(0);
	}

	protected List<ItemStack> inputList = null;

	public void onSlotChange(ContainerItemStructureCraftNormal gui) {
		ItemStack result = ItemStack.EMPTY;
		InventoryCraftingUseInventory craftMatrix = new InventoryCraftingUseInventory(gui, inputInv, 3);
		IRecipe irecipe = CraftingManager.findMatchingRecipe(craftMatrix, world);
		if (irecipe == null) {
			if (this.getOutput().isEmpty()) return;
			this.ouputInv.setStackInSlot(0, ItemStack.EMPTY);
			this.markDirty();
			return;
		}
		result = irecipe.getCraftingResult(craftMatrix);
		if (result.isEmpty()) return;
		NonNullList<ItemStack> remains = irecipe.getRemainingItems(craftMatrix);
		// 获取实际值
		ItemStackHandlerInventory inputList = new ItemStackHandlerInventory(inputInv.getSlots());
		for (int i = 0; i < inputInv.getSlots(); i++) {
			ItemStack s = inputInv.getStackInSlot(i);
			if (!s.isEmpty()) {
				s = s.copy();
				s.setCount(1);
			}
			inputList.setStackInSlot(i, s);
		}

		for (int i = 0; i < remains.size(); i++) {
			ItemStack remain = remains.get(i);
			if (remain.isEmpty()) continue;
			for (int j = 0; j < inputList.getSlots(); j++) {
				ItemStack out = inputList.extractItem(j, remain.getCount(), false);
				if (out.isEmpty()) continue;
				remain.shrink(out.getCount());
				if (remain.isEmpty()) break;
			}
		}
		this.inputList = inputList.getListAndClear();
		this.ouputInv.setStackInSlot(0, result);
		this.markDirty();
	}

	@Override
	public List<ItemStack> getInputs() {
		return inputList;
	}

	public String getDisplayTitle() {
		return this.world.getBlockState(pos).getBlock().getLocalizedName();
	}

	@SideOnly(Side.CLIENT)
	public void initGui(ContainerItemStructureCraftNormal gui) {
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j) gui.addInputSlot(inputInv, j + i * 3, 30 + j * 18, 17 + i * 18);
		gui.addOutputSlot(ouputInv, 0, 124, 35);
		gui.addPlayerSlot(8, 84);
	}

	@SideOnly(Side.CLIENT)
	public void drawGui(GuiItemStructureCraftNormal gui, float partialTicks, int mouseX, int mouseY) {
		gui.mc.getTextureManager().bindTexture(RenderObjects.CRAFTING_TABLE);
		int offsetX = (gui.width - gui.getXSize()) / 2, offsetY = (gui.height - gui.getYSize()) / 2;
		gui.drawTexturedModalRect(offsetX, offsetY, 0, 0, gui.getXSize(), gui.getYSize());
	}

}
