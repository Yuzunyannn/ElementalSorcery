package yuzunyannn.elementalsorcery.api.computer;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

public interface IDevice extends ICalculatorObject {

	@Nonnull
	public List<IDisk> getDisks();

	@Nonnull
	public String getName();

	@Nonnull
	public UUID getUDID();

	public void notice(String method, Object... objects);
}
