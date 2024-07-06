package yuzunyannn.elementalsorcery.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class DOItem extends BaseDisplayObject {

	protected ItemStack stack;
	protected float scale = 1;

	public DOItem(ItemStack stack) {
		super("E:I");
		this.stack = stack;
		setScale(1);
	}

	public DOItem setScale(float scale) {
		this.scale = scale;
		this.size = new Vec3d(16, 16, 16).scale(scale);
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void doRender(float partialTicks) {
		String s = null;
		Minecraft mc = Minecraft.getMinecraft();
		if (stack.getCount() > 64)
			s = TextFormatting.WHITE.toString() + TextHelper.toAbbreviatedNumber(stack.getCount(), 0);
		else if (stack.getCount() > 1) s = TextFormatting.WHITE.toString() + stack.getCount();
		GlStateManager.pushMatrix();
		if (scale != 1) GlStateManager.scale(scale, scale, scale);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		RenderItem itemRender = mc.getRenderItem();
		itemRender.zLevel = -150;
		itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);
		itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, stack, 0, 0, s);
		itemRender.zLevel = 0;
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (stack.isEmpty()) return nbt;
		nbt.setInteger("i", Item.REGISTRY.getIDForObject(stack.getItem()));
		if (stack.getCount() != 1) nbt.setByte("n", (byte) stack.getCount());
		if (stack.getItemDamage() != 0) nbt.setShort("d", (short) stack.getItemDamage());
		NBTTagCompound tag = null;
		if (stack.getItem().isDamageable() || stack.getItem().getShareTag())
			tag = stack.getItem().getNBTShareTag(stack);
		if (tag != null) nbt.setTag("t", tag);
		if (this.scale != 1) nbt.setFloat("s", scale);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (!nbt.hasKey("i")) this.stack = ItemStack.EMPTY;
		else {
			Item item = Item.REGISTRY.getObjectById(nbt.getInteger("i"));
			stack = new ItemStack(item);
			if (nbt.hasKey("n", NBTTag.TAG_NUMBER)) stack.setCount(nbt.getInteger("n"));
			if (nbt.hasKey("d", NBTTag.TAG_NUMBER)) stack.setItemDamage(nbt.getInteger("d"));
			if (nbt.hasKey("t", NBTTag.TAG_COMPOUND)) stack.getItem().readNBTShareTag(stack, nbt.getCompoundTag("t"));
		}
		this.setScale(nbt.hasKey("s") ? nbt.getFloat("s") : 1);
	}

}
