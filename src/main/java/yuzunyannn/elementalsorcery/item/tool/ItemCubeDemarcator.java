package yuzunyannn.elementalsorcery.item.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryStronger;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class ItemCubeDemarcator extends Item {

	public ItemCubeDemarcator() {
		this.setTranslationKey("cubeDemarcator");
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		TileEntity tile = BlockHelper.getTileEntity(worldIn, pos, TileEntity.class);
		IElementInventory eInv = ElementHelper.getElementInventory(tile);
		if (eInv == null) return EnumActionResult.PASS;
		if (!(eInv instanceof ElementInventoryStronger)) return EnumActionResult.PASS;
		if (worldIn.isRemote) return EnumActionResult.SUCCESS;
		ElementInventoryStronger eInvStronger = (ElementInventoryStronger) eInv;
		if (eInvStronger.getTerminal() != 0) return EnumActionResult.FAIL;
		eInvStronger.setTerminal((byte) 1);
		if (!EntityHelper.isCreative(player)) stack.shrink(1);
		player.sendMessage(new TextComponentTranslation("info.installer.success")
				.setStyle(new Style().setColor(TextFormatting.YELLOW)));
		return EnumActionResult.SUCCESS;
	}

}
