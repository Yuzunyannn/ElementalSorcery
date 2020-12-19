package yuzunyannn.elementalsorcery.summon;

import static net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SummonRecipeZombieCage extends SummonRecipe {

	public SummonRecipeZombieCage() {
		this.setCost(64);
	}

	public EntityEntry getEntityEntry(ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.ROTTEN_FLESH) return ENTITIES.getValue(new ResourceLocation("minecraft", "zombie"));
		if (item == Items.BONE) return ENTITIES.getValue(new ResourceLocation("minecraft", "skeleton"));
		if (item == Items.SPIDER_EYE) return ENTITIES.getValue(new ResourceLocation("minecraft", "spider"));
		if (item == Items.ENDER_PEARL) return ENTITIES.getValue(new ResourceLocation("minecraft", "enderman"));
		if (item == Items.SKULL) {
			switch (stack.getMetadata()) {
			case 0:
				return ENTITIES.getValue(new ResourceLocation("minecraft", "skeleton"));
			case 2:
				return ENTITIES.getValue(new ResourceLocation("minecraft", "zombie"));
			case 4:
				return ENTITIES.getValue(new ResourceLocation("minecraft", "creeper"));
			}
		}
		if (item == Items.SPAWN_EGG) {
			ResourceLocation id = ItemMonsterPlacer.getNamedIdFrom(stack);
			if (id == null) return null;
			return ForgeRegistries.ENTITIES.getValue(id);
		}
		return null;
	}

	private int getColor(EntityEntry entry) {
		if (entry == null) return this.color;
		Class<? extends Entity> cls = entry.getEntityClass();
		if (cls == EntityZombie.class) return 0x192b13;
		return entry.getEgg().primaryColor;
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
		if (entry == null) return new SummonZombieCage(world, pos);
		return new SummonZombieCage(world, pos, getColor(entry), entry.getEntityClass());
	}

}
