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
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class ItemMagicalCrystal extends ItemCrystal {

	public ItemMagicalCrystal() {
		super("magicalCrystal", 16.5f, 0x7d7db3);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.world.isRemote) return false;
		if (entityItem.ticksExisted % 80 == 0) tryCraft(entityItem, true);
		return false;
	}

	public static boolean tryCraft(EntityItem magicalCrystal, boolean needLava) {
		final float size = 0.5f;
		AxisAlignedBB aabb = new AxisAlignedBB(magicalCrystal.posX - size, magicalCrystal.posY - size,
				magicalCrystal.posZ - size, magicalCrystal.posX + size, magicalCrystal.posY + size,
				magicalCrystal.posZ + size);
		// 寻找青金石
		List<EntityItem> list = magicalCrystal.world.getEntitiesWithinAABB(EntityItem.class, aabb);
		EntityItem lapis = null;
		for (EntityItem ei : list) {
			ItemStack stack = ei.getItem();
			if (stack.getItem() == Items.DYE && stack.getMetadata() == 4) {
				lapis = ei;
				break;
			}
		}
		if (lapis == null) return false;
		BlockPos lava = null;
		if (needLava) {
			// 寻找岩浆
			lava = BlockHelper.tryFind(magicalCrystal.world, Blocks.LAVA.getDefaultState(),
					magicalCrystal.getPosition(), 6, 3, 1);
			if (lava == null) return false;
		}
		// 特效
		NBTTagCompound nbt = FireworkEffect.fastNBT(0, 2, 0.2f, new int[] { 0x1660a8, 0x0a3e70 },
				new int[] { 0x5ea7e6 });
		Effects.spawnEffect(magicalCrystal.world, Effects.FIREWROK, magicalCrystal.getPositionVector(), nbt);
		// 生成
		magicalCrystal.getItem().shrink(1);
		lapis.getItem().shrink(1);
		if (lava != null) magicalCrystal.world.setBlockToAir(lava);
		Block.spawnAsEntity(magicalCrystal.world, magicalCrystal.getPosition(),
				new ItemStack(ESInit.ITEMS.AZURE_CRYSTAL));
		return true;
	}
}
