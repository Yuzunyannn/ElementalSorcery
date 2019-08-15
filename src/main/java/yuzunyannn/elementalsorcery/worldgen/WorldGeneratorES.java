package yuzunyannn.elementalsorcery.worldgen;

import net.minecraft.client.util.JsonBlendingMode;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.util.parsing.json.JSON;
import scala.util.parsing.json.JSONObject;

public class WorldGeneratorES {

	private BlockPos pos;

	@SubscribeEvent
	public void onOreGenPost(OreGenEvent.Post event) {
		// 山地会调用两次
		if (!event.getPos().equals(this.pos)) {
			this.pos = event.getPos();
			this.genKynateOre(event);
			this.genStarStone(event); 
		}
	}
 
	public void genKynateOre(OreGenEvent.Post event) {
		WorldGenerator generator = new WorldGenKynaiteOre();
		if (TerrainGen.generateOre(event.getWorld(), event.getRand(), generator, event.getPos(),
				OreGenEvent.GenerateMinable.EventType.CUSTOM))
			generator.generate(event.getWorld(), event.getRand(), event.getPos());
	}

	public void genStarStone(OreGenEvent.Post event) {
		WorldGenerator generator = new WorldGenStarStone();
		if (TerrainGen.generateOre(event.getWorld(), event.getRand(), generator, event.getPos(),
				OreGenEvent.GenerateMinable.EventType.CUSTOM))
			generator.generate(event.getWorld(), event.getRand(), event.getPos());
	} 
}
