package yuzunyannn.elementalsorcery.api.element;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.api.util.ESImplRegister;
import yuzunyannn.elementalsorcery.api.util.client.ESResources;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;

public class Element extends IForgeRegistryEntry.Impl<Element> {

	public static final ESImplRegister<Element> REGISTRY = new ESImplRegister(Element.class);

	protected Random rand = new Random();

	/** 用来计数的id */
	private final int registryId;
	/** 元素的颜色 */
	protected int color;
	/** 非本地化名称 */
	protected String unlocalizedName;
	/** 跃迁数据 */
	protected ElementTransition elementTransition;

	public static int rgb(int r, int g, int b) {
		return r << 16 | g << 8 | b;
	}

	public Element(MapColor color) {
		this(color.colorValue);
	}

	public Element(int color) {
		this.color = color;
		this.registryId = -1;
	}

	public int getRegistryId() {
		return registryId;
	}

	/**
	 * 设置元素的"非本地化名称"(UnlocalizedName), 前缀为"element."
	 */
	public Element setTranslationKey(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
	}

	/**
	 * 获取"非本地化名称"，有前缀"element."
	 */
	public String getTranslationKey(ElementStack estack) {
		return "element." + this.unlocalizedName;
	}

	/** 获取颜色 */
	public int getColor(ElementStack estack) {
		return color;
	}

	/** 默认能量级 */
	static public final int DP_TOOLS = 5;
	static public final int DP_BOX = 20;
	static public final int DP_ALTAR = 100;
	static public final int DP_ALTAR_ADV = 300;
	static public final int DP_ALTAR_SURPREME = 500;

	/**
	 * 物品被析构成元素时候，获取真正可以得到的元素
	 * 
	 * @param stack   被析构的物品，只读，方块的话也会变成对应的物品栈传入（注意：传入的stack是有数量的，但是该函数在处理时，应认为数量只有一个）
	 * @param estack  析构时核查的元素，请直接修改这个元素栈
	 * @param lvPower 进行析构祭坛（工具）的能量
	 * @param complex 析构物品的复杂度
	 * @return 返回真正得到的元素数量和能量
	 */
	public ElementStack onDeconstructToElement(World world, ItemStack stack, ElementStack estack, int complex,
			int lvPower) {
		if (lvPower <= Element.DP_TOOLS) {
			if (estack.getCount() > 16) estack.setCount(16);
			if (complex >= 10) estack.rise(-0.8f);
			else estack.rise(-0.5f);
			estack.weaken(0.2f);
			if (estack.getPower() > 16) estack.setPower(16);
		} else if (lvPower <= Element.DP_BOX) { // 20
			if (estack.getCount() > 100) estack.setCount(100);
			if (complex > 10) estack.rise(-0.85f);
			else estack.rise(-0.7f);
			if (complex > 20) estack.weaken(0.05f);
			else estack.weaken(0.35f);
		} else if (lvPower <= Element.DP_ALTAR) { // 100
			float r = (lvPower - Element.DP_BOX) / (float) (Element.DP_ALTAR - Element.DP_BOX);
			if (estack.getCount() > 200) estack.setCount(200);
			estack.rise(-0.5f * (1 - r) + -0.2f * r);
			estack.weaken(0.4f * (1 - r) + 0.7f * r);
		} else if (lvPower <= Element.DP_ALTAR_ADV) { // 300
			float r = (lvPower - Element.DP_ALTAR) / (float) (Element.DP_ALTAR_ADV - Element.DP_ALTAR);
			if (estack.getCount() > 500) estack.setCount(500);
			estack.rise(-0.15f * (1 - r) + -0.075f * r);
			estack.weaken(0.75f * (1 - r) + 0.85f * r);
		} else if (lvPower <= Element.DP_ALTAR_SURPREME) { // 500
			float r = (lvPower - Element.DP_ALTAR_ADV) / (float) (Element.DP_ALTAR_SURPREME - Element.DP_ALTAR_ADV);
			estack.rise(-0.075f * (1 - r) + -0.02f * r);
			estack.weaken(0.9f * (1 - r) + 1f * r);
		} else estack.rise(-0.005f);
		return estack;
	}

	/**
	 * 获取元素的跃迁的数据，用于元素间转化
	 * 
	 * @return 跃迁的数据，返回null表示无法跃迁
	 */
	@Nullable
	public ElementTransition getTransition() {
		return elementTransition;
	}

	public Element setTransition(ElementTransition elementTransition) {
		this.elementTransition = elementTransition;
		return this;
	}

	public Element setTransition(float level, float kernelAngle, float regionAngle) {
		return setTransition(new ElementTransition(level, kernelAngle, regionAngle));
	}

	/**
	 * 获取当前元素和另一个元素处于同一个物品时候的复杂度叠加值[求默认复杂度使用]
	 * 
	 * @param stack  当前处理的物品
	 * @param estack 当前处理的元素
	 * @param other  当前处理的另一个元素
	 */
	public int complexWith(ItemStack stack, ElementStack estack, ElementStack other) {
		return 1;
	}

	/** 根据元素获取注册名 */
	public static ResourceLocation getNameFromElement(Element element) {
		return REGISTRY.getKey(element);
	}

	/** 根据元素获取注册id */
	public static int getIdFromElement(Element element) {
		return REGISTRY.getId(element);
	}

	/** 根据注册名获取元素 */
	public static Element getElementFromName(ResourceLocation name) {
		return REGISTRY.getValue(name);
	}

	public static Element getElementFromName(String name) {
		return getElementFromName(new ResourceLocation(name));
	}

	/** 根据注册id获取元素 */
	public static Element getElementFromId(int id) {
		Element element = REGISTRY.getValue(id);
		return element == null ? ElementStack.EMPTY.getElement() : element;
	}

	public static Element getElementFromIndex(int index, boolean onlyValid) {
		List<Element> list = Element.REGISTRY.getValues();
		if (onlyValid) return list.get(index % (list.size() - 1) + 1);
		return list.get(index % list.size());
	}

	/** 获取资源 */
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResourceLocation() {
		return ESResources.ELEMENT_VIOD.getResource();
	}

	public static final int DRAW_GUI_FLAG_NO_SHADOW = 0x1;
	public static final int DRAW_GUI_FLAG_NO_INFO = 0x2;
	public static final int DRAW_GUI_FLAG_CENTER = 0x4;

	/** 在GUI绘画元素 */
	@SideOnly(Side.CLIENT)
	public void drawElemntIconInGUI(ElementStack estack, int x, int y, int flag) {
		Minecraft mc = Minecraft.getMinecraft();
		ResourceLocation res = this.getIconResourceLocation();
		mc.getTextureManager().bindTexture(res);
		GlStateManager.color(1, 1, 1);
		boolean isCenter = (flag & DRAW_GUI_FLAG_CENTER) != 0;
		if (isCenter) RenderFriend.drawTextureRectInCenter(x, y, 16, 16);
		else Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);

		if ((flag & DRAW_GUI_FLAG_NO_INFO) != 0) return;

		int count = estack.getCount();
		boolean isShadow = (flag & DRAW_GUI_FLAG_NO_SHADOW) == 0;
		if (count > 1) {
			String s = Integer.toString(count);
			int w = mc.fontRenderer.getStringWidth(s);
			mc.fontRenderer.drawString(s, x + (isCenter ? 11 : 19) - 2 - w, y + (isCenter ? 1 : 9), 16777215, isShadow);
		}
		int power = estack.getPower();
		if (power > 1) {
			String s = Integer.toString(power);
			int w = mc.fontRenderer.getStringWidth(s);
			mc.fontRenderer.drawString(s, x + (isCenter ? 11 : 19) - 2 - w, y + (isCenter ? -11 : -3), 0xfff993,
					isShadow);
		}
	}

}
