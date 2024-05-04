package yuzunyannn.elementalsorcery.nodegui;

import javax.annotation.Nullable;

public interface IAutoCompletable {
	@Nullable
	GInputShift tryComplete(String str, int cursor);
}
