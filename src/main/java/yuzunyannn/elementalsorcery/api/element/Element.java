package yuzunyannn.elementalsorcery.api.element;

import java.util.Random;

import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESRegister;

public class Element extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<Element> {

	protected Random rand = new Random();

	/** 元素的颜色 */
	private int color;

	/**
	 * 是否元素使用能量系统 如果使用，相同元素不同能量的ElementStack看作同一类，进行合并
	 */
	public final boolean usePower;

	public static int rgb(int r, int g, int b) {
		return r << 16 | g << 8 | b;
	}

	public Element(MapColor color) {
		this(color.colorValue);
	}

	public Element(int color) {
		this(color, true);
	}

	public Element(int color, boolean usePower) {
		this.color = color;
		this.usePower = usePower;

	}

	/** 非本地化名称 */
	protected String unlocalizedName;

	/**
	 * 设置元素的"非本地化名称"(UnlocalizedName), 前缀为"element."
	 */
	public Element setUnlocalizedName(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
	}

	/**
	 * 获取"非本地化名称"，有前缀"element."
	 */
	public String getUnlocalizedName(ElementStack estack) {
		return "element." + this.unlocalizedName;
	}

	/** 获取颜色 */
	public int getColor(ElementStack estack) {
		return color;
	}

	/** 默认能量级 */
	static public int DP_TOOLS = 5;
	static public int DP_BOX = 20;
	static public int DP_ALTAR = 100;

	/**
	 * 物品被析构成元素时候，获取真正可以得到的元素
	 * 
	 * @param stack
	 *            被析构的物品，方块的话也会变成对应的物品栈传入（注意：传入的stack是有数量的，但是该函数在处理时，应认为数量只有一个）
	 * @param estack
	 *            析构时核查的元素
	 * @param lvPower
	 *            进行析构祭坛（工具）的能量
	 * @param complex
	 *            析构物品的复杂度
	 * @return 返回真正得到的元素数量和能量
	 */
	public ElementStack getElementWhenDeconstruct(ItemStack stack, ElementStack estack, int complex, int lvPower) {
		if (lvPower <= Element.DP_TOOLS) {
			if (estack.getCount() > 25)
				estack.setCount(25);
			if (complex > 10)
				estack.rise(-0.9f);
			else
				estack.rise(-0.8f);
			estack.weaken(0.25f);
		} else if (lvPower <= Element.DP_BOX) {
			if (estack.getCount() > 100)
				estack.setCount(100);
			if (complex > 10)
				estack.rise(-0.85f);
			else
				estack.rise(-0.7f);
			estack.weaken(0.35f);
		} else if (lvPower <= Element.DP_ALTAR) {
			if (estack.getCount() > 200)
				estack.setCount(200);
			estack.rise(-0.6f);
			estack.weaken(0.45f);
		}
		return estack;
	}

	/**
	 * 获取当前元素和另一个元素处于同一个物品时候的复杂度叠加值[求默认复杂度使用]
	 * 
	 * @param stack
	 *            当前处理的物品
	 * @param estack
	 *            当前处理的元素
	 * @param other
	 *            当前处理的另一个元素
	 */
	public int complexWith(ItemStack stack, ElementStack estack, ElementStack other) {
		return 1;
	}

	/** 根据元素获取注册名 */
	public static ResourceLocation getNameFromElement(Element element) {
		return ESRegister.ELEMENT.getKey(element);
	}

	/** 根据注册名获取元素 */
	public static Element getElementFromName(ResourceLocation name) {
		return ESRegister.ELEMENT.getValue(name);
	}

	/** 默认的资源 */
	public final static ResourceLocation VIOD_RESOURCELOCATION = new ResourceLocation(ElementalSorcery.MODID,
			"textures/elements/void.png");

	/** 获取资源 */
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResourceLocation() {
		return VIOD_RESOURCELOCATION;
	}

	/** 在GUI绘画元素 */
	@SideOnly(Side.CLIENT)
	public void drawElemntIconInGUI(ElementStack estack, int x, int y, Minecraft mc) {
		ResourceLocation res = this.getIconResourceLocation();
		mc.getTextureManager().bindTexture(res);
		mc.currentScreen.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
		int count = estack.getCount();
		if (count > 1) {
			String s = Integer.toString(count);
			mc.fontRenderer.drawString(s, x + +19 - 2 - mc.fontRenderer.getStringWidth(s), y + 9, 16777215, true);
		}
		if (estack.usePower()) {
			int power = estack.getPower();
			if (power > 1) {
				String s = Integer.toString(power);
				mc.fontRenderer.drawString(s, x + +19 - 2 - mc.fontRenderer.getStringWidth(s), y - 3, 0xfff993, true);
			}
		}

	}

}