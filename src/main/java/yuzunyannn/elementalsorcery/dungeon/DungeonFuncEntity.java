package yuzunyannn.elementalsorcery.dungeon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncEntity extends GameFuncTimes {

	static public Vec3d getVec3d(JsonObject json, String key) {
		if (json.hasObject("offset")) {
			JsonObject offset = json.getObject("offset");
			return new Vec3d(offset.needNumber("x").doubleValue(), offset.needNumber("y").doubleValue(),
					offset.needNumber("z").doubleValue());
		} else if (json.hasArray("offset")) {
			JsonArray offset = json.getArray("offset");
			return new Vec3d(offset.needNumber(0).doubleValue(), offset.needNumber(1).doubleValue(),
					offset.needNumber(2).doubleValue());
		}
		return Vec3d.ZERO;
	}

	protected NBTTagCompound entityNBT;
	protected Vec3d offset = Vec3d.ZERO;

	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		if (json.hasObject("entityNBT")) entityNBT = json.getObject("entityNBT").asNBT();
		else entityNBT = new NBTTagCompound();
		entityNBT.setString("id", json.needString("entityId"));
		offset = getVec3d(json, "offset");
	};

	@Override
	protected void execute(GameFuncExecuteContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		Entity entity = AnvilChunkLoader.readWorldEntityPos(entityNBT, world, pos.getX() + 0.5, pos.getY(),
				pos.getZ() + 0.5, true);
		if (entity == null) return;
		entity.setLocationAndAngles(pos.getX() + 0.5 + offset.x, pos.getY() + offset.y, pos.getZ() + 0.5 + offset.z,
				(float) Math.random() * 3.14f * 2, entity.rotationPitch);
		if (entity instanceof EntityLiving) {
			((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
		}
		this.getFuncCarrier().giveTo(entity);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("entity", entityNBT);
		NBTHelper.setVec3d(nbt, "offset", offset);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.entityNBT = nbt.getCompoundTag("entity");
		this.offset = NBTHelper.getVec3d(nbt, "offset");
		super.deserializeNBT(nbt);
	}

	@Override
	public String toString() {
		return "<Entity> entity id:" + this.entityNBT.getString("id");
	}

}
