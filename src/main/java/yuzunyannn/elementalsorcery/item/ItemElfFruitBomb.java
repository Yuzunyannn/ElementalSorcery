package yuzunyannn.elementalsorcery.item;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemElfFruitBomb extends Item implements EntityThrow.IItemThrowAction {

	public ItemElfFruitBomb() {
		this.setTranslationKey("elfFruitBomb");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		EntityThrow.shoot(playerIn, playerIn.getHeldItem(handIn));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public void onImpact(EntityThrow entity, RayTraceResult result) {
		Vec3d vec = result.hitVec;
		if (vec == null) return;
		World world = entity.world;
		if (world.isRemote) return;

		List<PotionEffect> list = new LinkedList<>();
		int duration = 20 * 60;
		switch (entity.getRandom().nextInt(2)) {
		case 0:
			list.add(new PotionEffect(MobEffects.HUNGER, duration, 1));
			break;
		case 1:
			list.add(new PotionEffect(MobEffects.POISON, duration, 0));
			break;
		}
		WorldHelper.applySplash(world, vec, list);
		world.playEvent(2002, new BlockPos(vec), list.get(0).getPotion().getLiquidColor());
		world.createExplosion(entity, vec.x, vec.y, vec.z, 1.5F, true);
	}
}
