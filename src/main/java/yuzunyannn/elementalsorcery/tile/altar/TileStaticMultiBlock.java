package yuzunyannn.elementalsorcery.tile.altar;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public abstract class TileStaticMultiBlock extends TileEntityNetwork {

	static final public Random rand = new Random();

	// 祭坛是否成形
	protected boolean ok = false;
	// 多方块结构
	protected MultiBlock structure;
	// 检查时间
	protected int checkTime = 0;

	// 加载
	@Override
	public void onLoad() {
		super.onLoad();
		this.initMultiBlock();
		ok = structure.check(EnumFacing.NORTH);
		if (ok == false) ok = structure.check(EnumFacing.SOUTH);
		if (ok == false) ok = structure.check(EnumFacing.EAST);
		if (ok == false) ok = structure.check(EnumFacing.WEST);
	}

	// 初始化多方块
	public abstract void initMultiBlock();

	// 是否完整
	public boolean isIntact() {
		checkTime++;
		if (checkTime % 40 == 0) this.checkIntact(structure);
		return this.ok;
	}

	public boolean checkIntact(MultiBlock structure) {
		ok = structure.check(structure.face());
		// 优先使用north方向
		if (ok && structure.face() != EnumFacing.NORTH) {
			ok = structure.check(EnumFacing.NORTH);
			if (ok) {
				structure.face(EnumFacing.NORTH);
			} else ok = true;
		}
		if (!ok) structure.face(structure.face().rotateY());
		return ok;
	}

	public static IAltarWake getAlterWake(TileEntity tile) {
		if (tile instanceof IAltarWake) return (IAltarWake) tile;
		return null;
	}

	/** 根据条件，获取一个元素 */
	public ElementStack getElementFromSpPlace(ElementStack need, BlockPos animePos) {
		int size = structure.getSpecialBlockCount();
		int startIndex = getStartIndex();
		for (int i = 0; i < size; i++) {
			int index = (startIndex + i) % size;
			// 获取唤醒
			IAltarWake altarWake = getAlterWake(structure.getSpecialTileEntity(index));
			if (altarWake == null) continue;
			// 获取仓库
			IElementInventory einv = ElementHelper.getElementInventory(structure.getSpecialTileEntity(index));
			if (einv == null) continue;
			ElementStack extract = einv.extractElement(need, true);
			if (extract.arePowerfulAndMoreThan(need)) {
				altarWake.wake(IAltarWake.SEND);
				if (world.isRemote)
					genParticleElementTo(true, extract.getColor(), structure.getSpecialBlockPos(index), animePos);
				else {
					einv.extractElement(need, false);
					if (ElementHelper.isEmpty(einv)) {
						TileEntity tile = structure.getSpecialTileEntity(index);
						if (tile instanceof TileEntityNetwork) {
							((TileEntityNetwork) tile).updateToClient();
						}
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
			if (altarWake == null) continue;
			// 获取仓库
			IElementInventory einv = ElementHelper.getElementInventory(structure.getSpecialTileEntity(i));
			if (einv == null) continue;
			int color = estack.getColor();
			if (einv.insertElement(estack, true)) {
				altarWake.wake(IAltarWake.OBTAIN);
				einv.insertElement(estack, false);
				if (world.isRemote) genParticleElementTo(false, color, animePos, structure.getSpecialBlockPos(i));
				return true;
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void genParticleElementTo(boolean isGet, int color, BlockPos from, BlockPos to) {
		TileElementalCube.giveParticleElementTo(world, color, from, to, isGet ? 0.5f : 0.25f);
	}

	/** 获取元素开始检查的下表，用于决定优先选择哪个 */
	public int getStartIndex() {
		return rand.nextInt(structure.getSpecialBlockCount());
	}
}
