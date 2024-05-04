package yuzunyannn.elementalsorcery.api.computer;

public class DNResult extends DNBase {

	public static DNResult unavailable() {
		return new DNResult(DNResultCode.UNAVAILABLE);
	}

	public static DNResult invalid() {
		return new DNResult(DNResultCode.INVALID);
	}

	public static DNResult success() {
		return new DNResult(DNResultCode.SUCCESS);
	}

	public static DNResult fail() {
		return new DNResult(DNResultCode.FAIL);
	}

	public static DNResult refuse() {
		return new DNResult(DNResultCode.REFUSE);
	}

	// handled obj
	public static DNResult byRet(Object ret) {
		if (ret instanceof DNResult) return (DNResult) ret;
		else if (ret instanceof DNResultCode) return DNResult.of((DNResultCode) ret);
		else if (ret instanceof Boolean) {
			if ((Boolean) ret) return DNResult.success();
			else return DNResult.fail();
		} else if (ret != null) {
			DNResult result = DNResult.of(DNResultCode.SUCCESS);
			result.setReturn(ret);
			return result;
		}
		return refuse();
	}

	public static DNResult of(DNResultCode code) {
		return new DNResult(code);
	}

	public final DNResultCode code;

	public DNResult(DNResultCode code) {
		this.code = code;
	}

	public boolean isSuccess() {
		return code == DNResultCode.SUCCESS;
	}

}
