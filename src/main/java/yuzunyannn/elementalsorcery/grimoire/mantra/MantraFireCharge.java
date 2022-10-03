package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;

public class MantraFireCharge extends MantraTypeAccumulative {

	protected static class MyCollectRule extends CollectRuleRepeated {
		@Override
		public int getMaxNeed(CollectInfo info, World world, MantraDataCommon mData, ICaster caster) {
			return mData.has(POTENT_POWER) ? info.maxNeed / 4 : info.maxNeed;
		}

		@Override
		public int getInterval(World world, MantraDataCommon mData, ICaster caster) {
			return mData.has(POTENT_POWER) ? interval / 2 : interval;
		}
	}

	public MantraFireCharge() {
		this.setTranslationKey("fireCharge");
		this.setColor(0xeeac18);
		this.setIcon("fire_charge");
		this.setRarity(100);
		this.setOccupation(2);
		CollectRuleRepeated rule = new MyCollectRule();
		rule.setInterval(20);
		rule.addElementCollect(new ElementStack(ESObjects.ELEMENTS.FIRE, 1, 20), 4, 2);
		this.setMainRule(rule);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		ElementStack stack = getElement(caster, ESObjects.ELEMENTS.FIRE, 1, 20);
		if (stack.isEmpty()) return;
		if (world.isRemote) return;
		world.createExplosion(null, target.posX, target.posY + target.height / 2, target.posZ, 0.5f, false);
	}

	@Override
	protected void onCollectStart(World world, MantraDataCommon mData, ICaster caster) {
		float potent = caster.iWantBePotent(0.05f, true);
		if (potent >= 0.2f) mData.set(POTENT_POWER, caster.iWantBePotent(0.05f, false));
	}

	@Override
	protected boolean onCollectFinish(World world, MantraDataCommon mData, ICaster caster) {
		mData.remove(POTENT_POWER);
		if (world.isRemote) return true;

		Random rand = world.rand;
		Vec3d eyePos = caster.iWantCaster().getEyePosition();
		Vec3d tar = caster.iWantDirection();
		eyePos = eyePos.add(tar.scale(1.25)).add(rand.nextGaussian() * 0.25, rand.nextGaussian() * 0.25,
				rand.nextGaussian() * 0.25);

		world.playSound(null, eyePos.x, eyePos.y, eyePos.z, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 1, 1);
		EntityFireball ball = new EntitySmallFireball(world, eyePos.x, eyePos.y, eyePos.z, tar.x, tar.y, tar.z);
		world.spawnEntity(ball);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData mData, ICaster caster) {
		super.onSpellingEffect(world, mData, caster);
		this.addEffectEmitEffect(world, mData, caster);
	}

}
