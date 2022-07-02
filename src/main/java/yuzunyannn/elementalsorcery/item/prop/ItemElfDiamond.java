package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemElfDiamond extends Item implements EntityThrow.IItemThrowAction {

	public ItemElfDiamond() {
		this.setTranslationKey("elfDiamond");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		EntityThrow.shoot(playerIn, playerIn.getHeldItem(handIn));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public void onImpact(EntityThrow entity, RayTraceResult result) {
		if (entity.world.isRemote) return;
		EntityLivingBase thrower = entity.getThrower();
		if (result.entityHit instanceof EntityElfBase && thrower != null) {
			EntityElfBase elf = (EntityElfBase) result.entityHit;
			elf.givePresent(thrower, entity.getItemStack());
			return;
		}
		ItemHelper.dropItem(entity.world, result.hitVec, entity.getItemStack());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.AQUA + I18n.format("info.elf.diamond"));
	}

}
