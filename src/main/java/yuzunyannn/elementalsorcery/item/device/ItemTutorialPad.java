package yuzunyannn.elementalsorcery.item.device;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInitializable;
import yuzunyannn.elementalsorcery.api.computer.IDeviceStorage;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.ComputerDevice;
import yuzunyannn.elementalsorcery.computer.ComputerProviderOfItem;
import yuzunyannn.elementalsorcery.computer.DeviceNetworkLocal;
import yuzunyannn.elementalsorcery.computer.Disk;
import yuzunyannn.elementalsorcery.computer.soft.EOS;
import yuzunyannn.elementalsorcery.computer.softs.AppTutorial;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerVest;

public class ItemTutorialPad extends ItemPad {

	static class TutoiralComputer extends ComputerDevice {
		IInventory inventory = null;

		public TutoiralComputer(ItemStack stack) {
			super("tutorialPad", stack);
			IDeviceInitializable.Init init = new IDeviceInitializable.Init();
			init.network = new DeviceNetworkLocal(device);
			device.init(init);
		}

		@Override
		public ComputerDevice setEnv(IComputEnv env) {
			EntityLivingBase player = env.getEntityLiving();
			if (player instanceof EntityPlayer) inventory = ((EntityPlayer) player).inventory;
			return super.setEnv(env);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) return inventory != null;
			return super.hasCapability(capability, facing);
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
				if (inventory == null) return null;
				return (T) new ItemStackHandlerVest(inventory);
			}
			return super.getCapability(capability, facing);
		}

		@Override
		public DNResult notice(String method, DNRequest params) {
			if ("get-inventory".equals(method)) {
				if (env == null) return DNResult.unavailable();
				if (inventory == null) return DNResult.unavailable();
				DNResult result = DNResult.of(DNResultCode.SUCCESS);
				result.set("inventory", inventory);
				return result;
			}
			return super.notice(method, params);
		}
	}

	public static final ResourceLocation APP_ID = new ResourceLocation(ESAPI.MODID, "tutorial");

	public ItemTutorialPad() {
		this.setTranslationKey("tutorialPad");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this));
			try {
				ItemStack full = new ItemStack(this);
				IComputer computer = full.getCapability(Computer.COMPUTER_CAPABILITY, null);
				IDeviceStorage storage = AppTutorial.getTutorialData(computer.getDisks().get(0)).forceOpen();
				storage.set(AppTutorial.POGRESS, 999f);
				storage.markDirty().close();
				items.add(full);
				full.setTranslatableName("item.tutorialPad.full.name");
			} catch (Exception e) {}
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		ComputerProviderOfItem provider = new ComputerProviderOfItem(stack, new TutoiralComputer(stack));
		TutoiralComputer computer = (TutoiralComputer) provider.getComputer();
		computer.addDisk(EOS.setBoot(new Disk(), APP_ID.toString()));
		return provider;
	}

}
