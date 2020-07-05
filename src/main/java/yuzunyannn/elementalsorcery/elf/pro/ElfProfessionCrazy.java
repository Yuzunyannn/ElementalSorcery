package yuzunyannn.elementalsorcery.elf.pro;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;

public class ElfProfessionCrazy extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Blocks.RED_FLOWER));
	}

	@Override
	public List<EntityLivingBase> getAttackTarget(EntityElfBase elf) {
		final int size = 4;
		AxisAlignedBB aabb = new AxisAlignedBB(elf.posX - size, elf.posY - size, elf.posZ - size, elf.posX + size,
				elf.posY + size, elf.posZ + size);
		List<EntityLivingBase> list = elf.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, (entity) -> {
			return entity instanceof EntityElfBase == false;
		});
		return list;
	}

	@Override
	public int attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		if (elf.getRNG().nextInt(5) == 0 && source.getTrueSource() instanceof EntityLivingBase) {
			final int size = 16;
			AxisAlignedBB aabb = new AxisAlignedBB(elf.posX - size, elf.posY - size, elf.posZ - size, elf.posX + size,
					elf.posY + size, elf.posZ + size);
			List<EntityElf> list = elf.world.getEntitiesWithinAABB(EntityElf.class, aabb);
			for (EntityElf e : list)
				if (e.getRevengeTarget() == null) e.setRevengeTarget((EntityLivingBase) source.getTrueSource());
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_CRAZY;
	}
}
