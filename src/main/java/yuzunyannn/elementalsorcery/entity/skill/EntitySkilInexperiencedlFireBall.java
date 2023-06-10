package yuzunyannn.elementalsorcery.entity.skill;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireBall;

public class EntitySkilInexperiencedlFireBall extends EntitySkillTarget {

	public int remianTick = 0;

	public EntitySkilInexperiencedlFireBall(EntityLivingBase entity) {
		super(entity);
		this.setCD(30 * 60);
	}

	@Override
	public boolean checkCanUse() {
		return super.checkCanUse() && checkHPLowerThan(0.5);
	}

	@Override
	public int doSkill() {
		super.doSkill();
		this.living.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Blocks.TORCH));
		this.remianTick = 20 * 5;
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

	@Override
	public void doSkillFin() {
		this.living.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
		Random rand = getRandom();
		if (rand.nextDouble() < 0.3333) {
			boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.living);
			world.createExplosion(entity, living.posX, living.posY, living.posZ, 3, flag);
			return;
		}
		MantraFireBall.fire(world, this.living, 8 + rand.nextInt(8), true);

	}

	@Override
	public int doContinueSkill() {
		if (remianTick-- < 0) return EntitySkill.SKILL_RESULT_FIN;
		return EntitySkill.SKILL_RESULT_CONTINUE;
	}

}
