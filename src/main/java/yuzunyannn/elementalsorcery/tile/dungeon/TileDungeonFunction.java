package yuzunyannn.elementalsorcery.tile.dungeon;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.dungeon.DungeonFunc;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class TileDungeonFunction extends TileEntity {

	protected JsonObject config = new JsonObject();

	public void setConfig(String config) {
		setConfig(new JsonObject(config));
	}

	public JsonObject getConfig() {
		return config;
	}

	public void setConfig(JsonObject config) {
		this.config = config;
	}

	public DungeonFunc createTextDungeonFunc() {
		return DungeonFunc.create(this.config);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("dungeon_config", config.toString());
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		config = new JsonObject(compound.getString("dungeon_config"));
		super.readFromNBT(compound);
	}

}
