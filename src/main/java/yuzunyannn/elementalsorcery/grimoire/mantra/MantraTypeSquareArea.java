package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.mantra.CastStatus;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.ICasterObject;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.mantra.MantraEffectType;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet.Variable;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectMap;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicSquare;

/** 区域类型 */
public abstract class MantraTypeSquareArea extends MantraTypeAccumulative {

	public static final Variable<Integer> DELAY = new Variable<>("@delay", VariableSet.INT);

	public static class SquareData extends MantraDataCommon {

		public void setSize(int size) {
			set(SIZEI, size);
		}

		public void setSize(float size) {
			setSize(Math.round(size));
		}

		public void setDelay(int delay) {
			set(DELAY, delay);
		}

		public int getSize() {
			return get(SIZEI);
		}

		public int getDelay() {
			return get(DELAY);
		}

	}

	public MantraTypeSquareArea() {
		setAccumulatePreTick(4);
	}

	@Override
	public final IMantraData getData(NBTTagCompound origin, World world, ICaster caster) {
		return new SquareData();
	}

	@Override
	public void onSpelling(World world, IMantraData mData, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return;
		super.onSpelling(world, mData, caster);
	}

	@Override
	public void endSpelling(World world, IMantraData mData, ICaster caster) {
		SquareData data = (SquareData) mData;
		data.markContinue(false);
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return;
		WorldTarget wr = caster.iWantBlockTarget();
		BlockPos pos = wr.getPos();
		if (pos == null) return;
		if (wr.getFace() == EnumFacing.UP) pos = pos.up();
		caster.iWantDirectCaster().setPositionVector(new Vec3d(pos));
		if (!this.isAllElementMeetMinNeed(mData)) return;
		this.init(world, (SquareData) mData, caster, pos);
		this.sendMantraDataToClient(world, data, caster);
	}

	@Override
	public boolean afterSpelling(World world, IMantraData mData, ICaster caster) {
		SquareData data = (SquareData) mData;
		int size = data.getSize();
		int delay = data.getDelay();
		if (size <= 0) {
			if (delay <= 0) return false;
			data.setDelay(delay - 1);
			return true;
		}
		if (world.isRemote) this.addAfterEffect(data, caster, size);
		if (!this.tick(world, data, caster, caster.iWantDirectCaster().getPosition())) {
			if (world.isRemote) return true;
			if (delay <= 0) return false;
			data.setDelay(delay - 1);
			return true;
		}
		return true;
	}

	@Override
	protected void initDirectLaunchMantraGrimoire(EntityGrimoire grimoire, VariableSet params) {
		grimoire.setPosition(grimoire.posX, grimoire.posY + 1, grimoire.posZ);
		SquareData squareData = (SquareData) grimoire.getMantraData();
		this.init(grimoire.world, squareData, grimoire, grimoire.getPosition());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		super.onSpellingEffect(world, data, caster);
		this.addEffectBlockIndicatorEffect(world, data, caster);
	}

	@SideOnly(Side.CLIENT)
	public void addAfterEffect(SquareData data, ICaster caster, int size) {
		if (size <= 0) return;
		if (data.getEffectMap().hasMark(MantraEffectType.MANTRA_EFFECT_1)) return;
		ICasterObject casterObject = caster.iWantDirectCaster();
		EffectMagicSquare ems = new EffectMagicSquare(casterObject.getWorld(), casterObject.asEntity(), size,
				this.getColor(data));
		ems.setCondition(MantraEffectMap.condition(caster, data, CastStatus.AFTER_SPELLING));
		data.getEffectMap().addAndMark(MantraEffectType.MANTRA_EFFECT_1, ems);
		ems.setIcon(this.getMagicCircleIcon());
	}

	public abstract void init(World world, SquareData mData, ICaster caster, BlockPos pos);

	public abstract boolean tick(World world, SquareData mData, ICaster caster, BlockPos pos);
}
