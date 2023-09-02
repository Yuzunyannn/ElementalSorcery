package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet.Variable;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;

/** 积累类型，形如：收集一定内容，然后触发一次(可循环) */
public abstract class MantraTypeAccumulative extends MantraCommon {

	public static final Variable<Boolean> COLLECTING_FLAG = new Variable<>("@clFlag", VariableSet.BOOL);
	public static final Variable<Integer> ITICK = new Variable<>("@inTik", VariableSet.INT);
	public static final Variable<Integer> ITICKR = new Variable<>("@inTikR", VariableSet.INT);

	@SideOnly(Side.CLIENT)
	static public String createCollectInfomation(CollectInfo info, float charge) {
		String eName = info.eStack.getDisplayName();
		String power = String.valueOf(info.eStack.getPower());
		charge = charge * 20;
		String _charge = charge < 0.1 ? String.format("%.2f", charge) : String.format("%.1f", charge);
		String min = String.valueOf(info.minNeed);
		String max = String.valueOf(info.maxNeed < 0 || info.maxNeed == Integer.MAX_VALUE ? "∞" : info.maxNeed);
		if (info.minNeed == info.maxNeed) return I18n.format("info.grimoire.collect.sub2", eName, power, _charge, max);
		return I18n.format("info.grimoire.collect.sub1", eName, power, _charge, min, max);
	}

	protected static class CollectInfo {

		static public CollectInfo create(ElementStack eStack, int max, int min) {
			CollectInfo info = new CollectInfo();
			info.eStack = eStack;
			info.maxNeed = max;
			info.minNeed = min;
			return info;
		}

		public ElementStack eStack = ElementStack.EMPTY;
		public int maxNeed;
		public int minNeed;

		public String toString() {
			return eStack.toString() + " -- " + String.format("[%d,%d]", minNeed, maxNeed);
		}
	}

	/** 收集规则：普通积攒型 */
	protected static class CollectRule {

		protected int accumulatePreTick = 1;

		protected List<CollectInfo> collectList = new ArrayList<>();

		protected float potentCollectTick = 0;

		protected float potentPowerMax = 0;

		protected boolean specialCollectionRule;

		public CollectRule() {
			specialCollectionRule = this.getClass() != CollectRule.class;
		}

		public void setAccumulatePreTick(int accumulatePreTick) {
			this.accumulatePreTick = accumulatePreTick;
		}

		public void addElementCollect(ElementStack need, int maxNeed, int minNeed) {
			if (need.isEmpty()) return;
			CollectInfo info = null;
			for (CollectInfo i : collectList) if (i.eStack.getElement() == need.getElement()) {
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
			info.eStack = need;
			info.maxNeed = maxNeed;
			info.minNeed = minNeed;
		}

		public void setPotentPowerCollect(float tickGet, float max) {
			this.potentCollectTick = tickGet;
			this.potentPowerMax = max;
		}

		/** 是否所有元素都满足了最小值 */
		public boolean isAllElementMeetMinNeed(IMantraData data) {
			MantraDataCommon mData = (MantraDataCommon) data;
			for (CollectInfo info : collectList) {
				ElementStack estack = mData.get(info.eStack.getElement());
				if (estack.getCount() < info.minNeed) return false;
			}
			return true;
		}

		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
			boolean isFirst = true;
			for (CollectInfo info : collectList) {
				String value = createCollectInfomation(info, (info.eStack.getCount() / (float) this.accumulatePreTick));
				if (isFirst) {
					if (specialCollectionRule) value += "[*]";
					tooltip.add(TextFormatting.LIGHT_PURPLE + value);
				} else tooltip.add(TextFormatting.DARK_PURPLE + value);
				isFirst = false;
			}
		}

		/** 真正的是收集进度，收集进度满了就不进行收集了，节约计算资源 */
		public float calcRealCollectProgress(World world, MantraDataCommon mData, ICaster caster) {
			if (collectList.isEmpty()) return 0;
			CollectInfo info = collectList.get(0);
			ElementStack eStack = mData.get(info.eStack.getElement());
			return eStack.getCount() / (float) getMaxNeed(info, world, mData, caster);
		}

		/** 当前的使用进度 */
		public float calcCurrProgress(World world, MantraDataCommon mData, ICaster caster) {
			return calcRealCollectProgress(world, mData, caster);
		}

		/** 当被重置，出现在循环中 */
		public void onReset(MantraDataCommon mData) {
			for (CollectInfo info : collectList) mData.remove(info.eStack.getElement());
		}

		/** 获取某个元素的最大需求量，允许动态切换量 */
		public int getMaxNeed(CollectInfo info, World world, MantraDataCommon mData, ICaster caster) {
			return info.maxNeed;
		}
	}

	/** 收集规则：循环触发型 */
	protected static class CollectRuleRepeated extends CollectRule {

		protected int interval = 20;

		public CollectRuleRepeated() {
			specialCollectionRule = this.getClass() != CollectRuleRepeated.class;
		}

		public void setInterval(int interval) {
			this.interval = interval;
		}

		public int getInterval(World world, MantraDataCommon mData, ICaster caster) {
			return mData.has(INTERVAL) ? mData.get(INTERVAL) : this.interval;
		}

		@Override
		public float calcCurrProgress(World world, MantraDataCommon mData, ICaster caster) {
			int tick = caster.iWantKnowCastTick();
			if (mData.get(ITICK) != tick) {
				mData.set(ITICK, tick);
				mData.set(ITICKR, mData.get(ITICKR) + 1);
			}
			float i = this.getInterval(world, mData, caster);
			float f = super.calcCurrProgress(world, mData, caster);
			return i > 0 ? Math.min(mData.get(ITICKR) / i, f) : f;
		}

		@Override
		public void onReset(MantraDataCommon mData) {
			super.onReset(mData);
			mData.remove(ITICKR);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
			boolean isFirst = true;
			for (CollectInfo info : collectList) {
				float avgChargePerTick = Math.min((info.eStack.getCount() / (float) this.accumulatePreTick),
						info.maxNeed / (float) this.interval);
				String value = "⚪" + createCollectInfomation(info, avgChargePerTick);
				if (isFirst) tooltip.add(TextFormatting.LIGHT_PURPLE + value);
				else tooltip.add(TextFormatting.DARK_PURPLE + value);
				isFirst = false;
			}
		}
	}

	protected CollectRule mainRule = new CollectRule();

	public MantraTypeAccumulative() {
	}

	public void setMainRule(@Nonnull CollectRule mainRule) {
		this.mainRule = mainRule;
	}

	public void setAccumulatePreTick(int accumulatePreTick) {
		this.mainRule.accumulatePreTick = accumulatePreTick;
	}

	protected void addElementCollect(ElementStack need, int maxNeed, int minNeed) {
		this.mainRule.addElementCollect(need, maxNeed, minNeed);
	}

	protected void setPotentPowerCollect(float tickGet, float max) {
		this.mainRule.setPotentPowerCollect(tickGet, max);
	}

	public boolean isAllElementMeetMinNeed(IMantraData data) {
		return this.mainRule.isAllElementMeetMinNeed(data);
	}

	// 元素反应堆的初始化
	protected void initAndAddDefaultMantraLauncher(double speedRatio) {
		CollectRule rule = this.mainRule;
		ArrayList<ElementStack> list = new ArrayList<>();
		float mainRatio = 0;
		for (int i = 0; i < rule.collectList.size(); i++) {
			CollectInfo info = rule.collectList.get(i);
			ElementStack eStack = info.eStack.copy();
			if (info.maxNeed < 0) eStack.setCount((int) (info.eStack.getCount() * mainRatio));
			else eStack.setCount(info.maxNeed);
			if (i == 0) mainRatio = info.maxNeed / (float) info.eStack.getCount();
			else {
				int min = Math.max(info.minNeed, 0);
				int max = eStack.getCount();
				eStack.setCount(min + (max - min) * 3 / 4);
			}
			list.add(eStack);
		}
		setDirectLaunchFragmentMantraLauncher(list, 3, speedRatio, null);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;
		CollectRule rule = this.getCurrCollectRule(world, mData, caster);
		if (mData.getProgress() >= 1) {
			boolean reset = onCollectFinish(world, mData, caster);
			if (reset) {
				rule.onReset(mData);
				mData.remove(COLLECTING_FLAG);
				mData.setProgress(0);
			} else mData.setProgress(rule.calcCurrProgress(world, mData, caster));
			if (mData.getProgress() >= 1) {
				if (world.isRemote && mData.isMarkContinue()) onSpellingEffect(world, data, caster);
				return;
			}
		}
		if (!mData.has(COLLECTING_FLAG)) {
			mData.set(COLLECTING_FLAG, true);
			this.onCollectStart(world, mData, caster);
		}
		if (rule.calcRealCollectProgress(world, mData, caster) < 1) super.onSpelling(world, data, caster);
		else if (world.isRemote && mData.isMarkContinue()) onSpellingEffect(world, data, caster);
		mData.setProgress(rule.calcCurrProgress(world, mData, caster));
	}

	@Override
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {
		MantraDataCommon mData = (MantraDataCommon) data;
		CollectRule rule = this.getCurrCollectRule(world, mData, caster);

		if (speedTick % rule.accumulatePreTick != 0) return;

		if (rule.potentCollectTick > 0 && mData.getProgress() < 1) {
			float pp = mData.get(POTENT_POWER);
			if (pp < rule.potentPowerMax || rule.potentPowerMax == -1) {
				float potent = caster.iWantBePotent(rule.potentCollectTick, false);
				if (potent > 0) mData.set(POTENT_POWER, pp + potent * rule.potentCollectTick);
			}
		}

		boolean firstInfo = true;
		for (CollectInfo info : rule.collectList) {
			ElementStack estack = mData.get(info.eStack.getElement());
			int maxNeed = rule.getMaxNeed(info, world, mData, caster);
			if (maxNeed > 0 && estack.getCount() >= maxNeed) continue;
			ElementStack stack = caster.iWantSomeElement(info.eStack, true);
			mData.add(stack);
			if (firstInfo) {
				// 第一个元素下，如果不存在，不进行其他元素的收获
				if (estack.isEmpty()) break;
				mData.markContinue(true);
			}
			firstInfo = false;
		}
	}

	protected boolean onCollectFinish(World world, MantraDataCommon mData, ICaster caster) {
		return false;
	}

	protected void onCollectStart(World world, MantraDataCommon mData, ICaster caster) {

	}

	@Nonnull
	protected CollectRule getCurrCollectRule(World world, MantraDataCommon mData, ICaster caster) {
		return mainRule;
	}

	@SideOnly(Side.CLIENT)
	protected void addRuleInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		this.mainRule.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		addRuleInformation(stack, worldIn, tooltip, flagIn);
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
}
