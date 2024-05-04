package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.computer.ComputerDevice;
import yuzunyannn.elementalsorcery.computer.ComputerEnvItem;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.item.IItemSmashable;
import yuzunyannn.elementalsorcery.item.prop.ItemPadEasyPart;
import yuzunyannn.elementalsorcery.item.prop.ItemPadEasyPart.EnumType;

public class ItemPad extends Item implements IItemSmashable {

	public ItemPad() {
		this.setMaxStackSize(1);
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

		ComputerDevice computer = (ComputerDevice) stack.getCapability(Computer.COMPUTER_CAPABILITY, null);
		if (computer.device().getNetwork().isDiscoverable())
			outputs.add(ItemPadEasyPart.create(EnumType.WIFI_CIRCUIT, 1));

		IItemHandler itemHandler = computer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (itemHandler != null) {
			for (int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack dropStack = itemHandler.getStackInSlot(i);
				if (!dropStack.isEmpty()) outputs.add(dropStack);
			}
		}

		stack.shrink(1);
		world.playSound(null, vec.x, vec.y, vec.z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1, 1);
	}
}
