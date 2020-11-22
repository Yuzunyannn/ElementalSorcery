package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class MantraFloat extends MantraCommon {

	public MantraFloat() {
		this.setUnlocalizedName("float");
		this.setRarity(125);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		onSpelling(world, data, caster);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		if (caster.iWantKnowCastTick() % 5 == 0 || !dataEffect.isMarkContinue()) {
			dataEffect.markContinue(false);
			ElementStack need = new ElementStack(ESInit.ELEMENTS.AIR, 1, 20);
			ElementStack get = caster.iWantSomeElement(need, true);
			if (get.isEmpty()) return;
		}
		dataEffect.markContinue(true);
		Entity entity = caster.iWantCaster();
		entity.motionY = 0.15;
		entity.fallDistance = 0;
		if (world.isRemote) onSpellingEffect(world, data, caster);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Element getMagicCircle() {
		return ESInit.ELEMENTS.AIR;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResource() {
		return RenderObjects.MANTRA_FLOAT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor() {
		return 0xacffff;
	}

}
