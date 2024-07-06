package yuzunyannn.elementalsorcery.util.render;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderObjects {

	static final public ItemStack MAGIC_STONE = new ItemStack(ESObjects.ITEMS.MAGIC_STONE);

	static final public ResourceLocation CRAFTING_TABLE = MCRes("textures/gui/container/crafting_table.png");
	static final public ResourceLocation STONE = MCRes("textures/blocks/stone.png");
	static final public ResourceLocation MC_PARTICLE = MCRes("textures/particle/particles.png");
	
	static final public ResourceLocation KYANITE_BLOCK = ESRes("textures/blocks/kyanite_block.png");
	static final public ResourceLocation KYANITE_ORE = ESRes("textures/blocks/kyanite_ore.png");
	static final public ResourceLocation ASTONE = ESRes("textures/blocks/astone.png");
	static final public ResourceLocation ASTONE_FRAGMENTED = ESRes("textures/blocks/astone_fragmented.png");

	static final public ResourceLocation MAGIC_CIRCLE_SUMMON = ESRes("textures/magic_circles/summon.png");
	static final public ResourceLocation MAGIC_CIRCLE_PICKAXE = ESRes("textures/magic_circles/pickaxe.png");

	public static final TextureBinder EFFECT_BUFF = new TextureBinder("textures/gui/effect_buff.png");
	
	public static final TextureBinder NUMBER_1 = new TextureBinder("textures/text/number_type_1.png");

	static public ResourceLocation ESRes(String path) {
		return new ResourceLocation(ESAPI.MODID, path);
	}

	static public ResourceLocation MCRes(String path) {
		return new ResourceLocation("minecraft", path);
	}
}
