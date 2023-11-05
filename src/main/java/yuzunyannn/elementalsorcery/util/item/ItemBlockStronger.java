package yuzunyannn.elementalsorcery.util.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.block.IBlockStronger;

public class ItemBlockStronger extends ItemBlock {

	protected final IBlockStronger stronger;

	public <T extends Block & IBlockStronger> ItemBlockStronger(T block) {
		super(block);
		this.stronger = block;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		return stronger.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		String key = stronger.getTranslationKey(stack);
		return key == null ? super.getTranslationKey(stack) : key;
	}

}
