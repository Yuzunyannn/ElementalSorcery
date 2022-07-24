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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.api.tile.IItemStructureCraft;
import yuzunyannn.elementalsorcery.crafting.MCCraftHandler;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class TileItemStructureCraftCC extends TileEntityNetwork implements IItemStructureCraft {

	public interface IISCCCraftHanlder {
		public List<Integer> getSlotIndexMap();

		public ItemStack match(World world, BlockPos pos, Map<Integer, ItemStack> slotMap, List<ItemStack> inputs,
				List<ItemStack> remains);
	}

	public static final Map<String, IISCCCraftHanlder> handlerMap = new HashMap<String, IISCCCraftHanlder>();

	static {
		handlerMap.put("mc", new MCCraftHandler());
	}

	public static final ItemStack defaultTypeStack = new ItemStack(Blocks.CRAFTING_TABLE);

	protected Map<Integer, ItemStack> slotMap = new HashMap<>();
	protected ItemStack output = ItemStack.EMPTY;
	protected List<ItemStack> inputs;
	protected List<ItemStack> remains;
	protected String typeName;
	protected ItemStack typeStack = defaultTypeStack;

	public TileItemStructureCraftCC() {

	}

	public TileItemStructureCraftCC(String typeName) {
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
		// TODO
	}

	@Override
	public void onLoad() {
		if (world.isRemote) return;
		if (slotMap.isEmpty()) {
			setCraftTypeName(typeName);
			refreshSlotMap();
		} else updateOutput();
	}

	public IISCCCraftHanlder getCraftHandler() {
		String typeName = getTypeName();
		if (typeName.equals("mc")) return handlerMap.get("mc");
		IISCCCraftHanlder handler = handlerMap.get(typeName);
		return handler == null ? handlerMap.get("mc") : handler;
	}

	public void setCraftTypeName(String typeName) {
		if (this.getTypeName().equals(typeName)) return;
		this.typeName = typeName;
		this.refreshSlotMap();
	}

	public void refreshSlotMap() {
		this.resetSlotMap();
		IISCCCraftHanlder craftHanlder = this.getCraftHandler();
		if (craftHanlder == null) return;
		List<Integer> list = craftHanlder.getSlotIndexMap();
		for (int index : list) addSlot(index);
		this.markDirty();
	}

	public void updateOutput() {
		this.output = ItemStack.EMPTY;
		this.remains = this.inputs = null;
		IISCCCraftHanlder craftHanlder = this.getCraftHandler();
		if (craftHanlder == null) return;
		try {
			List<ItemStack> inputs = new ArrayList<>();
			List<ItemStack> remains = new ArrayList<>();
			ItemStack output = craftHanlder.match(world, pos, slotMap, inputs, remains);
			if (output.isEmpty()) return;
			this.output = output;
			this.inputs = inputs;
			this.remains = remains;
			this.markDirty();
		} catch (Throwable e) {
			ElementalSorcery.logger.warn("处理合成出现异常！", e);
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

	@SideOnly(Side.CLIENT)
	public void setOutput(ItemStack stack) {
		this.output = stack;
	}

}
