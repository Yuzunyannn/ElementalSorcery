package yuzunyannn.elementalsorcery.item.crystal;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.block.env.BlockGoatGoldBrick;
import yuzunyannn.elementalsorcery.item.IItemStronger;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class ItemScarletCrystal extends ItemCrystal implements IItemStronger {

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
		entityItem.dropItem(ESObjects.ITEMS.MAGIC_STONE, RandomHelper.randomRange(1 * count, 3 * count));
		entityItem.dropItem(ESObjects.ITEMS.MAGIC_PIECE, RandomHelper.randomRange(2 * count, 8 * count));
		entityItem.setDead();
		// 特效
		FireworkEffect.spawn(entityItem.world, entityItem.getPositionVector().add(0, entityItem.height, 0), 0, 1, 0.1f,
				new int[] { 0x760e05, 0xd4584d }, TileMDBase.PARTICLE_COLOR_FADE);
		return false;
	}

	public static ItemStack create(float fre) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("fre", fre);
		ItemStack stack = new ItemStack(ESObjects.ITEMS.SCARLET_CRYSTAL);
		stack.setTagCompound(nbt);
		return stack;
	}

	@Override
	public void onProduced(ItemStack stack, @Nullable IWorldObject producer) {

		BlockPos pos;
		if (producer != null) pos = producer.getPosition();
		else {
			Random rand = RandomHelper.rand;
			pos = new BlockPos(rand.nextInt(), rand.nextInt(), rand.nextInt());
		}
		
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		int n = (x ^ z * 13099 ^ y * 29063) & 0x7fffffff;
		float fre = (n % 1000) / 10f;

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("fre", fre);
		stack.setTagCompound(nbt);
	}
}
