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
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.entity.FairyCubeModule;
import yuzunyannn.elementalsorcery.api.item.IPlatformTickable;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.entity.fcube.IFairyCubeModuleClient;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class ItemFairyCubeModule extends Item implements IPlatformTickable {

	public ItemFairyCubeModule() {
		this.setTranslationKey("fairyCubeModule");
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
		ItemStack stack = new ItemStack(ESObjects.ITEMS.FAIRY_CUBE_MODULE);
		setFairyCubeModule(stack, id);
		return stack;
	}

	public static void setFairyCubeModule(ItemStack stack, ResourceLocation id) {
		NBTTagCompound nbt = stack.getOrCreateSubCompound("fcm");
		nbt.setString("id", id.toString());
	}

	public static ResourceLocation getModuleId(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("fcm");
		if (nbt == null) return null;
		return new ResourceLocation(nbt.getString("id"));
	}

	public static NBTTagCompound getModuleData(ItemStack stack) {
		return stack.getSubCompound("fcm");
	}

	@Override
	public boolean platformUpdate(World world, ItemStack stack, IWorldObject caster, NBTTagCompound runData,
			int tick) {
		if (tick % (16 * 20) != 0) return false;
		if (getModuleId(stack) != null) return false;
		if (world.isRemote) return false;

		IElementInventory srcInv = ElementHelper.getElementInventory(caster);
		if (srcInv == null) return false;

		for (Class<? extends FairyCubeModule> cls : FairyCubeModule.REGISTRY.valueSet()) {
			if (FairyCubeModule.tryMatchAndConsumeForCraft(cls, world, caster.getPosition(), srcInv)) {
				ResourceLocation id = FairyCubeModule.REGISTRY.getKey(cls);
				setFairyCubeModule(stack, id);
				FireworkEffect.spawn(world, caster.getObjectPosition().add(0, 0.8, 0), 10, 1, 0.1f,
						new int[] { 0x173839, 0x198663, 0x2ee715 }, new int[] { 0x9cef91 });
				return true;
			}
		}

		return false;
	}
}
