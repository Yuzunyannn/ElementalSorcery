package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectResonance;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class MantraSprint extends MantraCommon {

	public MantraSprint() {
		this.setUnlocalizedName("sprint");
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataCommon = (MantraDataCommon) data;
		ElementStack need = new ElementStack(ESInit.ELEMENTS.AIR, 5, 30);
		ElementStack get = caster.iWantSomeElement(need, true);
		if (get.isEmpty()) return;
		dataCommon.markContinue(true);
		Entity entity = caster.iWantCaster();
		int power = get.getPower();
		double scale = MathHelper.clamp(MathHelper.sqrt(power / 10), 1.5, 6);
		scale = 6;
		Vec3d look = entity.getLookVec().addVector(0, 0.05, 0).normalize().scale(scale);
		entity.motionX += look.x;
		entity.motionY += look.y;
		entity.motionZ += look.z;
		world.playSound((EntityPlayer) null, entity.posX, entity.posY, entity.posZ,
				SoundEvents.ENTITY_PLAYER_BIG_FALL, SoundCategory.PLAYERS, 1.0F, 1.0F);
		onSpelling(world, dataCommon, caster);
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataCommon = (MantraDataCommon) data;
		if (!dataCommon.isMarkContinue()) return;
		Entity entity = caster.iWantCaster();
		entity.motionY = 0;
		entity.fallDistance = 0;
		if (world.isRemote) endEffect(world, caster);
	}

	@SideOnly(Side.CLIENT)
	public void endEffect(World world, ICaster caster) {
		Entity entity = caster.iWantCaster();
		EffectResonance effect = new EffectResonance(world, entity.posX, entity.posY + 1, entity.posZ);
		effect.setColor(0xffffff);
		Effect.addEffect(effect);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Element getMagicCircle() {
		return ESInit.ELEMENTS.AIR;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResource() {
		return RenderObjects.MANTRA_SPRINT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor() {
		return 0xabfffa;
	}

}
