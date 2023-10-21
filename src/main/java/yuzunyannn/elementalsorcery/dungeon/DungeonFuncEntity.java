package yuzunyannn.elementalsorcery.dungeon;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncEntity extends GameFuncTimes {

	public final static Item[] HELMETS = new Item[] { Items.DIAMOND_HELMET, Items.GOLDEN_HELMET, Items.IRON_HELMET,
			Items.CHAINMAIL_HELMET, Items.LEATHER_HELMET };

	static public Vec3d getVec3d(JsonObject json, String key) {
		if (json.hasObject(key)) {
			JsonObject offset = json.getObject(key);
			return new Vec3d(offset.needNumber("x").doubleValue(), offset.needNumber("y").doubleValue(),
					offset.needNumber("z").doubleValue());
		} else if (json.hasArray(key)) {
			JsonArray offset = json.getArray(key);
			return new Vec3d(offset.needNumber(0).doubleValue(), offset.needNumber(1).doubleValue(),
					offset.needNumber(2).doubleValue());
		}
		return Vec3d.ZERO;
	}

	protected NBTTagCompound entityNBT;
	protected Vec3d offset = Vec3d.ZERO;
	protected DungeonIntegerLoader summonCount = DungeonIntegerLoader.of(1);

	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		if (json.hasObject("entityNBT")) {
			entityNBT = json.getObject("entityNBT").asNBT();
			EntityHelper.parserConfigEntityNBT(entityNBT);
		} else entityNBT = new NBTTagCompound();
		entityNBT.setString("id", json.needString("entityId"));
		offset = getVec3d(json, "offset");
		summonCount = DungeonIntegerLoader.get(json, "count", 1);
	};

	protected Entity doOnce(World world, Vec3d vec) {
		Entity entity = AnvilChunkLoader.readWorldEntityPos(entityNBT, world, vec.x, vec.y, vec.z, true);
		if (entity == null) return null;

		// 戴帽子
		if (entity instanceof IMob && entity instanceof EntityLiving) {
			Random rand = getCurrRandom();
			EntityLiving living = (EntityLiving) entity;
			boolean loveHelmet = world.canSeeSky(new BlockPos(entity));
			loveHelmet = loveHelmet || rand.nextDouble() < 0.25;
			if (living.isEntityUndead() && loveHelmet) {
				ItemStack helmet = living.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
				if (helmet.isEmpty()) {
					helmet = new ItemStack(HELMETS[rand.nextInt(HELMETS.length)]);
					int maxDmg = helmet.getMaxDamage();
					helmet.setItemDamage(rand.nextInt(maxDmg / 2) + maxDmg / 3);
					living.setItemStackToSlot(EntityEquipmentSlot.HEAD, helmet);
					living.setDropChance(EntityEquipmentSlot.HEAD, 0);
				}
			}
		}

		entity.setLocationAndAngles(vec.x + offset.x, vec.y + offset.y, vec.z + offset.z,
				(float) Math.random() * 3.14f * 2, entity.rotationPitch);
		if (entity instanceof EntityLiving) {
			((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
		}
		this.getFuncCarrier().giveTo(entity);

		return entity;
	}

	@Override
	protected void execute(GameFuncExecuteContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		int count = summonCount.getInteger(currRandom.nextInt());
		count = MathHelper.clamp(count, 0, 64);
		for (int i = 0; i < count; i++) {
			Entity entity = doOnce(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5));
			if (entity == null) break;
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("entity", entityNBT);
		nbt.setTag("count", summonCount.serializeNBT());
		NBTHelper.setVec3d(nbt, "offset", offset);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.entityNBT = nbt.getCompoundTag("entity");
		this.offset = NBTHelper.getVec3d(nbt, "offset");
		this.summonCount = DungeonIntegerLoader.get(nbt.getTag("count"), 1);
		super.deserializeNBT(nbt);
	}

	@Override
	public String toString() {
		return "<Entity> entity id:" + this.entityNBT.getString("id");
	}

}
