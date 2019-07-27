package yuzunyannn.elementalsorcery.tile.altar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelSupremeCraftingTable;

public class TileSupremeCraftingTable extends TileStaticMultiBlock implements ITickable {

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.LARGE_ALTAR, this, new BlockPos(0, -4, 0));
	}

	@Override
	public void update() {
		if (!this.isIntact())
			return;
		if (this.world.isRemote)
			this.clientRender();
	}

	public float roate;
	public float prevRoate;
	public float legR;
	public float prevLegR;
	static final float ROATE_RATE = 1.0f;
	static final float LEG_RATE = 0.4f;
	static final float LEG_MAX = 16f;

	private void clientRender() {
		this.prevRoate = this.roate;
		this.prevLegR = this.legR;

		EntityPlayer entityplayer = this.world.getClosestPlayer((double) ((float) this.pos.getX() + 0.5F),
				(double) ((float) this.pos.getY() + 0.5F), (double) ((float) this.pos.getZ() + 0.5F), 3.5D, false);
		if (entityplayer != null) {
			if (legR < LEG_MAX)
				legR += LEG_RATE;
			roate += ROATE_RATE;
			this.clientParticle();
		} else {
			if (roate > 0) {
				this.clientParticle();
				roate += ROATE_RATE;
				if (roate < 180) {
					if (legR < LEG_MAX)
						legR += LEG_RATE;
				} else {
					if (roate % 360 > 360 - LEG_MAX / LEG_RATE * ROATE_RATE || legR < LEG_MAX) {
						legR -= LEG_RATE;
					}
					if (legR <= 0) {
						prevRoate = roate = 0;
						prevLegR = legR = 0;
					}
				}
			}
		}
	}

	private void clientParticle() {
		if (EventClient.tick % 10 != 0)
			return;
		float randRoate = EventClient.rand.nextFloat() * 2 * 3.1415926f;
		float r = EventClient.rand.nextFloat() * 0.35f;
		float x = this.pos.getX() + 0.5f + r * MathHelper.sin(randRoate);
		float z = this.pos.getZ() + 0.5f + r * MathHelper.cos(randRoate);

		float y = this.pos.getY() + ModelSupremeCraftingTable.roateToHight(roate / 180.0f * 3.1514926f) * 0.03125f
				+ 0.5f;

		Particle particle = new ParticleFirework.Spark(this.world, x, y, z, 0, 0, 0,
				Minecraft.getMinecraft().effectRenderer);
		Minecraft.getMinecraft().effectRenderer.addEffect(particle);
	}
}
