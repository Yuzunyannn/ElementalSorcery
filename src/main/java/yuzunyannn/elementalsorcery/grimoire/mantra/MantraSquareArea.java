package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.ConditionEffect;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.grimoire.WantedTargetResult;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicSquare;

public abstract class MantraSquareArea extends MantraCommon {

	public static class SquareData extends MantraDataCommon {
		// 记录的数据
		protected int size = 0;
		protected int delay = 0;

		public void setSize(int size) {
			this.size = size;
		}

		public void setDelay(int delay) {
			this.delay = delay;
		}

		public int getSize() {
			return size;
		}

		public int getDelay() {
			return delay;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = super.serializeNBT();
			nbt.setInteger("size", size);
			nbt.setInteger("delay", delay);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			super.deserializeNBT(nbt);
			size = nbt.getInteger("size");
			delay = nbt.getInteger("delay");
		}
	}

	@Override
	public final IMantraData getData(NBTTagCompound origin, World world, ICaster caster) {
		return this.getSquareData(origin, world, caster);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		dataEffect.markContinue(true);
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
		WantedTargetResult wr = caster.iWantBlockTarget();
		BlockPos pos = wr.getPos();
		if (pos == null) return;
		if (wr.getFace() == EnumFacing.UP) pos = pos.up();
		caster.iWantDirectCaster().setPosition(pos.getX(), pos.getY(), pos.getZ());
		this.onAfterSpellingInit(world, data, caster, pos);
	}

	@Override
	public boolean afterSpelling(World world, IMantraData mData, ICaster caster) {
		SquareData data = (SquareData) mData;
		if (data.size <= 0) {
			if (data.delay <= 0) return false;
			data.delay = data.delay - 1;
			return true;
		}
		if (world.isRemote) this.addAfterEffect(data, caster, data.size);
		if (!this.onAfterSpellingTick(world, data, caster)) {
			if (world.isRemote) return true;
			if (data.delay <= 0) return false;
			data.delay = data.delay - 1;
			return true;
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffectFlags(World world, IMantraData data, ICaster caster, MantraEffectFlags flags) {
		return caster.hasEffectFlags(flags);
	}

	@SideOnly(Side.CLIENT)
	public void addAfterEffect(SquareData data, ICaster caster, int size) {
		if (size <= 0) return;
		if (data.hasMarkEffect(1000)) return;
		Entity entity = caster.iWantDirectCaster();
		EffectMagicSquare ems = new EffectMagicSquare(entity.world, entity, size, this.getColor(data));
		ems.setCondition(new ConditionEffect(entity, data, 1000, false));
		data.addEffect(caster, ems, 1000);
		ems.setIcon(this.getMagicCircleIcon());
	}

	public SquareData getSquareData(NBTTagCompound origin, World world, ICaster caster) {
		return new SquareData();
	}

	public abstract void onAfterSpellingInit(World world, SquareData mData, ICaster caster, BlockPos pos);

	public abstract boolean onAfterSpellingTick(World world, SquareData mData, ICaster caster);

}
