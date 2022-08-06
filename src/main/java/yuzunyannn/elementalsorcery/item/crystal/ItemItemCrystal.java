package yuzunyannn.elementalsorcery.item.crystal;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemItemCrystal extends ItemCrystal {
	public ItemItemCrystal() {
		super("itemCrystal", 27.77f, 0xb26e0c);
		this.setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		IItemStructure istru = ItemStructure.getItemStructure(stack);
		if (istru.getItemCount() == 0) return;
		ItemStack showStack = istru.getStructureItem(0);
		IToElementInfo teInfo = istru.toElement(showStack);
		if (teInfo == null) return;
		String name = showStack.getDisplayName();
		tooltip.add(I18n.format("info.itemCrystal.data", name));
		tooltip.add(I18n.format("info.itemCrystal.complex", teInfo.complex()));
		tooltip.add(I18n.format("info.itemCrystal.z"));
		ElementStack[] estacks = teInfo.element();
		for (ElementStack esatck : estacks) {
			name = I18n.format(esatck.getElementUnlocalizedName());
			tooltip.add(I18n.format("info.itemCrystal.e", name, esatck.getCount(), esatck.getPower()));
		}

	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!player.isCreative()) return EnumActionResult.PASS;
		if (!player.isSneaking()) return EnumActionResult.PASS;
		if (!ESAPI.isDevelop) return EnumActionResult.PASS;
		if (worldIn.isRemote) return EnumActionResult.SUCCESS;

		ItemStack targetItem = ItemHelper.toItemStack(worldIn.getBlockState(pos));
		ItemStack stack = player.getHeldItem(hand);
		IItemStructure istru = ItemStructure.getItemStructure(stack);
		IToElementInfo info = ElementMap.instance.toElement(targetItem);
		if (info != null) istru.set(0, targetItem, info.complex(), info.element());
		else istru.set(0, targetItem, 1, ElementStack.magic(200, 10));
		istru.saveState(stack);

		return EnumActionResult.SUCCESS;
	}
}
