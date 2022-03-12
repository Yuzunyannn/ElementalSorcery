package yuzunyannn.elementalsorcery.render.effect;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
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
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementAbsorb;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectElementCrackAttack;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectSummonEntity;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectTreatEntity;

public class Effects {

	public static final int FIREWROK = 1;
	public static final int PARTICLE_EFFECT = 2;
	public static final int ENTITY_SOUL = 3;
	public static final int SUMMON_ENTITY = 4;
	public static final int TREAT_ENTITY = 5;
	public static final int ELEMENT_ABSORB = 6;
	public static final int ELEMENT_CRACK_ATTACK = 7;

	public static final int MAX_DIS = 64;

	/** 生成特殊效果 */
	public static void spawnEffect(World world, int id, Vec3d pos, NBTTagCompound nbt) {
		if (world.isRemote) {
			spawnEffect(id, pos, nbt);
			return;
		}
		for (EntityPlayer player : world.playerEntities) {
			if (player.getDistanceSq(pos.x, pos.y, pos.z) > MAX_DIS * MAX_DIS) continue;
			ESNetwork.instance.sendTo(new MessageEffect(id, pos, nbt), (EntityPlayerMP) player);
		}
	}

	public static void spawnEffect(World world, int id, BlockPos pos, NBTTagCompound nbt) {
		spawnEffect(world, id, new Vec3d(pos).add(0.5, 0.5, 0.5), nbt);
	}

	@SideOnly(Side.CLIENT)
	private static void spawnEffect(int id, Vec3d pos, NBTTagCompound nbt) {
		try {
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player.getDistanceSq(pos.x, pos.y, pos.z) > MAX_DIS * MAX_DIS) return;
			Effects.Factory factory = Effects.getFactory(id);
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
		public static final Map<Integer, Factory> map = new HashMap<>();

		public static void register(int id, Factory factory) {
			if (factory == null) return;
			map.put(id, factory);
		}
	}

	@SideOnly(Side.CLIENT)
	public static Factory getFactory(int id) {
		return EffectMap.map.get(id);
	}

	@SideOnly(Side.CLIENT)
	public static void registerAll() {
		EffectMap.map.clear();
		EffectMap.register(FIREWROK, FireworkEffect::show);
		EffectMap.register(PARTICLE_EFFECT, ParticleEffects::showShow);
		EffectMap.register(ENTITY_SOUL, EffectEntitySoul::show);
		EffectMap.register(SUMMON_ENTITY, EffectSummonEntity::show);
		EffectMap.register(ELEMENT_ABSORB, EffectElementAbsorb::show);
		EffectMap.register(TREAT_ENTITY, EffectTreatEntity::show);
		EffectMap.register(ELEMENT_CRACK_ATTACK, EffectElementCrackAttack::show);
	}

	// 下面是通用的show

	public static void spawnTypeEffect(World world, Vec3d pos, int type, int level) {
		NBTTagCompound effect = new NBTTagCompound();
		effect.setByte("lev", (byte) level);
		effect.setByte("type", (byte) 0);
		Effects.spawnEffect(world, Effects.PARTICLE_EFFECT, pos, effect);
	}

	public static void spawnSummonEntity(Entity entity, int[] colors) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("target", entity.getEntityId());
		if (colors != null && colors.length > 0) nbt.setIntArray("colors", colors);
		Effects.spawnEffect(entity.world, Effects.SUMMON_ENTITY, entity.getPositionVector(), nbt);
	}

	public static void spawnTreatEntity(Entity entity, int[] colors) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("target", entity.getEntityId());
		if (colors != null && colors.length > 0) nbt.setIntArray("colors", colors);
		Effects.spawnEffect(entity.world, Effects.TREAT_ENTITY, entity.getPositionVector(), nbt);
	}

	public static void spawnElementAbsorb(Vec3d from, Entity target, int count, int[] colors) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("targetEntity", target.getEntityId());
		nbt.setShort("times", (short) count);
		nbt.setIntArray("colors", colors);
		Effects.spawnEffect(target.world, Effects.ELEMENT_ABSORB, from, nbt);
	}

}
