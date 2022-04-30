package yuzunyannn.elementalsorcery.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.container.gui.GuiResearch;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.scrappy.EffectTopic;

public class BlockResearcher extends Block {

	public static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 0.625, 1);

	public BlockResearcher() {
		super(Material.ROCK, MapColor.QUARTZ);
		this.setHarvestLevel("pickaxe", 3);
		this.setTranslationKey("researcher");
		this.setHardness(8.5f);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		if (fortune < 3) return Items.AIR;
		if (rand.nextFloat() < 0.98) return Items.AIR;
		return super.getItemDropped(state, rand, fortune);
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return true;
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_RESEARCHER, worldIn, pos.getX(), pos.getY(),
				pos.getZ());
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(4) != 0) return;
		Minecraft mc = Minecraft.getMinecraft();
		GuiResearch gui = null;
		if (mc.currentScreen instanceof GuiResearch) gui = (GuiResearch) mc.currentScreen;
		if (gui == null || !gui.container.pos.equals(pos)) return;
		List<GuiResearch.TopicInfo> infos = gui.getTopics();
		String type = infos.get(rand.nextInt(infos.size())).topic.getTypeName();
		double x, y, z;
		x = pos.getX() + 0.2 + rand.nextFloat() * 0.8;
		y = pos.getY() + 0.7 + rand.nextFloat() * 0.2f - 0.1f;
		z = pos.getZ() + 0.2 + rand.nextFloat() * 0.8;
		EffectTopic effect = new EffectTopic(worldIn, type, x, y, z);
		Effect.addEffect(effect);
	}

}
