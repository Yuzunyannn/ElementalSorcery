package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectElementMove;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class MantraBlockCrash extends MantraCommon {

	public MantraBlockCrash() {
		this.setUnlocalizedName("blockCrash");
		this.setRarity(110);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		ElementStack need = new ElementStack(ESInitInstance.ELEMENTS.EARTH, 10, 25);
		ElementStack get = caster.iWantSomeElement(need, false);
		dataEffect.markContinue(!get.isEmpty());
	}

	@Override
	public boolean onSpellingEffect(World world, IMantraData data, ICaster caster) {
		if (super.onSpellingEffect(world, data, caster)) return true;
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		dataEffect.setProgress(Math.min(caster.iWantKnowCastTick() / 20.0f, 1), this.getRenderColor(), world, caster);
		return false;
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		if (caster.iWantKnowCastTick() < 20) return;
		BlockPos pos = null;
		EntityLivingBase living = caster.iWantLivingTarget(EntityLivingBase.class);
		if (living != null) pos = living.getPosition();
		if (pos == null) {
			pos = caster.iWantBlockTarget();
			if (pos == null) return;
			if (world.isAirBlock(pos)) return;
		}
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock().isReplaceable(world, pos)) {
			pos = pos.down();
			state = world.getBlockState(pos);
		}
		if (state.getBlock() instanceof ITileEntityProvider) return;
		if (!state.isFullBlock()) return;
		ElementStack need = new ElementStack(ESInitInstance.ELEMENTS.EARTH, 10, 25);
		ElementStack stack = caster.iWantSomeElement(need, true);
		if (stack.isEmpty()) return;
		float speed = MathHelper.clamp(MathHelper.sqrt(stack.getPower() / 10) / 4, 1, 5);
		AxisAlignedBB aabb = new AxisAlignedBB(pos.up());
		List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		for (EntityLivingBase entity : list) {
			entity.motionY = speed;
		}
		world.setBlockState(pos.up(), state);
		world.destroyBlock(pos, false);
		if (world.isRemote) addBlockEffect(pos, state);
	}

	@SideOnly(Side.CLIENT)
	public void addBlockEffect(BlockPos pos, IBlockState state) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.effectRenderer.addBlockDestroyEffects(pos, state);
		for (int i = 0; i < 20; i++) {
			EffectElementMove effect = new EffectElementMove(mc.world, new Vec3d(pos).addVector(0.5, 0.5, 0.5));
			effect.g = 0;
			Vec3d v = new Vec3d(mc.world.rand.nextFloat() - 0.5, mc.world.rand.nextFloat() - 0.5,
					mc.world.rand.nextFloat() - 0.5);
			effect.setColor(this.getRenderColor());
			effect.setVelocity(v.scale(0.2));
			Effect.addEffect(effect);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Element getMagicCircle() {
		return ESInitInstance.ELEMENTS.EARTH;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResource() {
		return RenderObjects.MANTRA_BLOCK_CRASH;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor() {
		return 0x785439;
	}

}
