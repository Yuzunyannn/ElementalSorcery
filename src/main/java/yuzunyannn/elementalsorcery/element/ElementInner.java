package yuzunyannn.elementalsorcery.element;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.element.IElementSpell;

//简单的icon返回的元素
public abstract class ElementInner extends Element implements IElementSpell {

	ResourceLocation TEXTURE;

	public ElementInner(int color, String resName) {
		super(color);
		TEXTURE = new ResourceLocation(ElementalSorcery.MODID, "textures/elements/" + resName + ".png");
		this.setUnlocalizedName(resName);
	}

	@Override
	public ResourceLocation getIconResourceLocation() {
		return TEXTURE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInfo(ElementStack estack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn,
			int level) {
		tooltip.add(I18n.format("info.element.spell." + unlocalizedName));
	}

}
