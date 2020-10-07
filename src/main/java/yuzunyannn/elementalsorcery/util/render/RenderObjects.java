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

	static final public ResourceLocation CRAFTING_TABLE = MCRes("textures/gui/container/crafting_table.png");
	static final public ResourceLocation STONE = MCRes("textures/blocks/stone.png");

	static final public ResourceLocation KYANITE_BLOCK = ESRes("textures/blocks/kyanite_block.png");
	static final public ResourceLocation KYANITE_ORE = ESRes("textures/blocks/kyanite_ore.png");
	static final public ResourceLocation ASTONE = ESRes("textures/blocks/astone.png");
	static final public ResourceLocation ASTONE_FRAGMENTED = ESRes("textures/blocks/astone_fragmented.png");

	static final public ResourceLocation MANTRA_VOID = ESRes("textures/mantras/void.png");
	static final public ResourceLocation MANTRA_TELEPORT = ESRes("textures/mantras/teleport.png");
	static final public ResourceLocation MANTRA_FLOAT = ESRes("textures/mantras/float.png");
	static final public ResourceLocation MANTRA_SPRINT = ESRes("textures/mantras/sprint.png");
	static final public ResourceLocation MANTRA_FIRE_BALL = ESRes("textures/mantras/fire_ball.png");

	static public ResourceLocation ESRes(String path) {
		return new ResourceLocation(ElementalSorcery.MODID, path);
	}

	static public ResourceLocation MCRes(String path) {
		return new ResourceLocation("minecraft", path);
	}
}
