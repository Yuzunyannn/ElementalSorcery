package yuzunyannn.elementalsorcery.render.effect;

import java.util.function.Function;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;

@SideOnly(Side.CLIENT)
public abstract class EffectCondition extends Effect implements IConditionEffect {

	public Function<Void, Boolean> condition;

	public EffectCondition(World world, Function<Void, Boolean> condition, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
		this.condition = condition;
	}

	public EffectCondition(World world, Vec3d pos) {
		super(world, pos.x, pos.y, pos.z);
	}

	public EffectCondition(World world, Function<Void, Boolean> condition) {
		this(world, condition, new Vec3d(0, 0, 0));
	}

	public EffectCondition(World world) {
		this(world, new Vec3d(0, 0, 0));
	}

	@Override
	public void setCondition(Function<Void, Boolean> condition) {
		this.condition = condition;
	}

	@Override
	public Function<Void, Boolean> getCondition() {
		return condition;
	}

	public boolean isEnd() {
		return condition == null ? true : (condition.apply(null) ? false : true);
	}

	@Override
	public boolean isDead() {
		return super.isDead() || isEnd();
	}

	@Override
	public void onUpdate() {

	}

	protected void renderTexRect(float x, float y, float width, float height, float u, float v, float texWidth,
			float texHeight, float textureWidth, float textureHeight, float r, float g, float b, float a, float anchorX,
			float anchorY) {
		RenderFriend.drawTexturedRectInCenter(x, y, width, height, u, v, texWidth, texHeight, textureWidth,
				textureHeight, r, g, b, a, anchorX, anchorY);
	}

	protected void drawTexturedRectInCenter(float x, float y, float width, float height, float u, float v,
			float texWidth, float texHeight, float textureWidth, float textureHeight, float r, float g, float b,
			float a) {
		RenderFriend.drawTexturedRectInCenter(x, y, width, height, u, v, texWidth, texHeight, textureWidth,
				textureHeight, r, g, b, a, 0.5f, 0.5f);
	}

	protected void renderTexRectInCenter(float x, float y, float width, float height, float r, float g, float b,
			float a) {
		RenderFriend.drawTexturedRectInCenter(x, y, width, height, 0, 0, 1, 1, 1, 1, r, g, b, a, 0.5f, 0.5f);
	}

}
