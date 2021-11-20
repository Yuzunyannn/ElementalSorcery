package yuzunyannn.elementalsorcery.potion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class PotionCalamity extends PotionCommon {

	public PotionCalamity() {
		super(true, 0x6c6c6c, "calamity");
		iconIndex = 43;
		registerPotionAttributeModifier(SharedMonsterAttributes.LUCK, "0ea10585-59ec-410a-af58-95b161fb429f", -1, 0);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 100 == 0;
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		World world = entity.world;

		if (entity.isInWater()) entity.motionY -= 1f;

		if (world.isRemote) return;

		Random rand = entity.getRNG();

		AxisAlignedBB aabb = WorldHelper.createAABB(entity, 6, 1, 2);
		List<EntityLiving> mobs = world.getEntitiesWithinAABB(EntityLiving.class, aabb);

		if (amplifier >= 3) {
			BlockPos pos = entity.getPosition().down();
			if (!BlockHelper.isBedrock(world, pos) && !BlockHelper.isFluid(world, pos)) world.destroyBlock(pos, false);
			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				BlockPos at = pos.offset(facing);
				if (!BlockHelper.isBedrock(world, at) && !BlockHelper.isFluid(world, pos))
					world.destroyBlock(at, false);
			}
		}

		if (entity instanceof IMob) {
			if (amplifier >= 2) {
				entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, Math.min(2, amplifier - 1)));
				entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, Math.min(2, amplifier - 1)));
				entity.setFire(180);
			}
		} else {
			if (amplifier >= 2 && mobs.size() < 16) {
				BlockPos pos = WorldHelper.tryFindPlaceToSpawn(world, entity.getRNG(), entity.getPosition(), 6);
				if (pos != null) {
					pos = pos.up();
					EntityMob mob;
					switch (rand.nextInt(3)) {
					case 1:
						mob = new EntitySkeleton(world);
						mob.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BOW));
						if (rand.nextFloat() < 0.25f)
							mob.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
						break;
					case 2:
						mob = new EntityCreeper(world);
						break;
					default:
						mob = new EntityZombie(world);
						if (rand.nextFloat() < 0.25f)
							mob.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.GOLDEN_SWORD));
						break;
					}
					if (rand.nextFloat() < 0.75f)
						mob.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
					if (rand.nextFloat() < 0.5f)
						mob.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
					if (rand.nextFloat() < 0.5f)
						mob.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
					if (rand.nextFloat() < 0.5f)
						mob.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));

					mob.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
					world.spawnEntity(mob);
					mob.setAttackTarget(entity);
				}
			}
		}

		if (amplifier >= 1) {
			if ((world.isRaining() || world.isThundering()) && world.canBlockSeeSky(entity.getPosition())) {
				EntityLightningBolt lightning = new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ,
						false);
				world.addWeatherEffect(lightning);
			}
		}

		for (EntityLiving living : mobs) {
			if (EntityHelper.isSameTeam(living, entity)) continue;
			if (living.getAttackTarget() == null) living.setAttackTarget(entity);
		}

	}

	public static void eliminateOres(int amplifier, List<ItemStack> drops, Random rand) {
		Iterator<ItemStack> iter = drops.iterator();
		while (iter.hasNext()) {
			ItemStack stack = iter.next();
			for (int i = 0; i < amplifier + 1; i++) {
				if (rand.nextFloat() < 0.8f) continue;
				int remainCount = MathHelper.floor(stack.getCount() * 0.9f);
				if (remainCount == 0) {
					iter.remove();
					break;
				} else stack.setCount(i);
			}
		}
	}

}
