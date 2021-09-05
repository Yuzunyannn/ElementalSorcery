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
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.init.ESInit;

public class MantraFireCharge extends MantraCommon {

	public MantraFireCharge() {
		this.setUnlocalizedName("fireCharge");
		this.setColor(0xeeac18);
		this.setIcon("fire_charge");
		this.setRarity(100);
		this.setOccupation(2);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		ElementStack stack = getElement(caster, ESInit.ELEMENTS.FIRE, 1, 20);
		if (stack.isEmpty()) return;
		if (world.isRemote) return;
		world.createExplosion(null, target.posX, target.posY + target.height / 2, target.posZ, 0.5f, false);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return;

		float potent = caster.iWantBePotent(0.05f, true);
		boolean isPotent = false;
		if (potent >= 0.2f) {
			caster.iWantBePotent(0.05f, false);
			isPotent = true;
		}

		if (!isPotent) {
			if (tick % 5 != 0) return;
		}

		((MantraDataCommon) data).markContinue(true);
		ElementStack get = getElement(caster, ESInit.ELEMENTS.FIRE, isPotent ? 1 : 4, 20);
		if (get.isEmpty()) return;

		if (world.isRemote) {
			this.onSpellingEffect(world, data, caster);
			return;
		}
		Random rand = world.rand;
		Vec3d eyePos = caster.iWantCaster().getEyePosition();
		Vec3d tar = caster.iWantDirection();
		eyePos = eyePos.add(tar.scale(1.25)).addVector(rand.nextGaussian() * 0.25, rand.nextGaussian() * 0.25,
				rand.nextGaussian() * 0.25);

		world.playSound(null, eyePos.x, eyePos.y, eyePos.z, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 1, 1);
		EntityFireball ball = new EntitySmallFireball(world, eyePos.x, eyePos.y, eyePos.z, tar.x, tar.y, tar.z);
		world.spawnEntity(ball);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData mData, ICaster caster) {
		super.onSpellingEffect(world, mData, caster);
		this.addEffectEmitEffect(world, mData, caster);
	}

}
