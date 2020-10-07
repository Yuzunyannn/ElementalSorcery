package yuzunyannn.elementalsorcery.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.block.BlockElfSapling;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectElementMove;
import yuzunyannn.elementalsorcery.render.effect.FirewrokShap;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class EntityExploreDust extends Entity implements IEntityAdditionalSpawnData {

	protected int level;
	protected ItemStack stack = new ItemStack(ESInitInstance.ITEMS.NATURE_CRYSTAL);
	/** 当前处理记录的数据 */
	protected NBTTagCompound data = new NBTTagCompound();

	public EntityExploreDust(World worldIn) {
		super(worldIn);
		this.setSize(1, 1);
		checkColor();
	}

	public EntityExploreDust(World worldIn, int level) {
		super(worldIn);
		this.level = level;
		checkColor();
	}

	@Override
	public void setDead() {
		if (!this.isDead) this.onDead();
		super.setDead();
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeByte(level);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		level = additionalData.readByte();
		checkColor();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		level = compound.getByte("lev");
		data = compound.getCompoundTag("data");
		checkColor();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setByte("lev", (byte) level);
		compound.setTag("data", data);
	}

	public int tick;

	@Override
	public void onUpdate() {
		if (world.isRemote) {
			this.onUpdateClient();
			return;
		}
		tick++;
		// 开始进行扫描
		if (tick % 10 != 0) return;
		if (!data.hasKey("world")) {
			data.setInteger("world", world.provider.getDimension());
			return;
		}
		if (!data.hasKey("biome")) {
			Biome biome = world.getBiome(this.getPosition());
			data.setString("biome", biome.getRegistryName().getResourcePath());
			return;
		}
		if (!data.hasKey("rainfall")) {
			Biome biome = world.getBiome(this.getPosition());
			data.setFloat("rainfall", biome.getRainfall());
			return;
		}
		if (!data.hasKey("elfTree")) {
			boolean can = world.provider.getDimension() == 0;
			can = can && BlockElfSapling.chunkCanGrow(world, this.getPosition());
			data.setBoolean("elfTree", can);
		}
		if (level > 1) {
			// 周邊的建築
			if (!data.hasKey("archi")) {
				ChunkProviderServer cp = ((WorldServer) world).getChunkProvider();
				String in = null;
				for (String str : new String[] { "Stronghold", "Mansion", "Monument", "Village", "Mineshaft",
						"Temple" }) {
					BlockPos to = cp.getNearestStructurePos(world, str, this.getPosition(), false);
					if (to == null) continue;
					Vec3d x1 = new Vec3d(posX, 0, posZ);
					Vec3d x2 = new Vec3d(to.getX(), 0, to.getZ());
					if (x1.squareDistanceTo(x2) <= 160 * 160) {
						in = str;
						break;
					}
				}
				if (in != null) data.setString("archi", in);
				else data.setString("archi", "");
				return;
			}
			// 扫描矿物
			NBTTagCompound ore = data.getCompoundTag("ore");
			int layer = ore.getInteger("layer");
			if (layer < 200) {
				final int layerUp = 15;
				if (layer < 5) layer = 5;
				BlockPos pos = this.getPosition();
				pos = new BlockPos((pos.getX() >> 4) << 4, layer, (pos.getZ() >> 4) << 4);
				for (int x = 0; x < 16; x++) {
					for (int y = 0; y < layerUp; y++) {
						for (int z = 0; z < 16; z++) scanning(pos.add(x, y, z), ore);
					}
				}
				ore.setInteger("layer", layer + layerUp);
				data.setTag("ore", ore);
				return;
			} else ore.removeTag("layer");
		}
		// 最后是pos pos是核心字段
		if (!NBTHelper.hasBlockPos(data, "pos")) {
			NBTHelper.setBlockPos(data, "pos", this.getPosition());
			return;
		}
		this.setDead();
	}

	public void scanning(BlockPos pos, NBTTagCompound nbt) {
		if (world.isAirBlock(pos)) return;
		IBlockState state = world.getBlockState(pos);
		ItemStack realOre = scanningOre(state);
		if (realOre.isEmpty()) return;
		String id = realOre.getItem().getRegistryName().toString();
		NBTTagCompound data = nbt.getCompoundTag(id);
		data.setInteger("meta", realOre.getMetadata());
		data.setInteger("count", data.getInteger("count") + 1);
		nbt.setTag(id, data);
	}

	public static ItemStack scanningOre(IBlockState state) {
		if (isSpecialBlock(state)) return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
		ItemStack oreStack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
		if (oreStack.isEmpty()) return ItemStack.EMPTY;
		int[] ore = OreDictionary.getOreIDs(oreStack);
		if (ore == null || ore.length == 0) return ItemStack.EMPTY;
		String name = OreDictionary.getOreName(ore[0]);
		if (!BlockHelper.isOre(name)) return ItemStack.EMPTY;
		return OreDictionary.getOres(name).get(0);
	}

	/** 檢測是否是特定的方塊，不走礦物詞典 */
	public static boolean isSpecialBlock(IBlockState state) {
		return false;
	}

	public int[] DEAFULT_COLOR;

	public void checkColor() {
		switch (level) {
		case 1:
			DEAFULT_COLOR = new int[] { 0x179a4e, 0x056b30, 0x00856f };
			break;
		case 2:
			DEAFULT_COLOR = new int[] { 0x179a4e, 0x056b30, 0x00856f, 0x9eaa01, 0x7e3096 };
			break;
		default:
			DEAFULT_COLOR = new int[] { 0x179a4e, 0x056b30 };
			break;
		}
	}

	public void onDead() {
		if (world.isRemote) {
			Vec3d pos = this.getPositionVector().addVector(0, this.height / 2, 0);
			FirewrokShap.createECircle(world, pos, 0.25, 3, DEAFULT_COLOR);
			FirewrokShap.createECircle(world, pos, 0.5, 4, DEAFULT_COLOR);
			return;
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("natureData", data);
		stack.setTagCompound(nbt);
		Block.spawnAsEntity(world, this.getPosition(), stack);
	}

	public ItemStack getStack() {
		return stack;
	}

	@SideOnly(Side.CLIENT)
	public void onUpdateClient() {
		if (EventClient.tick % 4 != 0) return;
		Vec3d pos = this.getPositionVector().addVector(rand.nextDouble() * 1.5 - 0.75, 0.1,
				rand.nextDouble() * 1.5 - 0.75);
		EffectElementMove effect = new EffectElementMove(world, pos);
		effect.lifeTime = rand.nextInt(10) + 20;
		effect.dalpha = 1.0f / effect.lifeTime;
		effect.g = 0.005f;
		effect.setVelocity(0, 0.075f, 0);
		effect.setColor(DEAFULT_COLOR[rand.nextInt(DEAFULT_COLOR.length)]);
		Effect.addEffect(effect);
	}

}
