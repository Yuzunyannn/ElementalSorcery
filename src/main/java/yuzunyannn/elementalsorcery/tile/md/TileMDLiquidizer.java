package yuzunyannn.elementalsorcery.tile.md;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.render.effect.FirewrokShap;
import yuzunyannn.elementalsorcery.render.effect.particle.ParticleWaterBubble;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerAdapter;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.world.Juice;
import yuzunyannn.elementalsorcery.world.JuiceMaterial;

public class TileMDLiquidizer extends TileMDBase implements ITickable {

	@Config(kind = "tile", sync = true)
	@Config.NumberRange(min = 1000, max = Integer.MAX_VALUE)
	static private int JUICE_MAX_CAPACITY = 3000;

	public class RotateData {
		public final JuiceMaterial material;
		public float remain = 1;

		public RotateData(JuiceMaterial material) {
			this.material = material == null ? JuiceMaterial.WATER : material;
		}
	}

	protected static class JuiceMax extends Juice {
		public JuiceMax() {
			super();
		}

		public JuiceMax(NBTTagCompound nbt) {
			super(nbt);
		}

		@Override
		public float getMaxJuiceCount() {
			return JUICE_MAX_CAPACITY;
		};
	}

	protected int lastUpdateTick = 0;

	/** 服务器是判断是否需要更新的值，客户端是真实使用的值 */
	protected float water = 0;
	protected float rotateSpeed = 0;
	protected Juice juice = new JuiceMax();
	protected List<RotateData> ingredients = new ArrayList<>();

	
	// -------- client --------
	
	public float rotate;
	public float prevRotate;
	protected float prevWater = 0;
	protected Vec3d waterColor = new Vec3d(0, 0, 0);
	protected float waterServer = -1;
	protected Vec3d waterServerColor;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("juice", juice.juiceData);
		nbt.setTag("ingredients", serializeIngredients());
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		juice = new JuiceMax(nbt.getCompoundTag("juice"));
		deserializeIngredients(nbt.getTagList("ingredients", NBTTag.TAG_COMPOUND));
	}

	public NBTTagList serializeIngredients() {
		NBTTagList list = new NBTTagList();
		for (RotateData data : ingredients) {
			NBTTagCompound nbtDat = new NBTTagCompound();
			nbtDat.setString("id", data.material.key);
			nbtDat.setFloat("remain", data.remain);
			list.appendTag(nbtDat);
		}
		return list;
	}

	public void deserializeIngredients(NBTTagList list) {
		ingredients.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbtDat = list.getCompoundTagAt(i);
			RotateData dat = new RotateData(JuiceMaterial.fromKey(nbtDat.getString("id")));
			dat.remain = nbtDat.getFloat("remain");
			ingredients.add(dat);
		}
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setTag("ingredients", serializeIngredients());
		nbt.setFloat("WW", water = juice.getJuiceCount());
		nbt.setInteger("C", juice.getColor());
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		this.customUpdate(tag);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			// 马甲物品箱，让漏斗等其他设备可以直接插入
			return (T) new ItemStackHandlerAdapter() {
				@Override
				public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
					if (addIngredient(stack, simulate)) {
						ItemStack testStack = stack.copy();
						testStack.setCount(testStack.getCount() - 1);
						return testStack;
					}
					return stack;
				}
			};
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return super.hasCapability(capability, facing) || this.getCapability(capability, facing) != null;
	}

	@Override
	public void update() {
		this.autoTransfer();
		this.updateBlade();
		if (world.isRemote) {
			updateClient();
			return;
		}
		float dWater = Math.abs(juice.getJuiceCount() - water);
		if (dWater > 0) {
			boolean needUpdate = tick - lastUpdateTick >= 20;
			if (needUpdate) sendWaterUpdateToClient();
		} else lastUpdateTick = tick;
	}

	public void updateBlade() {
		boolean isStoping = ingredients.isEmpty() || this.magic.getCount() < 1;
		if (world.isRemote) isStoping = isStoping || water >= juice.getMaxJuiceCount();
		else isStoping = isStoping || juice.getJuiceCount() >= juice.getMaxJuiceCount();
		if (isStoping) {
			rotateSpeed = rotateSpeed + (0 - rotateSpeed) * 0.02f;
			return;
		}
		if (this.tick % 3 == 0) this.magic.shrink(1);
		// 增加转速
		rotateSpeed += (1 - rotateSpeed) * 0.025f;
		if (rotateSpeed < 0.25) return;
		// 进行打汁
		Iterator<RotateData> iter = ingredients.iterator();
		float onceDrop = rotateSpeed * 0.005f;
		while (iter.hasNext()) {
			RotateData rd = iter.next();
			float drop = rd.material.isMain ? Math.min(onceDrop, rd.remain) : rd.remain;
			rd.remain = rd.remain - drop;
			if (world.isRemote) {
				float waterAdd = rd.material.occupancy * drop;
				if (waterServer > 0) waterServer = Math.min(waterServer + waterAdd, juice.getMaxJuiceCount());
				else water = Math.min(water + waterAdd, juice.getMaxJuiceCount());
			} else juice.modulate(rd.material, drop);
			if (rd.remain <= 0) iter.remove();
		}
		this.markDirty();
	}

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		prevWater = water;
		prevRotate = rotate;
		rotate += rotateSpeed * 1.2f;
		if (waterServer >= 0) {
			water = water + (waterServer - water) * 0.125f;
			if (Math.abs(waterServer - water) <= 0.1) {
				water = waterServer;
				waterServer = -1;
			}
		}
		if (waterServerColor != null) {
			waterColor = new Vec3d(waterColor.x + (waterServerColor.x - waterColor.x) * 0.1,
					waterColor.y + (waterServerColor.y - waterColor.y) * 0.1,
					waterColor.z + (waterServerColor.z - waterColor.z) * 0.1);
		}
		if (rotateSpeed > 0.25) {
			float r = getJuiceRate(0);
			Random rand = RandomHelper.rand;
			Vec3d vec = new Vec3d(this.pos).add(0.5, 0.1, 0.5);

			{
				Vec3d at = vec.add(rand.nextGaussian() * 0.1, 0, rand.nextGaussian() * 0.1);
				ParticleWaterBubble p = new ParticleWaterBubble(world, at, new Vec3d(0, 0, 0));
				p.yAccelerate = 0.004f * r;
				p.setScale((float) rand.nextDouble() * 0.3f + 0.1f);
				p.setRBGColorF((float) waterColor.x, (float) waterColor.y, (float) waterColor.z);
				FirewrokShap.manager.addEffect(p);
			}

		}
	}

	public List<RotateData> getIngredients() {
		return ingredients;
	}

	public Juice getJuice() {
		return juice;
	}

	@SideOnly(Side.CLIENT)
	public float getJuiceRate(float partialTicks) {
		return RenderHelper.getPartialTicks(water, prevWater, partialTicks) / juice.getMaxJuiceCount();
	}

	@SideOnly(Side.CLIENT)
	public Vec3d getJuiceColor() {
		return waterColor;
	}

	public boolean addIngredient(ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) return false;
		for (JuiceMaterial material : JuiceMaterial.values()) {
			if (material.item.isItemEqual(stack)) {
				if (!world.isRemote) return addIngredient(material, simulate);
				return true;
			}
		}
		return false;
	}

	public boolean addIngredient(JuiceMaterial material, boolean simulate) {
		if (ingredients.size() > 5) return false;
		if (world.isRemote) {
			if (simulate) return true;
			addIngredientClient(material);
			return true;
		}
		if (juice.getJuiceCount() >= juice.getMaxJuiceCount()) return false;
		if (simulate) return true;

		RotateData rd = new RotateData(material);
		ingredients.add(rd);

		if (!rd.material.isMain) return true;

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("M", material.key);
		nbt.setShort("MC", (short) magic.getCount());
		nbt.setShort("MP", (short) magic.getPower());
		this.updateToClient(nbt);

		return true;
	}

	@SideOnly(Side.CLIENT)
	public void addIngredientClient(JuiceMaterial material) {
		RotateData rd = new RotateData(material);
		ingredients.add(rd);
		if (waterServerColor == null) waterColor = waterServerColor = ColorHelper.color(rd.material.color);
	}

	public void sendWaterUpdateToClient() {
		if (world.isRemote) return;
		water = juice.getJuiceCount();
		lastUpdateTick = tick;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("C", juice.getColor());
		nbt.setFloat("W", water);
		nbt.setShort("MC", (short) magic.getCount());
		nbt.setShort("MP", (short) magic.getPower());
		this.updateToClient(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void customUpdate(NBTTagCompound nbt) {
		if (nbt.hasKey("ingredients", NBTTag.TAG_LIST))
			deserializeIngredients(nbt.getTagList("ingredients", NBTTag.TAG_COMPOUND));
		if (nbt.hasKey("W")) waterServer = nbt.getFloat("W");
		if (nbt.hasKey("WW")) {
			water = nbt.getFloat("WW");
			waterServer = -1;
		}
		if (nbt.hasKey("C")) {
			Vec3d color = ColorHelper.color(nbt.getInteger("C"));
			if (waterServerColor == null) waterColor = color;
			waterServerColor = color;
		}
		if (nbt.hasKey("M")) addIngredient(JuiceMaterial.fromKey(nbt.getString("M")), false);
		if (nbt.hasKey("MC")) this.magic.setCount(nbt.getInteger("MC"));
		if (nbt.hasKey("MP")) this.magic.setPower(nbt.getInteger("MP"));
	}

}
