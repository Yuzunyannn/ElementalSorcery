package yuzunyannn.elementalsorcery.api.computer;

import java.util.concurrent.CompletableFuture;

public class DNResult extends DNBase {

	public static CompletableFuture<DNResult> unavailable() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.UNAVAILABLE));
	}

	public static CompletableFuture<DNResult> invalid() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.INVALID));
	}

	public static CompletableFuture<DNResult> success() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.SUCCESS));
	}

	public static CompletableFuture<DNResult> fail() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.FAIL));
	}

	public static CompletableFuture<DNResult> refuse() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.REFUSE));
	}

	// handled obj
	public static CompletableFuture<DNResult> byRet(Object ret) {
		if (ret instanceof DNResult) return CompletableFuture.completedFuture((DNResult) ret);
		else if (ret instanceof DNResultCode) return CompletableFuture.completedFuture(DNResult.of((DNResultCode) ret));
		else if (ret instanceof CompletableFuture) {
			CompletableFuture toret = new CompletableFuture();
			((CompletableFuture) ret).thenAccept(r -> {
				byRet(r).thenAccept(result -> toret.complete(toret));
			});
			return toret;
		} else if (ret instanceof Boolean) {
			if ((Boolean) ret) return DNResult.success();
			else return DNResult.fail();
		} else if (ret != null) {
			DNResult result = DNResult.of(DNResultCode.SUCCESS);
			result.setReturn(ret);
			return CompletableFuture.completedFuture(result);
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
