package yuzunyannn.elementalsorcery.ts;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.ts.PocketWatch.WorldData;
import yuzunyannn.elementalsorcery.util.render.Shaders;

@SideOnly(Side.CLIENT)
public class PocketWatchClient {

	static public Minecraft mc = Minecraft.getMinecraft();
	static public ParticleManager originParticleManager;
	static public TextureManager originTextureManager;
	static public Profiler originProfiler;
	static public RenderPlayer originRenderPlayer;

	static private PocketWatch.WorldData data;

	static public boolean isActive() {
		return data != null;
	}

	static public void stopWorld(int tick, @Nullable EntityLivingBase caster) {
		if (PocketWatch.disable) return;
		if (isActive()) {
			data.remain = tick;
			if (data.remain > data.totalRemain) data.totalRemain = data.remain;
			return;
		}
		data = new WorldData(mc.world, tick).setCaster(caster);
		doStopAll(mc.world);
		PocketWatch.doStopAll(mc.world, data);
	}

	static public void tick() {
		if (data == null) return;
		World world = data.getWorld();
		if (world == null || world != mc.world) {
			doResumeAll(world);
			data = null;
			return;
		}
		if (--data.remain <= 0) {
			doResumeAll(world);
			PocketWatch.doResumeAll(world, data);
			data = null;
		} else {
			PocketWatch.tickStopAll(world, data);
		}
	}

	static public void bindGray() {
		if (ESAPI.isDevelop) {
			if (!isActive()) {
				ESAPI.logger.warn("颜色测试异常");
			}
		}
		if (!Shaders.GRAY.isActive()) Shaders.GRAY.bind();
	}

	static public void unbindGray() {
		if (Shaders.GRAY.isActive()) Shaders.GRAY.unbind();
	}

	static public void doStopAll(World world) {
		doResumeAll(world);

		originParticleManager = mc.effectRenderer;
		mc.effectRenderer = new ParticleManagerPacker(originParticleManager);

		originTextureManager = mc.getTextureManager();
		ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, new TextureManagerPacker(originTextureManager),
				"field_71446_o");

		RenderManager rm = mc.getRenderManager();
		// 实体渲染器包装
		replaceData(rm.entityRenderMap, render -> {
			return new RenderPacker<>(render);
		});
		// tile实体渲染器包装
		replaceData(TileEntityRendererDispatcher.instance.renderers, render -> {
			return new TileEntitySpecialRendererPacker<>(render);
		});
		// Profiler包装
		originProfiler = mc.profiler;
		ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, new ProfilerPacker(originProfiler),
				"field_71424_I");
		// 默认玩家渲染
		originRenderPlayer = ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, rm, "field_178637_m");
		ObfuscationReflectionHelper.setPrivateValue(RenderManager.class, rm, new RenderPlayerPacker(originRenderPlayer),
				"field_178637_m");
		// 皮肤包装
		Map<String, RenderPlayer> skinMap = ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, rm,
				"field_178636_l");
		replaceData(skinMap, render -> {
			return new RenderPlayerPacker(render);
		});
		// 音效
		mc.getSoundHandler().stopSounds();
		// 世界相关
		if (world != null) {
			world.provider.setWeatherRenderer(new RenderSilentSnowRain(mc.entityRenderer));
			world.provider.setSkyRenderer(new RenderSky(world.provider.getSkyRenderer()));
		}

	}

	static public <T, K> void replaceData(Map<K, T> map, Function<T, T> check) {
		Map<K, T> replaceMap = new HashMap<>();
		for (Entry<K, T> entry : map.entrySet()) {
			T val = entry.getValue();
			if (val == null) continue;
			T replace = check.apply(val);
			if (replace != null) replaceMap.put(entry.getKey(), replace);
		}
		for (Entry<K, T> entry : replaceMap.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}

	}

	static public void doResumeAll(World world) {
		if (originParticleManager != null) {
			mc.effectRenderer = originParticleManager;
			originParticleManager = null;
		}

		if (originTextureManager != null) {
			ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, originTextureManager, "field_71446_o");
			originTextureManager = null;
		}

		{
			replaceData(mc.getRenderManager().entityRenderMap, render -> {
				if (render instanceof RenderPacker) return ((RenderPacker) render).parent;
				return null;
			});
		}

		{
			replaceData(TileEntityRendererDispatcher.instance.renderers, render -> {
				if (render instanceof TileEntitySpecialRendererPacker)
					return ((TileEntitySpecialRendererPacker) render).parent;
				return null;
			});
		}

		if (originProfiler != null) {
			ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, originProfiler, "field_71424_I");
			originProfiler = null;
		}

		if (originRenderPlayer != null) {
			ObfuscationReflectionHelper.setPrivateValue(RenderManager.class, mc.getRenderManager(), originRenderPlayer,
					"field_178637_m");
			originRenderPlayer = null;
		}

		{
			Map<String, RenderPlayer> skinMap = ObfuscationReflectionHelper.getPrivateValue(RenderManager.class,
					mc.getRenderManager(), "field_178636_l");
			replaceData(skinMap, render -> {
				if (render instanceof RenderPlayerPacker) return ((RenderPlayerPacker) render).parent;
				return null;
			});
		}

		if (world != null) {
			world.provider.setWeatherRenderer(null);
			if (world.provider.getSkyRenderer() instanceof RenderSky) {
				world.provider.setSkyRenderer(((RenderSky) world.provider.getSkyRenderer()).parent);
			} else world.provider.setSkyRenderer(null);
		}

		PocketWatchClient.unbindGray();
	}

}
