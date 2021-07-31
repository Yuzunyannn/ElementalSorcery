package yuzunyannn.elementalsorcery.ts;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSilentSnowRain extends IRenderHandler {

	public static final ResourceLocation RAIN_TEXTURES = new ResourceLocation("textures/environment/rain.png");
	public static final ResourceLocation SNOW_TEXTURES = new ResourceLocation("textures/environment/snow.png");

	public int rendererUpdateCount;
	public float rainStrength = -1;
	public final float[] rainXCoords;
	public final float[] rainYCoords;
	public Random random = new Random();

	public RenderSilentSnowRain(EntityRenderer render) {
		rendererUpdateCount = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class,
				PocketWatchClient.mc.entityRenderer, "field_78529_t");
		rainXCoords = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class,
				PocketWatchClient.mc.entityRenderer, "field_175076_N");
		rainYCoords = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class,
				PocketWatchClient.mc.entityRenderer, "field_175077_O");
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		if (rainStrength < 0) rainStrength = world.getRainStrength(partialTicks);
		EntityRenderer self = mc.entityRenderer;
		if (rainStrength <= 0.0F) return;
		
		self.enableLightmap();
		Entity entity = mc.getRenderViewEntity();
		int vPosX = MathHelper.floor(entity.posX);
		int vPosY = MathHelper.floor(entity.posY);
		int vPosZ = MathHelper.floor(entity.posZ);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.disableCull();
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.alphaFunc(516, 0.1F);
		double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
		double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
		double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
		int l = MathHelper.floor(posY);
		int i1 = 5;
		if (mc.gameSettings.fancyGraphics) i1 = 10;
		int j1 = -1;
		partialTicks = 0;
		float f1 = (float) this.rendererUpdateCount + partialTicks;
		bufferbuilder.setTranslation(-posX, -posY, -posZ);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		for (int k1 = vPosZ - i1; k1 <= vPosZ + i1; ++k1) {
			for (int l1 = vPosX - i1; l1 <= vPosX + i1; ++l1) {
				int i2 = (k1 - vPosZ + 16) * 32 + l1 - vPosX + 16;
				double d3 = (double) this.rainXCoords[i2] * 0.5D;
				double d4 = (double) this.rainYCoords[i2] * 0.5D;
				blockpos$mutableblockpos.setPos(l1, 0, k1);
				Biome biome = world.getBiome(blockpos$mutableblockpos);

				if (biome.canRain() || biome.getEnableSnow()) {
					int precipitationY = world.getPrecipitationHeight(blockpos$mutableblockpos).getY();
					int k2 = vPosY - i1;
					int l2 = vPosY + i1;
					if (k2 < precipitationY) k2 = precipitationY;
					if (l2 < precipitationY) l2 = precipitationY;
					int i3 = precipitationY;
					if (precipitationY < l) i3 = l;
					if (k2 != l2) {
						this.random.setSeed((long) (l1 * l1 * 3121 + l1 * 45238971 ^ k1 * k1 * 418711 + k1 * 13761));
						blockpos$mutableblockpos.setPos(l1, k2, k1);
						float f2 = biome.getTemperature(blockpos$mutableblockpos);

						if (world.getBiomeProvider().getTemperatureAtHeight(f2, precipitationY) >= 0.15F) {
							if (j1 != 0) {
								if (j1 >= 0) tessellator.draw();
								j1 = 0;
								mc.getTextureManager().bindTexture(RAIN_TEXTURES);
								bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
							}

							double d5 = -((double) (this.rendererUpdateCount + l1 * l1 * 3121 + l1 * 45238971
									+ k1 * k1 * 418711 + k1 * 13761 & 31) + (double) partialTicks) / 32.0D
									* (3.0D + this.random.nextDouble());
							double d6 = (double) ((float) l1 + 0.5F) - entity.posX;
							double d7 = (double) ((float) k1 + 0.5F) - entity.posZ;
							float f3 = MathHelper.sqrt(d6 * d6 + d7 * d7) / (float) i1;
							float f4 = ((1.0F - f3 * f3) * 0.5F + 0.5F) * rainStrength;
							blockpos$mutableblockpos.setPos(l1, i3, k1);
							int j3 = world.getCombinedLight(blockpos$mutableblockpos, 0);
							int k3 = j3 >> 16 & 65535;
							int l3 = j3 & 65535;
							bufferbuilder.pos((double) l1 - d3 + 0.5D, (double) l2, (double) k1 - d4 + 0.5D)
									.tex(0.0D, (double) k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3)
									.endVertex();
							bufferbuilder.pos((double) l1 + d3 + 0.5D, (double) l2, (double) k1 + d4 + 0.5D)
									.tex(1.0D, (double) k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3)
									.endVertex();
							bufferbuilder.pos((double) l1 + d3 + 0.5D, (double) k2, (double) k1 + d4 + 0.5D)
									.tex(1.0D, (double) l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3)
									.endVertex();
							bufferbuilder.pos((double) l1 - d3 + 0.5D, (double) k2, (double) k1 - d4 + 0.5D)
									.tex(0.0D, (double) l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3)
									.endVertex();
						} else {
							if (j1 != 1) {
								if (j1 >= 0) tessellator.draw();
								j1 = 1;
								mc.getTextureManager().bindTexture(SNOW_TEXTURES);
								bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
							}

							double d8 = (double) (-((float) (this.rendererUpdateCount & 511) + partialTicks) / 512.0F);
							double d9 = this.random.nextDouble()
									+ (double) f1 * 0.01D * (double) ((float) this.random.nextGaussian());
							double d10 = this.random.nextDouble()
									+ (double) (f1 * (float) this.random.nextGaussian()) * 0.001D;
							double d11 = (double) ((float) l1 + 0.5F) - entity.posX;
							double d12 = (double) ((float) k1 + 0.5F) - entity.posZ;
							float f6 = MathHelper.sqrt(d11 * d11 + d12 * d12) / (float) i1;
							float f5 = ((1.0F - f6 * f6) * 0.3F + 0.5F) * rainStrength;
							blockpos$mutableblockpos.setPos(l1, i3, k1);
							int i4 = (world.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 15728880) / 4;
							int j4 = i4 >> 16 & 65535;
							int k4 = i4 & 65535;
							bufferbuilder.pos((double) l1 - d3 + 0.5D, (double) l2, (double) k1 - d4 + 0.5D)
									.tex(0.0D + d9, (double) k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5)
									.lightmap(j4, k4).endVertex();
							bufferbuilder.pos((double) l1 + d3 + 0.5D, (double) l2, (double) k1 + d4 + 0.5D)
									.tex(1.0D + d9, (double) k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5)
									.lightmap(j4, k4).endVertex();
							bufferbuilder.pos((double) l1 + d3 + 0.5D, (double) k2, (double) k1 + d4 + 0.5D)
									.tex(1.0D + d9, (double) l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5)
									.lightmap(j4, k4).endVertex();
							bufferbuilder.pos((double) l1 - d3 + 0.5D, (double) k2, (double) k1 - d4 + 0.5D)
									.tex(0.0D + d9, (double) l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5)
									.lightmap(j4, k4).endVertex();
						}
					}
				}
			}
		}

		if (j1 >= 0) tessellator.draw();
		bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(516, 0.1F);
		self.disableLightmap();
	}

}
