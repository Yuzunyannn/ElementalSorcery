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
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;

/** 持续类型，形如：一直释放，间隔一段时间消耗一次 */
public abstract class MantraTypePersistent extends MantraCommon {

	@SideOnly(Side.CLIENT)
	static public String createCollectInfomation(CollectInfo info, CollectRule rule) {
		String eName = info.eStack.getDisplayName();
		String power = String.valueOf(info.eStack.getPower());
		float charge = info.eStack.getCount() * 20f / rule.interval;
		String _charge = charge < 0.01 ? String.format("%.4f", charge) : String.format("%.2f", charge);
		return I18n.format("info.grimoire.collect.sub3", eName, power, _charge);
	}

	protected static class CollectInfo {

		static public CollectInfo create(ElementStack eStack, boolean must) {
			CollectInfo info = new CollectInfo();
			info.eStack = eStack;
			info.must = must;
			return info;
		}

		public ElementStack eStack = ElementStack.EMPTY;
		public boolean must;

		public String toString() {
			return eStack.toString() + " -- " + must;
		}
	}

	/** 持续规则 */
	protected static class CollectRule {

		protected int interval = 1;

		protected List<CollectInfo> collectList = new ArrayList<>();

		public void setInterval(int interval) {
			this.interval = interval;
		}

		public void addElementCollect(ElementStack need, boolean must) {
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
				if (!must) throw new IllegalArgumentException("第一个元素的必须must！");
			}
			info.eStack = need;
			info.must = must;
		}

		public void onReset(MantraDataCommon mData) {
			for (CollectInfo info : collectList) mData.remove(info.eStack.getElement());
		}

		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
			for (CollectInfo info : collectList) {
				String value = createCollectInfomation(info, this);
				if (info.must) tooltip.add(TextFormatting.LIGHT_PURPLE + value);
				else tooltip.add(TextFormatting.DARK_PURPLE + value);
			}
		}
	}

	protected CollectRule mainRule = new CollectRule();

	public MantraTypePersistent() {
	}

	public void setMainRule(@Nonnull CollectRule mainRule) {
		this.mainRule = mainRule;
	}

	protected void addElementCollect(ElementStack need, boolean must) {
		this.mainRule.addElementCollect(need, must);
	}

	public void setInterval(int interval) {
		this.mainRule.setInterval(interval);
	}

	@Nonnull
	protected CollectRule getCurrCollectRule(World world, MantraDataCommon mData, ICaster caster) {
		return mainRule;
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;
		CollectRule rule = this.getCurrCollectRule(world, mData, caster);
		int tick = caster.iWantKnowCastTick();
		if (tick % rule.interval == 0 || !mData.isMarkContinue()) {
			if (mData.isMarkContinue()) rule.onReset(mData);
			onCollectElement(world, mData, caster, tick);
		}
		if (mData.isMarkContinue()) {
			onUpdate(world, mData, caster);
			if (world.isRemote) onSpellingEffect(world, mData, caster);
		}
	}

	@Override
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {
		MantraDataCommon mData = (MantraDataCommon) data;
		CollectRule rule = this.getCurrCollectRule(world, mData, caster);
		mData.markContinue(false);
		for (CollectInfo info : rule.collectList) {
			ElementStack get = caster.iWantSomeElement(info.eStack, false);
			if (get.isEmpty() && info.must) return;
		}
		mData.markContinue(true);
		for (CollectInfo info : rule.collectList) mData.add(caster.iWantSomeElement(info.eStack, true));
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;
		CollectRule rule = this.getCurrCollectRule(world, mData, caster);
		mData.markContinue(false);
		for (CollectInfo info : rule.collectList) mData.remove(info.eStack.getElement());
	}

	protected void onUpdate(World world, IMantraData data, ICaster caster) {

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
