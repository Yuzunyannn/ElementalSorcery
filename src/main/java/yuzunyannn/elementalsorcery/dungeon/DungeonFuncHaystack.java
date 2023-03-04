package yuzunyannn.elementalsorcery.dungeon;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonHaystack;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncHaystack extends GameFunc {

	protected byte haystackHighLevel = -1;
	protected boolean pressure = false;

	public void loadFromJson(JsonObject json) {
		super.loadFromJson(json);
		if (json.hasNumber("highLevel")) haystackHighLevel = json.getNumber("highLevel").byteValue();
		if (json.hasBoolean("pressure")) pressure = json.getBoolean("pressure");
	};

	@Override
	protected void execute(GameFuncExecuteContext context) {
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		Random rand = context.getRand();
		world.setBlockState(pos, ESObjects.BLOCKS.DUNGEON_HAYSTACK.getDefaultState());
		TileDungeonHaystack tile = BlockHelper.getTileEntity(world, pos, TileDungeonHaystack.class);
		if (tile == null) return;
		tile.setFuncCarrier(this.getFuncCarrier());
		int highLevel = haystackHighLevel;
		if (highLevel == -1) highLevel = (byte) rand.nextInt(16);
		tile.setHightLevel(highLevel);
		tile.setPressure(pressure);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		if (pressure) nbt.setBoolean("pressure", true);
		nbt.setByte("high", haystackHighLevel);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		pressure = nbt.getBoolean("pressure");
		haystackHighLevel = nbt.getByte("high");
		super.deserializeNBT(nbt);
	}

	@Override
	public String toString() {
		return "<Haystack> trigger:" + carrier;
	}

}
