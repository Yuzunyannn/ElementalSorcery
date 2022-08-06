package yuzunyannn.elementalsorcery.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.api.element.IISCraftHanlder;
import yuzunyannn.elementalsorcery.api.element.IISCraftHanlderMap;
import yuzunyannn.elementalsorcery.api.tile.IItemStructureCraft;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.crafting.ISMCCraftHandler;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public abstract class TileItemStructureCraft extends TileEntityNetwork implements IItemStructureCraft {

	public static final Map<String, IISCraftHanlder> handlerMap = new HashMap<String, IISCraftHanlder>();

	public static void init() {
		ElementalSorcery.setAPIField(new IISCraftHanlderMap() {
			@Override
			public void put(String id, IISCraftHanlder handler) {
				handlerMap.put(id, handler);
			}

			@Override
			public IISCraftHanlder get(String id) {
				return handlerMap.get(id);
			}
		});
		ESAPI.ISCraftMap.put("mc", new ISMCCraftHandler());
//		ESAPI.ISCraftMap.put("smelt", new ISSmeltCraftHandler());
	}

	public static final ItemStack defaultTypeStack = new ItemStack(Blocks.CRAFTING_TABLE);

	protected Map<Integer, ItemStack> slotMap = new HashMap<>();
	protected String typeName;
	protected ItemStack typeStack = defaultTypeStack;

	protected ItemStack output = ItemStack.EMPTY;
	protected List<ItemStack> inputs;
	protected List<ItemStack> remains;
	protected int complexInrc;

	public TileItemStructureCraft() {

	}

	public TileItemStructureCraft(String typeName) {
		this.typeName = typeName;
	}

	public Map<Integer, ItemStack> getSlotMap() {
		return slotMap;
	}

	public void resetSlotMap() {
		slotMap.clear();
	}

	public void addSlot(int x, int y) {
		int index = getSlotIndex(x, y);
		slotMap.put(index, ItemStack.EMPTY);
	}

	public void addSlot(int index) {
		slotMap.put(index, ItemStack.EMPTY);
	}

	public static int getSlotIndex(int x, int y) {
		return x << 16 | y;
	}

	static public ItemStack getInput(Map<Integer, ItemStack> slotMap, int x, int y) {
		ItemStack input = slotMap.get(getSlotIndex(x, y));
		input = input == null ? ItemStack.EMPTY : input;
		return input;
	}

	public static ItemStack getRealItemStack(ItemStack itemStack) {
		if (itemStack.isEmpty()) return itemStack;
		IItemStructure is = ItemStructure.getItemStructure(itemStack);
		if (is.isEmpty()) {
			if (ItemStructure.canStorageItemStructure(itemStack)) return ItemStack.EMPTY;
			return itemStack;
		}
		return is.getStructureItem(0);
	}

	public ItemStack getSlotItemStack(int x, int y) {
		int index = getSlotIndex(x, y);
		ItemStack stack = slotMap.get(index);
		return stack == null ? ItemStack.EMPTY : stack;
	}

	public ItemStack getSlotItemStack(int index) {
		ItemStack stack = slotMap.get(index);
		return stack == null ? ItemStack.EMPTY : stack;
	}

	public void setSlotItemStack(int x, int y, ItemStack itemStack) {
		setSlotItemStack(getSlotIndex(x, y), itemStack);
	}

	public void setSlotItemStack(int index, ItemStack itemStack) {
		if (slotMap.containsKey(index)) slotMap.put(index, itemStack);
		if (world.isRemote) return;
		updateOutput();
		this.markDirty();
	}

	public int getSlotMapSize() {
		return slotMap.size();
	}

	public Collection<Integer> getSlotIndexs() {
		return slotMap.keySet();
	}

	public String getTypeName() {
		return (typeName == null || typeName.isEmpty()) ? "mc" : typeName;
	}

	public ItemStack getTypeStack() {
		return typeStack;
	}

	public void setTypeStack(ItemStack typeStack) {
		this.typeStack = typeStack.isEmpty() ? defaultTypeStack : typeStack;
	}

	public void updateTypeStackWithItem(ItemStack itemStack) {
		try {
			if (itemStack.isEmpty()) {
				setTypeStack(itemStack);
				setCraftTypeName("mc");
				return;
			}
			for (Entry<String, IISCraftHanlder> entry : handlerMap.entrySet()) {
				IISCraftHanlder handler = entry.getValue();
				if (handler.isKeyItem(itemStack)) {
					setTypeStack(itemStack);
					setCraftTypeName(entry.getKey());
					return;
				}
			}
		} catch (Exception e) {
			ESAPI.logger.warn("选择处理器出现异常！", e);
			setTypeStack(itemStack);
			setCraftTypeName("mc");
		}
	}

	@Override
	public void onLoad() {
		if (world.isRemote) return;
		if (slotMap.isEmpty()) {
			setCraftTypeName(typeName);
			refreshSlotMap();
		} else updateOutput();
	}

	public IISCraftHanlder getCraftHandler() {
		String typeName = getTypeName();
		IISCraftHanlder handler = handlerMap.get(typeName);
		return handler == null ? handlerMap.get("mc") : handler;
	}

	public void setCraftTypeName(String typeName) {
		if (this.getTypeName().equals(typeName)) return;
		this.typeName = typeName;
		this.refreshSlotMap();
	}

	public void refreshSlotMap() {
		this.resetSlotMap();
		IISCraftHanlder craftHanlder = this.getCraftHandler();
		if (craftHanlder == null) return;
		List<Integer> list = craftHanlder.getSlotIndexMap();
		for (int index : list) addSlot(index);
		updateOutput();
		markDirty();
	}

	public void updateOutput() {
		this.output = ItemStack.EMPTY;
		this.remains = this.inputs = null;
		IISCraftHanlder craftHanlder = this.getCraftHandler();
		if (craftHanlder == null) return;
		try {
			List<ItemStack> inputs = new ArrayList<>();
			List<ItemStack> remains = new ArrayList<>();
			ItemStack output = craftHanlder.match(world, pos, slotMap, inputs, remains);
			if (output.isEmpty()) return;
			this.output = output.copy();
			this.inputs = inputs;
			this.remains = remains;
			this.complexInrc = craftHanlder.complexIncr();
			this.markDirty();
		} catch (Throwable e) {
			ESAPI.logger.warn("处理合成出现异常！", e);
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (Entry<Integer, ItemStack> entry : slotMap.entrySet()) {
			ItemStack stack = entry.getValue();
			NBTTagCompound nbt = isSending() ? NBTHelper.serializeItemStackForSend(stack) : stack.serializeNBT();
			nbt.setInteger("_Slot", entry.getKey());
			list.appendTag(nbt);
		}
		compound.setTag("slotList", list);
		if (isSending()) return super.writeToNBT(compound);
		if (typeName != null) compound.setString("typeName", typeName);
		if (typeStack.getItem() != defaultTypeStack.getItem()) compound.setTag("typeStack", typeStack.serializeNBT());
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		slotMap.clear();
		NBTTagList list = compound.getTagList("slotList", NBTTag.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			ItemStack stack = isSending() ? NBTHelper.deserializeItemStackFromSend(nbt) : new ItemStack(nbt);
			slotMap.put(nbt.getInteger("_Slot"), stack);
		}
		if (isSending()) {
			super.readFromNBT(compound);
			return;
		}
		typeName = compound.getString("typeName");
		if (compound.hasKey("typeStack")) typeStack = new ItemStack(compound.getCompoundTag("typeStack"));
		super.readFromNBT(compound);
	}

	@Override
	public Collection<ItemStack> getInputs() {
		return inputs;
	}

	@Override
	public Collection<ItemStack> getRemains() {
		return remains;
	}

	@Override
	public ItemStack getOutput() {
		return output;
	}

	@Override
	public int getComplexIncr() {
		return complexInrc;
	}

	@SideOnly(Side.CLIENT)
	public void setOutput(ItemStack stack) {
		this.output = stack;
	}

}
