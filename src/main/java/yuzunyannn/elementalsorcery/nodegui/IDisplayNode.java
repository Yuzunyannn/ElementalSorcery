package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayObject;

@SideOnly(Side.CLIENT)
public interface IDisplayNode extends IDisplayObject {

	@SideOnly(Side.CLIENT)
	GNode getGNode();

	@Override
	@SideOnly(Side.CLIENT)
	default Vec3d getSize() {
		GNode node = getGNode();
		if (node == null) return Vec3d.ZERO;
		return node.getSize();
	}

	@Override
	@SideOnly(Side.CLIENT)
	default void update() {
		GNode node = getGNode();
		if (node == null || node.isInScene()) return;
		node.update();
	}

	@Override
	@SideOnly(Side.CLIENT)
	default void doRender(float partialTicks) {
		GNode node = getGNode();
		if (node == null) return;
		node.draw(partialTicks);
	}

}
