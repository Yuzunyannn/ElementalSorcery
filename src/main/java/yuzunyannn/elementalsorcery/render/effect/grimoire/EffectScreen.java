package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect.IGUIEffect;

@SideOnly(Side.CLIENT)
public abstract class EffectScreen extends EffectCondition implements IGUIEffect {

	public final Minecraft mc = Minecraft.getMinecraft();

	public EffectScreen(World world) {
		super(world);
		this.updateSize();
	}

	public float width;
	public float height;

	public void updateSize() {
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		width = scaledresolution.getScaledWidth();
		height = scaledresolution.getScaledHeight();
	}

}
