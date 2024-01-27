package yuzunyannn.elementalsorcery.api.computer.soft;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISoftGui {

	void init(ISoftGuiRuntime runtime);

	void update();

	void render(float partialTicks);

	void onMouseEvent(Vec3d vec3d);

}
