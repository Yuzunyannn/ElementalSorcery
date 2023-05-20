package yuzunyannn.elementalsorcery.item;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.crafting.IToElementItem;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.crafting.element.ToElementInfoStatic;
import yuzunyannn.elementalsorcery.elf.research.AncientPaper;
import yuzunyannn.elementalsorcery.elf.research.KnowledgeType;
import yuzunyannn.elementalsorcery.elf.research.Researcher;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraCommon;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class ItemAncientPaper extends Item implements IToElementItem {

	@Config
	static public float UNSCRAMBLE_COEFFICIENT = 1;
	@Config
	static public float TIRED_BASIC_PROBABILITY = 0.5f;
	@Config
	static public float RESEARCH_TOPIC_GROW_COEFFICIENT = 1f;

	static public ItemStack createPaper(Mantra mantra, float progress) {
		ItemStack stack = new ItemStack(ESObjects.ITEMS.ANCIENT_PAPER, 1, 0);
		AncientPaper ap = new AncientPaper();
		ap.setMantra(mantra);
		if (progress >= 1) ap.setStart(0).setEnd(100);
		else {
			float r = RandomHelper.rand.nextFloat();
			if (r - progress * 0.5f < 0) r = 0;
			else if (r + progress * 0.5f > 1) r = 1 - progress;
			else r = r - progress * 0.5f;
			ap.setStart((int) (r * 100)).setEnd((int) ((r + progress) * 100));
		}
		ap.saveState(stack);
		return stack;
	}

	public ItemAncientPaper() {
		this.setHasSubtypes(true);
		this.setTranslationKey("ancientPaper");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;

		for (Mantra m : Mantra.REGISTRY) {
			AncientPaper ap = new AncientPaper();
			EnumType type = EnumType.NORMAL;
			if (m instanceof MantraCommon) type = ((MantraCommon) m).getMantraSubItemType();
			if (type == null) continue;
			ItemStack stack = new ItemStack(this, 1, type.getMetadata());
			ap.setMantra(m).setStart(0).setEnd(100);
			ap.saveState(stack);
			items.add(stack);
		}
		items.add(new ItemStack(this, 1, EnumType.NEW.getMetadata()));
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return "item.ancientPaper." + EnumType.byMetadata(stack.getMetadata()).getName();
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	static public enum EnumType implements IStringSerializable {
		NORMAL("normal"),
		UNSCRAMBLE("unscramble"),
		NEW("new"),
		NEW_WRITTEN("newWritten");

		final String name;

		EnumType(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMetadata() {
			return this.ordinal();
		}

		static public EnumType byMetadata(int meta) {
			return EnumType.values()[0x3 & meta];
		}
	}

	public static void eliminateFatigue(EntityPlayer player, boolean showInfo) {
		NBTTagCompound playerData = ESData.getPlayerNBT(player);
		if (!playerData.hasKey("unsNext")) return;
		playerData.removeTag("unsNext");
		if (showInfo) player.sendMessage(new TextComponentTranslation("info.not.atigue")
				.setStyle(new Style().setColor(TextFormatting.GOLD).setBold(true)));

	}

	/** 进行一次解读 */
	protected void doUnscramble(AncientPaper ap, World world, EntityPlayer player, EnumHand handIn) {
		if (world.isRemote) return;
		NBTTagCompound playerData = ESData.getPlayerNBT(player);
		// 获取增益
		PotionEffect effect = player.getActivePotionEffect(ESObjects.POTIONS.ENTHUSIASTIC_STUDY);
		int amplifier = effect == null ? 0 : (effect.getAmplifier() + 1);
		// 上次的时间
		long nextTime = playerData.getLong("unsNext");
		if (nextTime > world.getWorldTime() && !player.isCreative() && amplifier == 0) {
			player.sendMessage(new TextComponentTranslation("info.can.not.unscramble")
					.setStyle(new Style().setColor(TextFormatting.GRAY).setBold(true)));
			return;
		}
		Random rand = world.rand;
		// 一些前置处理
		EnumHand offHand = handIn == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		int noteEnergy = ItemUnscrambleNote.getNoteEnergy(player.getHeldItem(offHand), player);
		float energy = Math.max(playerData.getFloat("unsEnergy"), 1);
		boolean setTired = rand.nextFloat() < TIRED_BASIC_PROBABILITY / energy;
		if (setTired) {
			if (amplifier == 0) playerData.setLong("unsNext", world.getWorldTime() + 12000);
			else player.sendMessage(new TextComponentTranslation("info.want.not.unscramble")
					.setStyle(new Style().setColor(TextFormatting.GRAY)));
		}
		// 增加研究力
		playerData.setFloat("unsEnergy", Math.min(energy + 0.1f * (1 + amplifier / 2), 3));
		ItemUnscrambleNote.growNoteEnergy(player.getHeldItem(offHand), player, rand.nextInt(3) + amplifier, false);
		// 增加进度
		float originProgress = ap.getProgress();
		float grow = rand.nextFloat() * 0.04f + 0.01f + rand.nextFloat() * noteEnergy / 1000;
		grow = grow * UNSCRAMBLE_COEFFICIENT * (1 + amplifier / 5);
		ap.setProgress(originProgress + grow);
		if (player instanceof EntityPlayerMP)
			ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "unscramble:once");
		if (!ap.hasType()) return;
		// 增加研究点数
		KnowledgeType type = ap.getType();
		List<Entry<String, Integer>> topics = type.getTopics();
		if (topics == null) return;
		Entry<String, Integer> topic = topics.get(rand.nextInt(topics.size()));
		float rCount = topic.getValue() * grow * (ap.getEnd() - ap.getStart()) / 100.0f;
		int count = MathHelper.floor(rCount);
		float expect = (rCount) - MathHelper.floor(rCount);
		count += rand.nextFloat() <= expect ? 1 : 0;
		if (count > 0) Researcher.research(player, topic.getKey(), count);
	}

	/** 开始解读 @return true表示可以 */
	protected boolean startUnscramble(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		EnumHand hand = handIn == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		ItemStack feather = playerIn.getHeldItem(hand);
		if (feather.getItem() != ESObjects.ITEMS.QUILL) return false;
		feather.shrink(1);
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		int meta = stack.getMetadata();
		if (meta == EnumType.NORMAL.getMetadata()) {
			AncientPaper ap = new AncientPaper(stack);
			if (!ap.isLocked()) return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
			if (!startUnscramble(worldIn, playerIn, handIn))
				return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
			stack.setItemDamage(EnumType.UNSCRAMBLE.getMetadata());
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		} else if (meta == EnumType.UNSCRAMBLE.getMetadata()) {
			AncientPaper ap = new AncientPaper(stack);
			doUnscramble(ap, worldIn, playerIn, handIn);
			ap.saveState(stack);
			if (ap.getProgress() >= 1) {
				stack.setItemDamage(EnumType.NORMAL.getMetadata());
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		AncientPaper ap = new AncientPaper(stack);
		if (ap.getProgress() < 1) tooltip.add(TextFormatting.DARK_AQUA + I18n.format("info.ancientPaper.unknow"));
		if (ap.isLocked()) {
			float rogress = ap.getProgress();
			if (rogress > 0 || stack.getMetadata() == EnumType.UNSCRAMBLE.getMetadata())
				tooltip.add(I18n.format("info.ancientPaper.progress", String.format("%.2f%%", rogress * 100)));
			return;
		}
		if (ap.hasMantra()) {
			Mantra m = ap.getMantra();
			String name = m.getDisplayName() + TextFormatting.RESET + TextFormatting.YELLOW;
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.ancientPaper.mantra", name));
		} else if (ap.hasType()) {
			KnowledgeType type = ap.getType();
			String name = I18n.format(type.getTranslationKey() + ".name");
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.ancientPaper.normal", name));
		} else return;
		int start = ap.getStart();
		int end = ap.getEnd();
		if (end - start < 0) return;
		if (end - start >= 100) return;
		tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("info.ancientPaper.record", start, end));
	}

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		ElementStack[] estacks = new ElementStack[2];
		estacks[1] = new ElementStack(ESObjects.ELEMENTS.WOOD, 8, 20);
		estacks[0] = new ElementStack(ESObjects.ELEMENTS.KNOWLEDGE, 4, 28);

		AncientPaper ap = new AncientPaper(stack);
		if (ap.hasType()) {
			KnowledgeType type = ap.getType();
			ElementStack knowledge = type.getKnowledge().copy();
			knowledge.setCount((int) (knowledge.getCount() * (1 - ap.getProgress())));
			estacks[0].grow(knowledge);
		}
		if (ap.hasMantra()) mantra: {
			Mantra mantra = ap.getMantra();
			int rarity = mantra.getRarity(null, null);
			if (rarity < 0) break mantra;
			rarity = MathHelper.clamp(rarity, 10, 200);
			ElementStack knowledge = new ElementStack(ESObjects.ELEMENTS.KNOWLEDGE);
			double power = 28 - MathHelper.sqrt(rarity * 1.45);
			knowledge.setPower(MathHelper.floor(power * power) + 10);
			int count = (110 - rarity / 2) / 3;
			knowledge.setCount(count + (int) (count * 2 * (1 - ap.getProgress())));
			estacks[0].grow(knowledge);
		}
		return ToElementInfoStatic.create(8, estacks);
	}

}
