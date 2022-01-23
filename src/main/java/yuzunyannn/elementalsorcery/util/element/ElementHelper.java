package yuzunyannn.elementalsorcery.util.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IElementInventoryModifiable;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class ElementHelper {

	/** 复制src到dst */
	public static void toElementInventory(IElementInventory src, IElementInventory dst) {
		if (dst instanceof IElementInventoryModifiable) ((IElementInventoryModifiable) dst).setSlots(src.getSlots());
		for (int i = 0; i < Math.min(src.getSlots(), dst.getSlots()); i++)
			dst.setStackInSlot(i, src.getStackInSlot(i).copy());
		for (int i = src.getSlots(); i < dst.getSlots(); i++) dst.setStackInSlot(i, ElementStack.EMPTY);
		NBTTagCompound nbt = new NBTTagCompound();
		src.writeCustomDataToNBT(nbt);
		dst.readCustomDataFromNBT(nbt);
	}

	public static boolean canInsert(IElementInventory inventory) {
		if (inventory == null) return false;
		// 规定认为，插入EMPTY必然会成功，否则表示这个仓库不能插入
		return inventory.insertElement(ElementStack.EMPTY, true);
	}

	public static boolean canExtract(IElementInventory inventory) {
		if (inventory == null) return false;
		// 规定认为，去取的ElementStack，必然和传入的ElementStack的地址不一样，否则认为不能取出
		return inventory.extractElement(ElementStack.EMPTY, true) != ElementStack.EMPTY;
	}

	// 元素仓库为空
	public static boolean isEmpty(IElementInventory inventory) {
		if (inventory == null) return true;
		for (int i = 0; i < inventory.getSlots(); i++) {
			if (!inventory.getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}

	// 添加元素的信息
	@SideOnly(Side.CLIENT)
	public static boolean addElementInformation(IElementInventory inventory, @Nullable World worldIn,
			List<String> tooltip, ITooltipFlag flagIn) {
		if (inventory == null) return false;
		boolean has = false;
		for (int i = 0; i < inventory.getSlots(); i++) {
			ElementStack estack = inventory.getStackInSlot(i);
			if (estack.isEmpty()) continue;
			addElementInformation(estack, tooltip);
			has = true;
		}
		return has;
	}

	@SideOnly(Side.CLIENT)
	public static void addElementInformation(ElementStack estack, List<String> tooltip) {
		if (estack.isEmpty()) return;
		String str = I18n.format("info.elementCrystal.has", estack.getDisplayName(), estack.getCount(),
				estack.getPower());
		tooltip.add(TextFormatting.RED + str);
	}

	/** 从tileentity获取元素仓库 */
	public static IElementInventory getElementInventory(TileEntity tile) {
		if (tile == null) return null;
		if (tile.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null))
			return tile.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		if (tile instanceof IElementInventory) return (IElementInventory) tile;
		return null;
	}

	/** 获取一个物品里可插入和取出的元素仓库 */
	@Nullable
	static public IElementInventory getElementInventory(ItemStack stack) {
		if (stack.isEmpty()) return null;
		if (!stack.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null)) return null;
		IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		inventory.loadState(stack);
		return inventory;
	}

	@Nullable
	static public IElementInventory getElementInventory(ICapabilityProvider provider) {
		if (!provider.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null)) return null;
		return provider.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
	}

	/** 获取一组元素的默认复杂度 */
	static public int getComplexFromElements(ItemStack stack, ElementStack[] estacks) {
		if (estacks == null || estacks.length == 0) return 0;
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

	/** 合并 */
	static public ElementStack[] merge(ElementStack[] estacks1, ElementStack... estacks2) {
		ArrayList<ElementStack> list = new ArrayList<ElementStack>();
		for (ElementStack e1 : estacks1) list.add(e1);
		for (ElementStack e2 : estacks2) {
			for (ElementStack e1 : estacks1) {
				if (e1.areSameType(e2)) {
					e1.grow(e2);
					e2 = ElementStack.EMPTY;
					break;
				}
			}
			if (!e2.isEmpty()) list.add(e2);
		}
		return (ElementStack[]) list.toArray(new ElementStack[list.size()]);
	}

	static public void merge(IElementInventory to, IElementInventory from) {
		if (to == null || from == null) return;
		for (int j = 0; j < from.getSlots(); j++) {
			ElementStack estack = from.getStackInSlot(j);
			if (estack.isEmpty()) continue;
			to.insertElement(estack, false);
		}
	}

	/** 转颜色 */
	static public int[] toColor(Collection<ElementStack> estacks) {
		if (estacks == null) return null;
		int[] colors = new int[estacks.size()];
		int i = 0;
		for (ElementStack estack : estacks) {
			colors[i] = estack.getElement().getColor(estack);
			i++;
		}
		return colors;
	}

	/** 随机取出 */
	static public ElementStack randomExtract(IElementInventory einv) {
		if (einv == null) return ElementStack.EMPTY;
		int s = RandomHelper.rand.nextInt(einv.getSlots());
		for (int i = 0; i < einv.getSlots(); i++) {
			ElementStack estack = einv.getStackInSlot((s + i) % einv.getSlots());
			if (!estack.isEmpty()) return estack;
		}
		return ElementStack.EMPTY;
	}

	static public void sort(ElementStack[] estacks) {
		Arrays.sort(estacks, (e1, e2) -> {
			int n1 = e1.getCount();
			int n2 = e2.getCount();
			if (n1 > n2) return -1;
			else if (n1 == n2) return 0;
			else return 1;
		});
	}

	static public ElementStack[] toArray(Object... objects) {
		List<ElementStack> list = toList(objects);
		return list.toArray(new ElementStack[list.size()]);
	}

	static public List<ElementStack> toList(Object... objects) {
		List<ElementStack> list = new ArrayList<>();
		for (int i = 0; i < objects.length; i++) {
			Object obj = objects[i];
			if (obj instanceof ElementStack) list.add((ElementStack) obj);
			else if (obj instanceof Element) {
				int count = 1, power = 1;
				int now = i;
				if (now < objects.length - 1) {
					if (objects[now + 1] instanceof Number) {
						count = ((Number) objects[now + 1]).intValue();
						i = now + 1;
						if (now < objects.length - 2) {
							if (objects[now + 2] instanceof Number) {
								power = ((Number) objects[now + 2]).intValue();
								i = now + 2;
							}
						}
					}
				}
				list.add(new ElementStack((Element) obj, count, power));
			}
		}
		return list;
	}

	static public List<ElementStack> toList(ElementStack... stacks) {
		List<ElementStack> list = new ArrayList<ElementStack>(stacks.length);
		for (ElementStack stack : stacks) list.add(stack);
		return list;
	}

	/** 复制 */
	static public ElementStack[] copy(ElementStack[] estacks) {
		if (estacks == null) return null;
		ElementStack[] newEStacks = new ElementStack[estacks.length];
		for (int i = 0; i < estacks.length; i++) newEStacks[i] = estacks[i].copy();
		return newEStacks;
	}

}
