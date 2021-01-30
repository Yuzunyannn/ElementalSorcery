package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;

public abstract class MantraSquareAreaAdv extends MantraSquareArea {

	protected static class CollectInfo {
		public ElementStack element = ElementStack.EMPTY;
		public int maxNeed;
		public int minNeed;
	}

	protected List<CollectInfo> collectList = new ArrayList<>();

	protected void addElementCollect(ElementStack need, int maxNeed, int minNeed) {
		if (need.isEmpty()) return;
		CollectInfo info = null;
		for (CollectInfo i : collectList) if (i.element.getElement() == need.getElement()) {
			info = i;
			break;
		}
		if (info == null) {
			info = new CollectInfo();
			collectList.add(info);
		}
		if (collectList.size() == 1) {
			if (maxNeed <= 0) throw new IllegalArgumentException("第一个元素的最大收集必须是正整数！");
		}
		info.element = need;
		info.maxNeed = maxNeed;
		info.minNeed = minNeed;
	}

	@Override
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {
		SquareData mData = (SquareData) data;
		if (speedTick % this.getAccumulatePreTick() != 0) return;
		boolean firstInfo = true;
		for (CollectInfo info : collectList) {
			ElementStack estack = mData.get(info.element.getElement());
			if (info.maxNeed > 0 && estack.getCount() >= info.maxNeed) {
				// 第一个元素是主元素，收集到指标后就不再进行收集了
				if (firstInfo) break;
				else continue;
			}
			ElementStack stack = caster.iWantSomeElement(info.element, true);
			mData.add(stack);
			if (firstInfo) {
				mData.setProgress(estack.getCount() / (float) info.maxNeed);
				// 第一个元素下，如果不存在，不进行其他元素的收获
				if (estack.isEmpty()) break;
			}
			firstInfo = false;
		}
	}

	@Override
	public void onAfterSpellingInit(World world, SquareData mData, ICaster caster, BlockPos pos) {
		for (CollectInfo info : collectList) {
			ElementStack estack = mData.get(info.element.getElement());
			if (estack.getCount() < info.minNeed) return;
		}
		this.init(world, (SquareData) mData, caster, pos);
	}

	@Override
	public boolean onAfterSpellingTick(World world, SquareData mData, ICaster caster) {
		Entity entity = caster.iWantDirectCaster();
		return this.tick(world, (SquareData) mData, caster, entity.getPosition());
	}

	public int getAccumulatePreTick() {
		return 5;
	}

	public abstract void init(World world, SquareData mData, ICaster caster, BlockPos pos);

	public abstract boolean tick(World world, SquareData mData, ICaster caster, BlockPos pos);

}
