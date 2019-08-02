package yuzunyannn.elementalsorcery.tile.altar;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ability.IGetItemStack;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingLib;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.crafting.altar.CraftingBuildingRecord;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemMagicRuler;
import yuzunyannn.elementalsorcery.render.entity.AnimeRenderBuildingRecord;

public class TileBuildingAltar extends TileStaticMultiBlock implements IGetItemStack, ICraftingLaunch {

	static final BlockPos RULER_POS = new BlockPos(0, -2, 3);

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.BUILING_ALTAR, this, new BlockPos(0, -3, 1));
		structure.addSpecialBlock(new BlockPos(3, 1, 0));
		structure.addSpecialBlock(new BlockPos(-3, 1, 0));
		structure.addSpecialBlock(new BlockPos(2, 1, 2));
		structure.addSpecialBlock(new BlockPos(-2, 1, 2));
	}

	/** 获取标尺 */
	private ItemStack getRuler() {
		BlockPos pos = this.pos.add(Building.BuildingBlocks.facePos(RULER_POS, structure.face()));
		TileEntity tile = this.world.getTileEntity(pos);
		if (tile instanceof IGetItemStack) {
			ItemStack stack = ((IGetItemStack) tile).getStack();
			return stack;
		}
		return ItemStack.EMPTY;
	}

	/** 检测标尺 */
	private boolean dealRuler(EntityLivingBase entity, ItemStack stack) {
		Integer dimensionId = ItemMagicRuler.getDimensionId(stack);
		if (dimensionId == null)
			return false;
		if (entity == null || entity.dimension != dimensionId)
			return false;
		this.pos1 = ItemMagicRuler.getRulerPos(stack, true);
		this.pos2 = ItemMagicRuler.getRulerPos(stack, false);
		return pos1 != null && pos2 != null;
	}

	private boolean checkStack() {
		if (this.stack.isEmpty())
			return false;
		if (this.canSetStack(this.stack))
			return true;
		return false;
	}

	private boolean checkToward(BlockPos center) {
		if (center.getY() > this.pos.getY())
			return false;
		Vec3i vec = structure.face().getOpposite().getDirectionVec();
		Vec3i tar = center.subtract(this.pos);
		Vec3d v1 = new Vec3d(vec.getX(), vec.getY(), vec.getZ());
		Vec3d v2 = new Vec3d(tar.getX(), tar.getY(), tar.getZ());
		double cos = v1.dotProduct(v2) / (v1.lengthVector() * v2.lengthVector());
		if (cos < 0.5253219)
			return false;
		return true;
	}

	// 临时记录
	private BlockPos pos1 = null;
	private BlockPos pos2 = null;
	// 这里不仅作为表示
	private boolean working = false;
	// 继续
	private boolean canContinue = true;
	// 记录当前玩家
	private EntityLivingBase player = null;
	// 死亡时间，如果长时间不提供元素
	private int deadTime;

	@SideOnly(Side.CLIENT)
	public void endWork() {
		this.working = false;
	}

	@Override
	public boolean isWorking() {
		return working;
	}

	@Override
	public boolean canCrafting(String type, @Nullable EntityLivingBase player) {
		if (!ICraftingLaunch.TYPE_BUILING_RECORD.equals(type))
			return false;
		if (!this.checkStack())
			return false;
		ItemStack stack = this.getRuler();
		if (!this.dealRuler(player, stack))
			return false;
		BlockPos center = new BlockPos((this.pos1.getX() + this.pos2.getX()) / 2,
				(this.pos1.getY() + this.pos2.getY()) / 2, (this.pos1.getZ() + this.pos2.getZ()) / 2);
		if (center.distanceSq(this.pos) > ItemMagicRuler.MAX_DIS_SQ)
			return false;
		if (!checkToward(center))
			return false;
		return true;
	}

	@Override
	public ICraftingCommit craftingBegin(String type, EntityLivingBase player) {
		this.working = true;
		this.canContinue = true;
		this.player = player;
		this.deadTime = 0;
		return new CraftingBuildingRecord(pos1, pos2, player.getName());
	}

	@Override
	public ICraftingCommit recovery(String type, EntityLivingBase player, NBTTagCompound nbt) {
		this.working = true;
		this.canContinue = true;
		this.deadTime = 0;
		ItemStack stack = this.getRuler();
		if (ElementalSorcery.side.isServer())
			return new CraftingBuildingRecord(nbt).setTile(this);
		EnumDyeColor c = ItemMagicRuler.getColor(stack);
		return new CraftingBuildingRecord(nbt).setTile(this).setColor(c.getColorValue());
	}

	public static final ElementStack ENEED1 = new ElementStack(ESInitInstance.ELEMENTS.EARTH, 1, 25);

	@Override
	public void craftingUpdate(ICraftingCommit commit) {
		if (this.stack.isEmpty()) {
			this.canContinue = false;
			return;
		}
		ElementStack esatck = this.getElementFromSpPlace(ENEED1, this.pos);
		if (esatck.isEmpty()) {
			this.deadTime++;
			if (this.deadTime > 20 * 20) {
				this.canContinue = false;
			}
			return;
		}
		this.deadTime = 0;
		CraftingBuildingRecord cnr = (CraftingBuildingRecord) commit;
		if (!cnr.onUpdate(this.world)) {
			this.canContinue = false;
		}
	}

	@Override
	public void craftingUpdateClient(ICraftingCommit commit) {
		this.craftingUpdate(commit);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICraftingLaunchAnime getAnime(ICraftingCommit commit) {
		return new AnimeRenderBuildingRecord(this.world, this.stack, (CraftingBuildingRecord) commit);
	}

	@Override
	public int getEndingTime(ICraftingCommit commit) {
		return 80;
	}

	@Override
	public boolean canContinue(ICraftingCommit commit) {
		return this.canContinue;
	}

	@Override
	public int craftingEnd(ICraftingCommit commit) {
		CraftingBuildingRecord cnr = (CraftingBuildingRecord) commit;
		working = false;
		boolean success = cnr.isSuccess();
		if (success) {
			Building building = cnr.createBuilding(world);
			building.setName(building.getAuthor() + "的建筑");
			building.mkdir();
			String key = BuildingLib.instance.addBuilding(building);
			ArcInfo.initArcInfoToItem(this.stack, key);
		} else
			this.badEnd();
		return ICraftingLaunch.SUCCESS;
	}

	public void badEnd() {
		this.world.createExplosion(null, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, 2.0f,
				true);
	}

	private ItemStack stack = ItemStack.EMPTY;

	@Override
	public void setStack(ItemStack stack) {
		this.stack = stack;
		this.updateToClient();
		this.markDirty();
	}

	@Override
	public ItemStack getStack() {
		return stack;
	}

	@Override
	public boolean canSetStack(ItemStack stack) {
		return (stack.getItem() == ESInitInstance.ITEMS.ARCHITECTURE_CRYSTAL
				|| stack.getSubCompound("building") != null) && !ArcInfo.isArc(stack);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("stack"))
			stack = new ItemStack(compound.getCompoundTag("stack"));
		else
			stack = ItemStack.EMPTY;
		super.readFromNBT(compound);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (!stack.isEmpty())
			compound.setTag("stack", stack.serializeNBT());
		return super.writeToNBT(compound);
	}

}
