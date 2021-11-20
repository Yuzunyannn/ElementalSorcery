package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.item.IPlatformTickable;
import yuzunyannn.elementalsorcery.grimoire.ICasterObject;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemCalamityGem extends Item implements IPlatformTickable {

	public ItemCalamityGem() {
		this.setUnlocalizedName("calamityGem");
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (worldIn.isRemote) return;
		if (entityIn.ticksExisted % 100 == 0) {

			EnumHand hand = ItemVortex.inEntityHand(entityIn, stack, itemSlot, isSelected);
			if (hand == null) return;

			calamityEntity((EntityLivingBase) entityIn, 100);
		}
	}

	public void calamityEntity(EntityLivingBase living, int tick) {
		PotionEffect effect = living.getActivePotionEffect(ESInit.POTIONS.CALAMITY);
		int amplifier = Math.min(effect == null ? 0 : (effect.getAmplifier() + 1), 3);
		int lTime = (int) ((tick + 20 + (effect == null ? 0 : effect.getDuration())) * (1 + amplifier * 0.1f));
		int time = (int) Math.min(Short.MAX_VALUE, lTime);
		living.addPotionEffect(new PotionEffect(ESInit.POTIONS.CALAMITY, time, amplifier));
	}

	@Override
	public boolean platformUpdate(World world, ItemStack stack, ICasterObject caster, NBTTagCompound runData,
			int tick) {
		if (world.isRemote) {
			randEffect(world, caster.getPosition(), 8, new int[] { 0x777777, 0x933700, 0x006685, 0x001a9c, 0x7f8c00 });
			return false;
		}
		if (tick % 200 != 0) return false;
		tryAddPotionEffect(world, caster.getPosition(), 8, e -> {
			calamityEntity(e, 200);
			Effects.spawnSummonEntity(e, new int[] { 0x777777, 0x933700, 0x006685, 0x001a9c, 0x7f8c00 });
			return null;
		});
		return false;
	}

	public static void tryAddPotionEffect(World world, BlockPos center, float range,
			Function<EntityLivingBase, Void> adder) {
		AxisAlignedBB aabb = WorldHelper.createAABB(center, range, range / 2, 1.5);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		for (EntityLivingBase entity : entities) adder.apply(entity);
	}

	@SideOnly(Side.CLIENT)
	public static void randEffect(World world, BlockPos center, float range, int[] colors) {
		Random rand = Effect.rand;
		Vec3d vec = new Vec3d(center).addVector(0.5 + rand.nextGaussian() * range, 0.5 + rand.nextFloat() * range / 2,
				0.5 + rand.nextGaussian() * range);
		EffectElementMove effect = new EffectElementMove(world, vec);
		effect.setColor(colors[rand.nextInt(colors.length)]);
		Vec3d speed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize();
		effect.setVelocity(speed.scale(rand.nextDouble() * 0.05 + 0.025));
		effect.xDecay = effect.yDecay = effect.zDecay = 0.95;
		Effect.addEffect(effect);
	}

}
