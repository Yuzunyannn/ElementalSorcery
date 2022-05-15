package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSummon;
import yuzunyannn.elementalsorcery.render.effect.EffectCondition;

@SideOnly(Side.CLIENT)
public class EffectSummonRender extends EffectCondition {

	public final MantraSummon.Data data;
	public int endTick;

	public EffectSummonRender(World world, MantraSummon.Data data) {
		super(world);
		this.lifeTime = 1;
		this.data = data;
		this.endTick = data.getSummon().getRenderEndTick();
	}

	@Override
	public void onUpdate() {
		if (this.isEnd()) {
			if (--endTick <= 0) {
				this.lifeTime = 0;
				return;
			}
		}
		data.getSummon().updateRender(endTick);
	}

	@Override
	protected void doRender(float partialTicks) {
		data.getSummon().doRender(mc, partialTicks);
	}

}
