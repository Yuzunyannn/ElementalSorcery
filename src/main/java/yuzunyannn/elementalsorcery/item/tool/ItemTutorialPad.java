package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

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
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.DNParams;
import yuzunyannn.elementalsorcery.api.computer.DNResult;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.ComputerEnvItem;
import yuzunyannn.elementalsorcery.computer.ComputerProviderOfItem;
import yuzunyannn.elementalsorcery.computer.DeviceNetwork;
import yuzunyannn.elementalsorcery.computer.DeviceNetworkLocal;
import yuzunyannn.elementalsorcery.computer.Disk;
import yuzunyannn.elementalsorcery.computer.soft.EOS;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.item.IItemSmashable;
import yuzunyannn.elementalsorcery.item.prop.ItemPadEasyPart;
import yuzunyannn.elementalsorcery.item.prop.ItemPadEasyPart.EnumType;

public class ItemTutorialPad extends ItemPad implements IItemSmashable {

	static class TutoiralComputer extends Computer {

		public TutoiralComputer() {
			super("tutorialPad");
		}

		protected DeviceNetwork initCreateNetwork() {
			return new DeviceNetworkLocal(this);
		}

		public boolean hasAbility(String ability) {
			return "item-writer".equals(ability);
		};

		@Override
		public CompletableFuture<DNResult> notice(String method, DNParams params) {
			if ("get-inventory".equals(method)) {
				CompletableFuture<DNResult> future = new CompletableFuture<DNResult>();
				addUpdateFunc(env -> {
					IInventory inventory = null;
					EntityLivingBase player = env.getEntityLiving();
					if (player instanceof EntityPlayer) inventory = ((EntityPlayer) player).inventory;
					if (inventory == null) future.complete(DNResult.of(DNResultCode.FAIL));
					else {
						DNResult result = DNResult.of(DNResultCode.SUCCESS);
						result.set("inventory", inventory);
						future.complete(result);
					}
				});
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
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		ComputerProviderOfItem provider = new ComputerProviderOfItem(stack, new TutoiralComputer());
		Computer computer = provider.getComputer();
		Disk disk = new Disk();
		disk.set(EOS.BOOT, APP_ID.toString());
		computer.addDisk(disk);
		return provider;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (worldIn.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

		BlockPos pos = playerIn.getPosition();
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_COMPUTER_ITEM, worldIn, pos.getX(), pos.getY(),
				pos.getZ());

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		ComputerEnvItem env = new ComputerEnvItem(entityItem);
		entityItem.getItem().getCapability(Computer.COMPUTER_CAPABILITY, null).update(env);
		return super.onEntityItemUpdate(entityItem);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		ComputerEnvItem env = new ComputerEnvItem(entityIn, stack, itemSlot);
		stack.getCapability(Computer.COMPUTER_CAPABILITY, null).update(env);
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
