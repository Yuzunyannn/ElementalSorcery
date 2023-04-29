package yuzunyannn.elementalsorcery.dungeon;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonMagicCircleA;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncMagicCircle extends GameFuncTimes {

	protected DungeonIntegerLoader activeCD = DungeonIntegerLoader.of(600);
	protected int color = 0x803298;

	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		activeCD = DungeonIntegerLoader.get(json, "activeCD", 600);
		if (json.hasNumber("color")) color = json.getNumber("color").intValue();
	};

	@Override
	protected void execute(GameFuncExecuteContext context) {
		Random rand = getCurrRandom();
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();

		world.setBlockState(pos, ESObjects.BLOCKS.DUNGEON_MAGIC_CIRCLE_A.getDefaultState());
		TileDungeonMagicCircleA tile = BlockHelper.getTileEntity(world, pos, TileDungeonMagicCircleA.class);
		if (tile == null) return;
		this.getFuncCarrier().giveTo(tile);
		tile.setColor(new Color(color));
		tile.setActiveCD(activeCD.getInteger(rand.nextInt()));
		tile.onBuild();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("activeCD", activeCD.serializeNBT());
		nbt.setInteger("color", color);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		activeCD = DungeonIntegerLoader.get(nbt.getTag("activeCD"), 600);
		color = nbt.getInteger("color");
	}

	@Override
	public String toString() {
		return "<MagicCircle> trigger:" + carrier;
	}

}
