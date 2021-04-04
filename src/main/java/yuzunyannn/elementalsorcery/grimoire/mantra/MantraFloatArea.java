package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.ICasterObject;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.element.EffectElementMove;

public class MantraFloatArea extends MantraSquareAreaAdv {

	public MantraFloatArea() {
		this.setUnlocalizedName("floatArea");
		this.setColor(0x9cddfb);
		this.setIcon("float_area");
		this.setRarity(80);
		this.setOccupation(4);
		this.addElementCollect(new ElementStack(ESInit.ELEMENTS.AIR, 2, 25), 60, 10);
	}

	@Override
	public void init(World world, SquareData data, ICaster caster, BlockPos pos) {
		ElementStack fire = data.get(ESInit.ELEMENTS.AIR);
		data.setSize(Math.min(fire.getPower() / 80, 12) + 6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addAfterEffect(SquareData data, ICaster caster, int size) {
		super.addAfterEffect(data, caster, size);
		ICasterObject co = caster.iWantDirectCaster();
		World world = co.getWorld();
		Random rand = world.rand;
		int times = (int) (size / 16 + 1);
		for (int i = 0; i < times; i++) {
			float hSize = size / 2;
			Vec3d pos = co.getPositionVector().addVector(rand.nextDouble() * size - hSize, 0.1,
					rand.nextDouble() * size - hSize);
			EffectElementMove effect = new EffectElementMove(world, pos);
			effect.g = 0;
			effect.setVelocity(0, rand.nextDouble() * 0.5 + 0.5, 0);
			effect.setColor(getRenderColor());
			Effect.addEffect(effect);
		}

	}

	@Override
	public boolean tick(World world, SquareData data, ICaster caster, BlockPos originPos) {
		int tick = caster.iWantKnowCastTick();
		ElementStack air = data.get(ESInit.ELEMENTS.AIR);
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
