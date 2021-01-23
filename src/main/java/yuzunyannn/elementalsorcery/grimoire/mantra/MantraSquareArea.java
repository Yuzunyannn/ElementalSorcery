package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.ConditionEffect;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectLookAt;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircle;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircleIcon;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicSquare;
import yuzunyannn.elementalsorcery.util.NBTTag;

public abstract class MantraSquareArea extends MantraCommon {

	public static class SquareData extends MantraDataCommon {

		// 处理的数据
		protected float progress = 0;

		// 记录的数据
		protected Map<Element, ElementStack> eMap = new HashMap<>();
		protected int size = 0;
		protected int delay = 0;

		public void setSize(int size) {
			this.size = size;
		}

		public void setProgress(float progress) {
			this.progress = progress;
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

		public float getProgress() {
			return progress;
		}

		public void addElement(ElementStack estack) {
			if (estack.isEmpty()) return;
			ElementStack origin = this.getElement(estack.getElement());
			if (origin.isEmpty()) origin.become(estack);
			else origin.grow(estack);
		}

		public ElementStack getElement(Element element) {
			ElementStack estack = eMap.get(element);
			if (estack == null) eMap.put(element, estack = ElementStack.EMPTY.copy());
			return estack;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("size", size);
			nbt.setInteger("delay", delay);
			if (!eMap.isEmpty()) {
				NBTTagList list = new NBTTagList();
				for (ElementStack estack : eMap.values()) if (!estack.isEmpty()) list.appendTag(estack.serializeNBT());
				nbt.setTag("eles", list);
			}
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			size = nbt.getInteger("size");
			delay = nbt.getInteger("delay");
			eMap.clear();
			NBTTagList list = nbt.getTagList("eles", NBTTag.TAG_COMPOUND);
			for (NBTBase base : list) this.addElement(new ElementStack((NBTTagCompound) base));
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
		Map.Entry<BlockPos, EnumFacing> entry = caster.iWantBlockTarget();
		if (entry == null) return;
		BlockPos pos = entry.getKey();
		if (entry.getValue() == EnumFacing.UP) pos = pos.up();
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
		if (world.isRemote) {
			this.addAfterEffect(data, caster, data.size);
			return true;
		}
		if (!this.onAfterSpellingTick(world, data, caster)) {
			if (data.delay <= 0) return false;
			data.delay = data.delay - 1;
			return true;
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData mData, ICaster caster) {
		super.onSpellingEffect(world, mData, caster);
		if (!caster.hasEffectFlags(MantraEffectFlags.INDICATOR)) return;
		SquareData data = (SquareData) mData;
		if (!data.isMarkContinue()) return;
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		if (caster.iWantCaster() == Minecraft.getMinecraft().player) if (!dataEffect.hasMarkEffect(1))
			dataEffect.addEffect(caster, new EffectLookAt(world, caster, this.getColor(mData)), 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getProgressRate(World world, IMantraData mData, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return -1;
		SquareData data = (SquareData) mData;
		return data.progress;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EffectMagicCircle getEffectMagicCircle(World world, EntityLivingBase entity, IMantraData mData) {
		EffectMagicCircle emc = new EffectMagicCircleIcon(world, entity, this.getMagicCircleIcon());
		emc.setColor(this.getColor(mData));
		return emc;
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

	@SideOnly(Side.CLIENT)
	public ResourceLocation getMagicCircleIcon() {
		return this.getIconResource();
	}

}
