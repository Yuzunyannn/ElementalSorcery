package yuzunyannn.elementalsorcery.summon.recipe;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.summon.Summon;
import yuzunyannn.elementalsorcery.summon.SummonMob;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class SummonRecipeMob extends SummonRecipe {

	public static ItemStack createKeepsake(Class<? extends EntityLivingBase> clazz, int count, @Nullable Entity target,
			boolean isChild) {
		ItemStack keepsake = new ItemStack(ESObjects.ITEMS.KYANITE);
		NBTTagCompound nbt = keepsake.getOrCreateSubCompound("SummonMob");
		ResourceLocation id = EntityList.getKey(clazz);
		if (id != null) nbt.setString("id", id.toString());
		nbt.setInteger("count", count);
		if (target != null) nbt.setUniqueId("target", target.getUniqueID());
		if (isChild) nbt.setBoolean("child", true);
		return keepsake;
	}

	public SummonRecipeMob() {
		this.setCost(16);
	}

	public EntityEntry getEntityEntry(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("SummonMob");
		if (nbt == null) return null;
		if (!nbt.hasKey("id", NBTTag.TAG_STRING)) return null;
		ResourceLocation id = TextHelper.toMCResourceLocation(nbt.getString("id"));
		return ForgeRegistries.ENTITIES.getValue(id);
	}

	private int getColor(EntityEntry entry) {
		if (entry == null) return this.color;
		Class<? extends Entity> cls = entry.getEntityClass();
		if (cls == EntityZombie.class) return 0x6b0e0e;
		return entry.getEgg() == null ? 0x6b0e0e : entry.getEgg().primaryColor;
	}

	@Override
	public int getColor(ItemStack keepsake) {
		EntityEntry entry = getEntityEntry(keepsake);
		if (entry == null) return this.color;
		return getColor(entry);
	}

	@Override
	public boolean canBeKeepsake(ItemStack keepsake, World world, BlockPos pos) {
		return getEntityEntry(keepsake) != null;
	}

	@Override
	public Summon createSummon(ItemStack keepsake, World world, BlockPos pos) {
		EntityEntry entry = this.getEntityEntry(keepsake);
		if (entry == null) return new SummonMob(world, pos);

		NBTTagCompound nbt = keepsake.getSubCompound("SummonMob");
		return new SummonMob(world, pos, getColor(entry), entry.getEntityClass(), Math.max(1, nbt.getInteger("count")))
				.setAttackTargetUUID(nbt.getUniqueId("target")).setChild(nbt.getBoolean("child"));
	}

}
