package yuzunyannn.elementalsorcery.item;

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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.crafting.mc.RecipeRiteWrite;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.parchment.Page;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;

public class ItemParchment extends Item {

	/** 获取页面羊皮卷 */
	public static ItemStack getParchment(String id) {
		if (id == null || id.isEmpty()) return new ItemStack(ESInit.ITEMS.PARCHMENT);
		return Pages.setPage(id, new ItemStack(ESInit.ITEMS.PARCHMENT));
	}

	public ItemParchment() {
		this.setUnlocalizedName("parchment");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (Pages.isVaild(stack)) {
			Page page = Pages.getPage(stack);
			page.addItemInformation(stack, worldIn, tooltip, flagIn);
		}
		ItemStack inner = RecipeRiteWrite.getInnerStack(stack);
		if (!inner.isEmpty()) {
			TileRiteTable.Recipe r = TileRiteTable.findRecipe(inner);
			tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("info.written", r.getOutput().getDisplayName()));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (handIn != EnumHand.MAIN_HAND) return super.onItemRightClick(worldIn, playerIn, handIn);
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!Pages.isVaild(stack)) return super.onItemRightClick(worldIn, playerIn, handIn);
		if (worldIn.isRemote) {
			BlockPos pos = playerIn.getPosition();
			playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_PARCHMENT, worldIn, pos.getX(), pos.getY(),
					pos.getZ());
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

}
