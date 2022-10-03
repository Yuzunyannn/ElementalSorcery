package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.element.ElementKnowledge;
import yuzunyannn.elementalsorcery.element.ElementMetal;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.remote.FMantraFireBall;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.helper.OreHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.var.Variables;

public class MantraFireBall extends MantraTypeAccumulative {

	static public void fire(World world, EntityLivingBase spller, int power, boolean needKnowledge) {
		fire(world, spller, spller.getLookVec(), power, needKnowledge);
	}

	static public void fire(World world, EntityLivingBase spller, Vec3d orient, int power, boolean needKnowledge) {
		VariableSet set = new VariableSet();
		orient = orient.normalize();
		Vec3d pos = spller.getPositionVector().add(0, spller.getEyeHeight(), 0).add(orient.scale(2));
		set.set(POWERI, power);
		set.set(VEC, pos);
		set.set(TOWARD, orient);
		if (needKnowledge) set.set(Variables.KNOWLEDGE, new ElementStack(ESObjects.ELEMENTS.KNOWLEDGE, 20, 150));
		MantraCommon.fireMantra(world, ESObjects.MANTRAS.FIRE_BALL, spller, set);
	}

	protected class MyCollectRule extends CollectRule {
		@Override
		public float calcRealCollectProgress(World world, MantraDataCommon mData, ICaster caster) {
			ElementStack fire = mData.get(ESObjects.ELEMENTS.FIRE);
			float f = super.calcRealCollectProgress(world, mData, caster);
			return Math.max(f, getPower(fire) / 32f);
		}
	}

	public MantraFireBall() {
		this.setTranslationKey("fireBall");
		this.setColor(0xff8f02);
		this.setIcon("fire_ball");
		this.setRarity(50);
		this.setOccupation(3);
		this.setMainRule(new MyCollectRule());
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.FIRE, 2, 50), 500, 25);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.METAL, 1, 100), 40, 0);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.KNOWLEDGE, 1, 100), 20, 0);
		this.addFragmentMantraLauncher(new FMantraFireBall());
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		if (world.rand.nextInt(5) != 0) return;
		ElementStack stack = getElement(caster, ESObjects.ELEMENTS.FIRE, 10, 50);
		if (stack.isEmpty()) return;
		ElementStack knowledge = getElement(caster, ESObjects.ELEMENTS.KNOWLEDGE, 2, 50);

		int power = 3 + Math.min(stack.getPower() / 200, 4);
		MantraFireBall.fire(world, caster.iWantCaster().asEntityLivingBase(), power, !knowledge.isEmpty());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData mData, ICaster caster) {
		super.onSpellingEffect(world, mData, caster);
		this.addEffectEmitEffect(world, mData, caster);
	}

	protected float getPower(ElementStack fire) {
		return fire.getCount() / 2 * (0.1f + Math.min(fire.getPower() / 1000f, 0.4f));
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		if (!isAllElementMeetMinNeed(data)) return;
		MantraDataCommon mData = (MantraDataCommon) data;

		IWorldObject co = caster.iWantCaster();
		mData.set(TOWARD, caster.iWantDirection());
		mData.set(VEC, co.getEyePosition().add(mData.get(TOWARD).scale(2)));
		float potent = caster.iWantBePotent(5, false);
		mData.set(POWERF, Math.min(getPower(mData.get(ESObjects.ELEMENTS.FIRE)), 32) * (1 + potent * 0.25f));
	}

	@Override
	public boolean afterSpelling(World world, IMantraData mData, ICaster caster) {
		MantraDataCommon data = (MantraDataCommon) mData;
		float power = data.get(POWERF);
		if (power <= 0) return false;
		data.set(POWERF, --power);
		Vec3d pos = data.get(VEC);
		Vec3d toward = data.get(TOWARD);
		if (world.isRemote) {
			// 客户端动画
			Vec3d towards = data.get(TOWARD).scale(0.1);
			for (double i = 0.1; i <= 1; i += 0.1) {
				pos = pos.add(towards);
				afterSpellingEffect(world, pos, getRandomEffectColorFromData(world, data));
			}
		}
		data.set(VEC, data.get(VEC).add(toward));
		if (world.isRemote) return true;
		// 方块计算
		float sqPower = Math.min(10, MathHelper.sqrt(power));
		int size = (int) (sqPower / 2);
		BlockPos bPos = new BlockPos(pos);
		for (int x = -size; x <= size; x++) {
			for (int y = size; y >= -size; y--) {
				for (int z = -size; z <= size; z++) {
					IBlockState toState = affect(world, bPos, new Vec3i(x, y, z), data);
					if (toState != null) world.setBlockState(bPos.add(x, y, z), toState);
				}
			}
		}
		// 攻击敌人
		double x = bPos.getX() + 0.5;
		double y = bPos.getY() + 0.5;
		double z = bPos.getZ() + 0.5;
		Entity casterEntity = caster.iWantCaster().asEntity();

		AxisAlignedBB aabb = new AxisAlignedBB(x - size, y - size, z - size, x + size, y + size, z + size);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, (living) -> {
			if (living == casterEntity) return false;
			return true;
		});
		boolean passTeam = data.get(Variables.KNOWLEDGE).getCount() >= 12;
		for (EntityLivingBase living : entities) {
			if (passTeam && isCasterFriend(caster, living)) continue;
			DamageSource ds = caster.iWantDamageSource(ESObjects.ELEMENTS.FIRE);
			float dmg = power * power / 32;
			if (living.attackEntityFrom(ds, dmg)) living.setFire((int) (power * 2));
		}
		return true;
	}

	/** 方块操作 */
	public static IBlockState affect(World world, BlockPos pos, Vec3i add, @Nullable MantraDataCommon data) {
		pos = pos.add(add);
		if (world.isAirBlock(pos)) return null;
		IBlockState origin = world.getBlockState(pos);
		float hardness = origin.getBlockHardness(world, pos);
		if (hardness < 0) return null;
		Block block = origin.getBlock();
		boolean isLiquid = block instanceof BlockLiquid;
		if (!isLiquid && world.rand.nextInt(100) < hardness * 1.5f) return null;
		if (block instanceof BlockFire) return null;
		if (block == Blocks.FLOWING_LAVA || block == Blocks.LAVA) {
			if (world.rand.nextInt(5) == 0) return Blocks.LAVA.getDefaultState();
			return null;
		}
		if (block == Blocks.OBSIDIAN) return null;
		int s = Math.abs(add.getX() * add.getY() * add.getZ()) + 1;
		if (block == Blocks.FLOWING_WATER || block == Blocks.WATER) {
			if (world.rand.nextInt(s) != 0) return Blocks.OBSIDIAN.getDefaultState();
			return Blocks.AIR.getDefaultState();
		}
		ItemStack stack = ItemHelper.toItemStack(origin);
		boolean noChange = data == null ? false : data.get(Variables.KNOWLEDGE).getCount() >= 20;
		if (!stack.isEmpty()) {
			ItemStack ore = stack;
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(ore);
			if (!result.isEmpty()) {
				stack = result.copy();
				if (data != null && !data.get(Variables.METAL).isEmpty()) {
					if (OreHelper.isOre(ore)) {
						data.get(Variables.METAL).shrink(1);
						stack.grow(1);
					}
				}
				EntityItem entityitem = ItemHelper.dropItem(world, pos, stack);
				if (world.rand.nextInt(8) == 0) {
					entityitem.setFire(100);
					entityitem.attackEntityFrom(DamageSource.IN_FIRE, 3);
				}
				return Blocks.AIR.getDefaultState();
			} else {
				int fire = block.getFlammability(world, pos, EnumFacing.NORTH);
				if (noChange && fire <= 0) return null;
				if (fire > 0) return Blocks.FIRE.getDefaultState();
				EntityItem entityitem = ItemHelper.dropItem(world, pos, stack);
				entityitem.setFire(100);
				entityitem.attackEntityFrom(DamageSource.IN_FIRE, 3);
				return Blocks.AIR.getDefaultState();
			}
		}
		return noChange ? null : Blocks.AIR.getDefaultState();
	}

	@SideOnly(Side.CLIENT)
	public void afterSpellingEffect(World world, Vec3d lineAt, int color) {
		EffectElementMove effect = new EffectElementMove(world, lineAt);
		effect.setColor(color);
		Random rand = world.rand;
		Vec3d vec = new Vec3d(rand.nextDouble() * 2 - 1, rand.nextDouble() * 2 - 1, rand.nextDouble() * 2 - 1)
				.normalize();
		vec = vec.scale(0.2);
		effect.setVelocity(vec);
		Effect.addEffect(effect);
		world.spawnParticle(EnumParticleTypes.FLAME, lineAt.x, lineAt.y, lineAt.z, vec.x, vec.y, vec.z);
	}

	@SideOnly(Side.CLIENT)
	public int getRandomEffectColorFromData(World world, MantraDataCommon data) {
		int color = getColor(data);
		switch (world.rand.nextInt(6)) {
		case 0:
			if (!data.get(Variables.KNOWLEDGE).isEmpty()) color = ElementKnowledge.COLOR;
			break;
		case 1:
			if (!data.get(Variables.METAL).isEmpty()) color = ElementMetal.COLOR;
			break;
		default:
			break;
		}
		return color;
	}

}
