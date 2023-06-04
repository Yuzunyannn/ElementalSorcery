package yuzunyannn.elementalsorcery.entity.fcube;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.entity.FairyCubeModule;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;

@SideOnly(Side.CLIENT)
public interface IFairyCubeModuleClient {

	public static final Map<ResourceLocation, IFairyCubeModuleClient> HANDLER = new HashMap<>();

	public static IFairyCubeModuleClient get(FairyCubeModule module) {
		return HANDLER.get(module.getRegistryName());
	}

	public static IFairyCubeModuleClient get(ResourceLocation id) {
		return HANDLER.get(id);
	}

	public void doRenderGUIIcon(FairyCubeModule module);

	public void doRenderIcon();

	public String getDiplayName();

	public static class FairyCubeModuleDeafultRender implements IFairyCubeModuleClient {
		public final int xoff, yoff;
		public final String unlocalizedName;

		public FairyCubeModuleDeafultRender(int c, int r, String unlocalizedName) {
			this.xoff = c;
			this.yoff = r;
			this.unlocalizedName = unlocalizedName;
		}

		@Override
		public void doRenderGUIIcon(FairyCubeModule module) {
			RenderFriend.drawTextureRectInCenter(0, -6, 32, 32, xoff, yoff, 32, 32, 256, 256);
		}

		@Override
		public void doRenderIcon() {
			RenderFriend.drawTextureRectInCenter(0, -6, 32, 32, xoff, yoff, 32, 32, 256, 256);
		}

		@Override
		public String getDiplayName() {
			return I18n.format("fairy.cube." + unlocalizedName + ".name");
		}
	}

}
