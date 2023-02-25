package yuzunyannn.elementalsorcery.dungeon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import yuzunyannn.elementalsorcery.dungeon.DungeonFuncExecuteContext.DungeonFuncExecuteType;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncEntity extends DungeonFunc {

	protected NBTTagCompound entityNBT;

	public void loadFromJson(JsonObject json) {
		super.loadFromJson(json);
		if (json.hasObject("entityNBT")) entityNBT = json.getObject("entityNBT").asNBT();
		else entityNBT = new NBTTagCompound();
		entityNBT.setString("id", json.needString("entityId"));
	};

	@Override
	protected void execute(DungeonFuncExecuteContext context) {
		if (context.executeType != DungeonFuncExecuteType.BUILD) return;
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		Entity entity = AnvilChunkLoader.readWorldEntityPos(entityNBT, world, pos.getX() + 0.5, pos.getY(),
				pos.getZ() + 0.5, true);
		if (entity == null) return;
		entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, (float) Math.random() * 3.14f * 2,
				entity.rotationPitch);
		if (entity instanceof EntityLiving) {
			((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("entity", entityNBT);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.entityNBT = nbt.getCompoundTag("entity");
		super.deserializeNBT(nbt);
	}

	@Override
	public String toString() {
		return "[DungeonFunc] Entity\nentity id:" + this.entityNBT.getString("id");
	}

}
