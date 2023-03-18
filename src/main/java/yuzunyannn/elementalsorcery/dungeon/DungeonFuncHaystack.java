package yuzunyannn.elementalsorcery.dungeon;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonHaystack;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncHaystack extends GameFuncTimes {

	protected byte haystackHighLevel = -1;
	protected byte pressure = -1;

	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		if (json.hasNumber("highLevel")) haystackHighLevel = json.getNumber("highLevel").byteValue();
		if (json.hasBoolean("pressure")) pressure = (byte) (json.getBoolean("pressure") ? 1 : 0);
		else if (json.hasString("pressure")) {
			if ("random".equals(json.getString("pressure"))) pressure = -1;
		}
	};

	@Override
	protected void execute(GameFuncExecuteContext context) {
		Random rand = getCurrRandom();
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		world.setBlockState(pos, ESObjects.BLOCKS.DUNGEON_HAYSTACK.getDefaultState());
		TileDungeonHaystack tile = BlockHelper.getTileEntity(world, pos, TileDungeonHaystack.class);
		if (tile == null) return;
		this.getFuncCarrier().giveTo(tile);
		int highLevel = haystackHighLevel;
		if (highLevel == -1) highLevel = (byte) rand.nextInt(16) + 1;
		tile.setHightLevel(highLevel);
		if (pressure == 1) tile.setPressure(true);
		else if (pressure == -1) tile.setPressure(rand.nextBoolean());
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		if (pressure != 0) nbt.setByte("pressure", pressure);
		nbt.setByte("high", haystackHighLevel);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		pressure = nbt.getByte("pressure");
		haystackHighLevel = nbt.getByte("high");
		super.deserializeNBT(nbt);
	}

	@Override
	public String toString() {
		return "<Haystack> trigger:" + carrier;
	}

}
