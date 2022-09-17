package yuzunyannn.elementalsorcery.render.effect;

import java.util.function.Function;

public interface IConditionEffect {

	public void setCondition(Function<Void, Boolean> condition);

	public Function<Void, Boolean> getCondition();

}
