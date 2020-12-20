package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class MantraLightningArea extends MantraSquareAreaAdv {

	public MantraLightningArea() {
		this.setUnlocalizedName("lightningArea");
		this.setRarity(50);
		this.setColor(0x0076ee);
		this.addElementCollect(new ElementStack(ESInit.ELEMENTS.AIR, 2, 50), 80, 25);
		this.addElementCollect(new ElementStack(ESInit.ELEMENTS.FIRE, 2, 40), -1, 25);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResource() {
		return RenderObjects.MANTRA_LIGHTNING;
	}

	@Override
	public void init(World world, SquareDataAdv data, ICaster caster, BlockPos pos) {
		ElementStack air = data.getElement(ESInit.ELEMENTS.AIR);
		data.setSize(Math.min(air.getPower() / 80, 12) + 6);
	}

	@Override
	public boolean tick(World world, SquareDataAdv data, ICaster caster, BlockPos originPos) {
		int tick = caster.iWantKnowCastTick();
		ElementStack air = data.getElement(ESInit.ELEMENTS.AIR);
		if (air.isEmpty()) return false;
		if (tick % 20 != 0) return true;
		Random rand = world.rand;
		air.shrink(8);
		ElementStack fire = data.getElement(ESInit.ELEMENTS.FIRE);
		int maxCount = MathHelper.ceil(fire.getCount() / 16);
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
					float addDamage = (fire.getPower() - 200) / 125;
					living.attackEntityFrom(DamageSource.LIGHTNING_BOLT, addDamage);
				}
			}
		}
		return true;
	}

}
