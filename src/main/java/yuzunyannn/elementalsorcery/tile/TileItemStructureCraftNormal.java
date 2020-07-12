package yuzunyannn.elementalsorcery.tile;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.container.ContainerItemStructureCraft;
import yuzunyannn.elementalsorcery.container.gui.GuiItemStructureCraft;
import yuzunyannn.elementalsorcery.util.item.InventoryCraftingUseInventory;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class TileItemStructureCraftNormal extends TileItemStructureCraft {

	@Override
	protected int initItemStackCount() {
		return 9;
	}

	protected List<ItemStack> inputList = null;

	@Override
	public void onSlotChange(ContainerItemStructureCraft gui) {
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

	@Override
	public void initGui(ContainerItemStructureCraft gui) {
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j) gui.addInputSlot(inputInv, j + i * 3, 30 + j * 18, 17 + i * 18);
		gui.addOutputSlot(ouputInv, 0, 124, 35);
		gui.addPlayerSlot(8, 84);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawGui(GuiItemStructureCraft gui, float partialTicks, int mouseX, int mouseY) {
		gui.mc.getTextureManager().bindTexture(RenderObjects.CRAFTING_TABLE);
		int offsetX = (gui.width - gui.getXSize()) / 2, offsetY = (gui.height - gui.getYSize()) / 2;
		gui.drawTexturedModalRect(offsetX, offsetY, 0, 0, gui.getXSize(), gui.getYSize());
	}

}
