package yuzunyannn.elementalsorcery.item.crystal;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.entity.EntityParticleEffect;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class ItemMagicalCrystal extends ItemCrystal {

	public ItemMagicalCrystal() {
		super("magicalCrystal", 16.5f, 0x7d7db3);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.world.isRemote) return false;
		if (RandomHelper.rand.nextInt(40) == 0) {
			final float size = 0.5f;
			AxisAlignedBB aabb = new AxisAlignedBB(entityItem.posX - size, entityItem.posY - size,
					entityItem.posZ - size, entityItem.posX + size, entityItem.posY + size, entityItem.posZ + size);
			// 寻找青金石
			List<EntityItem> list = entityItem.world.getEntitiesWithinAABB(EntityItem.class, aabb);
			EntityItem lapis = null;
			for (EntityItem ei : list) {
				ItemStack stack = ei.getItem();
				if (stack.getItem() == Items.DYE && stack.getMetadata() == 4) {
					lapis = ei;
					break;
				}
			}
			if (lapis == null) return false;
			// 寻找岩浆
			BlockPos target = BlockHelper.tryFind(entityItem.world, Blocks.LAVA.getDefaultState(),
					entityItem.getPosition(), 6, 3, 1);
			if (target == null) return false;
			// 特效
			NBTTagCompound nbt = EntityParticleEffect.fastNBT(0, 2, 0.2f, new int[] { 0x1660a8, 0x0a3e70 },
					new int[] { 0x5ea7e6 });
			EntityParticleEffect.spawnParticleEffect(entityItem.world, entityItem.getPositionVector(), nbt);
			// 生成
			entityItem.getItem().shrink(1);
			lapis.getItem().shrink(1);
			entityItem.world.setBlockToAir(target);
			Block.spawnAsEntity(entityItem.world, entityItem.getPosition(),
					new ItemStack(ESInitInstance.ITEMS.AZURE_CRYSTAL));
		}
		return false;
	}
}
