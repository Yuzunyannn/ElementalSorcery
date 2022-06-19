package yuzunyannn.elementalsorcery.elf.research;

import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicZombie;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;

public class Researcher implements INBTSerializable<NBTTagCompound> {

	public static final Random rand = new Random();

	public static void onDestoryBlock(EntityPlayer player, IBlockState state) {
		if (rand.nextFloat() > 0.0075f) return;
		Block block = state.getBlock();
		if (!state.isFullBlock() || !state.isFullCube()) return;
		if (block instanceof ITileEntityProvider) {
			if (rand.nextBoolean()) researchSP(player, Topics.ENGINE);
		} else {
			if (block.getRegistryName().getPath().indexOf("ender") != -1) researchSP(player, Topics.ENDER);
			else researchSP(player, Topics.NATURAL);
		}
	}

	public static void onAttackWithEntity(EntityPlayer player, Entity target) {
		if (rand.nextFloat() > 0.01f) return;
		if (!(target instanceof EntityLivingBase)) return;
		researchSP(player, Topics.BIOLOGY);
		if (target instanceof EntityEnderman) if (rand.nextBoolean()) researchSP(player, Topics.ENDER);
		else if (target instanceof EntityDragon) researchSP(player, Topics.ENDER);
		else if (target instanceof EntityRelicZombie) researchSP(player, Topics.STRUCT);
	}

	public static void onInteractWithEntity(EntityPlayer player, Entity entity, EnumHand hand) {
		if (rand.nextFloat() > 0.01f) return;
		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) return;
		if (stack.getItem() == Items.SHEARS) {
			if (entity instanceof IShearable) {
				if (((IShearable) entity).isShearable(stack, player.world, entity.getPosition()))
					researchSP(player, Topics.BIOLOGY);
			}
		} else if (entity instanceof EntityAnimal) {
			EntityAnimal animal = (EntityAnimal) entity;
			if (animal.isBreedingItem(stack)) {
				if (animal.isChild()) researchSP(player, Topics.BIOLOGY);
				else if (!animal.isInLove() && animal.getGrowingAge() == 0) researchSP(player, Topics.BIOLOGY);
			}
		}
	}

	private static void researchSP(EntityLivingBase player, String topic) {
		Researcher researcher = new Researcher(player);
		int point = researcher.get(topic);
		if (point > 50) {
			if (rand.nextFloat() > 2f / point) return;
		} else if (rand.nextFloat() > 10f / point) return;
		researcher.grow(topic, 1);
		researcher.save(player);
		if (player instanceof EntityPlayerMP) ItemAncientPaper.sendTopicGrowMessage((EntityPlayerMP) player, topic);
	}

	public static void research(EntityLivingBase player, String topic, int count) {
		Researcher researcher = new Researcher(player);
		researcher.grow(topic, count);
		researcher.save(player);
		if (player instanceof EntityPlayerMP) ItemAncientPaper.sendTopicGrowMessage((EntityPlayerMP) player, topic);
	}

	protected NBTTagCompound map;

	public Researcher() {
		this(new NBTTagCompound());
	}

	public Researcher(EntityLivingBase player) {
		this(EventServer.getPlayerNBT(player).getCompoundTag("knowPoint"));
	}

	public Researcher(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	public int get(String key) {
		return map.getInteger(key);
	}

	public void shrink(String key, int point) {
		map.setInteger(key, Math.max(this.get(key) - point, 0));
	}

	public void grow(String key, int point) {
		map.setInteger(key, this.get(key) + point);
	}

	public Set<String> keySet() {
		return map.getKeySet();
	}

	public void save(EntityLivingBase player) {
		NBTTagCompound nbt = EventServer.getPlayerNBT(player);
		nbt.setTag("knowPoint", map);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return map;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		map = nbt.copy();
	}

}
