package yuzunyan.elementalsorcery.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.ElementalSorcery;
import yuzunyan.elementalsorcery.container.ESGuiHandler;
import yuzunyan.elementalsorcery.init.ESInitInstance;
import yuzunyan.elementalsorcery.parchment.Page;

public class ItemParchment extends Item {

	public static ItemStack getParchment(int id) {
		return Page.setPageAt(new ItemStack(ESInitInstance.ITEMS.PARCHMENT), id);
	}

	public ItemParchment() {
		this.setUnlocalizedName("parchment");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		Page page = Page.getPage(stack);
		if (page.getId() == 0)
			return;
		tooltip.add("§e" + I18n.format(page.getItemInfo()));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (handIn != EnumHand.MAIN_HAND)
			return super.onItemRightClick(worldIn, playerIn, handIn);
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (Page.getPageId(stack) == 0)
			return super.onItemRightClick(worldIn, playerIn, handIn);
		if (worldIn.isRemote) {
			BlockPos pos = playerIn.getPosition();
			playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_PARCHMENT, worldIn, pos.getX(), pos.getY(),
					pos.getZ());
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

}
