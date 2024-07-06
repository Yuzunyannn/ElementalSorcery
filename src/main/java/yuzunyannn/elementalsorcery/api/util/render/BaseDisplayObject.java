package yuzunyannn.elementalsorcery.api.util.render;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BaseDisplayObject implements IDisplayObject {

	private final String id;
	protected Vec3d size = Vec3d.ZERO;

	public BaseDisplayObject(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void update(IDisplayMaster master) {
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3d getSize() {
		return size;
	}
}
