package yuzunyan.elementalsorcery.api.util;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.api.ESCapability;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.capability.ElementInventory;

public class ElementHelper {

	public static boolean canInsert(IElementInventory inventory) {
		if (inventory == null)
			return false;
		// 规定认为，插入EMPTY必然会成功，否则表示这个仓库不能插入
		return inventory.insertElement(ElementStack.EMPTY, true);
	}

	public static boolean canExtract(IElementInventory inventory) {
		if (inventory == null)
			return false;
		// 规定认为，去取的ElementStack，必然和传入的ElementStack的地址不一样，否则认为不能取出
		return inventory.extractElement(ElementStack.EMPTY, true) != ElementStack.EMPTY;
	}

	// 元素仓库为空
	public static boolean isEmpty(IElementInventory inventory) {
		for (int i = 0; i < inventory.getSlots(); i++) {
			if (!inventory.getStackInSlot(i).isEmpty())
				return false;
		}
		return true;
	}

	// 添加元素的信息
	@SideOnly(Side.CLIENT)
	public static void addElementInformation(IElementInventory inventory, World worldIn,
			List<String> tooltip, ITooltipFlag flagIn) {
		for (int i = 0; i < inventory.getSlots(); i++) {
			ElementStack estack = inventory.getStackInSlot(i);
			if (estack.isEmpty())
				continue;
			String str;
			if (estack.usePower())
				str = I18n.format("info.elementalCrystal.has", I18n.format(estack.getElementUnlocalizedName()),
						estack.getCount(), estack.getPower());
			else
				str = I18n.format("info.elementalCrystal.hasnp", I18n.format(estack.getElementUnlocalizedName()),
						estack.getCount());
			tooltip.add("§c" + str);
		}
	}

	/** 从tileentity获取元素仓库 */
	public static IElementInventory getElementInventory(TileEntity tile) {
		if (tile == null)
			return null;
		if (tile.hasCapability(ESCapability.ELEMENTINVENTORY_CAPABILITY, null))
			return tile.getCapability(ESCapability.ELEMENTINVENTORY_CAPABILITY, null);
		if (tile instanceof IElementInventory)
			return (IElementInventory) tile;
		return null;
	}

	/** 获取一个物品里可插入和取出的元素仓库 */
	@Nullable
	static public IElementInventory getElementInventory(ItemStack stack) {
		if (stack.isEmpty())
			return null;
		if (!stack.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null))
			return null;
		IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		inventory.loadState(stack);
		return inventory;
	}

	/** 获取一组元素的默认复杂度 */
	static public int getComplexFromElements(ItemStack stack, ElementStack[] estacks) {
		if (estacks == null || estacks.length == 0)
			return 0;
		int sum = estacks.length;
		for (int i = 0; i < estacks.length - 1; i++) {
			ElementStack es1 = estacks[i];
			for (int j = i + 1; j < estacks.length; j++) {
				ElementStack es2 = estacks[j];
				sum += es1.getElement().complexWith(stack, es1, es2);
			}
		}
		return sum;
	}

}
