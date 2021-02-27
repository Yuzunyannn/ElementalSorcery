package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.item.ItemMagicBlastWand;

public class ItemMagicCore extends Item implements EntityThrow.IItemThrowAction {

	public ItemMagicCore() {
		this.setUnlocalizedName("magicCore");
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
		if (result.entityHit != null) vec = vec.addVector(0, result.entityHit.height / 2, 0);
		ItemMagicBlastWand.blast(ElementStack.magic(200, 128), entity.world, vec, entity.getThrower(), entity);
	}

}
