package yuzunyannn.elementalsorcery.api.util.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRenderOutline<T extends TileEntity> {

	void renderTileOutline(T tile, EntityPlayer player, BlockPos pos, float partialTicks);

}
