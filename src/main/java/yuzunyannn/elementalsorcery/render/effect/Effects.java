package yuzunyannn.elementalsorcery.render.effect;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageEffect;

public class Effects {

	public static final String FIREWROK = "firewrok";
	public static final String PARTICLE_EFFECT = "pEffect";
	public static final String ENTITY_SOUL = "eSoul";

	public static final int MAX_DIS = 64;

	/** 生成特殊效果 */
	public static void spawnEffect(World world, String name, Vec3d pos, NBTTagCompound nbt) {
		if (world.isRemote) {
			spawnEffect(name, pos, nbt);
			return;
		}
		for (EntityPlayer player : world.playerEntities) {
			if (player.getDistanceSq(pos.x, pos.y, pos.z) > MAX_DIS * MAX_DIS) continue;
			ESNetwork.instance.sendTo(new MessageEffect(name, pos, nbt), (EntityPlayerMP) player);
		}
	}

	public static void spawnEffect(World world, String name, BlockPos pos, NBTTagCompound nbt) {
		spawnEffect(world, name, new Vec3d(pos).addVector(0.5, 0.5, 0.5), nbt);
	}

	@SideOnly(Side.CLIENT)
	private static void spawnEffect(String name, Vec3d pos, NBTTagCompound nbt) {
		try {
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player.getDistanceSq(pos.x, pos.y, pos.z) > MAX_DIS * MAX_DIS) return;
			Effects.Factory factory = Effects.getFactory(name);
			factory.show(player.world, pos, nbt);
		} catch (Exception e) {}
		return;
	}

	@SideOnly(Side.CLIENT)
	public static interface Factory {

		void show(World world, Vec3d pos, NBTTagCompound nbt);

	}

	@SideOnly(Side.CLIENT)
	public static class EffectMap {
		public static final Map<String, Factory> map = new HashMap<>();

		public static void register(String name, Factory factory) {
			if (name == null || factory == null) return;
			map.put(name, factory);
		}
	}

	@SideOnly(Side.CLIENT)
	public static Factory getFactory(String key) {
		return EffectMap.map.get(key);
	}

	@SideOnly(Side.CLIENT)
	public static void registerAll() {
		EffectMap.map.clear();
		EffectMap.register(FIREWROK, FireworkEffect::show);
		EffectMap.register(PARTICLE_EFFECT, ParticleEffects::showShow);
		EffectMap.register(ENTITY_SOUL, EffectEntitySoul::show);
	}

}
