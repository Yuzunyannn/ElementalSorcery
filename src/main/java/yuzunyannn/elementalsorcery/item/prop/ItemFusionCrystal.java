package yuzunyannn.elementalsorcery.item.prop;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemSimpleFoiled;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemFusionCrystal extends ItemSimpleFoiled {

	public ItemFusionCrystal() {
		this.setUnlocalizedName("fusionCrystal");
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (entityIn.ticksExisted % (60 * 20) != 0) return;
		if (worldIn.isRemote) return;
		EnumHand hand = ItemVortex.inEntityHand(entityIn, stack, itemSlot, isSelected);
		if (hand == null) return;
		Random rand = worldIn.rand;
		EntityEquipmentSlot slot = hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND
				: EntityEquipmentSlot.OFFHAND;

		stack.setTagCompound(null);
		float at = 0.01f + rand.nextFloat() * 0.19f;
		ItemLifeLeather.addAttributeModifier(stack, SharedMonsterAttributes.MAX_HEALTH, slot, at, 1);
		ItemLifeLeather.addAttributeModifier(stack, SharedMonsterAttributes.ATTACK_SPEED, slot, at, 1);
		ItemLifeLeather.addAttributeModifier(stack, SharedMonsterAttributes.MOVEMENT_SPEED, slot, at, 1);
		ItemLifeLeather.addAttributeModifier(stack, SharedMonsterAttributes.LUCK, slot, at, 1);
	}

}
