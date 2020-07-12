package yuzunyannn.elementalsorcery.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import yuzunyannn.elementalsorcery.render.ESTileEntityItemStackRenderer;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.ItemRendererModel;

public class TileItemRenderRegistries implements ICustomModelLoader {

	public static TileItemRenderRegistries instance;

	private Map<ResourceLocation, IRenderItem> items = new HashMap<>();

	private static ResourceLocation getItemKey(ResourceLocation rsname) {
		return new ResourceLocation(rsname.getResourceDomain(), "models/item/" + rsname.getResourcePath());
	}

	public void register(Item item, IRenderItem render) {
		ResourceLocation rsname = item.getRegistryName();
		if (items.containsKey(rsname))
			return;
		if (render == null)
			return;
		ResourceLocation key = getItemKey(rsname);
		items.put(key, render);
		item.setTileEntityItemStackRenderer(new ESTileEntityItemStackRenderer(render));
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return new ModelResourceLocation(rsname, "inventory");
			}
		});
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return items.containsKey(modelLocation);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// 资源刷新时刷新内部数据
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		// 加载模型
		return new ItemRendererModel();
	}

	@Override
	public String toString() {
		return "ESCustomModelLoader";
	}

}
