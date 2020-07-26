package yuzunyannn.elementalsorcery.tile;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.tile.IAcceptBurnPower;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class TileMeltCauldron extends TileEntityNetwork implements IAcceptBurnPower, ITickable {

	/** 温度 */
	protected float temperature;

	/** 魔石量 */
	protected int magicCount;
	/** 石量 */
	protected int stoneCount;
	/** 蓝晶石量 */
	protected int kyaniteCount;
	/** 不稳定量，计数红石等，可以提升蓝晶石判定个数 */
	protected int instableCount;
	/** 产出物 */
	protected ItemStack result = ItemStack.EMPTY;
	/** 产出数量 */
	protected int resultCount;
	/** 产出物品的材质 */
	protected ResourceLocation resultTex;

	static public final int START_TEMPERATURE = 450;

	/** 吃掉一个物品 */
	public void eatItem(EntityItem item) {
		if (!this.world.isRemote) {
			if (temperature < START_TEMPERATURE) return;
			if (!this.result.isEmpty()) {
				item.setFire(5);
				return;
			}
			ItemStack stack = item.getItem();
			// 必须先放入魔石
			if (magicCount == 0) {
				if (stack.getItem() == ESInitInstance.ITEMS.MAGIC_STONE) {
					this.addMagicStone(stack.getCount());
					item.setDead();
				} else item.setFire(5);
				return;
			}
			// 之后的东西
			if (stack.getItem() == ESInitInstance.ITEMS.MAGIC_STONE) {
				this.addMagicStone(stack.getCount());
				item.setDead();
			} else if (Block.getBlockFromItem(stack.getItem()) == Blocks.STONE
					|| Block.getBlockFromItem(stack.getItem()) == Blocks.COBBLESTONE) {
						this.addStone(stack.getCount());
						item.setDead();
					} else
				if (stack.getItem() == Items.REDSTONE) {
					instableCount++;
					this.markDirty();
				} else {
					// 是否为蓝晶石
					if (OreDictionary.containsMatch(false, OreDictionary.getOres("kyanite"), stack)) {
						this.addKyanite(stack.getCount());
						item.setDead();
					} else item.setFire(5);
				}
		}
	}

	public void addMagicStone(int count) {
		for (int i = 0; i < count; i++) {
			this.magicCount++;
			if (this.getVolume() > 1000) {
				this.magicCount--;
				break;
			}
		}
		this.detectAndSend();
		this.markDirty();
	}

	public void addStone(int count) {
		for (int i = 0; i < count; i++) {
			this.stoneCount++;
			if (this.getVolume() > 1000) {
				this.stoneCount--;
				break;
			}
		}
		this.detectAndSend();
		this.markDirty();
	}

	public void addKyanite(int count) {
		for (int i = 0; i < count; i++) {
			this.kyaniteCount++;
			if (this.getVolume() > 1000) {
				this.kyaniteCount--;
				break;
			}
		}
		this.detectAndSend();
		this.markDirty();
	}

	private int lastVolume;

	/** 检测是否有变化，有的话，将数据发送过去 */
	protected void detectAndSend() {
		if (this.lastVolume != this.getVolume()) {
			this.lastVolume = this.getVolume();
			this.updateToClient();
		}
	}

	/** 当有生物进入 */
	public void livingEnter(EntityLivingBase entity) {
		if (temperature < 100) return;
		entity.setFire((int) (temperature / 100.0f));
		if (entity instanceof EntityPlayerMP) ESCriteriaTriggers.MELT_FIRE.trigger((EntityPlayerMP) entity);
	}

	/** 处理掉落 */
	public void drop() {
		if (this.world.isRemote) return;
		if (this.getVolume() <= 0) {
			Block.spawnAsEntity(world, pos, new ItemStack(ESInitInstance.BLOCKS.MELT_CAULDRON));
			return;
		}
		// 必须有结果和温度低于一定程度才可以掉落物品
		if (!this.result.isEmpty() && this.temperature < 50) {
			while (this.resultCount > 0) {
				if (this.resultCount > 64) {
					ItemStack stack = this.result.copy();
					stack.setCount(64);
					Block.spawnAsEntity(world, pos, stack);
				} else {
					ItemStack stack = this.result.copy();
					stack.setCount(this.resultCount);
					Block.spawnAsEntity(world, pos, stack);
				}
				this.resultCount -= 64;
			}
		}

	}

	/** 生成结果 */
	public void doResult() {
		// 判断是否有蓝晶石和石头
		if (this.stoneCount == 0 && this.kyaniteCount == 0) {
			if (this.kyaniteCount > 0) this.kyaniteCount--;
			return;
		} else if (this.stoneCount == 0) {
			this.result = new ItemStack(ESInitInstance.BLOCKS.KYANITE_BLOCK);
			this.resultCount = this.kyaniteCount / 9;
			return;
		} else if (this.kyaniteCount == 0) {
			this.result = new ItemStack(Blocks.STONE);
			this.resultCount = this.stoneCount;
			return;
		}
		// 根据魔石的个数，算最多有几个石头
		int maxStoneCount = this.magicCount * 16;
		// 如果石头超过了魔石个数
		if (this.stoneCount > maxStoneCount) {
			this.result = new ItemStack(ESInitInstance.BLOCKS.KYANITE_ORE);
			this.resultCount = this.kyaniteCount / 2;
			if (this.resultCount > this.stoneCount) this.resultCount = this.stoneCount;
			return;
		}
		// 根据不稳定的数字，增加蓝晶石个数
		if (this.instableCount > 0) this.kyaniteCount = (int) (this.kyaniteCount
				+ this.kyaniteCount * this.world.rand.nextInt(this.instableCount) * 0.1f);
		// 看蓝晶石是否位于0.5到1.5个石头之间的位置
		if (this.kyaniteCount >= this.stoneCount / 2 && this.kyaniteCount <= (int) (this.stoneCount * 1.5f)) {
			this.result = new ItemStack(ESInitInstance.BLOCKS.ASTONE, 1, 0);
			this.resultCount = this.stoneCount;
		} else {
			this.result = new ItemStack(ESInitInstance.BLOCKS.ASTONE, 1, 1);
			this.resultCount = this.stoneCount;
		}
	}

	/** 根据结果选材质 */
	@SideOnly(Side.CLIENT)
	public void dealResultTexture() {
		if (this.result.isEmpty()) {
			this.resultTex = null;
			return;
		}
		Block block = Block.getBlockFromItem(this.result.getItem());
		if (block == ESInitInstance.BLOCKS.KYANITE_BLOCK) {
			this.resultTex = RenderObjects.KYANITE_BLOCK;
		} else if (block == Blocks.STONE) {
			this.resultTex = RenderObjects.STONE;
		} else if (block == ESInitInstance.BLOCKS.KYANITE_ORE) {
			this.resultTex = RenderObjects.KYANITE_ORE;
		} else if (block == ESInitInstance.BLOCKS.ASTONE) {
			if (this.result.getMetadata() == 0) this.resultTex = RenderObjects.ASTONE;
			else this.resultTex = RenderObjects.ASTONE_FRAGMENTED;
		} else {
			this.resultTex = null;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("output", 10)) {
			result = new ItemStack(compound.getCompoundTag("output"));
			resultCount = compound.getInteger("outCount");
		} else result = ItemStack.EMPTY;
		temperature = compound.getFloat("T");
		magicCount = compound.getInteger("magic");
		stoneCount = compound.getInteger("stone");
		kyaniteCount = compound.getInteger("kyanite");
		if (this.isSending()) return;
		if (!compound.hasKey("id")) return;
		instableCount = compound.getInteger("instable");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (!result.isEmpty()) {
			compound.setTag("output", result.serializeNBT());
			compound.setInteger("outCount", resultCount);
		}
		compound.setFloat("T", temperature);
		compound.setInteger("magic", magicCount);
		compound.setInteger("stone", stoneCount);
		compound.setInteger("kyanite", kyaniteCount);
		if (this.isSending()) return compound;
		compound.setInteger("instable", instableCount);
		return super.writeToNBT(compound);
	}

	// A∞=(a*q)/(1-q) 0<q<1
	@Override
	public boolean acceptBurnPower(int amount, int level) {
		if (magicCount > 0) {
			if (temperature > START_TEMPERATURE) {
				temperature += amount * level;
				return true;
			}
			return false;
		} else {
			temperature += amount * level;
			return true;
		}
	}

	@Override
	public void update() {
		temperature *= 0.995f;
		if (this.result.isEmpty() && this.magicCount > 0 && !this.world.isRemote) {
			if (this.temperature < START_TEMPERATURE) {
				this.doResult();
				if (!this.result.isEmpty()) {
					this.markDirty();
					this.updateToClient();
				}
			}
		} else {
			if (this.world.isRemote) {
				if (resultTex == null && !this.result.isEmpty()) this.dealResultTexture();
			}
		}
	}

	public int getVolume() {
		return magicCount * 16 + stoneCount * 4 + kyaniteCount * 20;
	}

	public float getTemperature() {
		return temperature;
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getResultTexture() {
		return resultTex;
	}

}
