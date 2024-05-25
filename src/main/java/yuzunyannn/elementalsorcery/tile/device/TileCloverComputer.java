package yuzunyannn.elementalsorcery.tile.device;

import net.minecraft.util.EnumFacing;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class TileCloverComputer extends TileComputer {

	protected EnumFacing facing = EnumFacing.NORTH;

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		writer.write("facing", facing);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		facing = reader.facing("facing");
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		super.writeUpdateData(writer);
		writer.write("facing", facing);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		super.readUpdateData(reader);
		facing = reader.facing("facing");
	}

	@Override
	public String getAppearance() {
		return "cloverComputer";
	}

	public EnumFacing getFacing() {
		return facing;
	}

	public void setFacing(EnumFacing facing) {
		this.facing = facing;
	}
}
