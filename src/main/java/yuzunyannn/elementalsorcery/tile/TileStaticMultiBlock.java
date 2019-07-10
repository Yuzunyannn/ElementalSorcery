package yuzunyannn.elementalsorcery.tile;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.api.ability.IAltarWake;
import yuzunyannn.elementalsorcery.api.ability.IElementInventory;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.util.ElementHelper;
import yuzunyannn.elementalsorcery.building.MultiBlock;

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
		ok = structure.check(EnumFacing.NORTH);
		if (ok == false)
			ok = structure.check(EnumFacing.SOUTH);
		if (ok == false)
			ok = structure.check(EnumFacing.EAST);
		if (ok == false)
			ok = structure.check(EnumFacing.WEST);
	}

	// 初始化多方块
	public abstract void initMultiBlock();

	// 是否完整
	public boolean isIntact() {
		check_tick++;
		if (check_tick % 40 == 0) {
			if (!ok) {
				structure.face(structure.face().rotateY());
			}
			ok = structure.check(structure.face());
			// 优先使用north方向
			if (ok && structure.face() != EnumFacing.NORTH) {
				ok = structure.check(EnumFacing.NORTH);
				if (ok) {
					structure.face(EnumFacing.NORTH);
				} else
					ok = true;
			}
		}
		return this.ok;
	}

	public static IAltarWake getAlterWake(TileEntity tile) {
		if (tile instanceof IAltarWake)
			return (IAltarWake) tile;
		return null;
	}

	/** 根据条件，获取一个元素 */
	public ElementStack getElementFromSpPlace(ElementStack need, BlockPos animePos) {
		for (int i = 0; i < structure.getSpecialBlockCount(); i++) {
			// 获取唤醒
			IAltarWake altarWake = getAlterWake(structure.getSpecialTileEntity(i));
			if (altarWake == null)
				continue;
			// 获取仓库
			IElementInventory einv = ElementHelper.getElementInventory(structure.getSpecialTileEntity(i));
			if (einv == null)
				continue;
			ElementStack extract = einv.extractElement(need, true);
			if (extract.arePowerfulAndMoreThan(need)) {
				altarWake.wake(IAltarWake.SEND);
				einv.extractElement(need, false);
				if (world.isRemote) {
					TileElementalCube.giveParticleElementTo(world, extract.getColor(), structure.getSpecialBlockPos(i),
							animePos, 0.3f);
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

	/** 根据给入，存储一个元素 */
	public boolean putElementToSpPlace(ElementStack estack, BlockPos animePos) {
		for (int i = 0; i < structure.getSpecialBlockCount(); i++) {
			// 获取唤醒
			IAltarWake altarWake = getAlterWake(structure.getSpecialTileEntity(i));
			if (altarWake == null)
				continue;
			// 获取仓库
			IElementInventory einv = ElementHelper.getElementInventory(structure.getSpecialTileEntity(i));
			if (einv == null)
				continue;
			int color = estack.getColor();
			if (einv.insertElement(estack, true)) {
				altarWake.wake(IAltarWake.OBTAIN);
				einv.insertElement(estack, false);
				if (world.isRemote) {
					TileElementalCube.giveParticleElementTo(world, color, animePos, structure.getSpecialBlockPos(i),
							0.3f);
				}
				return true;
			}
		}
		return false;
	}
}
