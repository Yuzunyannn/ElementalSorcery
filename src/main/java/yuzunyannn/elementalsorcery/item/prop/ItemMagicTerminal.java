package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class ItemMagicTerminal extends Item {

	public ItemMagicTerminal() {
		this.setTranslationKey("magicTerminal");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String str = TextFormatting.OBFUSCATED + "ElementalSorcery";
		TextFormatting[] formattiongs = TextFormatting.values();
		tooltip.add(formattiongs[RandomHelper.rand.nextInt(formattiongs.length)] + str);
	}

}
