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
import yuzunyannn.elementalsorcery.util.MultiRets;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class ItemNatureCrystal extends ItemCrystal {

	public ItemNatureCrystal() {
		super("natureCrystal", 24.99f, 0xb26e0c);
		this.setMaxStackSize(1);
	}

	/** 获取数据 */
	static public MultiRets getData(ItemStack natureCrystal, boolean all) {
		if (natureCrystal.isEmpty()) return MultiRets.ret();
		NBTTagCompound nbt = natureCrystal.getSubCompound("natureData");
		if (nbt == null || !NBTHelper.hasPos(nbt, "pos")) return MultiRets.ret();
		BlockPos pos = NBTHelper.getBlockPos(nbt, "pos");
		int dim = nbt.getInteger("world");
		if (!all) return MultiRets.ret(pos, dim);
		String biome = nbt.getString("biome");
		float rainfall = nbt.getFloat("rainfall");
		if (!nbt.hasKey("ore")) return MultiRets.ret(pos, dim, biome, rainfall);
		String archi = nbt.getString("archi");
		NBTTagCompound ore = nbt.getCompoundTag("ore");
		return MultiRets.ret(pos, dim, biome, rainfall, archi, ore);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		MultiRets rets = getData(stack, true);
		if (rets.isEmpty()) return;

		BlockPos pos = rets.get(0, BlockPos.class);
		Integer wrold = rets.get(1, Integer.class);
		String biome = rets.get(2, String.class);
		tooltip.add(TextFormatting.GREEN + I18n.format("info.select.axis", pos.getX(), pos.getY(), pos.getZ()));
		tooltip.add(TextFormatting.GREEN + I18n.format("info.dimension.id", wrold) + " :: " + biome);
		tooltip.add(TextFormatting.GREEN + I18n.format("info.rainfall", rets.get(3, Float.class)));

		String archi = rets.get(4, String.class);
		if (archi == null) return;
		if (!archi.isEmpty()) tooltip.add(TextFormatting.GREEN + I18n.format("info.around.have", archi));
		
		NBTTagCompound ore = rets.get(5, NBTTagCompound.class);
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
