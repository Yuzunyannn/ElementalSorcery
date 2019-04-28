package yuzunyan.elementalsorcery.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.capability.Spellbook;
import yuzunyan.elementalsorcery.init.ESInitInstance;
import yuzunyan.elementalsorcery.render.item.RenderItemSpellbook;

public class ItemSpellbookEnchantment extends ItemSpellbook {

	public ItemSpellbookEnchantment() {
		this.setUnlocalizedName("spellbookEnchantment");
		this.setMaxStackSize(64);
	}

	@Override
	public boolean spellBegin(World world, EntityLivingBase entity, ItemStack stack, Spellbook book) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void initRenderInfo(RenderItemSpellbook.RenderInfo info) {
		info.texture = RenderItemSpellbook.instance.TEXTURE_ENCHANTING_BOOK;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() != ESInitInstance.BLOCKS.INVALID_ENCHANTMENT_TABLE)
			return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		worldIn.setBlockState(pos, Blocks.ENCHANTING_TABLE.getDefaultState());
		if (!player.isCreative()) {
			ItemStack itemstack = player.getHeldItem(hand);
			itemstack.shrink(1);
		}
		return EnumActionResult.SUCCESS;
	}

}
