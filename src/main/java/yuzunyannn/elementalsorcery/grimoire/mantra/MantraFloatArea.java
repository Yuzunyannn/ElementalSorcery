package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.ICasterObject;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;

public class MantraFloatArea extends MantraTypeSquareArea {

	public MantraFloatArea() {
		this.setTranslationKey("floatArea");
		this.setColor(0x9cddfb);
		this.setIcon("float_area");
		this.setRarity(80);
		this.setOccupation(4);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.AIR, 2, 25), 60, 10);
		this.initAndAddDefaultMantraLauncher(0.0075);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);

		ElementStack stack = getElement(caster, ESObjects.ELEMENTS.AIR, 2, 20);
		if (stack.isEmpty()) return;
		target.motionY += 1;
		if (target instanceof EntityLivingBase) {
			((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 120, 3));
		}
	}

	@Override
	public void init(World world, SquareData data, ICaster caster, BlockPos pos) {
		ElementStack air = data.get(ESObjects.ELEMENTS.AIR);
		float rate = 0.6f * caster.iWantBePotent(0.5f, false) + 1;
		data.setSize(Math.min(air.getPower() / 80, 12) * rate + 6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addAfterEffect(SquareData data, ICaster caster, int size) {
		super.addAfterEffect(data, caster, size);
		ICasterObject casterObj = caster.iWantDirectCaster();
		World world = casterObj.getWorld();
		Random rand = world.rand;
		int times = (int) (size / 16 + 1);
		for (int i = 0; i < times; i++) {
			float hSize = size / 2;
			Vec3d pos = casterObj.getObjectPosition().add(rand.nextDouble() * size - hSize, 0.1,
					rand.nextDouble() * size - hSize);
			EffectElementMove effect = new EffectElementMove(world, pos);
			effect.setVelocity(0, rand.nextDouble() * 0.5 + 0.5, 0);
			effect.setColor(getColor(data));
			Effect.addEffect(effect);
		}
	}

	@Override
	public boolean tick(World world, SquareData data, ICaster caster, BlockPos originPos) {
		int tick = caster.iWantKnowCastTick();
		ElementStack air = data.get(ESObjects.ELEMENTS.AIR);
		if (air.isEmpty()) return false;
		if (tick % 10 == 0) air.shrink(1);

		final float size = data.getSize() / 2;
		AxisAlignedBB aabb = new AxisAlignedBB(originPos.getX() - size, originPos.getY(), originPos.getZ() - size,
				originPos.getX() + size, originPos.getY() + size * 4, originPos.getZ() + size);
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb, (entity) -> {
			return entity instanceof EntityLivingBase || entity instanceof EntityItem;
		});
		for (Entity entity : entities) {
			double dy = Math.max(1, MathHelper.sqrt(Math.max(entity.posY - originPos.getY(), 0)));
			entity.motionY += 0.5 / dy;
			if (entity.motionY > 0) entity.fallDistance = 0;
		}
		return true;
	}
}
