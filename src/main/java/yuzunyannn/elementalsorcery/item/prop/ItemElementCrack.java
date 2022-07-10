package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageBlockDisintegrate;
import yuzunyannn.elementalsorcery.network.MessageBlockDisintegrate.DisintegratePackage;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.crack.EffectFragmentCrackMove;
import yuzunyannn.elementalsorcery.render.effect.crack.EffectItemConfusion;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemElementCrack extends Item {

	public ItemElementCrack() {
		this.setTranslationKey("elementCrack");
		this.setMaxStackSize(1);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		World world = entityItem.world;
		entityItem.setEntityInvulnerable(true);
		entityItem.setNoGravity(true);
		if (entityItem.posY < 256) entityItem.motionY = 0.001D;
		if (entityItem.motionX > 0.75) entityItem.motionX = 0.75;
		if (entityItem.motionZ > 0.75) entityItem.motionZ = 0.75;
		if (!world.isRemote) {
			entityItem.setNoDespawn();
			if (entityItem.ticksExisted % 3 == 0) disintegrateAround(world, entityItem.getPosition(), entityItem);
			if (entityItem.ticksExisted % 10 == 0) disintegrateAroundEntity(world, entityItem.getPositionVector());
			return super.onEntityItemUpdate(entityItem);
		}
		int tick = entityItem.ticksExisted;
		Vec3d vec = entityItem.getPositionVector();
		playTickEffect(entityItem, vec, tick);
		if (entityItem.ticksExisted % 40 == 0 && RandomHelper.rand.nextFloat() < 0.5f) playEffect(world, entityItem);
		return super.onEntityItemUpdate(entityItem);
	}

	@SideOnly(Side.CLIENT)
	public void playEffect(World world, EntityItem entityItem) {
		EffectItemConfusion bc = new EffectItemConfusion(world, entityItem);
		bc.lifeTime = 4 + EffectItemConfusion.rand.nextInt(6);
		bc.scale = 1.01f;
		Effect.addEffect(bc);
	}

	public void disintegrateAround(World world, BlockPos center, Entity target) {
		if (world.isRemote) return;
		MessageBlockDisintegrate mbd = new MessageBlockDisintegrate();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos at = center.add(x, y, z);
					DisintegratePackage pkg = MessageBlockDisintegrate.tryBlockDisintegrate(world, at);
					if (pkg == null) continue;
					if (target != null) pkg.setDst(target);
					mbd.addPackage(pkg);
				}
			}
		}
		if (mbd.isEmpty()) return;
		ESNetwork.sendMessage(mbd, world, center);
	}

	public void disintegrateAroundEntity(World world, Vec3d vec) {
		AxisAlignedBB aabb = WorldHelper.createAABB(vec, 1.5, 1.5, 1.5);
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, aabb);
		for (Entity e : list) crackAttack(world, e, null);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (EntityHelper.isCreative(entityIn)) return;
		if (entityIn.ticksExisted % 20 == 0) crackAttack(worldIn, entityIn, null);
	}

	static public boolean isCannotAttackItem(ItemStack stack) {
		if (stack.getItem().getRegistryName().toString().indexOf("crack") != -1) return true;
		return false;
	}

	/** 裂痕攻击 */
	static public void crackAttack(World world, Entity entity, @Nullable Entity source) {
		if (world.isRemote) return;

		if (entity instanceof EntityItem) {
			EntityItem entityItem = (EntityItem) entity;
			if (isCannotAttackItem(entityItem.getItem())) return;
			entityItem.setDead();
			Effects.spawnEffect(world, Effects.ELEMENT_CRACK_ATTACK, entityItem.getPositionEyes(0), null);
			return;
		}

		if (entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entity;

			if (living.getHealth() <= 0) return;
			if (EntityHelper.isCreative(living)) return;

			DamageSource ds = DamageHelper.getMagicDamageSource(source, null).setDamageAllowedInCreativeMode()
					.setDamageIsAbsolute().setDamageBypassesArmor();
			living.setHealth(living.getHealth() * 0.95f);
			living.attackEntityFrom(ds, 20f + living.getHealth() * 0.05f);

			if (living instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) living;
				for (int j = 0; j < 4; j++) {
					int i = world.rand.nextInt(player.inventory.getSizeInventory());
					if (player.inventory.getStackInSlot(i).isEmpty()) continue;
					if (isCannotAttackItem(player.inventory.getStackInSlot(i))) continue;
					player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
					break;
				}
			}

			NBTTagCompound effectNBT = new NBTTagCompound();

			PotionEffect effect = living.getActivePotionEffect(ESInit.POTIONS.ELEMENT_CRACK_ATTACK);
			Map<Potion, PotionEffect> activePotionsMap = null;
			try {
				activePotionsMap = ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, living,
						"field_70713_bf");
				PotionEffect inner = activePotionsMap.get(ESInit.POTIONS.ELEMENT_CRACK_ATTACK);
				if (effect == null) effect = inner;
				else if (inner != null) {
					if (effect.getAmplifier() < inner.getAmplifier()) effect = inner;
				}
			} catch (Exception e) {}

			int nextLevel = (effect == null ? -1 : effect.getAmplifier()) + 1;
			int remainTick = (int) (20 * 30 + Math.pow(5, nextLevel));

			effect = new PotionEffect(ESInit.POTIONS.ELEMENT_CRACK_ATTACK, remainTick, nextLevel);
			living.addPotionEffect(effect);
			// 部分生物無法添加的藥水效果，但这个物品一定要记录！
			if (activePotionsMap != null) {
				PotionEffect inner = activePotionsMap.get(effect.getPotion());
				if (inner == null || inner.getAmplifier() < effect.getAmplifier())
					activePotionsMap.put(effect.getPotion(), effect);
			}

			if (nextLevel == 8) {
				living.setAbsorptionAmount(living.getAbsorptionAmount() - living.getHealth());
				living.setHealth(0);
				effectNBT.setBoolean("T", true);
			}

			Effects.spawnEffect(world, Effects.ELEMENT_CRACK_ATTACK,
					living.getPositionVector().add(0, living.height / 2, 0), effectNBT);
			return;
		}

		DamageSource ds = DamageHelper.getMagicDamageSource(source, null).setDamageAllowedInCreativeMode()
				.setDamageIsAbsolute().setDamageBypassesArmor();
		if (entity.attackEntityFrom(ds, Float.MAX_VALUE / 2)) {
			Effects.spawnEffect(world, Effects.ELEMENT_CRACK_ATTACK,
					entity.getPositionVector().add(0, entity.height / 2, 0), null);
		}
	}

	final static int[] color = new int[] { 0xFFBFBF, 0xBFFFBF, 0xBFBFFF };

	@SideOnly(Side.CLIENT)
	public void playTickEffect(EntityItem entityItem, Vec3d vec, int tick) {
		World world = entityItem.world;
		float yoff = 0;
		if (entityItem != null) {
			yoff = MathHelper.sin((entityItem.getAge()) / 10.0F + entityItem.hoverStart) * 0.1F + 0.1F;
			GlStateManager.translate(0, yoff, 0);
		}
		for (int i = 0; i < 3; i++) {
			EffectFragmentCrackMove effect = new EffectFragmentCrackMove(world, vec.add(0, 0.3 + yoff, 0));
			effect.isGlow = true;
			effect.prevScale = effect.scale = effect.defaultScale = 0.025f;
			effect.lifeTime = 30;
			effect.setColor(color[i]);
			float sin = MathHelper.sin(tick * 3.1415926f / 20 + i * 3.1415926f * 2 / 3);
			float cos = MathHelper.cos(tick * 3.1415926f / 20 + i * 3.1415926f * 2 / 3);
			Vec3d speed = new Vec3d(sin, Effect.rand.nextGaussian() * 0.125f, cos).scale(0.2);
			effect.setVelocity(speed);
			effect.setAccelerate(speed.scale(-0.01));
			effect.xDecay = effect.zDecay = effect.yDecay = 0.8;
			Effect.addEffect(effect);
		}
	}
}
