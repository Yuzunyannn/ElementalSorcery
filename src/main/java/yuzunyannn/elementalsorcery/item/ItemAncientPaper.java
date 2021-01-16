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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.crafting.IToElementItem;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.elf.research.AncientPaper;
import yuzunyannn.elementalsorcery.elf.research.KnowledgeType;
import yuzunyannn.elementalsorcery.elf.research.Researcher;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.init.ESInit;

public class ItemAncientPaper extends Item implements IToElementItem {

	public ItemAncientPaper() {
		this.setHasSubtypes(true);
		this.setUnlocalizedName("ancientPaper");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;

		for (Mantra m : Mantra.REGISTRY) {
			AncientPaper ap = new AncientPaper();
			ItemStack stack = new ItemStack(this, 1, EnumType.NORMAL.getMetadata());
			ap.setMantra(m).setStart(0).setEnd(100);
			ap.saveState(stack);
			items.add(stack);
		}
		items.add(new ItemStack(this, 1, EnumType.NEW.getMetadata()));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
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

	/** 进行一次解读 */
	protected void doUnscramble(AncientPaper ap, World world, EntityPlayer player, EnumHand handIn) {
		if (world.isRemote) return;
		NBTTagCompound playerData = EventServer.getPlayerNBT(player);
		// 上次的时间
		long nextTime = playerData.getLong("unsNext");
		if (nextTime > world.getWorldTime() && !player.isCreative()) {
			player.sendMessage(new TextComponentTranslation("info.want.not.unscramble")
					.setStyle(new Style().setColor(TextFormatting.GRAY).setBold(true)));
			return;
		}
		Random rand = world.rand;
		// 一些前置处理
		EnumHand offHand = handIn == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		int noteEnergy = ItemUnscrambleNote.getNoteEnergy(player.getHeldItem(offHand), player);
		float energy = Math.max(playerData.getFloat("unsEnergy"), 1);
		boolean setTired = rand.nextFloat() < 0.5f / energy;
		if (setTired) playerData.setLong("unsNext", world.getWorldTime() + 12000);
		playerData.setFloat("unsEnergy", Math.min(energy + 0.1f, 3)); // 增加研究力
		ItemUnscrambleNote.growNoteEnergy(player.getHeldItem(offHand), player, rand.nextInt(3), false);
		// 增加进度
		float originProgress = ap.getProgress();
		float grow = rand.nextFloat() * 0.04f + 0.01f + rand.nextFloat() * noteEnergy / 1000;
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
		if (count > 0) {
			Researcher researcher = new Researcher(player);
			researcher.grow(topic.getKey(), count);
			researcher.save(player);
			sendTopicGrowMessage(player, topic.getKey());
		}
	}

	public static void sendTopicGrowMessage(EntityPlayer player, String type) {
		String tKey = "topic." + type + ".name";
		ITextComponent typeName;
		if (net.minecraft.util.text.translation.I18n.canTranslate(tKey))
			typeName = new TextComponentTranslation("topic." + type + ".name");
		else typeName = new TextComponentString(type);
		player.sendMessage(new TextComponentTranslation("info.research.increase", typeName)
				.setStyle(new Style().setColor(TextFormatting.YELLOW).setBold(true)));
	}

	/** 开始解读 @return true表示可以 */
	protected boolean startUnscramble(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		EnumHand hand = handIn == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		ItemStack feather = playerIn.getHeldItem(hand);
		if (feather.getItem() != ESInit.ITEMS.QUILL) return false;
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
		if (ap.isLocked()) {
			tooltip.add(TextFormatting.DARK_AQUA + I18n.format("info.ancientPaper.unknow"));
			float rogress = ap.getProgress();
			if (rogress > 0 || stack.getMetadata() == EnumType.UNSCRAMBLE.getMetadata())
				tooltip.add(I18n.format("info.ancientPaper.progress", String.format("%.2f%%", rogress * 100)));
			return;
		}
		if (ap.hasMantra()) {
			Mantra m = ap.getMantra();
			String name = I18n.format(m.getUnlocalizedName() + ".name");
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.ancientPaper.mantra", name));
		} else if (ap.hasType()) {
			KnowledgeType type = ap.getType();
			String name = I18n.format(type.getUnlocalizedName() + ".name");
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.ancientPaper.normal", name));
		} else return;
		int start = ap.getStart();
		int end = ap.getEnd();
		if (end - start < 0) return;
		if (end - start >= 100) return;
		tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("info.ancientPaper.record", start, end));
	}

	@Override
	public ElementStack[] toElement(ItemStack stack) {
		ElementStack[] estacks = new ElementStack[2];
		estacks[0] = new ElementStack(ESInit.ELEMENTS.WOOD, 8, 20);
		estacks[1] = new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, 4, 30);

		AncientPaper ap = new AncientPaper(stack);
		if (ap.hasType()) {
			KnowledgeType type = ap.getType();
			ElementStack knowledge = type.getKnowledge().copy();
			knowledge.setCount((int) (knowledge.getCount() * (1 - ap.getProgress())));
			estacks[1].grow(knowledge);
		}
		if (ap.hasMantra()) {
			Mantra mantra = ap.getMantra();
			int rarity = mantra.getRarity(null, null);
			rarity = MathHelper.clamp(rarity, 10, 200);
			ElementStack knowledge = new ElementStack(ESInit.ELEMENTS.KNOWLEDGE);
			double power = 20 - MathHelper.sqrt(rarity * 1.45);
			knowledge.setPower(MathHelper.floor(power * power) + 10);
			knowledge.setCount((220 - rarity) / 2);
			estacks[1].grow(knowledge);
		}

		return estacks;
	}

	@Override
	public int complex(ItemStack stack) {
		return 8;
	}

}
