package yuzunyannn.elementalsorcery.grimoire;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.crafting.IItemCapbiltitySyn;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoireInfo;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

/** 该能力仅仅是跟随物品，作为上下文数据使用 */
public class Grimoire implements IItemCapbiltitySyn, INBTSerializable<NBTTagCompound> {

	@CapabilityInject(Grimoire.class)
	public static Capability<Grimoire> GRIMOIRE_CAPABILITY;

	/** 渲染信息 */
	private Object renderInfo;

	/** 是否加载过，在施法过程中不会重复加载 */
	public int lastLoaded = 0;

	/** 当前施法实体的缓存 */
	protected WeakReference<EntityGrimoire> currGrimoireEntity;
	protected UUID currGrimoireEntityId;

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

		public void setData(NBTTagCompound data) {
			this.data = data == null ? this.data : data;
		}
	}

	/** 记录的仓库 */
	IElementInventory inventory = null;
	/** 咒文队列 */
	protected ArrayList<Info> mantraList = new ArrayList<>();
	protected short at = 0;
	protected short capacity = 0;
	protected short capacityMax = 20;
	/** 强效 */
	protected float potent = 0;
	public float potentPoint;

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

	public void add(Mantra m) {
		this.add(m, null);
	}

	public void remove(Mantra m) {
		Iterator<Info> iter = mantraList.iterator();
		while (iter.hasNext()) {
			Info info = iter.next();
			if (info.mantra == m) {
				iter.remove();
				break;
			}
		}
	}

	public Info getInfo(int i) {
		if (i < 0 || i >= mantraList.size()) return null;
		return mantraList.get(i);
	}

	public Info getSelectedInfo() {
		return getInfo(getSelected());
	}

	/** 当前选择的咒文 */
	public short getSelected() {
		return at;
	}

	public void growCapacity(int n) {
		capacity += n;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getCapacityMax() {
		return capacityMax;
	}

	public void setCapacityMax(int capacityMax) {
		this.capacityMax = (short) capacityMax;
	}

	public float getPotent() {
		if (potentPoint <= 0) return 0;
		return potent;
	}

	public void setPotent(float potent) {
		this.potent = Math.max(potent, 0);
	}

	public void addPotentPoint(float point) {
		this.potentPoint = this.potentPoint + point;
		if (this.potentPoint <= 0) {
			this.potentPoint = 0;
			this.potent = 0;
		}
	}

	public void addPotent(float potent, float point) {
		if (this.potent == 0) potentPoint = 0;
		this.potent = (potent * point + this.potent * potentPoint) / (point + potentPoint);
		addPotentPoint(point);
	}

	@Override
	public boolean hasState(NBTTagCompound nbt) {
		return nbt.hasKey("mantra", NBTTag.TAG_LIST);
	}

	public void tryLoadState(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null && nbt.hashCode() != lastLoaded) this.loadState(nbt);
	}

	@Override
	public void loadState(NBTTagCompound nbt) {
		lastLoaded = nbt.hashCode();
		if (inventory != null) inventory.loadState(nbt);
		NBTTagList mantras = nbt.getTagList("mantra", NBTTag.TAG_COMPOUND);
		mantraList = new ArrayList<>(mantras.tagCount());
		for (int i = 0; i < mantras.tagCount(); i++) {
			NBTTagCompound data = mantras.getCompoundTagAt(i);
			Mantra m = Mantra.getFromNBT(data);
			this.add(m, data);
		}
		capacity = nbt.getShort("capacity");
		capacityMax = nbt.getShort("capacityMax");
		at = nbt.getShort("at");
		potent = nbt.getFloat("potent");
		potentPoint = nbt.getFloat("potentP");
		if (nbt.hasUniqueId("cgeId")) currGrimoireEntityId = nbt.getUniqueId("cgeId");
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
		nbt.setShort("capacityMax", capacityMax);
		nbt.setShort("at", at);
		nbt.setFloat("potent", potent);
		nbt.setFloat("potentP", potentPoint);
		if (currGrimoireEntityId != null) nbt.setUniqueId("cgeId", currGrimoireEntityId);

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
	public static NBTTagCompound getMantraNBT(ItemStack stack) {
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
		if (list.isEmpty()) return;
		if (to < 0 || to >= list.tagCount()) return;
		nbt.setShort("at", to);
	}

	public void setGrimoireEntity(EntityGrimoire grimoireEntity) {
		if (grimoireEntity == null) {
			currGrimoireEntity = null;
			currGrimoireEntityId = null;
			return;
		}
		this.currGrimoireEntity = new WeakReference<EntityGrimoire>(grimoireEntity);
		this.currGrimoireEntityId = grimoireEntity.getUniqueID();
	}

	private EntityGrimoire findGrimoireEntity(World world) {
		Entity entity = WorldHelper.restoreLiving(world, this.currGrimoireEntityId);
		if (entity instanceof EntityGrimoire) {
			EntityGrimoire grimoire = (EntityGrimoire) entity;
			return grimoire;
		}
		return null;
	}

	public EntityGrimoire getGrimoireEntity(World world) {
		EntityGrimoire grimoire = currGrimoireEntity != null ? currGrimoireEntity.get() : null;
		if (grimoire == null) {
			if (currGrimoireEntityId == null) return null;
			grimoire = findGrimoireEntity(world);
			if (grimoire == null) {
				setGrimoireEntity(null);
				return null;
			}
		} else {
			if (grimoire.isDead) {
				setGrimoireEntity(null);
				return null;
			}
		}
		return grimoire;
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
	public static class Provider implements ICapabilityProvider {

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

	}

}
