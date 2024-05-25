package yuzunyannn.elementalsorcery.block.device;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.item.IItemSmashable;
import yuzunyannn.elementalsorcery.item.prop.ItemPadEasyPart;
import yuzunyannn.elementalsorcery.item.prop.ItemPadEasyPart.EnumType;

public abstract class BlockComputer extends BlockDevice implements IItemSmashable {

	protected BlockComputer(Material materialIn, String unlocalizedName, float hardness, MapColor color) {
		super(materialIn, unlocalizedName, hardness, color);
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
		outputs.add(ItemPadEasyPart.create(EnumType.WIFI_CIRCUIT, 1));

		IItemHandler itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
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
