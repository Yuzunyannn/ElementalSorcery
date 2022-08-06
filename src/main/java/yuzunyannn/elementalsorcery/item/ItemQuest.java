package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.QuestDescribe;
import yuzunyannn.elementalsorcery.elf.quest.QuestStatus;
import yuzunyannn.elementalsorcery.elf.quest.QuestType;

public class ItemQuest extends Item {

	public ItemQuest() {
		this.setTranslationKey("quest");
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!ItemQuest.isQuest(stack)) return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		BlockPos pos = playerIn.getPosition();
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_QUEST, worldIn, pos.getX(), pos.getY(),
				pos.getZ());
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (!ItemQuest.isQuest(stack)) return;
		Quest quest = getQuest(stack);
		QuestType type = quest.getType();
		QuestDescribe describe = type.getDescribe();
		String title = describe.getTitle();
		tooltip.add(TextFormatting.GOLD + I18n.format(title));
		if (quest.getStatus() == QuestStatus.UNDERWAY) tooltip.add(I18n.format("info.underway"));
		else if (quest.getStatus() == QuestStatus.FINISH)
			tooltip.add(TextFormatting.YELLOW + I18n.format("info.complete"));
	}

	public static ItemStack createQuest(Quest quest) {
		ItemStack stack = new ItemStack(ESObjects.ITEMS.QUEST);
		stack.setTagCompound(quest.serializeNBT());
		return stack;
	}

	public static boolean isQuest(ItemStack stack) {
		if (stack.isEmpty()) return false;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return false;
		return nbt.hasKey("status") && nbt.hasKey("type");
	}

	public static Quest getQuest(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		Quest quest = new Quest(nbt);
		return quest;
	}

	public static void setFinish(ItemStack stack) {
		stack.setItemDamage(1);
	}

}
