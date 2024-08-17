package yuzunyannn.elementalsorcery.api.computer;

import yuzunyannn.elementalsorcery.api.util.StateCode;

public class DNResult extends DNBase {

	public static DNResult unavailable() {
		return new DNResult(StateCode.UNAVAILABLE);
	}

	public static DNResult invalid() {
		return new DNResult(StateCode.INVALID);
	}

	public static DNResult success() {
		return new DNResult(StateCode.SUCCESS);
	}

	public static DNResult fail() {
		return new DNResult(StateCode.FAIL);
	}

	public static DNResult refuse() {
		return new DNResult(StateCode.REFUSE);
	}

	// handled obj
	public static DNResult byRet(Object ret) {
		if (ret instanceof DNResult) return (DNResult) ret;
		else if (ret instanceof StateCode) return DNResult.of((StateCode) ret);
		else if (ret instanceof Boolean) {
			if ((Boolean) ret) return DNResult.success();
			else return DNResult.fail();
		} else if (ret != null) {
			DNResult result = DNResult.of(StateCode.SUCCESS);
			result.setReturn(ret);
			return result;
		}
		return refuse();
	}

	public static DNResult of(StateCode code) {
		return new DNResult(code);
	}

	public final StateCode code;
	protected Object ret;

	public DNResult(StateCode code) {
		this.code = code;
	}

	public boolean isSuccess() {
		return code == StateCode.SUCCESS;
	}

	public <T> void setReturn(T obj) {
		ret = obj;
	}

	public <T> T getReturn(Class<T> cls) {
		if (ret == null) return null;
		if (cls.isAssignableFrom(ret.getClass())) return (T) ret;
		return null;
	}

	public <T> T getReturn() {
		return (T) ret;
	}

	public void setNetworkRoute(DeviceNetworkRoute route) {

	}

}
