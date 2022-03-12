package yuzunyannn.elementalsorcery.item.crystal;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.block.BlockGoatGoldBrick;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.FireworkEffect;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class ItemScarletCrystal extends ItemCrystal {

	public ItemScarletCrystal() {
		super("scarletCrystal", 50f, 0xa7271c);
	}

	@Override
	public float getFrequency(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null && nbt.hasKey("fre", NBTTag.TAG_NUMBER)) return nbt.getFloat("fre");
		return super.getFrequency(stack);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.world.isRemote) return false;
		if (!entityItem.isWet()) {
			BlockGoatGoldBrick.onScarletCrystalUpdate(entityItem);
			return false;
		}
		int count = entityItem.getItem().getCount();
		entityItem.dropItem(ESInit.ITEMS.MAGIC_STONE, RandomHelper.randomRange(1 * count, 3 * count));
		entityItem.dropItem(ESInit.ITEMS.MAGIC_PIECE, RandomHelper.randomRange(2 * count, 8 * count));
		entityItem.setDead();
		// 特效
		FireworkEffect.spawn(entityItem.world, entityItem.getPositionVector().add(0, entityItem.height, 0), 0, 1,
				0.1f, new int[] { 0x760e05, 0xd4584d }, TileMDBase.PARTICLE_COLOR_FADE);
		return false;
	}

	public static ItemStack create(float fre) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("fre", fre);
		ItemStack stack = new ItemStack(ESInit.ITEMS.SCARLET_CRYSTAL);
		stack.setTagCompound(nbt);
		return stack;
	}

}
