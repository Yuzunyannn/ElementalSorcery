package yuzunyannn.elementalsorcery.api.computer;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IDevice extends ICalculatorObject {

	@Nonnull
	public List<IDisk> getDisks();

	@Nullable
	public IMemory getMemory();

	@Nonnull
	public String getName();

	@Nonnull
	public UUID getUDID();

	public void notice(String method, Object... objects);
}
