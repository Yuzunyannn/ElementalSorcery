package yuzunyannn.elementalsorcery.element;

import java.util.Random;

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
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.register.ESRegister;
import yuzunyannn.elementalsorcery.init.ESImplRegister;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

public class Element extends IForgeRegistryEntry.Impl<Element> {

	public static final ESImplRegister<Element> REGISTRY = new ESImplRegister(Element.class);

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

	/**
	 * 当该元素转化成魔力的时候，获得魔力值
	 * 
	 * @param estack 转化魔力时的元素栈，请直接修改这个元素栈
	 * @return 返回值的元素会自动转变为magic，只需修改数量和能量即可
	 */
	public ElementStack changetoMagic(World world, ElementStack estack) {
		estack.weaken(0.5f);
		float n = estack.getPower();
		n = (float) Math.pow(n, 0.6) / 8 + 1;
		estack.setCount((int) (estack.getCount() * n));
		return estack;
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
	 * @param stack   被析构的物品，方块的话也会变成对应的物品栈传入（注意：传入的stack是有数量的，但是该函数在处理时，应认为数量只有一个）
	 * @param estack  析构时核查的元素，请直接修改这个元素栈
	 * @param lvPower 进行析构祭坛（工具）的能量
	 * @param complex 析构物品的复杂度
	 * @return 返回真正得到的元素数量和能量
	 */
	public ElementStack changetoElementWhenDeconstruct(World world, ItemStack stack, ElementStack estack, int complex,
			int lvPower) {
		if (lvPower <= Element.DP_TOOLS) {
			if (estack.getCount() > 25) estack.setCount(25);
			if (complex >= 10) estack.rise(-0.8f);
			else estack.rise(-0.5f);
			estack.weaken(0.2f);
		} else if (lvPower <= Element.DP_BOX) {
			if (estack.getCount() > 100) estack.setCount(100);
			if (complex > 10) estack.rise(-0.85f);
			else estack.rise(-0.7f);
			if (complex > 20) estack.weaken(0.05f);
			else estack.weaken(0.35f);
		} else if (lvPower <= Element.DP_ALTAR) {
			if (estack.getCount() > 200) estack.setCount(200);
			estack.rise(-0.5f);
			estack.weaken(0.5f);
		} else if (lvPower <= Element.DP_ALTAR_ADV) {
			if (estack.getCount() > 500) estack.setCount(500);
			estack.rise(-0.15f);
			estack.weaken(0.8f);
		} else if (lvPower <= Element.DP_ALTAR_SURPREME) {
			estack.rise(-0.02f);
		}
		return estack;
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
		return ESRegister.ELEMENT.getKey(element);
	}

	/** 根据注册名获取元素 */
	public static Element getElementFromName(ResourceLocation name) {
		return ESRegister.ELEMENT.getValue(name);
	}

	public static Element getElementFromName(String name) {
		return getElementFromName(new ResourceLocation(name));
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
		GlStateManager.color(1, 1, 1);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
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

	/** 绘画元素 ，在任何地方 */
	@SideOnly(Side.CLIENT)
	public void drawElemntIcon(ElementStack estack, float alpha) {
		ResourceLocation res = this.getIconResourceLocation();
		Minecraft.getMinecraft().getTextureManager().bindTexture(res);
		RenderHelper.drawTexturedRectInCenter(0, 0, 16, 16);
	}

}
