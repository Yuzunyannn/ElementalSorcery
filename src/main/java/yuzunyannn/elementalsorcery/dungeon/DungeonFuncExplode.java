package yuzunyannn.elementalsorcery.dungeon;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncExplode extends GameFuncTimes {

	protected float level = 1;

	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		if (json.hasNumber("level")) level = json.getNumber("level").floatValue();
	};

	@Override
	protected void execute(GameFuncExecuteContext context) {
		Vec3d vec = context.getSrcObj().getObjectPosition();
		float level = this.level;
		if (level < 0) level = Math.max(0.1f, getCurrRandom().nextFloat() * -level);
		World world = context.getWorld();
		if (world.isRemote) return;
		final float lv = level;
		EventServer.addWorldTask(world, (w) -> {
			w.createExplosion(null, vec.x, vec.y, vec.z, lv, false);
		});
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setFloat("level", level);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		level = nbt.getFloat("level");
		super.deserializeNBT(nbt);
	}

	@Override
	public String toString() {
		return "<Explode> level:" + level;
	}

}
