package yuzunyannn.elementalsorcery.render.effect.grimoire;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectListBufferConfusion;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityBlockMove;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

@SideOnly(Side.CLIENT)
public class EffectBlockConfusion extends Effect {

	public static final String GROUP_CONFUSION = "confusion";
	public static final EffectListBufferConfusion effectConfusion = new EffectListBufferConfusion();

	static {
		Effect.addEffectGroup(GROUP_CONFUSION, effectConfusion);
	}

	public IBlockState stateA;
	public ItemStack stackA = ItemStack.EMPTY;
	public TileEntity tileA;

	public IBlockState stateB;
	public ItemStack stackB = ItemStack.EMPTY;
	public TileEntity tileB;

	public float scale = 1;

	public boolean showA = true;
	public boolean clientSet;

	public EffectBlockConfusion(World world, Vec3d pos, IBlockState state) {
		super(world, pos.x, pos.y, pos.z);
		this.stateA = state;
		this.stackA = ItemHelper.toItemStack(state);
		this.lifeTime = 20;
	}

	public void setChangeTo(IBlockState state, boolean clientSet) {
		this.stateB = state;
		this.stackB = ItemHelper.toItemStack(state);
		this.clientSet = clientSet;
	}

	@Override
	protected String myGroup() {
		return GROUP_CONFUSION;
	}

	@Override
	public void onUpdate() {
		this.lifeTime--;
		if (this.lifeTime <= 5) {
			showA = false;
			if (clientSet) world.setBlockState(new BlockPos(posX, posY, posZ), stateB, 0x1);
		}
	}

	@Override
	protected void doRender(float partialTicks) {
		GlStateManager.pushMatrix();
		double x = getRenderX(partialTicks);
		double y = getRenderY(partialTicks);
		double z = getRenderZ(partialTicks);
		GlStateManager.translate(x, y, z);
		if (scale != 1) GlStateManager.scale(scale, scale, scale);
		GlStateManager.scale(1.05, 1.05, 1.05);
		GlStateManager.translate(0, -0.5, 0);
		try {
			BlockPos pos = new BlockPos(x, y, z);
			if (!showA && stateB != null)
				RenderEntityBlockMove.doRenderBlock(stateB, stackB, partialTicks, world, pos, null);
			else RenderEntityBlockMove.doRenderBlock(stateA, stackA, partialTicks, world, pos, tileA);
		} catch (Exception e) {
			if (tileA != null) tileA = null;
			else throw e;
		}
		GlStateManager.popMatrix();
	}

}
