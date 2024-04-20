package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class ItemSimpleMaterialContainer extends Item {

	public ItemSimpleMaterialContainer() {
		this.setTranslationKey("simpleMaterialContainer");
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (playerIn.isSneaking()) {
			if (worldIn.isRemote)
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
			BlockPos pos = playerIn.getPosition();
			playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_SIMPLE_MATERIAL_CONTAINER, worldIn, pos.getX(),
					pos.getY(), pos.getZ());
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (handler instanceof ItemSimpleMaterialContainerHandler) return ((ItemSimpleMaterialContainerHandler) handler)
				.doAutoUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("info.simpleMaterialContainer"));
		IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (handler instanceof ItemSimpleMaterialContainerHandler)
			((ItemSimpleMaterialContainerHandler) handler).addInformation(stack, worldIn, tooltip, flagIn);
	}

	public static class ItemSimpleMaterialContainerHandler implements IItemHandlerModifiable {

		ItemStack stack = ItemStack.EMPTY;

		public ItemSimpleMaterialContainerHandler(ItemStack stack) {
			this.stack = stack;
		}

		public int getAutoUseIndex() {
			NBTTagCompound nbt = NBTHelper.getStackTag(stack);
			return nbt.getInteger("autoUseIndex");
		}

		public void setAutoUseIndex(int index) {
			NBTTagCompound nbt = NBTHelper.getStackTag(stack);
			nbt.setShort("autoUseIndex", (short) index);
		}

		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
			int autoUseIndex = getAutoUseIndex();
			for (int i = 0; i < getSlots(); i++) {
				ItemStack iStack = getStackInSlot(i);
				if (iStack.isEmpty()) continue;
				if (i == autoUseIndex) tooltip.add(TextFormatting.UNDERLINE + "" + TextFormatting.BOLD + "⭐"
						+ iStack.getDisplayName() + "x" + iStack.getCount());
				else tooltip.add(iStack.getDisplayName() + "x" + iStack.getCount());
			}
		}

		public void toNextUseIndex() {
			int autoUseIndex = getAutoUseIndex();
			NBTTagList list = getStackListData();
			if (autoUseIndex == list.tagCount() - 1) {
				setAutoUseIndex(-1);
				return;
			}
			for (int i = 0; i < list.tagCount(); i++) {
				int slot = (i + autoUseIndex + 1) % list.tagCount();
				NBTTagCompound nbt = list.getCompoundTagAt(slot);
				int size = nbt.getInteger("size");
				if (size <= 0) continue;
				Item item = Item.getByNameOrId(nbt.getString("id"));
				if (item == null || item == Items.AIR) continue;
				setAutoUseIndex(slot);
				return;
			}
		}

		public EnumActionResult doAutoUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
				EnumFacing facing, float hitX, float hitY, float hitZ) {
			int autoUseIndex = getAutoUseIndex();
			NBTTagList list = getSatisfactoryStackList();
			if (autoUseIndex < 0 || autoUseIndex >= list.tagCount()) return EnumActionResult.PASS;
			NBTTagCompound nbt = list.getCompoundTagAt(autoUseIndex);

			int size = nbt.getInteger("size");
			if (size <= 0) return EnumActionResult.PASS;
			Item item = Item.getByNameOrId(nbt.getString("id"));
			if (item == null || item == Items.AIR) return EnumActionResult.PASS;

			int meta = Math.max(0, nbt.getShort("meta"));

			if (worldIn.isRemote) return EnumActionResult.PASS;

			ItemStack stack = new ItemStack(item, size, meta);
			stack.setCount(Math.min(stack.getMaxStackSize(), size));
			ItemStack holdStack = player.getHeldItem(hand);

			int originCount = stack.getCount();

			// player.setHeldItem(hand, stack);
			if (hand == EnumHand.MAIN_HAND) player.inventory.mainInventory.set(player.inventory.currentItem, stack);
			else player.inventory.offHandInventory.set(0, stack);
			EnumActionResult result = stack.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
			if (hand == EnumHand.MAIN_HAND) player.inventory.mainInventory.set(player.inventory.currentItem, holdStack);
			else player.inventory.offHandInventory.set(0, holdStack);
			// player.setHeldItem(hand, holdStack);

			if (originCount > stack.getCount() && !player.isCreative()) {
				nbt.setInteger("size", size - (originCount - stack.getCount()));
				checkAndSupple(autoUseIndex);
			}

			return result;
		}

		protected NBTTagList getStackListData() {
			NBTTagCompound nbt = NBTHelper.getStackTag(stack);
			if (nbt.hasKey("_void_stacks_", NBTTag.TAG_LIST))
				return nbt.getTagList("_void_stacks_", NBTTag.TAG_COMPOUND);
			NBTTagList newNbt = new NBTTagList();
			nbt.setTag("_void_stacks_", newNbt);
			return newNbt;
		}

		protected NBTTagList getSatisfactoryStackList() {
			NBTTagList list = getStackListData();
			while (list.tagCount() < getSlots()) list.appendTag(new NBTTagCompound());
			return list;
		}

		@Override
		public int getSlots() {
			return 12;
		}

		@Override
		public int getSlotLimit(int slot) {
			return Integer.MAX_VALUE;
		}

		public boolean isSimpleItem(ItemStack stack) {
			if (stack.isItemDamaged()) return false;
			if (stack.getTagCompound() != null) return false;
			if (stack.getMaxStackSize() < 16) return false;
			//if (JavaHelper.getFieldValue(stack, "capabilities") != null) return false;

			IToElementInfo info = ElementMap.instance.toElement(stack);
			if (info == null) return false;
			if (info.complex() > 5) return false;
			ElementStack[] eStacks = info.element();

			double fragment = 0;
			for (ElementStack eStack : eStacks) fragment += ElementTransition.toMagicFragment(eStack);
			if (fragment > 10000) return false;

			return true;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			NBTTagList list = getStackListData();
			if (slot < 0 || slot >= list.tagCount()) return ItemStack.EMPTY;
			NBTTagCompound nbt = list.getCompoundTagAt(slot);
			Item item = Item.getByNameOrId(nbt.getString("id"));
			if (item == null || item == Items.AIR) return ItemStack.EMPTY;
			int meta = Math.max(0, nbt.getShort("meta"));
			int size = nbt.getInteger("size");
			if (size <= 0) return ItemStack.EMPTY;
			return new ItemStack(item, size, meta);
		}

		@Override
		public void setStackInSlot(int slot, ItemStack stack) {
			NBTTagList list = getSatisfactoryStackList();
			if (slot < 0 || slot >= list.tagCount()) return;
			NBTTagCompound nbt = list.getCompoundTagAt(slot);
			nbt.setString("id", stack.getItem().getRegistryName().toString());
			nbt.setShort("meta", (short) stack.getMetadata());
			nbt.setInteger("size", stack.getCount());
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

			NBTTagList list = getSatisfactoryStackList();
			if (slot < 0 || slot >= list.tagCount()) return stack;
			NBTTagCompound nbt = list.getCompoundTagAt(slot);
			Item item = Item.getByNameOrId(nbt.getString("id"));
			int size = nbt.getInteger("size");

			if (size > 0 && item != null && item != Items.AIR) {
				if (item != stack.getItem()) return stack;
				int meta = Math.max(0, nbt.getShort("meta"));
				if (meta != stack.getMetadata()) return stack;
			} else {

				if (!isSimpleItem(stack)) return stack;

				item = stack.getItem();
				if (!simulate) {
					nbt.setString("id", item.getRegistryName().toString());
					nbt.setShort("meta", (short) stack.getMetadata());
					nbt.setInteger("size", 0);
				}
			}

			if (!simulate) {
				int newSize = (int) Math.min(size + (long) stack.getCount(), Integer.MAX_VALUE);
				nbt.setInteger("size", newSize);
			}

			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			NBTTagList list = getSatisfactoryStackList();
			if (slot < 0 || slot >= list.tagCount()) return ItemStack.EMPTY;
			NBTTagCompound nbt = list.getCompoundTagAt(slot);
			// 判断对应位置是否有数据
			int size = nbt.getInteger("size");
			if (size <= 0) return ItemStack.EMPTY;
			Item item = Item.getByNameOrId(nbt.getString("id"));
			if (item == null || item == Items.AIR) return ItemStack.EMPTY;
			// 尝试取出
			int meta = Math.max(0, nbt.getShort("meta"));
			int count = Math.min(size, amount);
			ItemStack extract = new ItemStack(item, count, meta);
			// 返回和更新
			if (simulate) return extract;
			nbt.setInteger("size", size - count);
			return extract;
		}

		public void checkAndSupple(int slot) {
			ItemStack currStack = getStackInSlot(slot);
			if (!currStack.isEmpty()) return;
			NBTTagList list = getSatisfactoryStackList();
			for (int i = list.tagCount() - 1; i > slot; i--) {
				ItemStack stack = getStackInSlot(i);
				if (stack.isEmpty()) continue;
				setStackInSlot(i, ItemStack.EMPTY);
				setStackInSlot(slot, stack);
				break;
			}
		}

	}

	protected static class UseProvider implements ICapabilitySerializable<NBTTagCompound> {

		private IItemHandler inventory;

		public UseProvider(ItemStack dyStack) {
			this.inventory = new ItemSimpleMaterialContainerHandler(dyStack);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability);
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) return (T) inventory;
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return new NBTTagCompound();
		}

		@Override
		public void deserializeNBT(NBTTagCompound compound) {
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new UseProvider(stack);
	}

}
