package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.DNParams;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.api.computer.IComputEnv;
import yuzunyannn.elementalsorcery.api.computer.IComputer;
import yuzunyannn.elementalsorcery.api.computer.soft.AppDiskType;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.ComputerDevice;
import yuzunyannn.elementalsorcery.computer.ComputerEnvItem;
import yuzunyannn.elementalsorcery.computer.ComputerProviderOfItem;
import yuzunyannn.elementalsorcery.computer.DeviceNetworkLocal;
import yuzunyannn.elementalsorcery.computer.Disk;
import yuzunyannn.elementalsorcery.computer.soft.AuthorityAppDisk;
import yuzunyannn.elementalsorcery.computer.soft.EOS;
import yuzunyannn.elementalsorcery.computer.softs.AppTutorial;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.item.IItemSmashable;
import yuzunyannn.elementalsorcery.item.prop.ItemPadEasyPart;
import yuzunyannn.elementalsorcery.item.prop.ItemPadEasyPart.EnumType;
import yuzunyannn.elementalsorcery.util.item.ItemHandlerVest;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemTutorialPad extends ItemPad implements IItemSmashable {

	static class TutoiralComputer extends ComputerDevice {
		IInventory inventory = null;

		public TutoiralComputer(ItemStack stack) {
			super("tutorialPad", stack);
//			device.setNetwork(new DeviceNetworkLocal(device));
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
				return (T) new ItemHandlerVest(inventory);
			}
			return super.getCapability(capability, facing);
		}

		@Override
		public CompletableFuture<DNResult> notice(String method, DNParams params) {
			if ("get-inventory".equals(method)) {
				if (env == null) return DNResult.unavailable();
				CompletableFuture<DNResult> future = new CompletableFuture<DNResult>();
				IInventory inventory = null;
				EntityLivingBase player = env.getEntityLiving();
				if (player instanceof EntityPlayer) inventory = ((EntityPlayer) player).inventory;
				if (inventory == null) future.complete(DNResult.of(DNResultCode.FAIL));
				else {
					DNResult result = DNResult.of(DNResultCode.SUCCESS);
					result.set("inventory", inventory);
					future.complete(result);
				}
				return future;
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
				AuthorityAppDisk disk = new AuthorityAppDisk(computer, ItemTutorialPad.APP_ID.toString(),
						computer.getDisks(), AppDiskType.USER_DATA);
				disk.set(AppTutorial.POGRESS, 999f);
				items.add(full);
				ItemHelper.getOrCreateTagCompound(full).setFloat("tprogress", 999f);
				full.setTranslatableName("item.tutorialPad.full.name");
			} catch (Exception e) {}
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		ComputerProviderOfItem provider = new ComputerProviderOfItem(stack, new TutoiralComputer(stack));
		Computer computer = provider.getComputer();
		Disk disk = new Disk();
		disk.set(EOS.BOOT, APP_ID.toString());
		computer.addDisk(disk);
		return provider;
	}

	@Override
	public NBTTagCompound getNBTShareTag(ItemStack stack) {
		NBTTagCompound nbt = super.getNBTShareTag(stack);
		if (nbt == null) nbt = new NBTTagCompound();
		try {
			IComputer computer = stack.getCapability(Computer.COMPUTER_CAPABILITY, null);
			AuthorityAppDisk disk = new AuthorityAppDisk(computer, ItemTutorialPad.APP_ID.toString(),
					computer.getDisks(), AppDiskType.USER_DATA);
			float progress = disk.get(AppTutorial.POGRESS);
			nbt.setFloat("tprogress", progress);
		} catch (Exception e) {}
		return nbt;
	}

	@Override
	public void readNBTShareTag(ItemStack stack, NBTTagCompound nbt) {
		super.readNBTShareTag(stack, nbt);
		try {
			if (nbt != null && nbt.hasKey("tprogress")) {
				IComputer computer = stack.getCapability(Computer.COMPUTER_CAPABILITY, null);
				AuthorityAppDisk appDisk = new AuthorityAppDisk(computer, ItemTutorialPad.APP_ID.toString(),
						computer.getDisks(), AppDiskType.USER_DATA);
				appDisk.set(AppTutorial.POGRESS, nbt.getFloat("tprogress"));
			}
		} catch (Exception e) {}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (worldIn.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

		BlockPos pos = playerIn.getPosition();
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_COMPUTER_ITEM, worldIn, pos.getX(), pos.getY(), pos.getZ());

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		ComputerDevice computer = (ComputerDevice) entityItem.getItem().getCapability(Computer.COMPUTER_CAPABILITY, null);
		computer.setEnv(new ComputerEnvItem(entityItem)).update();
		return super.onEntityItemUpdate(entityItem);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		ComputerDevice computer = (ComputerDevice) stack.getCapability(Computer.COMPUTER_CAPABILITY, null);
		computer.setEnv(new ComputerEnvItem(entityIn, stack, itemSlot)).update();
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	@Override
	public void doSmash(World world, Vec3d vec, ItemStack stack, List<ItemStack> outputs, Entity operator) {
		if (world.isRemote) return;

		Random rand = world.rand;

		outputs.add(ItemPadEasyPart.create(EnumType.FLUORESCENT_PARTICLE, rand.nextInt(32) + 16));
		outputs.add(ItemPadEasyPart.create(EnumType.CONTROL_CIRCUIT, rand.nextInt(5) + 3));
		outputs.add(ItemPadEasyPart.create(EnumType.ACCESS_CIRCUIT, rand.nextInt(3) + 1));
		outputs.add(ItemPadEasyPart.create(EnumType.DISPLAY_CIRCUIT, 1));
		outputs.add(ItemPadEasyPart.create(EnumType.CALCULATE_CIRCUIT, 1));

		stack.shrink(1);

		world.playSound(null, vec.x, vec.y, vec.z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1, 1);
	}

}
