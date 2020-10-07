package yuzunyannn.elementalsorcery.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IItemCapbiltitySyn;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class ItemGrimoire extends Item {

	public ItemGrimoire() {
		this.setUnlocalizedName("grimoire");
		this.setMaxStackSize(1);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			// 测试
			ItemStack s = new ItemStack(this);
			NBTTagCompound nbt = new NBTTagCompound();
			s.setTagCompound(nbt);
			Grimoire grimoire = s.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
			grimoire.getInventory().insertElement(new ElementStack(ESObjects.ELEMENTS.ENDER, 10000, 500), false);
			grimoire.getInventory().insertElement(new ElementStack(ESObjects.ELEMENTS.KNOWLEDGE, 10000, 500), false);
			grimoire.getInventory().insertElement(new ElementStack(ESObjects.ELEMENTS.FIRE, 10000, 500), false);
			grimoire.getInventory().insertElement(new ElementStack(ESObjects.ELEMENTS.METAL, 10000, 500), false);
			grimoire.saveState(s);

			items.add(s);
		}
	}

	public IElementInventory initElementInventory(ItemStack stack) {
		return new ElementInventory(4);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new Grimoire.Provider(this.initElementInventory(stack));
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			int cap = nbt.getShort("capacity");
			if (cap < 20) tooltip.add(I18n.format("info.grimoire.blank", 20 - cap));
			NBTTagList mantras = nbt.getTagList("mantra", 10);
			int n = mantras.tagCount();
			if (n == 0) tooltip.add(TextFormatting.GOLD + I18n.format("info.grimoire.nothing"));
			else {
				tooltip.add(TextFormatting.GOLD + I18n.format("info.grimoire.record", n));
				Mantra m = Mantra.getFromNBT(getOriginNBT(stack));
				if (m == null) tooltip.add(TextFormatting.AQUA + I18n.format("info.grimoire.error"));
				else {
					String name = I18n.format(m.getUnlocalizedName() + ".name");
					tooltip.add(TextFormatting.AQUA + I18n.format("info.grimoire.current", name));
					String describe = m.getUnlocalizedName() + ".describe";
					if (I18n.hasKey(describe)) tooltip.add(I18n.format(describe));
				}
			}
		} else tooltip.add(TextFormatting.GOLD + I18n.format("info.grimoire.nothing"));
		// 元素信息
		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (grimoire == null) return;
		grimoire.loadState(stack);
		IElementInventory inventory = grimoire.getInventory();
		if (inventory == null) return;
		tooltip.add(TextFormatting.YELLOW + I18n.format("info.grimoire.element"));
		ElementHelper.addElementInformation(inventory, worldIn, tooltip, flagIn);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		// 没有能力的，可能是别的内容
		if (!stack.hasCapability(Grimoire.GRIMOIRE_CAPABILITY, null))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		// 必须主手
		if (handIn != EnumHand.MAIN_HAND) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		// 开始释放！
		grimoire.loadState(stack);
		// 获取咒文
		NBTTagCompound originData = getOriginNBT(stack);
		Mantra mantra = Mantra.getFromNBT(originData);
		if (mantra == null) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		if (!mantra.canStart(playerIn)) return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		// 开始
		EntityGrimoire.start(worldIn, playerIn, mantra, originData);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	/** 根据stack获取当前使用的咒文数据 */
	@Nullable
	public static NBTTagCompound getOriginNBT(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return null;
		NBTTagList mantras = nbt.getTagList("mantra", 10);
		int at = nbt.getShort("at");
		return mantras.getCompoundTagAt(at);
	}

	public static void shiftMantra(ItemStack stack, short to) {
		if (stack.isEmpty()) return;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return;
		NBTTagList list = nbt.getTagList("mantra", 10);
		if (list.hasNoTags()) return;
		if (to < 0 || to >= list.tagCount()) return;
		nbt.setShort("at", to);
	}

	/** 多数咒文据处理 */
	static public class MantrasData implements IItemCapbiltitySyn {

		public static class Info {
			Mantra mantra;
			NBTTagCompound data;

			public Info(Mantra mantra, NBTTagCompound data) {
				this.mantra = mantra;
				this.data = data;
			}

			public Mantra getMantra() {
				return mantra;
			}

			public NBTTagCompound getData() {
				return data;
			}
		}

		protected ArrayList<Info> mantraList;
		protected short at = 0;
		protected short capacity = 0;

		public MantrasData(NBTTagCompound nbt) {
			this.loadState(nbt);
		}

		public boolean isEmpty() {
			return mantraList.isEmpty();
		}

		public int size() {
			return mantraList.size();
		}

		public void add(Mantra m, NBTTagCompound nbt) {
			if (m == null) return;
			nbt = nbt == null ? new NBTTagCompound() : nbt;
			Info info = new Info(m, nbt);
			mantraList.add(info);
		}

		public Info getInfo(int i) {
			return mantraList.get(i);
		}

		public short getSelected() {
			return at;
		}

		public void growCapacity(int n) {
			capacity += n;
		}

		public int getCapacity() {
			return capacity;
		}

		public int getCapacityTotally() {
			return 20;
		}

		@Override
		public boolean hasState(ItemStack stack) {
			NBTTagCompound nbt = stack.getTagCompound();
			return nbt == null ? false : nbt.hasKey("mantra", 9);
		}

		@Override
		public void loadState(NBTTagCompound nbt) {
			NBTTagList mantras = nbt.getTagList("mantra", 10);
			mantraList = new ArrayList<>(mantras.tagCount());
			for (int i = 0; i < mantras.tagCount(); i++) {
				NBTTagCompound data = mantras.getCompoundTagAt(i);
				Mantra m = Mantra.getFromNBT(data);
				this.add(m, data);
			}
			capacity = nbt.getShort("capacity");
			at = nbt.getShort("at");
		}

		@Override
		public void saveState(NBTTagCompound nbt) {
			NBTTagList mantras = new NBTTagList();
			for (Info info : mantraList) {
				info.data.setString("id", info.mantra.getRegistryName().toString());
				mantras.appendTag(info.data);
			}
			nbt.setTag("mantra", mantras);
			nbt.setShort("capacity", capacity);
			nbt.setShort("at", at);
		}
	}

	@Nonnull
	public static MantrasData getAllMantra(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return null;
		return new MantrasData(nbt);
	}

	@Nonnull
	public static void setAllMantra(ItemStack stack, MantrasData data) {
		data.saveState(stack);
	}

}
