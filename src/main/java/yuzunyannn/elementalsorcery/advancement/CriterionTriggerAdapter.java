package yuzunyannn.elementalsorcery.advancement;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

/** 默认的成就条件触发适配，方便继承和完成 */
public abstract class CriterionTriggerAdapter<T extends ICriterionInstance> implements ICriterionTrigger<T> {

	protected final ResourceLocation id;
	protected final Map<PlayerAdvancements, CriterionTriggerAdapter.Listeners> listeners = Maps.<PlayerAdvancements, CriterionTriggerAdapter.Listeners>newHashMap();

	public CriterionTriggerAdapter(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener) {
		CriterionTriggerAdapter.Listeners listeners = this.listeners.get(playerAdvancementsIn);
		if (listeners == null) {
			listeners = new CriterionTriggerAdapter.Listeners(playerAdvancementsIn);
			this.listeners.put(playerAdvancementsIn, listeners);
		}
		listeners.add(listener);
	}

	@Override
	public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener) {
		CriterionTriggerAdapter.Listeners listeners = this.listeners.get(playerAdvancementsIn);
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty()) this.listeners.remove(playerAdvancementsIn);
		}
	}

	@Override
	public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	@Override
	abstract public T deserializeInstance(JsonObject json, JsonDeserializationContext context);

	/** 对内容进行测试，该函数是在try环境中运行的，出现异常认为返回false */
	abstract boolean test(EntityPlayerMP player, T criterion, Object... objs);

	/** 触发这个成就的内容 */
	public void trigger(EntityPlayerMP player, Object... objs) {
		CriterionTriggerAdapter.Listeners listeners = this.listeners.get(player.getAdvancements());
		if (listeners != null) listeners.trigger(player, objs);
	}

	private class Listeners {
		private final PlayerAdvancements playerAdvancements;
		private final Set<ICriterionTrigger.Listener<T>> listeners = Sets.<ICriterionTrigger.Listener<T>>newHashSet();

		public Listeners(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}

		public void add(ICriterionTrigger.Listener<T> listener) {
			this.listeners.add(listener);
		}

		public void remove(ICriterionTrigger.Listener<T> listener) {
			this.listeners.remove(listener);
		}

		public void trigger(EntityPlayerMP player, Object... objs) {
			List<ICriterionTrigger.Listener<T>> list = null;

			for (ICriterionTrigger.Listener<T> listener : this.listeners) {
				try {
					if (test(player, (T) listener.getCriterionInstance(), objs)) {
						if (list == null) list = Lists.<ICriterionTrigger.Listener<T>>newArrayList();
						list.add(listener);
					}
				} catch (Exception e) {}
			}
			if (list != null) {
				for (ICriterionTrigger.Listener<T> listener1 : list) {
					listener1.grantCriterion(this.playerAdvancements);
				}
			}
		}
	}

}
