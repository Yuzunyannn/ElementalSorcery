package yuzunyannn.elementalsorcery.tile.device;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.DNParams;
import yuzunyannn.elementalsorcery.api.computer.DNResultCode;

public class DeviceFeatureMap {

	protected static final Map<Class<?>, DeviceFeatureMap> FEATURE_MAP = new IdentityHashMap<>();

	public synchronized static DeviceFeatureMap getOrCreate(Class<?> clazz) {
		DeviceFeatureMap feature = FEATURE_MAP.get(clazz);
		if (feature == null) FEATURE_MAP.put(clazz, feature = new DeviceFeatureMap(clazz));
		if (ESAPI.isDevelop) feature = new DeviceFeatureMap(clazz);
		return feature;
	}

	protected static class Call {

		protected final Method[] methods;

		public Call(List<Method> methods) {
			Iterator<Method> iter = methods.iterator();
			while (iter.hasNext()) {
				Method method = iter.next();
				method.setAccessible(true);
			}
			methods.sort((a, b) -> b.getParameterCount() - a.getParameterCount());
			this.methods = methods.toArray(new Method[methods.size()]);
		}

		boolean isEmpty() {
			return methods.length == 0;
		}

		public Object invoke(Object self, DNParams params) throws ReflectiveOperationException {
			for (Method method : methods) {
				Object[] args = checkAndGetMeetParams(method, params);
				if (args != null) {
					Object ret = method.invoke(self, args);
					if (ret == null) return DNResultCode.SUCCESS;
					return ret;
				}
			}
			return DNResultCode.REFUSE;
		}

		public final static Object[] EMPTY = new Object[0];

		public Object[] checkAndGetMeetParams(Method method, DNParams params) {
			int paramterCount = method.getParameterCount();
			if (paramterCount == 0) return EMPTY;
			if (paramterCount > params.size()) return null;
			Parameter[] paramaters = method.getParameters();
			Object[] objs = new Object[paramterCount];
			for (int i = 0; i < paramterCount; i++) {
				Parameter paramater = paramaters[i];
				Object obj = params.ask(String.valueOf(i + 1), paramater.getType());
				if (obj == null) {
					obj = params.ask(paramater.getName(), paramater.getType());
					if (obj == null) return null;
				}
				objs[i] = obj;
			}
			return objs;
		}

	}

	protected final Map<String, Call> callMap = new HashMap<>();

	public DeviceFeatureMap(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		Map<String, List<Method>> map = new HashMap<>();
		for (Method method : methods) {
			DeviceFeature featrue = method.getAnnotation(DeviceFeature.class);
			if (featrue == null) continue;
			String[] keys = toIds(method, featrue);
			for (String key : keys) {
				List<Method> calls = map.get(key);
				if (calls == null) map.put(key, calls = new ArrayList<>());
				calls.add(method);
			}
		}

		for (String key : map.keySet()) {
			Call call = new Call(map.get(key));
			if (call.isEmpty()) continue;
			callMap.put(key, call);
		}
	}

	protected String[] toIds(Method method, DeviceFeature featrue) {
		String[] ids = featrue.id();
		if (ids.length > 0) return ids;
		String methoName = method.getName();
		return new String[] { methoName };
	}

	public boolean has(String id) {
		return callMap.containsKey(id);
	}

	public Object invoke(Object self, String id, DNParams params) {
		Call call = callMap.get(id);
		if (call == null) return DNResultCode.INVALID;
		try {
			return call.invoke(self, params);
		} catch (Exception e) {
			callMap.remove(id);
			ESAPI.logger.warn("invoke err!", e);
			return DNResultCode.FAIL;
		}
	}

}
