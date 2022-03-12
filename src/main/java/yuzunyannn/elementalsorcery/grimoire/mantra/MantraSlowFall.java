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
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;

public class MantraSlowFall extends MantraCommon {

	public MantraSlowFall() {
		this.setTranslationKey("slowFall");
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
		Entity entity = caster.iWantCaster().asEntity();
		if (entity == null) return;
		dataEffect.markContinue(true);

		float potent = caster.iWantBePotent(0.05f, true);
		Vec3d look = entity.getLookVec();

		if (potent >= 0.75f) {
			caster.iWantBePotent(0.05f, false);
			look = look.scale(0.05 * (1 + potent));
			if (entity.motionY < 0) {
				entity.fallDistance *= 0.7;
				entity.motionY *= 0.7;
			}
			entity.motionX += look.x;
			entity.motionZ += look.z;
			entity.motionY += look.y;
		} else if (entity.motionY < -0.2) {
			entity.fallDistance *= 0.7;
			entity.motionY *= 0.7;
			if (!entity.onGround) {
				potent = caster.iWantBePotent(0.01f, false);
				look = look.scale(0.05 * (1 + potent));
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
		Vec3d pos = caster.iWantCaster().getPositionVector();
		float r = 0.5f;
		float theta = EventClient.globalRotate / 180 * 3.14f * 10;
		Vec3d at = pos.add(MathHelper.sin(theta) * r, 1, MathHelper.cos(theta) * r);
		EffectElementMove effect = new EffectElementMove(world, at);
		effect.scale = 0.2f;
		Vec3d v = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize();
		effect.setVelocity(v.scale(rand.nextFloat() * 0.1f));
		effect.setColor(this.getColor(data));
		Effect.addEffect(effect);
	}

}
