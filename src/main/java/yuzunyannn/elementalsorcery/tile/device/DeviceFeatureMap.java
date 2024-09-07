package yuzunyannn.elementalsorcery.tile.device;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.util.text.TextComponentTranslation;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.computer.DNRequest;
import yuzunyannn.elementalsorcery.api.util.StateCode;

public class DeviceFeatureMap {

	protected static final Map<Class<?>, DeviceFeatureMap> FEATURE_MAP = new IdentityHashMap<>();

	public synchronized static DeviceFeatureMap getOrCreate(Class<?> clazz) {
		DeviceFeatureMap feature = FEATURE_MAP.get(clazz);
		if (feature == null) FEATURE_MAP.put(clazz, feature = new DeviceFeatureMap(clazz));
		if (ESAPI.isDevelop) feature = new DeviceFeatureMap(clazz);
		return feature;
	}

	protected final static class Call {

		protected final Method[] methods;
		protected final DeviceFeature[] features;

		public Call(List<Method> methods) {
			Iterator<Method> iter = methods.iterator();
			while (iter.hasNext()) {
				Method method = iter.next();
				method.setAccessible(true);
			}
			methods.sort((a, b) -> b.getParameterCount() - a.getParameterCount());
			int methodSize = methods.size();
			this.methods = methods.toArray(new Method[methodSize]);
			this.features = methods.stream().map(e -> e.getAnnotation(DeviceFeature.class)).toArray(DeviceFeature[]::new);
		}

		boolean isEmpty() {
			return methods.length == 0;
		}

		public Object invoke(Object self, DNRequest request) throws ReflectiveOperationException {
			boolean insufficientPermissions = false;
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				DeviceFeature feature = features[i];
				if (feature.authority() > request.getAuthority()) {
					insufficientPermissions = true;
					continue;
				}
				Object[] args = checkAndGetMeetParams(method, request);
				if (args != null) {
					Object ret = method.invoke(self, args);
					if (ret == null) return StateCode.SUCCESS;
					return ret;
				}
			}
			if (request.isLogEnable()) {
				if (insufficientPermissions)
					request.log(new TextComponentTranslation("es.app.err.insufficientPermissions"));
				else request.log(new TextComponentTranslation("es.app.err.parameterError"));
			}
			return StateCode.REFUSE;
		}

		public final static Object[] EMPTY = new Object[0];

		public Object[] checkAndGetMeetParams(Method method, DNRequest params) {
			int paramterCount = method.getParameterCount();
			if (paramterCount == 0) return EMPTY;
			if (paramterCount > params.pSize()) return null;
			Parameter[] paramaters = method.getParameters();

			if (paramterCount == 1 && paramaters[0].getType().isAssignableFrom(DNRequest.class))
				return new Object[] { params };

			Object[] objs = new Object[paramterCount];
			for (int i = 0; i < paramterCount; i++) {
				Parameter paramater = paramaters[i];
				Object obj = params.ask(String.valueOf(i + 1), paramater.getType());
				if (obj == null) obj = params.ask(paramater.getName(), paramater.getType());
				if (obj == null) return null;
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

	public Object invoke(Object self, String id, DNRequest params) {
		Call call = callMap.get(id);
		if (call == null) return StateCode.INVALID;
		try {
			return call.invoke(self, params);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		} catch (ReflectiveOperationException e) {
			callMap.remove(id);
			ESAPI.logger.error("invoke err!", e);
			return StateCode.FAIL;
		} catch (Exception e) {
			throw e;
		}
	}

}
