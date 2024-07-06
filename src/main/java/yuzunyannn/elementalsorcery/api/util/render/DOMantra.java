package yuzunyannn.elementalsorcery.api.util.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class DOMantra extends BaseDisplayObject {

	protected Mantra mantra;
	protected float scale = 1;

	public DOMantra(Mantra mantra) {
		super("E:M");
		this.mantra = mantra;
		setScale(1);
	}

	public DOMantra setScale(float scale) {
		this.scale = scale;
		this.size = new Vec3d(16, 16, 16).scale(scale);
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void doRender(float partialTicks) {
		if (this.mantra == null) return;
		GlStateManager.translate(8 * scale, 8 * scale, 0);
		Color c = new Color(this.mantra.getColor(null));
		GlStateManager.color(c.r, c.g, c.b);
		this.mantra.renderShiftIcon(null, 16 * scale, 1, partialTicks);
		GlStateManager.translate(-8 * scale, -8 * scale, 0);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (this.mantra == null) return nbt;
		nbt.setInteger("i", Mantra.REGISTRY.getId(mantra));
		if (this.scale != 1) nbt.setFloat("s", scale);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.mantra = Mantra.REGISTRY.getValue(nbt.getInteger("i"));
		this.setScale(nbt.hasKey("s") ? nbt.getFloat("s") : 1);
	}

}
