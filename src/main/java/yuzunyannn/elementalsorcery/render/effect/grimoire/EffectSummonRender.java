package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSummon;

@SideOnly(Side.CLIENT)
public class EffectSummonRender extends EffectCondition {

	public final MantraSummon.Data data;

	public EffectSummonRender(World world, MantraSummon.Data data) {
		super(world);
		this.lifeTime = 1;
		this.data = data;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		data.getSummon().updateRender();
	}

	@Override
	protected void doRender(float partialTicks) {
		data.getSummon().doRender(mc, partialTicks);
	}

}
