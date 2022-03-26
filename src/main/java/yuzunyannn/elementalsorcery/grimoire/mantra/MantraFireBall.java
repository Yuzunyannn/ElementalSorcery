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
import net.minecraft.nbt.NBTTagCompound;
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
import yuzunyannn.elementalsorcery.element.ElementKnowledge;
import yuzunyannn.elementalsorcery.element.ElementMetal;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.ICasterObject;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.helper.OreHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class MantraFireBall extends MantraCommon {

	static public void fire(World world, EntityLivingBase spller, int power, boolean needKnowledge) {
		fire(world, spller, spller.getLookVec(), power, needKnowledge);
	}

	static public void fire(World world, EntityLivingBase spller, Vec3d orient, int power, boolean needKnowledge) {
		EntityGrimoire grimoire = new EntityGrimoire(world, spller, ESInit.MANTRAS.FIRE_BALL, null,
				EntityGrimoire.STATE_AFTER_SPELLING);
		Data data = (Data) grimoire.getMantraData();
		data.power = power;
		data.pos = spller.getPositionVector().add(0, spller.getEyeHeight(), 0);
		data.toward = orient.normalize();
		data.pos = data.pos.add(data.toward.scale(2));
		if (needKnowledge) data.knowledge = new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, 20, 150);
		grimoire.setPosition(spller.posX, spller.posY, spller.posZ);
		world.spawnEntity(grimoire);
	}

	protected static class Data extends MantraDataCommon {
		protected float power = 0;
		protected Vec3d pos;
		protected Vec3d toward;
		protected ElementStack metal = new ElementStack(ESInit.ELEMENTS.METAL, 0);
		protected ElementStack knowledge = new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, 0);

		protected int color = 0;
		protected boolean powerUp = false;

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setFloat("power", power);
			NBTHelper.setVec3d(nbt, "pos", pos);
			NBTHelper.setVec3d(nbt, "toward", toward);
			if (!metal.isEmpty()) nbt.setTag("metal", metal.serializeNBT());
			if (!knowledge.isEmpty()) nbt.setTag("know", knowledge.serializeNBT());
			return nbt;
		}

		@Override
		public NBTTagCompound serializeNBTForSend() {
			return this.serializeNBT();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			power = nbt.getFloat("power");
			pos = NBTHelper.getVec3d(nbt, "pos");
			toward = NBTHelper.getVec3d(nbt, "toward");
			metal = new ElementStack(nbt.getCompoundTag("metal"));
			knowledge = new ElementStack(nbt.getCompoundTag("know"));
		}

	}

	public MantraFireBall() {
		this.setTranslationKey("fireBall");
		this.setColor(0xff8f02);
		this.setIcon("fire_ball");
		this.setRarity(50);
		this.setOccupation(3);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		if (world.rand.nextInt(5) != 0) return;
		ElementStack stack = getElement(caster, ESInit.ELEMENTS.FIRE, 10, 50);
		if (stack.isEmpty()) return;
		ElementStack knowledge = getElement(caster, ESInit.ELEMENTS.KNOWLEDGE, 2, 50);

		int power = 3 + Math.min(stack.getPower() / 200, 4);
		MantraFireBall.fire(world, caster.iWantCaster().asEntityLivingBase(), power, !knowledge.isEmpty());
	}

	@Override
	public IMantraData getData(NBTTagCompound origin, World world, ICaster caster) {
		return new Data();
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		dataEffect.markContinue(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData mData, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return;
		super.onSpellingEffect(world, mData, caster);
		this.addEffectEmitEffect(world, mData, caster);
	}

	@Override
	public void onCollectElement(World world, IMantraData mData, ICaster caster, int speedTick) {
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return;
		Data data = (Data) mData;
		data.powerUp = false;
		if (data.power >= 32) return;
		// 每tick两点消耗，积攒火球，火元素是必须的，否则没法积攒其他元素
		ElementStack need = new ElementStack(ESInit.ELEMENTS.FIRE, 2, 50);
		ElementStack stack = caster.iWantSomeElement(need, true);
		if (stack.isEmpty()) return;
		data.power = data.power + 0.2f + Math.min(stack.getPower() / 1000f, 0.4f);
		data.powerUp = true;
		data.setProgress(data.power, 32);
		// 获取金
		if (data.metal.getCount() < 40) out: {
			need = new ElementStack(ESInit.ELEMENTS.METAL, 1, 100);
			stack = caster.iWantSomeElement(need, true);
			if (stack.isEmpty()) break out;
			data.metal.growOrBecome(stack);
			if (world.isRemote) if (world.rand.nextInt(4) == 0) data.color = ElementMetal.COLOR;
			else data.color = 0;
		}
		// 获取知识
		if (data.knowledge.getCount() < 20) out: {
			need = new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, 1, 100);
			stack = caster.iWantSomeElement(need, true);
			if (stack.isEmpty()) break out;
			data.knowledge.growOrBecome(stack);
			if (world.isRemote) if (world.rand.nextInt(5) == 0) data.color = ElementKnowledge.COLOR;
			else data.color = 0;
		}
	}

	@Override
	public void endSpelling(World world, IMantraData mData, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return;
		Data data = (Data) mData;
		ICasterObject co = caster.iWantCaster();
		data.pos = co.getEyePosition();
		data.toward = caster.iWantDirection();
		data.pos = data.pos.add(data.toward.scale(2));
		float potent = caster.iWantBePotent(5, false);
		data.power = Math.min(data.power, 32) * (1 + potent * 0.25f);
	}

	@Override
	public boolean afterSpelling(World world, IMantraData mData, ICaster caster) {
		Data data = (Data) mData;
		if (data.power <= 0) return false;
		data.power--;
		Vec3d pos = data.pos;
		if (world.isRemote) {
			// 客户端动画
			Vec3d toward = data.toward.scale(0.1);
			for (double i = 0.1; i <= 1; i += 0.1) {
				pos = pos.add(toward);
				afterSpellingEffect(world, pos, getRandomEffectColorFromData(world, data));
			}
		}
		data.pos = data.pos.add(data.toward);
		if (world.isRemote) return true;
		// 方块计算
		float power = Math.min(10, MathHelper.sqrt(data.power));
		int size = (int) (power / 2);
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
		Entity entity = caster.iWantCaster().asEntity();
		AxisAlignedBB aabb = new AxisAlignedBB(x - size, y - size, z - size, x + size, y + size, z + size);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, (living) -> {
			if (living == entity) return false;
			return true;
		});

		boolean passTeam = data.knowledge.getCount() >= 12;
		for (EntityLivingBase living : entities) {
			if (passTeam && EntityHelper.isSameTeam(entity, living)) continue;
			DamageSource ds = DamageHelper.getMagicDamageSource(entity, caster.iWantDirectCaster().asEntity());
			float dmg = data.power * data.power / 32;
			if (living.attackEntityFrom(ds, dmg)) living.setFire((int) (power * 2));
		}
		return true;
	}

	/** 方块操作 */
	public static IBlockState affect(World world, BlockPos pos, Vec3i add, @Nullable Data data) {
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
		boolean noChange = data == null ? false : data.knowledge.getCount() >= 20;
		if (!stack.isEmpty()) {
			ItemStack ore = stack;
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(ore);
			if (!result.isEmpty()) {
				stack = result.copy();
				if (data != null && !data.metal.isEmpty()) {
					if (OreHelper.isOre(ore)) {
						data.metal.shrink(1);
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
	public int getRandomEffectColorFromData(World world, Data data) {
		int color = this.getRenderColor();
		switch (world.rand.nextInt(6)) {
		case 0:
			if (!data.knowledge.isEmpty()) color = ElementKnowledge.COLOR;
			break;
		case 1:
			if (!data.metal.isEmpty()) color = ElementMetal.COLOR;
			break;
		default:
			break;
		}
		return color;
	}

}
