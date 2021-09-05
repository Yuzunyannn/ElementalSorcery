package yuzunyannn.elementalsorcery.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
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

	@Override
	public int getItemBurnTime(ItemStack itemStack) {
		return 75;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (Pages.isVaild(stack)) {
			Page page = Pages.getPage(stack);
			page.addItemInformation(stack, worldIn, tooltip, flagIn);
		} else {
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player != null && player.isCreative())
				tooltip.add(TextFormatting.YELLOW + I18n.format("info.page.creative.check"));
		}
		ItemStack inner = RecipeRiteWrite.getInnerStack(stack);
		if (!inner.isEmpty()) {
			TileRiteTable.Recipe r = TileRiteTable.findRecipe(inner);
			tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("info.written", r.getOutput().getDisplayName()));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		if (handIn != EnumHand.MAIN_HAND) return super.onItemRightClick(world, player, handIn);
		ItemStack stack = player.getHeldItem(handIn);
		if (!Pages.isVaild(stack)) {
			if (player.isCreative() && handIn == EnumHand.MAIN_HAND) {
				Page page = Pages.itemToPage(player.getHeldItem(EnumHand.OFF_HAND));
				if (page != null) {
					Pages.setPage(page.getId(), stack);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
				}
			}
			return super.onItemRightClick(world, player, handIn);
		}
		if (world.isRemote) {
			BlockPos pos = player.getPosition();
			player.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_PARCHMENT, world, pos.getX(), pos.getY(),
					pos.getZ());
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
	}

}
