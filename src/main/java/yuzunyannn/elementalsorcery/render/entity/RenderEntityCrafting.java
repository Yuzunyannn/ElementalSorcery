package yuzunyannn.elementalsorcery.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.crafting.CraftingLaunchAnimeNone;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.entity.EntityCrafting;

@SideOnly(Side.CLIENT)
public class RenderEntityCrafting extends Render<EntityCrafting> {

	public static ICraftingLaunchAnime getDefultAnime() {
		return new CraftingLaunchAnimeNone();
	}

	public RenderEntityCrafting(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCrafting entity) {
		return null;
	}

	@Override
	public void doRender(EntityCrafting entity, double x, double y, double z, float entityYaw, float partialTicks) {
		ICraftingLaunchAnime anime = entity.getCraftingLaunchAnime();
		if (anime == null)
			return;
		anime.doRender(entity.getCommit(), x, y, z, entityYaw, partialTicks);
	}
}
