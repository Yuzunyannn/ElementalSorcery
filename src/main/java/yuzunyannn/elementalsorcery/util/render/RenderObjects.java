package yuzunyannn.elementalsorcery.util.render;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

@SideOnly(Side.CLIENT)
public class RenderObjects {

	static final public ItemStack MAGIC_STONE = new ItemStack(ESInitInstance.ITEMS.MAGIC_STONE);

	static final public ResourceLocation KYANITE_BLOCK = new ResourceLocation(ElementalSorcery.MODID,
			"textures/blocks/kyanite_block.png");
	static final public ResourceLocation KYANITE_ORE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/blocks/kyanite_ore.png");
	static final public ResourceLocation ASTONE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/blocks/astone.png");
	static final public ResourceLocation ASTONE_FRAGMENTED = new ResourceLocation(ElementalSorcery.MODID,
			"textures/blocks/astone_fragmented.png");

	static final public ResourceLocation STONE = new ResourceLocation("minecraft", "textures/blocks/stone.png");
}
