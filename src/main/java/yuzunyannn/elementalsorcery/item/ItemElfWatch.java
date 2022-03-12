package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.ElfTime;

public class ItemElfWatch extends Item {

	public ItemElfWatch() {
		this.setTranslationKey("elfWatch");
		this.setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (worldIn == null) return;
		ElfTime time = new ElfTime(worldIn);
		tooltip.add(TextFormatting.GREEN + I18n.format("elf.time.calendar", time.getDate()));

		if (!ElementalSorcery.isDevelop) return;
		for (ElfTime.Period period : ElfTime.Period.values()) {
			if (time.at(period)) {
				tooltip.add(TextFormatting.YELLOW + period.name());
				break;
			}
		}
	}

}
