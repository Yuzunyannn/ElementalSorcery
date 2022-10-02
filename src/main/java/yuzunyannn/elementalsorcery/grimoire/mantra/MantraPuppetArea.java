package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.entity.mob.EntityPuppet;

public class MantraPuppetArea extends MantraSquareAreaAdv {

	public MantraPuppetArea() {
		this.setTranslationKey("puppetArea");
		this.setColor(0x187a00);
		this.setIcon("puppet_area");
		this.setRarity(40);
		this.setOccupation(5);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.WOOD, 2, 100), 500, 100);
		this.initAndAddDefaultMantraLauncher(0.01);
	}

	@Override
	public void init(World world, SquareData data, ICaster caster, BlockPos pos) {
		ElementStack earth = data.get(ESObjects.ELEMENTS.WOOD);
		float potent = caster.iWantBePotent(0.75f, false);
		float rate = 0.5f * potent + 1;
		data.setSize(Math.min(earth.getPower() / 125, 8) * rate + 4);
		data.set(POWERF, potent);
	}

	@Override
	public boolean tick(World world, SquareData data, ICaster caster, BlockPos originPos) {
		if (world.isRemote) return true;

		int tick = caster.iWantKnowCastTick();
		ElementStack wood = data.get(ESObjects.ELEMENTS.WOOD);
		if (wood.isEmpty()) return false;

		if (tick % 100 != 0) return true;
		wood.shrink(12);

		final float size = data.getSize() / 2;
		final float hight = Math.max(size * 2, 2);
		AxisAlignedBB aabb = new AxisAlignedBB(originPos.getX() - size, originPos.getY(), originPos.getZ() - size,
				originPos.getX() + size, originPos.getY() + hight, originPos.getZ() + size);

		EntityLivingBase casterEntity = caster.iWantCaster().asEntityLivingBase();

		float potent = data.get(POWERF);
		float pDamage = Math.min(0.05f, 0.01f + potent / 50);
		float nDamage = Math.min(3, wood.getPower() / 200f);

		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		for (EntityLivingBase entity : entities) {
			if (isCasterFriend(caster, entity)) continue;
			if (entity instanceof EntityPuppet) continue;
			boolean needCome = entity instanceof IMob || (entity.getAttackingEntity() != null);
			if (!needCome) continue;
			wood.shrink(2);
			EntityPuppet puppet = new EntityPuppet(world, 200, casterEntity, entity);
			puppet.setDamage(pDamage, nDamage);
			puppet.setPosition(entity.posX + world.rand.nextGaussian() * 0.25, entity.posY,
					entity.posZ + world.rand.nextGaussian() * 0.25);
			world.spawnEntity(puppet);
		}

		return true;
	}
}
