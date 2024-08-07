package yuzunyannn.elementalsorcery.grimoire.mantra.crack;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraCommon;
import yuzunyannn.elementalsorcery.item.prop.ItemElementCrack;
import yuzunyannn.elementalsorcery.render.item.RenderItemElementCrack;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public abstract class MantraCrackCommon extends MantraCommon {

	public double maxFragment;
	public double maxUseTick = 20 * 5;

	public void setMaxFragment(double maxFragment) {
		this.maxFragment = maxFragment;
	}

	public double getMaxFragment() {
		return maxFragment;
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		caster.iWantBePotent(0.2f, false);
		ItemElementCrack.crackAttack(world, target, caster.iWantCaster().toEntityLiving());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDisplayName() {
		TextFormatting tf = TextFormatting.values()[RandomHelper.rand.nextInt(TextFormatting.values().length)];
		if (tf.isColor()) return tf + TextFormatting.OBFUSCATED.toString() + super.getDisplayName();
		return TextFormatting.OBFUSCATED.toString() + super.getDisplayName();
	}

	@Override
	public void renderShiftIcon(NBTTagCompound mantraData, float suggestSize, float suggestAlpha, float partialTicks) {
		super.renderShiftIcon(mantraData, suggestSize, suggestAlpha, partialTicks);
		GlStateManager.color(1, 1, 1);
		RenderItemElementCrack.bindCrackTexture();
		RenderFriend.drawTextureRectInCenter(0, 0, suggestSize / 2, suggestSize / 2);
		GlStateManager.rotate(45, 0, 0, 1);
		RenderFriend.drawTextureRectInCenter(0, 0, suggestSize / 2, suggestSize / 2);
		GlStateManager.rotate(-45, 0, 0, 1);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		mdc.markContinue(true);
	}

	@Override
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		if (mdc.get(FRAGMENT) >= maxFragment) return;
		ElementStack eStack = caster.iWantAnyElementSample(speedTick);
		if (eStack.isEmpty()) return;
		double fragment = maxFragment / maxUseTick;
		double count = ElementTransition.fromMagicFragmentByPower(eStack.getElement(), fragment, eStack.getPower());
		eStack.setCount((int) Math.max(count, 1));
		ElementStack get = caster.iWantSomeElement(eStack, true);
		if (get.isEmpty()) return;
		fragment = mdc.get(FRAGMENT) + ElementTransition.toMagicFragment(eStack);
		mdc.set(FRAGMENT, fragment);
		mdc.setProgress(fragment, maxFragment);
	}

	@Override
	protected void initDirectLaunchMantraGrimoire(EntityGrimoire grimoire, VariableSet params) {
		((MantraDataCommon) grimoire.getMantraData()).markContinue(true);
	}

}
