package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.item.IJuice;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;
import yuzunyannn.elementalsorcery.world.Juice;

public class ItemGlassCup extends Item {

	static public IJuice getJuice(ItemStack stack) {
		return new Juice(stack);
	}

	public ItemGlassCup() {
		this.setUnlocalizedName("glassCup");
		this.setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IJuice juice = getJuice(stack);
		if (juice != null) juice.addJuiceInformation(worldIn, tooltip, flagIn);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		IJuice juice = getJuice(stack);
		if (juice == null) return stack;
		juice.onDrink(worldIn, entityLiving);
		if (entityLiving instanceof EntityPlayer) {
			((EntityPlayer) entityLiving).addStat(StatList.getObjectUseStats(this));
		}
		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		IJuice juice = getJuice(itemstack);
		if (juice == null) return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);

		float dis = (float) playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		RayTraceResult ray = WorldHelper.getLookAtBlock(worldIn, playerIn, dis, true, false, false);
		if (ray != null) {
			if (juice.onContain(worldIn, playerIn, ray.getBlockPos()))
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		}

		if (juice.canDrink(worldIn, playerIn)) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		}

		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
	}

}
