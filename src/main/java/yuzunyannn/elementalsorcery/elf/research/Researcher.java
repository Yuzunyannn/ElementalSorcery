package yuzunyannn.elementalsorcery.elf.research;

import java.util.Collection;
import java.util.Random;

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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IShearable;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.api.crafting.IResearcher;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicZombie;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

public class Researcher implements IResearcher {

	@Config
	public static int RESEARCH_POINT_TIPS_SHOW_MODE = 1;

	public static final Random rand = new Random();

	public static boolean isPlayerResearchable(EntityPlayer player) {
		NBTTagCompound nbt = ESData.getPlayerNBT(player);
		return nbt.getBoolean(ESData.PLAYER_RESEARCHABLE);
	}

	public static void letPlayerResearchable(EntityPlayer player) {
		NBTTagCompound nbt = ESData.getPlayerNBT(player);
		nbt.setBoolean(ESData.PLAYER_RESEARCHABLE, true);
		Researcher researcher = new Researcher(player);
		Researcher researcherNew = new Researcher();
		boolean hasAny = false;
		for (String topic : researcher.getTopics()) {
			hasAny = true;
			float count = researcher.get(topic);
			researcherNew.grow(topic, count);
			trySendMsg(player, topic, count);
		}
		if (!hasAny) {
			researcherNew.grow(Topics.NATURAL, 1);
			trySendMsg(player, Topics.NATURAL, 1);
		}
		researcherNew.save(player);
	}

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
		float point = researcher.get(topic);
		if (point > 50) {
			if (rand.nextFloat() > 2f / point) return;
		} else if (rand.nextFloat() > 10f / point) return;
		researcher.grow(topic, 1);
		researcher.save(player);
		if (player instanceof EntityPlayerMP) trySendMsg((EntityPlayer) player, topic, 1);
	}

	public static void research(EntityLivingBase player, String topic, float count) {
		Researcher researcher = new Researcher(player);
		researcher.grow(topic, count * ItemAncientPaper.RESEARCH_TOPIC_GROW_COEFFICIENT);
		researcher.save(player);
		if (player instanceof EntityPlayerMP) trySendMsg((EntityPlayer) player, topic, count);
	}

	public static void trySendMsg(EntityPlayer player, String topic, float count) {
		if (!isPlayerResearchable(player)) return;
		if (RESEARCH_POINT_TIPS_SHOW_MODE == 1) sendTopicGrowMessage(player, topic);
		else if (RESEARCH_POINT_TIPS_SHOW_MODE == 2) {
			Researcher researcher = new Researcher(player);
			float point = researcher.get(topic);
			float oldPoint = point - count;
			int d = MathSupporter.digit(point);
			int od = MathSupporter.digit(oldPoint);
			if (d != od) sendTopicGrowMessage(player, topic);
			else {
				double dnum = Math.pow(10, d - 1);
				if (MathHelper.floor(point / dnum) != MathHelper.floor(oldPoint / dnum))
					sendTopicGrowMessage(player, topic);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void sendTopicGrowMessage(EntityPlayer player, String type) {
		String tKey = "topic." + type + ".name";
		ITextComponent typeName;
		if (net.minecraft.util.text.translation.I18n.canTranslate(tKey))
			typeName = new TextComponentTranslation("topic." + type + ".name");
		else typeName = new TextComponentString(type);
		player.sendMessage(new TextComponentTranslation("info.research.increase", typeName)
				.setStyle(new Style().setColor(TextFormatting.YELLOW).setBold(true)));
	}

	protected NBTTagCompound map;

	public Researcher() {
		this(new NBTTagCompound());
	}

	public Researcher(EntityLivingBase player) {
		this(ESData.getPlayerNBT(player).getCompoundTag("knowPoint"));
	}

	public Researcher(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	@Override
	public float get(String topic) {
		return map.getFloat(topic);
	}

	@Override
	public void set(String topic, float count) {
		if (count <= 0) map.removeTag(topic);
		else map.setFloat(topic, count);
	}

	@Override
	public Collection<String> getTopics() {
		return map.getKeySet();
	}

	public void save(EntityLivingBase player) {
		NBTTagCompound nbt = ESData.getPlayerNBT(player);
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
