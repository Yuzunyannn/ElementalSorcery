package yuzunyannn.elementalsorcery.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IResearchRecipe;
import yuzunyannn.elementalsorcery.api.util.MatchHelper;
import yuzunyannn.elementalsorcery.elf.research.ResearchRecipeManagement;
import yuzunyannn.elementalsorcery.elf.research.Researcher;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ContainerResearch extends Container implements IContainerNetwork {

	/** 交互的玩家 */
	public final EntityPlayer player;
	/** 交互方块 */
	public final BlockPos pos;
	/** 点数 */
	public final Researcher reasearher;
	/** 是否结束 */
	protected boolean isEnd = false;
	/** 来自server的更新标记，客户端用 */
	public boolean fromSrverUpdateFlag = false;

	public ContainerResearch(EntityPlayer player, BlockPos pos) {
		this.player = player;
		this.pos = pos;
		this.reasearher = new Researcher(player);
		if (player.world.isRemote) return;
		EventServer.addTask(() -> {
			this.sendToClient(reasearher.serializeNBT(), player);
		});

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		if (isEnd) return false;
		if (player.world.getBlockState(this.pos).getBlock() != ESObjects.BLOCKS.RESEARCHER) return false;
		else return player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64;
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.CLIENT) {
			reasearher.deserializeNBT(nbt);
			fromSrverUpdateFlag = true;
			return;
		}
		doCrafting(new Researcher(nbt));
		isEnd = true;
	}

	private void onFailEnd() {
		NBTTagCompound nbt = FireworkEffect.fastNBT(0, 1, 0.05f, new int[] { 0xc90000 }, new int[] { 0xec8282 });
		Effects.spawnEffect(player.world, Effects.FIREWROK, new Vec3d(pos).add(0.5, 0.8, 0.5), nbt);
	}

	public void doCrafting(Researcher costReasearher) {
		if (!player.isCreative()) {
			// 检查是否合法，创造模式不检查
			for (String key : costReasearher.getTopics()) {
				int point = reasearher.getPoint(key);
				if (point < costReasearher.getPoint(key)) {
					this.onFailEnd();
					return;
				}
			}
		}
		List<IResearchRecipe> recipes = ResearchRecipeManagement.instance.findMatchingRecipe(costReasearher,
				player.world);
		if (recipes.isEmpty()) {
			this.onFailEnd();
			return;
		}
		double minWeight = 0;
		RandomHelper.WeightRandom<IResearchRecipe> wr = new RandomHelper.WeightRandom();
		for (IResearchRecipe recipe : recipes) {
			if (!MatchHelper.unorderMatch(recipe.getIngredients(), player.inventory).isSuccess()) continue;
			// 满足要求，添加
			double weight = recipe.getMatchWeight(costReasearher, recipes, player.world);
			if (weight < minWeight) minWeight = weight;
			wr.add(recipe, weight);
		}
		if (wr.isEmpty()) {
			this.onFailEnd();
			return;
		}
		wr.fixWeight(-minWeight + 1);
		IResearchRecipe recipe = wr.get();
		ItemStack stack = recipe.getRecipeOutput(costReasearher);
		ItemHelper.addItemStackToPlayer(player, stack.copy());
		// 减少数据，创造模式不减少
		if (!player.isCreative()) {
			// for (String key : costReasearher.keySet()) reasearher.shrink(key,
			// costReasearher.get(key));
			// reasearher.save(player);
			MatchHelper.unorderMatch(recipe.getIngredients(), player.inventory).doShrink();
		}
		// 特效
		NBTTagCompound nbt = FireworkEffect.fastNBT(0, 1, 0.05f, new int[] { 0x096b18 }, new int[] { 0x5ac37b });
		Effects.spawnEffect(player.world, Effects.FIREWROK, new Vec3d(pos).add(0.5, 0.8, 0.5), nbt);
	}

}
