package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.tool.ItemSoulWoodSword;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemSoulFragment extends Item {

	public ItemSoulFragment() {
		this.setTranslationKey("soulFragment");
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.ticksExisted % 60 == 0) {
			if (entityItem.onGround) entityItem.motionY = 0.5;
			if (entityItem.world.isRemote) return false;
			tryTwine(entityItem);
		}
		return false;
	}

	public static void tryTwine(EntityItem entityItem) {
		World world = entityItem.world;
		final int size = 1;
		AxisAlignedBB aabb = new AxisAlignedBB(entityItem.posX - size, entityItem.posY - size, entityItem.posZ - size,
				entityItem.posX + size, entityItem.posY + size, entityItem.posZ + size);
		List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, aabb);
		EntityItem woodSword = null;
		EntityItem soulWoodSword = null;
		for (EntityItem ei : list) {
			ItemStack stack = ei.getItem();
			if (stack.getItem() == Items.WOODEN_SWORD) woodSword = ei;
			else if (stack.getItem() == Items.LEATHER) woodSword = ei;
			else if (stack.getItem() == ESInit.ITEMS.SOUL_WOOD_SWORD) soulWoodSword = ei;
			else if (stack.getItem() == ESInit.ITEMS.LIFE_LEATHER && stack.getMetadata() == 0) soulWoodSword = ei;
			if (soulWoodSword != null) break;
		}
		int count = entityItem.getItem().getCount();
		Entity sword = null;
		if (soulWoodSword != null) {
			entityItem.setDead();
			ItemStack stack = soulWoodSword.getItem();
			int meta = stack.getMetadata();
			addSoul(stack, count);
			if (meta != stack.getMetadata()) soulWoodSword.setItem(stack);
			sword = soulWoodSword;
		} else if (woodSword != null) {
			entityItem.setDead();
			ItemStack swSword;
			if (woodSword.getItem().getItem() == Items.LEATHER) swSword = new ItemStack(ESInit.ITEMS.LIFE_LEATHER);
			else swSword = new ItemStack(ESInit.ITEMS.SOUL_WOOD_SWORD);
			swSword.setItemDamage(woodSword.getItem().getItemDamage());
			swSword.setTagCompound(woodSword.getItem().getTagCompound());
			addSoul(swSword, count);
			sword = ItemHelper.dropItem(world, woodSword.getPosition(), swSword);
			woodSword.getItem().shrink(1);
		}
		if (sword != null) {
			NBTTagCompound nbt = FireworkEffect.fastNBT(1, 1, 0.05f, new int[] { 0x3ad2f2, 0x7ef5ff },
					new int[] { 0xe0ffff });
			Effects.spawnEffect(world, Effects.FIREWROK, sword.getPositionVector().add(0, 0.5, 0), nbt);
		}
	}

	public static void addSoul(ItemStack sword, int count) {
		ItemSoulWoodSword.addSoul(sword, count);
		if (sword.getItem() == ESInit.ITEMS.LIFE_LEATHER) ItemLifeLeather.transform(sword);
	}
}
