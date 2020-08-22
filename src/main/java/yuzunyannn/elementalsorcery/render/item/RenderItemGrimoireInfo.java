package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderItemGrimoireInfo {

	public static RenderItemGrimoireInfo getFromStack(ItemStack stack) {
		return null;
	}

	public int tickCount = 0;
	public float pageFlipPrev = 0;
	public float pageFlip = 0;
	public float bookSpreadPrev = 0;
	public float bookSpread = 0;

	public float dBookSpread;

	public boolean update() {
		tickCount++;
		bookSpreadPrev = bookSpread;
		pageFlipPrev = pageFlip;

		bookSpread = MathHelper.clamp(bookSpread + dBookSpread, 0, 1);
		pageFlip += (1.0f - bookSpread) * 1.25;
		pageFlip *= 0.85F;

		return bookSpread == 0 || bookSpread == 1;
	}

	public void open() {
		dBookSpread = 0.05f;
	}

	public void close() {
		dBookSpread = -0.05f;
	}

	public void reset() {
		tickCount = 0;
		bookSpread = bookSpreadPrev = 0;
		pageFlipPrev = pageFlip = 0;
		dBookSpread = 0;
	}

	public void bind() {

	}
}
