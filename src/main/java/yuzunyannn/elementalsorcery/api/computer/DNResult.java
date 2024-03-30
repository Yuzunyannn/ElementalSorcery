package yuzunyannn.elementalsorcery.api.computer;

import java.util.concurrent.CompletableFuture;

public class DNResult extends DNBase {

	// now is invalid maybe valid in furture
	public static CompletableFuture<DNResult> unavailable() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.UNAVAILABLE));
	}

	// is invalid, means cannot handle this request
	public static CompletableFuture<DNResult> invalid() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.INVALID));
	}

	// handled success
	public static CompletableFuture<DNResult> success() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.REFUSE));
	}

	// handled fail
	public static CompletableFuture<DNResult> fail() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.FAIL));
	}

	// can handle but refuse this request
	public static CompletableFuture<DNResult> refuse() {
		return CompletableFuture.completedFuture(new DNResult(DNResultCode.REFUSE));
	}

	public static DNResult of(DNResultCode code) {
		return new DNResult(code);
	}

	public final DNResultCode code;

	public DNResult(DNResultCode code) {
		this.code = code;
	}

}
