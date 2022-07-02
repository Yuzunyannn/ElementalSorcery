package yuzunyannn.elementalsorcery.util.helper;

import java.io.IOException;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityHelper {

	public static final DataSerializer<Integer> DS_INT = new DataSerializer<Integer>() {

		public void write(PacketBuffer buf, Integer value) {
			buf.writeInt(value);
		}

		public Integer read(PacketBuffer buf) throws IOException {
			return buf.readInt();
		}

		public DataParameter<Integer> createKey(int id) {
			return new DataParameter<Integer>(id, this);
		}

		public Integer copyValue(Integer value) {
			return value;
		}
	};

	static public double getChanceFromUUID(UUID uuid) {
		return (Math.abs(uuid.getLeastSignificantBits() * (uuid.getMostSignificantBits() + 37)) % 100000) / 100000.0;
	}

	static public double getChanceFrom2UUID(UUID uuid1, UUID uuid2) {
		return (Math.abs(uuid1.getLeastSignificantBits() * (uuid2.getMostSignificantBits() + 25601)) % 100000)
				/ 100000.0;
	}

	static public boolean isCreative(Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			return player.isCreative() || player.isSpectator();
		}
		return false;
	}

	static public boolean isSameTeam(Entity entity1, Entity entity2) {
		if (entity1 == entity2) return true;
		return entity1.isOnSameTeam(entity2);
	}

	static public void setPotionEffectDuration(PotionEffect effect, int duration) {
		try {
			ObfuscationReflectionHelper.setPrivateValue(PotionEffect.class, effect, duration, "field_76460_b");
		} catch (Exception e) {}
	}

	public static void setPositionAndUpdate(Entity entity, Vec3d vec) {
		entity.setPositionAndUpdate(vec.x, vec.y, vec.z);
	}

	public static boolean isEnder(EntityLivingBase living) {
		EntityEntry entry = EntityRegistry.getEntry(living.getClass());
		if (entry == null) return false;
		return entry.getRegistryName().getPath().toLowerCase().indexOf("ender") != -1;
	}

}
