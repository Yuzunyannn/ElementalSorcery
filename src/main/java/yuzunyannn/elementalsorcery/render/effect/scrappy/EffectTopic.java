package yuzunyannn.elementalsorcery.render.effect.scrappy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.elf.research.Topic;
import yuzunyannn.elementalsorcery.render.effect.Effect;

@SideOnly(Side.CLIENT)
public class EffectTopic extends Effect {

	protected float alpha, prevAlpha;
	protected float size, prevSize;
	protected float rotate;
	protected Topic topic;

	public EffectTopic(World worldIn, String type, double posXIn, double posYIn, double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
		topic = Topic.create(type);
		this.lifeTime = 30 + rand.nextInt(20);
		this.size = rand.nextFloat() * 0.2f + 0.1f;
		this.rotate = rand.nextFloat() * 360;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevAlpha = this.alpha;
		this.prevSize = this.size;
		if (this.lifeTime < 10) this.alpha = this.lifeTime / 10f;
		else this.alpha += (1 - this.alpha) * 0.1f;
		this.size += (0.6f - this.size) * 0.05f;
		topic.update(true);
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(posX, posY, posZ);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(rotate, 0, 0, 1);
		float a = RenderFriend.getPartialTicks(alpha, prevAlpha, partialTicks);
		float s = RenderFriend.getPartialTicks(size, prevSize, partialTicks);
		topic.render(Minecraft.getMinecraft(), s, a, partialTicks);
		GlStateManager.popMatrix();
	}

}
