package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayMaster;
import yuzunyannn.elementalsorcery.api.util.render.IDisplayObject;

@SideOnly(Side.CLIENT)
public interface IDisplayNode extends IDisplayObject {

	GNode getGNode();

	@Override
	default Vec3d getSize() {
		GNode node = getGNode();
		if (node == null) return Vec3d.ZERO;
		return node.getSize();
	}

	@Override
	default void update(IDisplayMaster master) {
		GNode node = getGNode();
		if (node == null) return;
		node.update();
	}

	@Override
	default void doRender(float partialTicks) {
		GNode node = getGNode();
		if (node == null) return;
		node.draw(partialTicks);
	}

}
