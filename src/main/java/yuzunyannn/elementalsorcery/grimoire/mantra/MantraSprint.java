package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.remote.FMantraSprint;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.scrappy.EffectResonance;

public class MantraSprint extends MantraCommon {

	public MantraSprint() {
		this.setTranslationKey("sprint");
		this.setColor(0xabfffa);
		this.setIcon("sprint");
		this.setRarity(100);
		this.addFragmentMantraLauncher(new FMantraSprint(this));
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		ElementStack stack = getElement(caster, ESObjects.ELEMENTS.AIR, 2, 25);
		if (stack.isEmpty()) return;

		float potent = caster.iWantBePotent(0.2f, false);
		doPotentAttackEffect(world, caster, target);

		Vec3d dir = caster.iWantDirection();
		dir = dir.add(0, 0.5, 0).normalize();
		float speed = MathHelper.clamp(MathHelper.sqrt(stack.getPower() / 10f), 1.5f, 5) * (1 + potent / 5);
		dir = dir.scale(speed);
		target.motionX += dir.x;
		target.motionY += dir.y;
		target.motionZ += dir.z;
		if (target instanceof EntityLivingBase) {
			((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 100, 3));
		}
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataCommon = (MantraDataCommon) data;
		ElementStack need = new ElementStack(ESObjects.ELEMENTS.AIR, 4, 30);
		ElementStack get = caster.iWantSomeElement(need, true);
		if (get.isEmpty()) return;
		Entity entity = caster.iWantCaster().asEntity();
		if (entity == null) return;
		dataCommon.markContinue(true);
		int power = get.getPower();
		double scale = MathHelper.clamp(MathHelper.sqrt(power / 10f), 1.5, 6);
		Vec3d look = entity.getLookVec().add(0, 0.05, 0).normalize().scale(scale);
		entity.motionX += look.x;
		entity.motionY += look.y;
		entity.motionZ += look.z;
		world.playSound((EntityPlayer) null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_PLAYER_BIG_FALL,
				SoundCategory.PLAYERS, 1.0F, 1.0F);
		if (entity instanceof EntityLivingBase) {
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 100, 3));
		}
		onSpelling(world, dataCommon, caster);
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataCommon = (MantraDataCommon) data;
		if (!dataCommon.isMarkContinue()) return;
		Entity entity = caster.iWantCaster().asEntity();
		if (entity == null) return;
		entity.motionY = 0;
		entity.fallDistance = 0;
		if (world.isRemote) endEffect(world, entity);
	}

	@SideOnly(Side.CLIENT)
	public void endEffect(World world, Entity entity) {
		EffectResonance effect = new EffectResonance(world, entity.posX, entity.posY + 1, entity.posZ);
		effect.setColor(0xffffff);
		Effect.addEffect(effect);
	}

}
