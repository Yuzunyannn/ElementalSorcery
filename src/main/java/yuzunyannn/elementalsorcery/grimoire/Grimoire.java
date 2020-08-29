package yuzunyannn.elementalsorcery.grimoire;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoireInfo;

/** 该能力仅仅是跟随物品，作为上下文数据使用 */
public class Grimoire {

	@CapabilityInject(Grimoire.class)
	public static Capability<Grimoire> GRIMOIRE_CAPABILITY;

	/** 渲染信息 */
	private Object renderInfo;

	@SideOnly(Side.CLIENT)
	public RenderItemGrimoireInfo getRenderInfo() {
		if (renderInfo == null) renderInfo = new RenderItemGrimoireInfo();
		return (RenderItemGrimoireInfo) renderInfo;
	}

	/** 记录的仓库 */
	IElementInventory inventory = null;

	/** 获取仓库 */
	@Nullable
	public IElementInventory getInventory() {
		return inventory;
	}

	public void load(ItemStack stack) {
		if (inventory != null) inventory.loadState(stack);
	}

	public void save(ItemStack stack) {
		if (inventory != null) inventory.saveState(stack);
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
