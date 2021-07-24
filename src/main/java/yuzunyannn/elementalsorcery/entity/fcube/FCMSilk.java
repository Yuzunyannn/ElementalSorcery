package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class FCMSilk extends FairyCubeModule {

	@FairyCubeModuleRecipe
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv) {
		ElfTime time = new ElfTime(world);
		if (!time.at(ElfTime.Period.DUSK)) return false;
		return matchAndConsumeForCraft(world, pos, inv, ItemHelper.toList(Blocks.COAL_ORE, 8),
				ElementHelper.toList(ESInit.ELEMENTS.EARTH, 20, 10, ESInit.ELEMENTS.METAL, 20, 10));
	}

	public FCMSilk(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.MODIFY_ADD);
	}

	@Override
	public float modifyAttribute(String attribute, float value) {
		if ("silk".equals(attribute)) return value + 1;
		return value;
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "enchantment.untouching";
		return super.getStatusUnlocalizedValue(status);
	}

}
