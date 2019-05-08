package yuzunyan.elementalsorcery.render.entity;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyan.elementalsorcery.entity.EntityCrafting;

@SideOnly(Side.CLIENT)
public class RenderEntityCrafting extends Render<EntityCrafting> {

	public static ICraftingLaunchAnime getDefultAnime() {
		return new AnimeRenderCrafting();
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
		anime.deRender(entity, x, y, z, entityYaw, partialTicks);
	}
}
