package yuzunyannn.elementalsorcery.elf.pro;

import java.util.List;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireBall;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElement;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoireInfo;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;

public class ElfProfessionMaster extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESObjects.ITEMS.GRIMOIRE));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0);// 不能掉书
	}

	@Override
	public boolean canEquip(EntityElfBase elf, ItemStack stack, EntityEquipmentSlot slot) {
		return slot != EntityEquipmentSlot.MAINHAND;
	}

	@Override
	public float getAttackDistance() {
		return 32;
	}

	@Override
	public boolean canDespawn(EntityElfBase elf) {
		return true;
	}

	@Override
	public boolean attackEntity(EntityElfBase elf, Entity target) {
		float dis = (float) target.getPositionVector().distanceTo(elf.getPositionVector());
		if (dis <= 5) {
			BlockPos pos = elf.getRandomTeleportPos(4, 6, 4, elf.getPosition());
			if (pos != null) elf.teleportTo(target, pos);
			else {
				Vec3d v3d = target.getPositionVector().subtract(elf.getPositionVector()).normalize();
				target.motionX = v3d.x * 3.5;
				target.motionZ = v3d.z * 3.5;
			}
		} else {
			World world = elf.world;
			int what = elf.getRNG().nextInt(5);
			switch (what) {
			case 0:
				Entity entity = new EntityLightningBolt(world, target.posX, target.posY, target.posZ, false);
				world.addWeatherEffect(entity);
				this.tryBlessing(elf, target.getPosition(), 4);
				break;
			case 1:
				world.createExplosion(null, target.posX, target.posY, target.posZ, 2, false);
				this.tryBlessing(elf, target.getPosition(), 3);
				break;
			case 2:
				BlockPos pos = target.getPosition().up();
				if (world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
					IBlockState state = Blocks.LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL, 15);
					world.setBlockState(pos, state);
					world.neighborChanged(pos, state.getBlock(), pos);
					this.tryBlessing(elf, target.getPosition(), 3);
				}
				break;
			case 3:
				if (target instanceof EntityLivingBase) {
					EntityLivingBase base = (EntityLivingBase) target;
					base.addPotionEffect(new PotionEffect(MobEffects.WITHER, 20 * 10, 1));
					base.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 20 * 10, 1));
				}
				break;
			case 4: {
				Vec3d tar = target.getPositionVector()
						.subtract(elf.getPositionVector().add(0, elf.getEyeHeight(), 0));
				MantraFireBall.fire(world, elf, tar, 28, true);

			}
				break;
			}
		}
		if (target instanceof EntityLivingBase) ((EntityLivingBase) target).setRevengeTarget(elf);
		return true;
	}

	private void tryBlessing(EntityElfBase elf, BlockPos pos, int size) {
		AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() - size, pos.getY() - size, pos.getZ() - size,
				pos.getX() + size, pos.getY() + size, pos.getZ() + size);
		List<EntityElfBase> list = elf.world.getEntitiesWithinAABB(EntityElfBase.class, aabb);
		if (list.isEmpty()) return;
		for (EntityElfBase e : list) {
			e.extinguish();
			e.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20 * 5, 2));
			e.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 5, 4));
			e.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 20));
		}
	}

	@Override
	public Float attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		super.attackedFrom(elf, source, amount);
		// 远程攻击吸引
		if (DamageHelper.isRangedDamage(source)) {
			if (source.getTrueSource() instanceof EntityLivingBase) {
				BlockPos pos = elf.getRandomTeleportPos(54, 2, 4, source.getTrueSource().getPosition());
				if (pos == null) return null;
				elf.teleportTo(elf, pos);
				return Math.max(amount * 0.5f, 5);
			}
		} else {
			// 回复
			if (elf.getRNG().nextInt(4) == 0) {
				elf.extinguish();
				elf.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20 * 10, 1));
				elf.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 * 10, 2));
				elf.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 10, 3));
				elf.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 10));
			}

			boolean teleportFlag = false;
			if (source.getTrueSource() != null) {
				// 人太多逃跑
				AxisAlignedBB aabb = new AxisAlignedBB(elf.posX - 2, elf.posY - 2, elf.posZ - 2, elf.posX + 2,
						elf.posY + 2, elf.posZ + 2);
				List<EntityMob> list = elf.world.getEntitiesWithinAABB(EntityMob.class, aabb);
				teleportFlag |= list.size() > 3;
			}
			teleportFlag |= amount >= 10;
			if (teleportFlag) {
				BlockPos pos = elf.getRandomTeleportPos(5, 4, 4, elf.getPosition());
				elf.teleportTo(elf, pos);
			}
			// 荒废创造模式
			if (!elf.world.isRemote) {
				if (source.getTrueSource() instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP) source.getTrueSource();
					if (player.isCreative() && elf.getRNG().nextInt(3) == 0) {
						ESCriteriaTriggers.NO_CREATIVE.trigger(player);
						player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_FIREWORK_LARGE_BLAST,
								SoundCategory.VOICE, 1.0f, 0);
						player.setGameType(GameType.SURVIVAL);
						NBTTagCompound nbt = FireworkEffect.fastNBT(0, 3, 0.375f, TileMDBase.PARTICLE_COLOR,
								TileMDBase.PARTICLE_COLOR_FADE);
						Effects.spawnEffect(elf.world, Effects.FIREWROK, elf.getPositionVector().add(0, 1, 0),
								nbt);
					}
				}
			}
		}
		return Math.max(amount * 0.5f, 5);
	}

	@SideOnly(Side.CLIENT)
	protected void showEffect(EntityElfBase elf) {
		if (Math.random() < 0.3) {
			Vec3d v3d = elf.getPositionVector();
			v3d = v3d.add(Math.random() * 2 - 1, Math.random() * 0.4f - 0.2f, Math.random() * 2 - 1);
			EffectElement effect = new EffectElement(elf.world, v3d.x, v3d.y, v3d.z);
			effect.setColor((float) Math.random(), (float) Math.random(), (float) Math.random());
			EffectElement.addEffect(effect);
		}
	}

	@Override
	public void tick(EntityElfBase elf) {
		if (elf.world.isRemote) {
			this.tickClient(elf);
			return;
		}
		if (elf.motionY < 0) {
			elf.motionY *= 0.75;
			elf.fallDistance *= 0.75;
		}
		NBTTagCompound nbt = elf.getTempNBT();
		boolean open = nbt.getBoolean("isOpen");
		boolean isOpen = elf.getAttackTarget() != null;
		if (open != isOpen) {
			if (isOpen) {
				elf.setFlyMode(true);
				elf.setActiveHand(EnumHand.MAIN_HAND);
				nbt.setBoolean("isOpen", isOpen);
			} else {
				elf.setFlyMode(false);
				elf.resetActiveHand();
				nbt.setBoolean("isOpen", isOpen);
			}
		} else if (isOpen) {
			if (!elf.world.isAirBlock(elf.getPosition().down(2))) elf.motionY += 0.1;
		}
	}

	@SideOnly(Side.CLIENT)
	public void tickClient(EntityElfBase elf) {
		// 展示书效果
		ItemStack stack = elf.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (grimoire == null) return;
		RenderItemGrimoireInfo renderInfo = grimoire.getRenderInfo();
		NBTTagCompound nbt = elf.getTempNBT();
		boolean open = nbt.getBoolean("isOpen");
		boolean isOpen = elf.isHandActive();
		if (open != isOpen) {
			if (isOpen) {
				renderInfo.reset();
				renderInfo.open();
				nbt.setBoolean("isOpen", isOpen);
			} else {
				renderInfo.close();
				if (renderInfo.update()) {
					renderInfo.reset();
					nbt.setBoolean("isOpen", isOpen);
				}
			}
		} else if (isOpen) {
			renderInfo.update();
			showEffect(elf);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_MASTER;
	}
}
