package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;

public class MantraLightningArea extends MantraSquareAreaAdv {

	public MantraLightningArea() {
		this.setTranslationKey("lightningArea");
		this.setColor(0x0076ee);
		this.setIcon("lightning");
		this.setRarity(50);
		this.setOccupation(3);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.AIR, 2, 50), 80, 20);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.FIRE, 2, 40), -1, 20);
		this.setPotentPowerCollect(0.1f, 2);
		this.initAndAddDefaultMantraLauncher(0.002);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		if (world.rand.nextInt(3) != 0) return;

		ElementStack stack = getElement(caster, ESObjects.ELEMENTS.AIR, 4, 40);
		if (stack.isEmpty()) return;

		if (world.isRemote) return;
		EntityLightningBolt lightning = new EntityLightningBolt(world, target.posX, target.posY, target.posZ, false);
		world.addWeatherEffect(lightning);
	}

	@Override
	public void init(World world, SquareData data, ICaster caster, BlockPos pos) {
		ElementStack air = data.get(ESObjects.ELEMENTS.AIR);
		float rate = 0.6f * caster.iWantBePotent(0.75f, false) + 1;
		data.setSize(Math.min(air.getPower() / 80, 12) * rate + 6);
	}

	@Override
	public boolean tick(World world, SquareData data, ICaster caster, BlockPos originPos) {
		if (world.isRemote) return true;
		int tick = caster.iWantKnowCastTick();
		ElementStack air = data.get(ESObjects.ELEMENTS.AIR);
		if (air.isEmpty()) return false;
		
		float pp = data.get(POTENT_POWER);
		int preTick = pp >= 1 ? 20 : 30;
		if (tick % preTick != 0) return true;
		
		Random rand = world.rand;
		air.shrink(8);
		ElementStack fire = data.get(ESObjects.ELEMENTS.FIRE);
		int maxCount = MathHelper.ceil(fire.getCount() / 16 * (1 + pp));
		final float size = data.getSize() / 2;
		AxisAlignedBB aabb = new AxisAlignedBB(originPos.getX() - size, originPos.getY(), originPos.getZ() - size,
				originPos.getX() + size, originPos.getY() + 3, originPos.getZ() + size);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		if (entities.isEmpty()) {
			double x = originPos.getX() + rand.nextDouble() * size * 2 - size;
			double z = originPos.getZ() + rand.nextDouble() * size * 2 - size;
			EntityLightningBolt lightning = new EntityLightningBolt(world, x, originPos.getY(), z, false);
			world.addWeatherEffect(lightning);
		} else {
			int startIndex = rand.nextInt(entities.size());
			for (int i = 0; i < Math.min(maxCount, entities.size()); i++) {
				EntityLivingBase living = entities.get((i + startIndex) % entities.size());
				EntityLightningBolt lightning = new EntityLightningBolt(world, living.posX, living.posY, living.posZ,
						false);
				world.addWeatherEffect(lightning);
				if (fire.getPower() > 200) {
					float addDamage = MathHelper.sqrt((fire.getPower() - 200) / 125) * (1 + pp * 0.5f);
					living.attackEntityFrom(DamageSource.LIGHTNING_BOLT, addDamage);
				}
			}
		}
		return true;
	}

}
