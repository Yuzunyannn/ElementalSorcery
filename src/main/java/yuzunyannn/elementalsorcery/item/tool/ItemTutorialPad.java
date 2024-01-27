package yuzunyannn.elementalsorcery.item.tool;

import java.util.concurrent.CompletableFuture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
import yuzunyannn.elementalsorcery.computer.Disk;
import yuzunyannn.elementalsorcery.computer.soft.EOS;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;

public class ItemTutorialPad extends ItemPad {

	static class TutoiralComputer extends Computer {

		public TutoiralComputer() {
			super("tutorialPad");
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

}
