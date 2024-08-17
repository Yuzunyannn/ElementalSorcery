package yuzunyannn.elementalsorcery.tile.dungeon;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
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

	public GameFunc createTextDungeonFunc() {
		return GameFunc.create(this.config);
	}

	@Nullable
	public String getAssetsPath() {
		if (config.hasString("/assets")) return config.getString("/assets");
		return null;
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
