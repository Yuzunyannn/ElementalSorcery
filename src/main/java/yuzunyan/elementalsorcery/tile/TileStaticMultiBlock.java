package yuzunyan.elementalsorcery.tile;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.util.ElementHelper;
import yuzunyan.elementalsorcery.building.MultiBlock;

public abstract class TileStaticMultiBlock extends TileEntityNetwork {

	static public Random rand = new Random();

	// 祭坛是否成形
	protected boolean ok = false;
	// 多方块结构
	protected MultiBlock structure;
	// 检查时间
	protected int check_tick = 0;

	// 加载
	@Override
	public void onLoad() {
		super.onLoad();
		this.initMultiBlock();
		ok = structure.check();
	}

	/** 设置玩家 */
	public void setPlayer(EntityPlayer player) {

	}

	// 初始化多方块
	public abstract void initMultiBlock();

	// 是否完整
	public boolean isIntact() {
		check_tick++;
		if (check_tick % 40 == 0) {
			ok = structure.check();
		}
		return this.ok;
	}

	/** 根据条件，获取一个元素 */
	protected ElementStack getElementFromSpPlace(ElementStack need, BlockPos animePos) {
		for (int i = 0; i < structure.getSpecialBlockCount(); i++) {
			IElementInventory einv = ElementHelper.getElementInventory(structure.getSpecialTileEntity(i));
			if (einv == null)
				continue;
			ElementStack extract = einv.extractElement(need, true);
			if (extract.arePowerfulAndMoreThan(need)) {
				einv.extractElement(need, false);
				if (world.isRemote) {
					TileElementalCube.giveParticleElementTo(world, extract.getColor(), structure.getSpecialBlockPos(i),
							animePos, 0.2f);
				} else if (ElementHelper.isEmpty(einv)) {
					TileEntity tile = structure.getSpecialTileEntity(i);
					if (tile instanceof TileEntityNetwork) {
						((TileEntityNetwork) tile).updateToClient();
					}
				}
				return extract;
			}
		}
		return ElementStack.EMPTY;
	}
}
