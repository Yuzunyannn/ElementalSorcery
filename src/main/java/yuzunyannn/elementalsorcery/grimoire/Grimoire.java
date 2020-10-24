package yuzunyannn.elementalsorcery.grimoire;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.crafting.IItemCapbiltitySyn;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoireInfo;

/** 该能力仅仅是跟随物品，作为上下文数据使用 */
public class Grimoire implements IItemCapbiltitySyn, INBTSerializable<NBTTagCompound> {

	@CapabilityInject(Grimoire.class)
	public static Capability<Grimoire> GRIMOIRE_CAPABILITY;

	/** 渲染信息 */
	private Object renderInfo;

	@SideOnly(Side.CLIENT)
	public RenderItemGrimoireInfo getRenderInfo() {
		if (renderInfo == null) renderInfo = new RenderItemGrimoireInfo();
		return (RenderItemGrimoireInfo) renderInfo;
	}

	/** 咒文内容 */
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

	/** 记录的仓库 */
	IElementInventory inventory = null;
	/** 咒文队列 */
	protected ArrayList<Info> mantraList = new ArrayList<>();
	protected short at = 0;
	protected short capacity = 0;

	/** 获取仓库 */
	@Nullable
	public IElementInventory getInventory() {
		return inventory;
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
		if (inventory != null) inventory.loadState(nbt);
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
		if (inventory != null) inventory.saveState(nbt);
		NBTTagList mantras = new NBTTagList();
		for (Info info : mantraList) {
			info.data.setString("id", info.mantra.getRegistryName().toString());
			mantras.appendTag(info.data);
		}
		nbt.setTag("mantra", mantras);
		nbt.setShort("capacity", capacity);
		nbt.setShort("at", at);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.saveState(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.loadState(nbt);
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

	/** 切换咒文的位置 */
	public static void shiftMantra(ItemStack stack, short to) {
		if (stack.isEmpty()) return;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return;
		NBTTagList list = nbt.getTagList("mantra", 10);
		if (list.hasNoTags()) return;
		if (to < 0 || to >= list.tagCount()) return;
		nbt.setShort("at", to);
	}

	// 保存能力
	public static class Storage implements Capability.IStorage<Grimoire> {

		@Override
		public NBTBase writeNBT(Capability<Grimoire> capability, Grimoire instance, EnumFacing side) {
			return new NBTTagCompound();
		}

		@Override
		public void readNBT(Capability<Grimoire> capability, Grimoire instance, EnumFacing side, NBTBase tag) {
		}

	}

	// 能力提供者
	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

		public final static IStorage<Grimoire> storage = GRIMOIRE_CAPABILITY.getStorage();
		private Grimoire instance = new Grimoire();

		public Provider() {
			this(null);
		}

		public Provider(IElementInventory inventory) {
			instance.inventory = inventory;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return GRIMOIRE_CAPABILITY == capability;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (GRIMOIRE_CAPABILITY == capability) return (T) instance;
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

}
