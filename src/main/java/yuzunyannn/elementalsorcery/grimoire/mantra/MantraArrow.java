package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.ICasterObject;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.ESFakePlayer;

public class MantraArrow extends MantraCommon {

	public MantraArrow() {
		this.setUnlocalizedName("arrow");
		this.setColor(0xccffff);
		this.setIcon("arrow");
		this.setRarity(95);
		this.setOccupation(3);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;
		mData.set(POWER, 26);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		int tick = caster.iWantKnowCastTick();

		MantraDataCommon mData = (MantraDataCommon) data;
		int preTick = Math.max(mData.get(POWER), 8);

		if ((tick - 2) % preTick == 0) {
			ElementStack need = new ElementStack(ESInit.ELEMENTS.AIR, 1, 3);
			ElementStack get = caster.iWantSomeElement(need, true);
			mData.markContinue(!get.isEmpty());
		}

		if (!mData.isMarkContinue()) return;
		if (world.isRemote) this.onSpellingEffect(world, data, caster);
		if (tick % preTick != 0) return;

		tryShoot(world, mData, caster, 20);

		if (preTick > 8) mData.set(POWER, preTick - 1);
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;
		if (!mData.isMarkContinue()) return;
		int tick = caster.iWantKnowCastTick();
		if (tick > 20) return;
		tryShoot(world, mData, caster, tick);
	}

	public void tryShoot(World world, IMantraData data, ICaster caster, int count) {

		ICasterObject co = caster.iWantCaster();
		Vec3d vec = co.getEyePosition();
		Vec3d dir = caster.iWantDirection();
		if (world.isRemote) {
			playEffect(world, vec, dir, this.getColor(data));
			return;
		}

		boolean infinite = false;
		ItemStack arrow = ItemStack.EMPTY;

		EntityLivingBase entity = co.asEntityLivingBase();
		if (entity == null) {
			entity = ESFakePlayer.get((WorldServer) world);
			entity.setPosition(vec.x, vec.y - entity.getEyeHeight() + 0.1, vec.z);
			float raw = (float) MathHelper.atan2(dir.z, dir.x) / 3.1415926f * 180 - 90;
			entity.rotationYaw = raw;
			entity.rotationPitch = (float) (-dir.y * 90);
		} else if (entity instanceof EntityPlayer) {
			arrow = findAmmo((EntityPlayer) entity);
			infinite = ((EntityPlayer) entity).isCreative();
		}

		if (arrow.isEmpty()) {
			ElementStack earth = new ElementStack(ESInit.ELEMENTS.EARTH, 1, 4);
			ElementStack wood = new ElementStack(ESInit.ELEMENTS.WOOD, 1, 3);
			if (caster.iWantSomeElement(earth, false).isEmpty()) return;
			if (caster.iWantSomeElement(wood, false).isEmpty()) return;
			caster.iWantSomeElement(earth, true);
			caster.iWantSomeElement(wood, true);
			infinite = true;
		}

		boolean fire = false;
		float power = 0;
		{
			ElementStack fireNeed = new ElementStack(ESInit.ELEMENTS.FIRE, 1, 8);
			ElementStack fireGet = caster.iWantSomeElement(fireNeed, true);
			fire = !fireGet.isEmpty();
			if (fire) power = MathHelper.sqrt(fireGet.getPower()) / 20f;
		}
		int punch = 0;
		{
			ElementStack earthNeed = new ElementStack(ESInit.ELEMENTS.EARTH, 1, 20);
			ElementStack earthGet = caster.iWantSomeElement(earthNeed, true);
			if (!earthGet.isEmpty()) punch = (int) (MathHelper.sqrt(earthGet.getPower()) / 10f);
		}

		shoot(entity, count + 2, arrow, power, punch, fire ? 100 : 0, infinite);

		if (entity instanceof EntityPlayer) {
			if (!arrow.isEmpty() && !((EntityPlayer) entity).isCreative()) arrow.shrink(1);
		}

	}

	static public boolean isArrow(ItemStack stack) {
		return stack.getItem() instanceof ItemArrow;
	}

	static public ItemStack findAmmo(EntityPlayer player) {
		if (isArrow(player.getHeldItem(EnumHand.OFF_HAND))) return player.getHeldItem(EnumHand.OFF_HAND);
		else if (isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) return player.getHeldItem(EnumHand.MAIN_HAND);
		else for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
			ItemStack itemstack = player.inventory.getStackInSlot(i);
			if (isArrow(itemstack)) return itemstack;
		}
		return ItemStack.EMPTY;
	}

	static public boolean shoot(EntityLivingBase shooter, int time, ItemStack arrow, float power, int punch, int fire,
			boolean infinite) {
		World world = shooter.world;
		float v = ItemBow.getArrowVelocity(time);
		if (v <= 0.1D) return false;

		ItemArrow itemarrow = (ItemArrow) (arrow.getItem() instanceof ItemArrow ? arrow.getItem() : Items.ARROW);
		EntityArrow entityarrow = itemarrow.createArrow(world, arrow, shooter);
		entityarrow.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, v * 3.0F, 1.0F);

		if (v == 1.0F) entityarrow.setIsCritical(true);

		if (power > 0) entityarrow.setDamage(entityarrow.getDamage() + power * 0.5 + 0.5);
		if (punch > 0) entityarrow.setKnockbackStrength(punch);
		if (fire > 0) entityarrow.setFire(fire);

		if (infinite) entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;

		world.spawnEntity(entityarrow);

		world.playSound((EntityPlayer) null, shooter.posX, shooter.posY, shooter.posZ, SoundEvents.ENTITY_ARROW_SHOOT,
				SoundCategory.PLAYERS, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + v * 0.5F);

		return true;
	}

	@SideOnly(Side.CLIENT)
	static public void playEffect(World world, Vec3d vec, Vec3d dir, int color) {
		Random rand = world.rand;
		for (int i = 0; i < 8; i++) {
			vec = vec.add(dir);
			EffectElementMove effect = new EffectElementMove(world, vec);
			effect.g = 0;
			Vec3d speed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize();
			effect.setVelocity(speed.scale(0.1f));
			effect.setColor(color);
			Effect.addEffect(effect);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getProgressRate(World world, IMantraData data, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		MantraDataCommon mData = (MantraDataCommon) data;
		int preTick = Math.max(mData.get(POWER), 8);

		int count = tick % preTick + 1;
		return count / (float) preTick;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData mData, ICaster caster) {
		super.onSpellingEffect(world, mData, caster);
		this.addEffectEmitEffect(world, mData, caster);
	}

}
