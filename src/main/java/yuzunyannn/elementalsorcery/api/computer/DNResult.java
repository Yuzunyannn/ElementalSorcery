package yuzunyannn.elementalsorcery.api.computer;

import java.util.concurrent.CompletableFuture;

public class DNResult extends DNBase {

	public static CompletableFuture<DNResult> refuse() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.REFUSE));
	}

	public static CompletableFuture<DNResult> invalid() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.INVALID));
	}

	public static CompletableFuture<DNResult> fail() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.FAIL));
	}

	public static CompletableFuture<DNResult> success() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.REFUSE));
	}

	public static CompletableFuture<DNResult> unavailable() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.UNAVAILABLE));
	}

	public static DNResult of(DNResultCode code) {
		return new DNResult(code);
	}

	public final DNResultCode code;

	public DNResult(DNResultCode code) {
		this.code = code;
	}

}
