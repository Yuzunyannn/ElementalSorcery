package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.entity.EntityThrow;

public class ItemRelicGuardCore extends Item implements EntityThrow.IItemThrowAction {

	public ItemRelicGuardCore() {
		this.setTranslationKey("relicGuardCore");
	}

	@Override
	public void onImpact(EntityThrow entity, RayTraceResult result) {
		Vec3d vec = result.hitVec;
		if (vec == null) return;
		if (entity.world.isRemote) return;
		for (int i = 0; i < entity.getRandom().nextInt(3) + 1; i++)
			entity.entityDropItem(new ItemStack(ESObjects.ITEMS.RELIC_GEM), 0);
		entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_GLASS_BREAK,
				SoundCategory.NEUTRAL, 1, 1);
	}

}
