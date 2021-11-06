package yuzunyannn.elementalsorcery.potion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.FireworkEffect;

public class PotionRebirthFromFire extends PotionCommon {

	public PotionRebirthFromFire() {
		super(false, 0xcc7204, "rebirthFromFire");
		setBeneficial();
		iconIndex = 8;
	}

	public static boolean needRebirth(EntityLivingBase entity, DamageSource source) {
		return entity.isPotionActive(ESInit.POTIONS.REBIRTH_FROM_FIRE) && entity.isBurning() && source.isFireDamage();
	}

	public static void doRebirth(EntityLivingBase entity) {
		if (entity.world.isRemote) return;

		PotionEffect effect = entity.getActivePotionEffect(ESInit.POTIONS.REBIRTH_FROM_FIRE);
		int amplifier = effect.getAmplifier();
		entity.setHealth(0.5f);

		entity.clearActivePotions();

		int time = 20 * (amplifier + 2);
		entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, time * 3));
		entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, time, amplifier));

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("eId", entity.getEntityId());
		nbt.setByte("type", (byte) 1);
		Effects.spawnEffect(entity.world, Effects.PARTICLE_EFFECT,
				new Vec3d(entity.posX, entity.posY + entity.height / 2, entity.posZ), nbt);
	}

	@SideOnly(Side.CLIENT)
	public static void doEffect(World world, Vec3d pos, NBTTagCompound nbt) {
		int id = nbt.getInteger("eId");
		Entity entity = world.getEntityByID(id);

		world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(),
				1.0F, 1.0F, false);

		NBTTagCompound fireWork = FireworkEffect.fastNBT(10, 2, 0.2f,
				new int[] { 0xcc7204, 0xb24200, 0xebc242, 0xf8e9a3, 0xffffff }, null);
		nbt.setByte("extra", (byte) 1);
		Effects.spawnEffect(world, Effects.FIREWROK, pos, fireWork);
	}

}
