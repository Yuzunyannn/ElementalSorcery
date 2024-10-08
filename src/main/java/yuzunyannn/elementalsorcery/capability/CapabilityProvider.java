package yuzunyannn.elementalsorcery.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import yuzunyannn.elementalsorcery.api.entity.IFairyCubeMaster;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;

public class CapabilityProvider {

	/** 使用仓库提供 */
	public static class ElementInventoryUseProvider implements ICapabilityProvider {

		private IElementInventory inventory;

		public ElementInventoryUseProvider(ItemStack dyStack) {
			this(dyStack, null);
		}

		public ElementInventoryUseProvider(ItemStack dyStack, IElementInventory inventory) {
			this.inventory = inventory == null ? new ElementInventory() : inventory;
			ElementInventory.sensor(inventory, dyStack);
			inventory.applyUse();
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return (T) inventory;
			return null;
		}

	}

	/** 使用仓库提供，检测是否有stack */
	public static class ElementInventoryUseProviderCheck implements ICapabilityProvider {

		private IElementInventory inventory;
		private final ItemStack stack;

		public ElementInventoryUseProviderCheck(ItemStack dyStack) {
			this(dyStack, null);
		}

		public ElementInventoryUseProviderCheck(ItemStack dyStack, IElementInventory inventory) {
			this.inventory = inventory == null ? new ElementInventory() : inventory;
			ElementInventory.sensor(inventory, this.stack = dyStack);
			inventory.applyUse();
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability && ElementInventory.hasInvData(stack);
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability)
				if (ElementInventory.hasInvData(stack)) return (T) inventory;
			return null;
		}

	}

	/** 使用spellbook提供 */
	public static class SpellbookUseProvider implements ICapabilityProvider {

		private Spellbook instance = new Spellbook();

		public SpellbookUseProvider(ItemStack dyStack) {
			this(dyStack, null);
		}

		public SpellbookUseProvider(ItemStack dyStack, IElementInventory inventory) {
			instance.inventory = inventory;
			if (instance.inventory != null) {
				ElementInventory.sensor(inventory, dyStack);
				inventory.applyUse();
			}
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return Spellbook.SPELLBOOK_CAPABILITY == capability;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (Spellbook.SPELLBOOK_CAPABILITY == capability) return (T) instance;
			return null;
		}

	}

	/** 精灵主人 */
	public static class FairyCubeMasterProvider implements ICapabilitySerializable<NBTTagCompound> {

		private IFairyCubeMaster master;
		public final static IStorage<IFairyCubeMaster> storage = FairyCubeMaster.FAIRY_CUBE_MASTER_CAPABILITY.getStorage();

		public FairyCubeMasterProvider() {
			this(null);
		}

		public FairyCubeMasterProvider(IFairyCubeMaster master) {
			this.master = master == null ? new FairyCubeMaster() : master;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return FairyCubeMaster.FAIRY_CUBE_MASTER_CAPABILITY.equals(capability);
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (FairyCubeMaster.FAIRY_CUBE_MASTER_CAPABILITY.equals(capability)) return (T) master;
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound) storage.writeNBT(FairyCubeMaster.FAIRY_CUBE_MASTER_CAPABILITY, master, null);
		}

		@Override
		public void deserializeNBT(NBTTagCompound compound) {
			storage.readNBT(FairyCubeMaster.FAIRY_CUBE_MASTER_CAPABILITY, master, null, compound);
		}
	}

	public static class AdventurerProvider implements ICapabilitySerializable<NBTTagCompound> {

		private IAdventurer adventurer;
		public final static IStorage<IAdventurer> storage = Adventurer.ADVENTURER_CAPABILITY.getStorage();

		public AdventurerProvider() {
			this(null);
		}

		public AdventurerProvider(IAdventurer adventurer) {
			this.adventurer = adventurer == null ? new Adventurer() : adventurer;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return Adventurer.ADVENTURER_CAPABILITY.equals(capability);
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (Adventurer.ADVENTURER_CAPABILITY.equals(capability)) return (T) adventurer;
			return null;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound) storage.writeNBT(Adventurer.ADVENTURER_CAPABILITY, adventurer, null);
		}

		@Override
		public void deserializeNBT(NBTTagCompound compound) {
			storage.readNBT(Adventurer.ADVENTURER_CAPABILITY, adventurer, null, compound);
		}
	}

}
