package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.fcube.FairyCubeModule;
import yuzunyannn.elementalsorcery.entity.fcube.IFairyCubeModuleClient;
import yuzunyannn.elementalsorcery.init.ESInit;

public class ItemFairyCubeModule extends Item {

	public ItemFairyCubeModule() {
		this.setUnlocalizedName("fairyCubeModule");
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;

		items.add(new ItemStack(this));
		for (ResourceLocation id : FairyCubeModule.REGISTRY.keySet()) items.add(getFairyCubeModule(id));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ResourceLocation id = getModuleId(stack);
		if (id == null) return;
		IFairyCubeModuleClient moduleClient = IFairyCubeModuleClient.get(id);
		if (moduleClient == null) return;
		tooltip.add(moduleClient.getDiplayName());
	}

	public static ItemStack getFairyCubeModule(ResourceLocation id) {
		ItemStack stack = new ItemStack(ESInit.ITEMS.FAIRY_CUBE_MODULE);
		NBTTagCompound nbt = stack.getOrCreateSubCompound("fcm");
		nbt.setString("id", id.toString());
		return stack;
	}

	public static ResourceLocation getModuleId(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("fcm");
		if (nbt == null) return null;
		return new ResourceLocation(nbt.getString("id"));
	}

	public static NBTTagCompound getModuleData(ItemStack stack) {
		return stack.getSubCompound("fcm");
	}

}
