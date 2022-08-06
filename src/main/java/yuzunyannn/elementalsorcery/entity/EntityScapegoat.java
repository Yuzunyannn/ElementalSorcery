package yuzunyannn.elementalsorcery.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;

public class EntityScapegoat extends EntityLiving {

	@Nullable
	protected NBTTagCompound itemNBT;

	public EntityScapegoat(World world) {
		super(world);
		this.setSize(0.8f, 2.25f);
	}

	public EntityScapegoat(World world, BlockPos pos, EntityLivingBase who, ItemStack stack) {
		super(world);
		this.setSize(0.8f, 2.25f);
		this.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		this.setCustomNameTag(who.getName());
		this.itemNBT = stack.getTagCompound();
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (itemNBT != null && !itemNBT.isEmpty()) compound.setTag("itemNBT", itemNBT);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("itemNBT", NBTTag.TAG_COMPOUND)) itemNBT = compound.getCompoundTag("itemNBT");
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (this.ticksExisted % 20 * 2 == 0) {
			this.setAir(300);
			final int size = 8;
			AxisAlignedBB aabb = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size,
					posZ + size);
			String name = this.getCustomNameTag();
			List<EntityCreature> entities = world.getEntitiesWithinAABB(EntityCreature.class, aabb);
			for (EntityCreature e : entities) {
				EntityLivingBase living = e.getRevengeTarget();
				if (living == null) living = e.getAttackTarget();
				if (living instanceof EntityScapegoat) continue;
				if (living == null) continue;
				// if (!living.isNonBoss()) continue;
				if (name.isEmpty() || name.equals(living.getName())) {
					e.setRevengeTarget(this);
					e.setAttackTarget(this);
				}
			}
		}
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {

		if (this.hasCustomName()) {
			if (!this.getCustomNameTag().equals(player.getName())) return EnumActionResult.FAIL;
		}

		float h = this.getHealth();
		if (h <= 0) return EnumActionResult.FAIL;

		if (world.isRemote) return EnumActionResult.SUCCESS;

		float f = this.getMaxHealth() - h;

		this.playSound(this.getDeathSound(), 1.0F, 1.0F);
		ItemStack stack = new ItemStack(ESObjects.ITEMS.SCAPEGOAT, 1, MathHelper.clamp((int) f, 0, 64));
		if (itemNBT != null) stack.setTagCompound(itemNBT);
		this.entityDropItem(stack, 0);
		this.setDead();

		return EnumActionResult.SUCCESS;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(64);
	}

	@Override
	public boolean isPotionApplicable(PotionEffect potioneffectIn) {
		Potion potion = potioneffectIn.getPotion();
		return potion == MobEffects.POISON;
	}

	@Override
	public void addVelocity(double x, double y, double z) {

	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source == DamageSource.FALL) return false;
		if (source == DamageSource.ANVIL) return super.attackEntityFrom(source, this.getHealth() * 2);
		if (DamageHelper.isRuleDamage(source)) return super.attackEntityFrom(source, amount);
		float finalDamage = amount;
		Entity entity = source.getTrueSource();
		if (entity == null) finalDamage = Math.min(1, amount);
		else if (!entity.isNonBoss()) {
			if (rand.nextInt(3) == 0) finalDamage = amount;
			else finalDamage = Math.min(8, amount);
		} else if (source.isMagicDamage()) finalDamage = Math.min(8, amount / 2);
		else finalDamage = Math.min(1, amount);
		return super.attackEntityFrom(source, finalDamage);
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
		if (!this.isInWater()) strength = strength * 0.02f;
		super.knockBack(entityIn, strength, xRatio, zRatio);
	}

}
