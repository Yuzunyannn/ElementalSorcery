package yuzunyannn.elementalsorcery.block.env;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class BlockDungeonStairs extends BlockStairs {

	public BlockDungeonStairs() {
		super(ESObjects.BLOCKS.DUNGEON_BRICK.getDefaultState());
		this.setTranslationKey("dungeonStairs");
		this.setSoundType(SoundType.STONE);
		this.setHardness(-1);
		this.setResistance(6000000.0F);
		this.useNeighborBrightness = true;
	}

}
