package yuzunyannn.elementalsorcery.api.computer;

import java.util.concurrent.CompletableFuture;

public interface IDeviceNoticeable {

	public CompletableFuture<DNResult> notice(String method, DNParams params);
}
