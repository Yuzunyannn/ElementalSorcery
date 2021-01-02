package yuzunyannn.elementalsorcery.elf.quest;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.explore.Explores;
import yuzunyannn.elementalsorcery.item.crystal.ItemNatureCrystal;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class QuestConditionNeedExplore extends QuestCondition {

	protected String biome;
	protected String structure;
	protected ItemStack block = ItemStack.EMPTY;

	public QuestConditionNeedExplore needBiome(Biome biome) {
		if (biome == null) return this;
		this.biome = biome.getRegistryName().toString();
		return this;
	}

	public QuestConditionNeedExplore needBlock(ItemStack block) {
		this.block = block;
		return this;
	}

	public QuestConditionNeedExplore needStructure(String structure) {
		this.structure = structure;
		return this;
	}

	// 检查参数是否都合法
	public QuestConditionNeedExplore check() {
		if (this.biome == null) {
			if (this.structure == null || this.block.isEmpty()) throw new RuntimeException("探索条件异常！");
		}
		return this;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		if (biome != null) nbt.setString("biome", biome);
		if (!block.isEmpty()) nbt.setTag("block", block.serializeNBT());
		if (structure != null) nbt.setString("structure", structure);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("biome", NBTTag.TAG_STRING)) biome = nbt.getString("biome");
		if (nbt.hasKey("block", NBTTag.TAG_COMPOUND)) block = new ItemStack(nbt.getCompoundTag("block"));
		if (nbt.hasKey("structure", NBTTag.TAG_STRING)) structure = nbt.getString("structure");
	}

	boolean checkResult = false;

	@Override
	public boolean check(Quest task, EntityPlayer player) {
		checkResult = false;
		InventoryPlayer inventory = player.inventory;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack origin = inventory.getStackInSlot(i);
			if (origin.isEmpty()) continue;
			NBTTagCompound data = ItemNatureCrystal.getData(origin);
			if (data == null) continue;
			if (this.check(data)) {
				checkResult = true;
				break;
			}
		}
		return checkResult;
	}

	private boolean check(NBTTagCompound data) {
		if (this.biome == null) {

			String str = Explores.STRUCTURE_FIND.getStructure(data);
			if (!str.equals(structure)) return false;

			ItemStack stack = Explores.BASE.getBlockAsItem(data);
			if (stack.isEmpty() || !stack.isItemEqual(block)) return false;

		} else {
			String biome = Explores.BASE.getBiome(data);
			if (!biome.equals(this.biome)) return false;
			if (!this.block.isEmpty()) {
				ItemStack stack = Explores.BASE.getBlockAsItem(data);
				if (stack.isEmpty() || !stack.isItemEqual(block)) return false;
			}
			if (structure != null) {
				String str = Explores.STRUCTURE_FIND.getStructure(data);
				if (!str.equals(structure)) return false;
			}
		}
		return true;
	}

	@Override
	public void finish(Quest task, EntityPlayer player) {
		InventoryPlayer inventory = player.inventory;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack origin = inventory.getStackInSlot(i);
			if (origin.isEmpty()) continue;
			NBTTagCompound data = ItemNatureCrystal.getData(origin);
			if (data == null) continue;
			if (this.check(data)) {
				origin.shrink(1);
				break;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDescribe(Quest quest, EntityLivingBase player, boolean dynamic) {
		StringBuilder builder = new StringBuilder();

		if (dynamic) {
			if (checkResult) builder.append(TextFormatting.GREEN.toString());
			else builder.append(TextFormatting.DARK_RED.toString());
		}

		if (this.biome == null) {
			String blockName = I18n.format(block.getUnlocalizedName() + ".name");
			builder.append(I18n.format("quest.explore.4", structure, blockName));
		} else {
			ResourceLocation biomeId = new ResourceLocation(biome);
			String biomeName = biomeId.getResourcePath();

			if (block.isEmpty()) {
				builder.append(I18n.format("quest.explore.1", biomeName));
			} else {
				String blockName = I18n.format(block.getUnlocalizedName() + ".name");
				if (structure != null) builder.append(I18n.format("quest.explore.3", biomeName, structure, blockName));
				else builder.append(I18n.format("quest.explore.2", biomeName, blockName));
			}
		}
		if (dynamic) builder.append(TextFormatting.RESET.toString());
		return builder.toString();
	}

}
