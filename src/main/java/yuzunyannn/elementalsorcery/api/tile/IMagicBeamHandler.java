package yuzunyannn.elementalsorcery.api.tile;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import yuzunyannn.elementalsorcery.api.util.WorldObjectEntity;
import yuzunyannn.elementalsorcery.api.util.WorldObjectTileEntity;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;

public interface IMagicBeamHandler extends IAliveStatusable {
	/**
	 * @retrun 剩余没有成功插入的片元
	 */
	public double insertMagicFragment(double count, boolean simulate);

	/**
	 * @retrun 真正能取出来的量
	 */
	public double extractMagicFragment(double count, boolean simulate);

	default public IWorldObject getWorldObject() {
		if (this instanceof TileEntity) return new WorldObjectTileEntity((TileEntity) this);
		if (this instanceof Entity) return new WorldObjectEntity((Entity) this);
		return null;
	}
}
