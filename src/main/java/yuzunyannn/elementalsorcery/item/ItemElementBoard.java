package yuzunyannn.elementalsorcery.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;

public class ItemElementBoard extends Item {

	public ItemElementBoard() {
		this.setTranslationKey("elementBoard");
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		BlockPos pos = playerIn.getPosition();
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_ELEMENT_BOARD, worldIn, pos.getX(), pos.getY(),
				pos.getZ());
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

}
