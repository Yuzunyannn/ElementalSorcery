package yuzunyannn.elementalsorcery.item.crystal;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class ItemNatureCrystal extends ItemCrystal {

	public ItemNatureCrystal() {
		super("natureCrystal", 24.99f, 0xb26e0c);
		this.setMaxStackSize(1);
	}

	/** 获取数据 */
	static public NBTTagCompound getData(ItemStack natureCrystal, boolean all) {
		if (natureCrystal.isEmpty()) return null;
		return natureCrystal.getSubCompound("natureData");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		NBTTagCompound nbt = stack.getSubCompound("natureData");
		if (nbt == null || !NBTHelper.hasPos(nbt, "pos")) return;

		BlockPos pos = NBTHelper.getBlockPos(nbt, "pos");
		int wrold = nbt.getInteger("world");
		String biome = nbt.getString("biome");
		float rainfall = nbt.getFloat("rainfall");
		boolean elfTree = nbt.getBoolean("elfTree");
		tooltip.add(TextFormatting.GREEN + I18n.format("info.select.axis", pos.getX(), pos.getY(), pos.getZ()));
		tooltip.add(TextFormatting.GREEN + I18n.format("info.dimension.id", wrold) + " :: " + biome);
		tooltip.add(TextFormatting.GREEN + I18n.format("info.rainfall", rainfall));
		if (elfTree) tooltip.add(TextFormatting.GREEN + I18n.format("info.can.grow.elf.edifice"));

		if (!nbt.hasKey("ore")) return;
		String archi = nbt.getString("archi");
		if (archi == null) return;
		if (!archi.isEmpty()) tooltip.add(TextFormatting.GREEN + I18n.format("info.around.have", archi));

		NBTTagCompound ore = nbt.getCompoundTag("ore");
		if (ore == null || ore.hasNoTags()) return;
		StringBuilder builder = new StringBuilder();
		for (String id : ore.getKeySet()) {
			Item item = Item.getByNameOrId(id);
			if (item == null) continue;
			NBTTagCompound data = ore.getCompoundTag(id);
			ItemStack o = new ItemStack(item, 1, data.getInteger("meta"));
			builder.append(I18n.format(o.getUnlocalizedName() + ".name"));
			builder.append("X").append(data.getInteger("count")).append(' ');
		}
		tooltip.add(TextFormatting.GREEN + I18n.format("info.mineral.deposits", builder.toString()));
	}
}
