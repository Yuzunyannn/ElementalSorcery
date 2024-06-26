package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;
import java.util.Random;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.util.target.WorldTarget;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.potion.PotionPoundWalker;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class MantraBlockCrash extends MantraTypeAccumulative {

	public MantraBlockCrash() {
		this.setTranslationKey("blockCrash");
		this.setColor(0x785439);
		this.setIcon("block_crash");
		this.setRarity(110);
		this.setAccumulatePreTick(4);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.EARTH, 1, 25), 5, 5);
		this.setDirectLaunchFragmentMantraLauncher(new ElementStack(ESObjects.ELEMENTS.EARTH, 20, 25), 2, 0.0075, null);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);

		ElementStack stack = getElement(caster, ESObjects.ELEMENTS.EARTH, 2, 25);
		if (stack.isEmpty()) return;

		Vec3d dir = caster.iWantDirection();
		dir = dir.add(0, 0.5, 0).normalize();
		float speed = MathHelper.clamp(MathHelper.sqrt(stack.getPower() / 10) / 4, 1, 5);
		dir = dir.scale(speed);

		target.motionX += dir.x;
		target.motionY += dir.y;
		target.motionZ += dir.z;
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		if (!isAllElementMeetMinNeed(data)) return;
		BlockPos pos = null;
		WorldTarget wr = caster.iWantEntityTarget(EntityLivingBase.class);
		EntityLivingBase living = (EntityLivingBase) wr.getEntity();
		if (living != null) pos = living.getPosition();
		if (pos == null) {
			wr = caster.iWantBlockTarget();
			pos = wr.getPos();
			if (pos == null) return;
			if (world.isAirBlock(pos)) return;
		}

		float potent = caster.iWantBePotent(0.5f, true);
		if (potent >= 0.5f) {
			caster.iWantBePotent(0.5f, false);
			doOnce(world, pos, caster, 1);
			doOnce(world, pos.offset(EnumFacing.NORTH), caster, 1);
			doOnce(world, pos.offset(EnumFacing.SOUTH), caster, 1);
			doOnce(world, pos.offset(EnumFacing.EAST), caster, 1);
			doOnce(world, pos.offset(EnumFacing.WEST), caster, 1);
		} else doOnce(world, pos, caster, 5);
	}

	@Override
	public boolean afterSpelling(World world, IMantraData data, ICaster caster) {
		if (world.isRemote) return false;
		MantraDataCommon mdc = (MantraDataCommon) data;
		ElementStack eStack = mdc.get(ESObjects.ELEMENTS.EARTH);
		if (eStack.isEmpty()) return false;
		float count = eStack.getCount();
		if (caster.iWantKnowCastTick() % 5 == 0) eStack.shrink(1);
		int range = (int) (4 + 4 * Math.min(1, count / 20f));
		for (int i = 0; i < range / 4; i++) {
			BlockPos center = caster.iWantDirectCaster().getPosition();
			BlockPos randomPos = center.add(world.rand.nextGaussian() * range, 0, world.rand.nextGaussian() * range);
			randomPos = randomPos.up(3);
			for (int j = 0; j < 6 && !PotionPoundWalker.canPound(world.getBlockState(randomPos)); j++)
				randomPos = randomPos.down();
			float amplifier = 2 + MathHelper.sqrt(eStack.getPower()) * 1f;
			PotionPoundWalker.pound(world, randomPos, amplifier, caster.iWantCaster().toEntityLiving());
		}
		return true;
	}

	private void doOnce(World world, BlockPos pos, ICaster caster, int needElementPoint) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock().isReplaceable(world, pos)) {
			pos = pos.down();
			state = world.getBlockState(pos);
		}
		if (state.getBlock() instanceof ITileEntityProvider) return;
		if (!state.isFullBlock()) return;
		if (!BlockHelper.isReplaceBlock(world, pos.up())) return;
		if (!BlockHelper.isHardnessLower(world, pos, 75)) return;

		ElementStack stack = getElement(caster, ESObjects.ELEMENTS.EARTH, needElementPoint, 25);
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
		addBlockElementEffect(new Vec3d(pos).add(0.5, 0.5, 0.5), getColor(null));
	}

	@SideOnly(Side.CLIENT)
	public static void addBlockElementEffect(Vec3d pos, int color) {
		Minecraft mc = Minecraft.getMinecraft();
		Random rand = mc.world.rand;
		for (int i = 0; i < 20; i++) {
			EffectElementMove effect = new EffectElementMove(mc.world, pos);
			Vec3d v = new Vec3d(rand.nextFloat() - 0.5, rand.nextFloat() - 0.5, rand.nextFloat() - 0.5).scale(0.2);
			effect.setColor(color);
			effect.setVelocity(v);
			v = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).scale(0.01);
			effect.xAccelerate = v.x;
			effect.yAccelerate = v.y;
			effect.zAccelerate = v.z;
			effect.xDecay = effect.yDecay = effect.zDecay = 0.8;

			Effect.addEffect(effect);
		}
	}

}
