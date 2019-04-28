package yuzunyan.elementalsorcery.init.registries;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import yuzunyan.elementalsorcery.render.IRenderItem;

public class TileItemRenderRegistries {

	private static Map<ResourceLocation, IRenderItem> items = new HashMap<>();

	public static boolean accepts(ResourceLocation modelLocation) {
		return items.containsKey(modelLocation);
	}

	private static ResourceLocation getItemKey(ResourceLocation rsname) {
		return new ResourceLocation(rsname.getResourceDomain(), "models/item/" + rsname.getResourcePath());
	}

	public static void register(Item item, IRenderItem render) {
		ResourceLocation rsname = item.getRegistryName();
		if (items.containsKey(rsname))
			return;
		if (render == null)
			return;
		ResourceLocation key = getItemKey(rsname);
		items.put(key, render);
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return new ModelResourceLocation(rsname, "inventory");
			}
		});
	}

	public static boolean renderItIfPossible(ItemStack stack, float partialTicks) {
		if (stack.isEmpty())
			return false;
		ResourceLocation key = getItemKey(stack.getItem().getRegistryName());
		if (!accepts(key))
			return false;
		items.get(key).render(stack, partialTicks);
		return true;
	}

	public static boolean canRenderIt(ItemStack stack, float partialTicks) {
		if (stack.isEmpty())
			return false;
		ResourceLocation key = getItemKey(stack.getItem().getRegistryName());
		if (!accepts(key))
			return false;
		return true;
	}
}
