package yuzunyannn.elementalsorcery.entity.elf;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemSpellbook;
import yuzunyannn.elementalsorcery.render.particle.EffectElement;

public class ElfProfessionMaster extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_ELEMENT));
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
	public boolean attackEntity(EntityElfBase elf, Entity target) {
		return elf.attackEntityAsMobMagic(target);
	}

	@Override
	public int attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		// 远程攻击吸引
		if (source instanceof EntityDamageSourceIndirect) {
			if (source.getTrueSource() instanceof EntityLivingBase) {
				BlockPos pos = elf.getRandomTeleportPos(54, 2, 4, source.getTrueSource().getPosition());
				if (pos == null) return 0;
				elf.teleportTo(elf, pos);
				return 1;
			}
		} else if (source.getTrueSource() != null) {
			// 人太多逃跑
			AxisAlignedBB aabb = new AxisAlignedBB(elf.posX - 2, elf.posY - 2, elf.posZ - 2, elf.posX + 2, elf.posY + 2,
					elf.posZ + 2);
			List<EntityMob> list = elf.world.getEntitiesWithinAABB(EntityMob.class, aabb);
			if (list.size() > 3) {
				BlockPos pos = elf.getRandomTeleportPos(5, 4, 4, elf.getPosition());
				elf.teleportTo(elf, pos);
			}
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	protected void showEffect(EntityElfBase elf) {
		if (Math.random() < 0.3) {
			Vec3d v3d = elf.getPositionVector();
			v3d = v3d.addVector(Math.random() * 2 - 1, Math.random() * 0.4f - 0.2f, Math.random() * 2 - 1);
			EffectElement effect = new EffectElement(elf.world, v3d.x, v3d.y, v3d.z);
			effect.setColor((float) Math.random(), (float) Math.random(), (float) Math.random());
			EffectElement.addEffect(effect);
		}
	}

	@Override
	public void tick(EntityElfBase elf) {
		if (elf.world.isRemote) {
			// 展示书效果
			ItemStack stack = elf.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
			Spellbook spbook = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
			if (spbook != null) {
				NBTTagCompound nbt = elf.getTempNBT();
				boolean open = nbt.getBoolean("isOpen");
				boolean isOpen = elf.isHandActive();
				if (open != isOpen) {
					if (isOpen) {
						ItemSpellbook.renderStart(spbook);
						nbt.setBoolean("isOpen", isOpen);
					} else {
						if (ItemSpellbook.renderClose(spbook)) {
							ItemSpellbook.renderEnd(spbook);
							nbt.setBoolean("isOpen", isOpen);
						}
					}
				} else if (isOpen) {
					ItemSpellbook.renderOpen(spbook);
					showEffect(elf);
				}
			}
		} else {
			if (elf.motionY < 0) {
				elf.motionY *= 0.6;
				elf.fallDistance *= 0.6;
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
	}
}
