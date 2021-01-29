package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectElementMove;

public class MantraSlowFall extends MantraCommon {

	public MantraSlowFall() {
		this.setUnlocalizedName("slowFall");
		this.setColor(0xacffff);
		this.setIcon("slow_fall");
		this.setRarity(125);
		this.setOccupation(1);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		onSpelling(world, data, caster);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		if (caster.iWantKnowCastTick() % 100 == 0 || !dataEffect.isMarkContinue()) {
			dataEffect.markContinue(false);
			ElementStack need = new ElementStack(ESInit.ELEMENTS.AIR, 1, 10);
			ElementStack get = caster.iWantSomeElement(need, true);
			if (get.isEmpty()) return;
		}
		dataEffect.markContinue(true);
		Entity entity = caster.iWantCaster();
		if (entity.motionY < -0.2) {
			entity.fallDistance *= 0.7;
			entity.motionY *= 0.7;
			if (!entity.onGround) {
				Vec3d look = entity.getLookVec().scale(0.05);
				entity.motionX += look.x;
				entity.motionZ += look.z;
			}
		}
		if (world.isRemote) onSpellingEffect(world, data, caster);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		if (!hasEffectFlags(world, data, caster, MantraEffectFlags.DECORATE)) return;
		Random rand = world.rand;
		Entity entity = caster.iWantCaster();
		Vec3d pos = entity.getPositionVector();
		float r = 0.5f;
		float theta = EventClient.globalRotate / 180 * 3.14f * 10;
		Vec3d at = pos.addVector(MathHelper.sin(theta) * r, 1, MathHelper.cos(theta) * r);
		EffectElementMove effect = new EffectElementMove(world, at);
		effect.scale = 0.2f;
		effect.g = 0;
		Vec3d v = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize();
		effect.setVelocity(v.scale(rand.nextFloat() * 0.1f));
		effect.setColor(this.getColor(data));
		Effect.addEffect(effect);
	}

}
