package yuzunyannn.elementalsorcery.tile;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.tile.IAcceptBurnPower;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.prop.ItemBlessingJadePiece;
import yuzunyannn.elementalsorcery.render.tile.RenderTileMeltCauldron;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.helper.NumberHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.Shaders;

public class TileMeltCauldron extends TileEntityNetwork implements IAcceptBurnPower, ITickable {

	public final static Map<Item, Integer> volumeMap = new IdentityHashMap();
	public final static List<MeltCauldronRecipe> recipes = new ArrayList<>();

	public static void initVolumeMap() {
		volumeMap.clear();
		volumeMap.put(ESInit.ITEMS.KYANITE, 20);
		volumeMap.put(ESInit.ITEMS.MAGIC_STONE, 16);
		volumeMap.put(Item.getItemFromBlock(Blocks.GLASS), 4);

		MeltCauldronRecipe astone = new MeltCauldronRecipe();
		astone.setMagicStoneCount(1);
		astone.add(new ItemStack(Blocks.STONE, 8), new ItemStack(Blocks.COBBLESTONE, 8));
		astone.add(new ItemStack(ESInit.ITEMS.KYANITE, 4));
		astone.addResult(2f, new ItemStack(ESInit.BLOCKS.ASTONE, 8, 0));
		astone.addResult(2048f, new ItemStack(ESInit.BLOCKS.ASTONE, 8, 1));
		recipes.add(astone);

		MeltCauldronRecipe magicCore = new MeltCauldronRecipe();
		magicCore.setMagicStoneCount(4);
		magicCore.add(new ItemStack(ESInit.ITEMS.ELF_CRYSTAL, 4));
		magicCore.add(new ItemStack(Items.GOLD_INGOT, 4));
		magicCore.add(new ItemStack(Items.ENDER_EYE));
		magicCore.addResult(2f, new ItemStack(ESInit.ITEMS.MAGIC_CORE, 4));
		magicCore.addResult(8f, new ItemStack(ESInit.ITEMS.MAGICAL_ENDER_EYE));
		magicCore.addResult(16f, new ItemStack(ESInit.ITEMS.MAGIC_STONE, 4));
		recipes.add(magicCore);

		MeltCauldronRecipe blessingJadePiece = new MeltCauldronRecipe();
		blessingJadePiece.setMagicStoneCount(4);
		blessingJadePiece.add(new ItemStack(ESInit.ITEMS.ORDER_CRYSTAL, 32));
		blessingJadePiece.add(new ItemStack(ESInit.ITEMS.MAGIC_CRYSTAL, 12));
		blessingJadePiece.add(new ItemStack(ESInit.BLOCKS.STAR_SAND, 4));
		blessingJadePiece.add(new ItemStack(Blocks.SEA_LANTERN, 5));
		blessingJadePiece.add(new ItemStack(Blocks.PURPUR_BLOCK, 16));
		blessingJadePiece.add(new ItemStack(Blocks.DIAMOND_BLOCK, 2));
		blessingJadePiece.add(new ItemStack(Blocks.GLASS, 64));
		blessingJadePiece.addResult(1f, ItemBlessingJadePiece.createPiece(4));
		recipes.add(blessingJadePiece);

		MeltCauldronRecipe iceRockSpar = new MeltCauldronRecipe();
		iceRockSpar.setMagicStoneCount(2);
		iceRockSpar.add(new ItemStack(ESInit.ITEMS.ICE_ROCK_CHIP, 8));
		iceRockSpar.add(new ItemStack(ESInit.ITEMS.INVERT_GEM, 1));
		iceRockSpar.add(new ItemStack(ESInit.ITEMS.MAGIC_CRYSTAL, 16));
		iceRockSpar.add(new ItemStack(Items.ENDER_PEARL, 16));
		iceRockSpar.addResult(0.5f, new ItemStack(ESInit.ITEMS.ICE_ROCK_SPAR, 12));
		iceRockSpar.addResult(3.0f, new ItemStack(ESInit.ITEMS.ICE_ROCK_SPAR, 6));
		iceRockSpar.addResult(10.0f, new ItemStack(ESInit.ITEMS.INVERT_GEM, 1));
		recipes.add(iceRockSpar);

		// 自定义
		Json.ergodicFile("recipes/melt_cauldron", (file, json) -> {
			if (!ElementMap.checkModDemands(json)) return false;
			return readJson(json);
		});
	}

	static public boolean readJson(JsonObject json) {

		String type = json.hasString("type") ? json.getString("type") : "";
		if ("volume".equals(type)) {
			JsonArray array = json.needArray("list", "volumes");
			for (int i = 0; i < array.size(); i++) {
				List<ItemRecord> items = array.needItems(i);
				for (ItemRecord item : items) {
					ItemStack stack = item.getStack();
					volumeMap.put(stack.getItem(), stack.getCount());
				}
			}
			return true;
		}

		MeltCauldronRecipe recipe = new MeltCauldronRecipe();
		JsonArray list = json.needArray("list", "items");
		for (int i = 0; i < list.size(); i++) recipe.add(ItemRecord.asIngredient(list.needItems(i)));
		recipe.setMagicStoneCount(json.needNumber("magicStoneCount", "magic").intValue());
		JsonArray result = json.needArray("result");
		for (int i = 0; i < list.size(); i++) {
			JsonObject dat = result.needObject(i);
			ItemRecord ir = dat.needItem("item");
			recipe.addResult(dat.needNumber("deviation").floatValue(), ir.getStack());
		}
		recipes.add(recipe);

		return true;
	}

	static public int getVolume(Item item) {
		Integer volume = volumeMap.get(item);
		if (volume != null) return volume;
		String name = item.getRegistryName().getPath().toLowerCase();
		if (name.indexOf("stone") != -1) return 4;
		if (name.indexOf("crystal") != -1) return 1;
		return 10;
	}

	static public class MeltCauldronRecipe {

		protected List<Entry<Float, ItemStack>> results = new ArrayList<>();
		protected List<Ingredient> list = new ArrayList<>();
		protected int magicStoneCount;

		static public float deviation(float src, float act) {
			float a = act / src;
			return (a - 1) * (a - 1);
		}

		static public int find(Ingredient ingredient, Collection<ItemStack> materials) {
			List<ItemStack> stacks = ItemHelper.find(ingredient, materials);
			int n = 0;
			for (ItemStack s : stacks) n = n + s.getCount();
			return n;
		}

		/** 匹配偏差值，越小越好 */
		public float matchingDeviation(int magicStoneCount, List<ItemStack> materials) {
			float[] nums = new float[list.size() + 1];
			nums[0] = magicStoneCount / this.magicStoneCount;
			for (int i = 0; i < list.size(); i++) {
				Ingredient ingredient = list.get(i);
				int src = ingredient.getMatchingStacks()[0].getCount();
				int act = find(ingredient, materials);
				if (act < src) return Float.MAX_VALUE;
				nums[i + 1] = act / src;
			}
			float variance = NumberHelper.variance(nums);
			for (ItemStack act : materials) {
				boolean hasMatch = false;
				for (Ingredient ingredient : list) {
					for (ItemStack src : ingredient.getMatchingStacks()) {
						if (ItemStack.areItemsEqual(src, act)) {
							hasMatch = true;
							break;
						}
					}
					if (hasMatch) break;
				}
				// 多餘的東西，比烂大會
				if (!hasMatch) variance = (variance + act.getCount()) * 2;
			}
			return MathHelper.sqrt(variance);
		}

		public boolean isMyResult(ItemStack result) {
			for (Entry<Float, ItemStack> entry : results) {
				if (ItemStack.areItemsEqual(entry.getValue(), result)) return true;
			}
			return false;
		}

		public ItemStack getResult(int magicStoneCount, List<ItemStack> materials, float deviation) {
			float minDeviation = Float.MAX_VALUE;
			ItemStack result = ItemStack.EMPTY;
			for (Entry<Float, ItemStack> entry : results) {
				if (deviation < entry.getKey() && minDeviation >= entry.getKey()) {
					result = entry.getValue();
					minDeviation = entry.getKey();
				}
			}
			if (result.isEmpty()) return ItemStack.EMPTY;
			result = result.copy();

			if (list.isEmpty()) return result;

			float minCount = magicStoneCount / this.magicStoneCount;
			for (int i = 0; i < list.size(); i++) {
				Ingredient ingredient = list.get(i);
				int src = ingredient.getMatchingStacks()[0].getCount();
				int act = find(ingredient, materials);
				float n = act / src;
				if (n < minCount) minCount = n;
			}
			result.setCount(MathHelper.floor(result.getCount() * minCount));
			return result;
		}

		public List<ItemStack> getResultList() {
			List<ItemStack> list = new ArrayList<>();
			for (Entry<Float, ItemStack> entry : results) list.add(entry.getValue());
			return list;
		}

		public void ergodicResult(Consumer<Entry<Float, ItemStack>> func) {
			for (Entry<Float, ItemStack> entry : results) func.accept(entry);
		}

		public List<Ingredient> getNeedList() {
			return list;
		}

		public MeltCauldronRecipe add(Ingredient item) {
			list.add(item);
			return this;
		}

		public MeltCauldronRecipe add(ItemStack... item) {
			list.add(Ingredient.fromStacks(item));
			return this;
		}

		public void addResult(float underDegree, ItemStack stack) {
			results.add(new AbstractMap.SimpleEntry(underDegree, stack));
		}

		public MeltCauldronRecipe setMagicStoneCount(int magicStoneCount) {
			this.magicStoneCount = magicStoneCount;
			return this;
		}

		public int getMagicStoneCount() {
			return magicStoneCount;
		}
	}

	/** 温度 */
	protected float temperature;
	/** 魔石数量 */
	protected int magicCount;
	/** 容量 */
	protected int volume;
	/** 所有扔进去的物品 */
	protected List<ItemStack> materials = new ArrayList<>();

	protected ItemStack result = ItemStack.EMPTY;
	protected int resultCount;
	protected MeltCauldronRecipe resultRecipe;

	static public final int START_TEMPERATURE = 450;

	/** 吃掉一个物品 */
	public void eatItem(EntityItem item) {
		if (world.isRemote) return;
		if (temperature < START_TEMPERATURE + 50) return;
		if (item.isDead) return;
		ItemStack stack = item.getItem();
		if (magicCount <= 0 && stack.getItem() != ESInit.ITEMS.MAGIC_STONE) {
			item.setFire(60);
			return;
		}

		while (!stack.isEmpty()) {
			if (eatOnce(stack)) stack.shrink(1);
			else {
				item.setFire(60);
				break;
			}
		}
		if (stack.isEmpty()) item.setDead();
		detectAndSend();
	}

	protected boolean eatOnce(ItemStack stack) {
		if (stack.getItem() == ESInit.ITEMS.MAGIC_STONE) {
			if (onEatItem(stack)) {
				magicCount++;
				return true;
			}
			return false;
		}
		for (ItemStack i : materials) {
			if (ItemStack.areItemsEqual(i, stack)) {
				if (onEatItem(stack)) {
					i.grow(1);
					return true;
				}
				return false;
			}
		}
		// 最多8种
		if (materials.size() > 8) return false;
		if (onEatItem(stack)) {
			stack = stack.copy();
			stack.setCount(1);
			materials.add(stack);
			return true;
		}
		return false;
	}

	protected boolean onEatItem(ItemStack stack) {
		if (volume >= 1000) return false;
		Item item = stack.getItem();
		volume = Math.min(volume + getVolume(item), 1000);
		return true;
	}

	public int getVolume() {
		return volume;
	}

	public float getTemperature() {
		return temperature;
	}

	/** 上一次的量，服务端判断是否发送，客户端渲染 */
	public int prevVolume;

	/** 检测是否有变化，有的话，将数据发送过去 */
	protected void detectAndSend() {
		if (world.isRemote) return;
		if (this.prevVolume != this.getVolume()) {
			this.prevVolume = this.getVolume();
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
			Block.spawnAsEntity(world, pos, new ItemStack(ESInit.BLOCKS.MELT_CAULDRON));
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

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		result = nbtReadItemStack(nbt, "result");
		volume = nbt.getInteger("volume");
		temperature = nbt.getFloat("temperature");
		if (isSending()) return;
		resultCount = nbt.getInteger("rCount");
		materials = NBTHelper.getItemListICount(nbt, "materials");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbtWriteItemStack(nbt, "result", result);
		nbt.setShort("volume", (short) volume);
		nbt.setFloat("temperature", temperature);
		if (isSending()) return nbt;
		NBTHelper.setItemListICount(nbt, "materials", materials);
		nbt.setInteger("rCount", resultCount);
		return nbt;
	}

	@Override
	public boolean acceptBurnPower(int amount, int level) {
		if (!result.isEmpty()) return false;

		// A∞=(a*q)/(1-q) 0<q<1
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

	/** 生成结果 */
	public void doResult() {
		float minDeviation = Float.MAX_VALUE;
		for (MeltCauldronRecipe recipe : recipes) {
			float deviation = recipe.matchingDeviation(magicCount, materials);
			if (deviation < minDeviation) {
				minDeviation = deviation;
				resultRecipe = recipe;
			}
		}
		if (resultRecipe != null) {
			result = resultRecipe.getResult(magicCount, materials, minDeviation);
			resultCount = result.getCount();
		}
		if (result.isEmpty()) {
			result = new ItemStack(Blocks.COBBLESTONE);
			resultCount = Math.max(getVolume() / 65, 1);
		}
	}

	@Override
	public void update() {

		if (world.isRemote) {
			temperature *= 0.995f;
			updateClient();
			return;
		}

		if ((result.isEmpty() && resultRecipe == null) && magicCount > 0) {
			if (getTemperature() < START_TEMPERATURE) {
				doResult();
				if (!result.isEmpty()) {
					this.markDirty();
					this.updateToClient();
				}
			}
		}
		temperature *= 0.995f;
	}

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		prevVolume = volume;
		if (result.isEmpty()) return;
	}

	@SideOnly(Side.CLIENT)
	public boolean isRendered;

	@SideOnly(Side.CLIENT)
	public void bindDynamicTexture() {
		if (result.isEmpty()) {
			RenderTileMeltCauldron.TEXTURE_FLUID_BLOCK.bind();
			return;
		}

		RenderHelper.renderOffscreenTexture128(v -> {
			GlStateManager.color(1, 1, 1, 1);

			Shaders.JCOLOR.bind();
			RenderTileMeltCauldron.TEXTURE_FLUID_BLOCK.bind();
			GlStateManager.translate(64, 64, -64);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos(-64, -64, 0).tex(0, 0).endVertex();
			bufferbuilder.pos(-64, 64, 0).tex(0, 1).endVertex();
			bufferbuilder.pos(64, 64, 0).tex(1, 1).endVertex();
			bufferbuilder.pos(64, -64, 0).tex(1, 0).endVertex();
			tessellator.draw();
			Shaders.JCOLOR.unbind();

			ItemStack drawItem = result;
			Shaders.BlockMeltCauldron.bind();
			RenderTileMeltCauldron.TEXTURE_FLUID_BLOCK.bindAtive(3);
			Shaders.BlockMeltCauldron.setUniform("mask", 3);
			if (Block.getBlockFromItem(drawItem.getItem()) != Blocks.AIR) {
				GlStateManager.scale(220, 220, 220);
				GlStateManager.rotate(-90, 1, 0, 0);
				Minecraft.getMinecraft().getRenderItem().renderItem(drawItem, ItemCameraTransforms.TransformType.FIXED);
			} else {
				GlStateManager.scale(100, 100, 100);
				Minecraft.getMinecraft().getRenderItem().renderItem(drawItem, ItemCameraTransforms.TransformType.FIXED);
			}
			RenderTileMeltCauldron.TEXTURE_FLUID_BLOCK.unbindAtive(3);
			Shaders.BlockMeltCauldron.unbind();
		});
		RenderHelper.bindOffscreenTexture128();
	}

}
