package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.EffectCondition;

@SideOnly(Side.CLIENT)
public abstract class EffectScreen extends EffectCondition {

	public final Minecraft mc = Minecraft.getMinecraft();

	public EffectScreen(World world) {
		super(world);
		this.updateSize();
	}

	public float width;
	public float height;

	@Override
	protected String myGroup() {
		return GROUP_GUI;
	}

	public void updateSize() {
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		width = scaledresolution.getScaledWidth();
		height = scaledresolution.getScaledHeight();
	}

}
