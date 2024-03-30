package yuzunyannn.elementalsorcery.grimoire.remote;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.util.target.WorldLocation;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.element.ElementFire;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.scrappy.EffectSphericalBlast;

public class FMantraFireBall extends FMantraBase {

	public FMantraFireBall() {
		addCanUseElementWithSameLevel(ESObjects.ELEMENTS.FIRE);
		setMaxCharge(new ElementStack(ESObjects.ELEMENTS.FIRE, 150, 40));
		setChargeSpeedRatio(4f / 10000f);
		setIconRes("textures/mantras/fire_ball_f.png");
	}

	@Override
	public void cast(World world, BlockPos pos, WorldLocation to, VariableSet content) {
		if (world.isRemote) return;
		double chager = content.get(CHARGE);
		ElementStack estack = new ElementStack(ESObjects.ELEMENTS.FIRE, (int) chager, (int) chager);
		ElementExplosion.doExplosion(world, to.getPos(), estack, null);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void castClientTo(World world, BlockPos to) {
		EffectSphericalBlast effect = new EffectSphericalBlast(world, new Vec3d(to).add(0.5, 0.5, 0.5), 2.5f);
		effect.maxLifeTime = effect.lifeTime = 40;
		effect.color.setColor(ElementFire.COLOR);
		Effect.addEffect(effect);
	}

}
