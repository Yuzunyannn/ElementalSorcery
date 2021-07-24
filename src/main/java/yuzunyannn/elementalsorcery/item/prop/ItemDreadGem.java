package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemDreadGem extends Item {

	public ItemDreadGem() {
		this.setUnlocalizedName("dreadGem");
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		EnumHand hand = ItemVortex.inEntityHand(entityIn, stack, itemSlot, isSelected);
		if (hand == null) return;
		if (entityIn.ticksExisted % 100 == 0) {
			AxisAlignedBB aabb = WorldHelper.createAABB(entityIn.getPositionVector(), 5, 2, 1);
			List<EntityLiving> mobs = worldIn.getEntitiesWithinAABB(EntityLiving.class, aabb, (e) -> {
				return e instanceof IMob;
			});
			for (EntityLiving mob : mobs) {
				EntityLivingBase player = (EntityLivingBase) entityIn;
				if (mob.getRevengeTarget() != null) mob.setRevengeTarget(player);
				else mob.setAttackTarget(player);
			}
		}

	}

}
