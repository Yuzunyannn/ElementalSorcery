package yuzunyannn.elementalsorcery.model;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

/** todo */
public class CheckModelRenderer extends ModelRenderer {

	public static class CheckData {
		public AxisAlignedBB box;
		public int id;

		public CheckData(int id, AxisAlignedBB box) {
			this.box = box;
			this.id = id;
		}
	}

	public final List<CheckModelRenderer.CheckData> checkList = new ArrayList<>();

	public CheckModelRenderer(ModelBase model) {
		super(model);
	}

	public void addCheckBox(int id, ModelBox modelbox) {
		this.cubeList.add(modelbox);
		AxisAlignedBB box = new AxisAlignedBB(modelbox.posX1 / 16, modelbox.posY1 / 16, modelbox.posZ1 / 16,
				modelbox.posX2 / 16, modelbox.posY2 / 16, modelbox.posZ2 / 16);
		this.checkList.add(new CheckData(id, box));
	}

	public Vec3d transform2Base(Vec3d vec) {
		if (rotateAngleX != 0.0F) {
			vec = MathSupporter.rotation(vec, new Vec3d(1, 0, 0), -rotateAngleX);
		}
		if (rotateAngleY != 0.0F) {
			vec = MathSupporter.rotation(vec, new Vec3d(0, 1, 0), -rotateAngleY);
		}
		if (rotateAngleZ != 0.0F) {
			vec = MathSupporter.rotation(vec, new Vec3d(1, 0, 0), -rotateAngleZ);
		}
		return vec;
	}

	public Vec3d transform2Origin(Vec3d vec) {
		if (rotateAngleZ != 0.0F) {
			vec = MathSupporter.rotation(vec, new Vec3d(1, 0, 0), rotateAngleZ);
		}
		if (rotateAngleY != 0.0F) {
			vec = MathSupporter.rotation(vec, new Vec3d(0, 1, 0), rotateAngleY);
		}
		if (rotateAngleX != 0.0F) {
			vec = MathSupporter.rotation(vec, new Vec3d(1, 0, 0), rotateAngleX);
		}
		return vec;
	}

	public RayTraceResult rayTrace(Vec3d startVec, Vec3d endVec, boolean isRevser) {

		Vec3d dStartVec = startVec;
		Vec3d dEndVec = endVec;

		if (isRevser) {
			dStartVec = new Vec3d(dStartVec.x, -dStartVec.y, -dStartVec.z);
			dEndVec = new Vec3d(dEndVec.x, -dEndVec.y, -dEndVec.z);
		}

		Vec3d rotationPoint = new Vec3d(rotationPointX / 16, rotationPointY / 16, rotationPointZ / 16);
		dStartVec = dStartVec.subtract(rotationPoint);
		dEndVec = dEndVec.subtract(rotationPoint);

		dStartVec = transform2Base(dStartVec);
		dEndVec = transform2Base(dEndVec);

//		if (worldIn.isRemote) {
//			Vec3d ss = dStartVec.add(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
//			Vec3d ee = dEndVec.add(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
//			{
//				EffectElementMove move = new EffectElementMove(worldIn, ss);
//				move.setVelocity(ee.subtract(ss).scale(0.02));
//				move.xDecay = move.yDecay = move.zDecay = 1;
//				move.prevScale = move.scale = 0.02f;
//				move.setColor(0x0000ff);
////				move.isGlow = true;
//				Effect.addEffect(move);
//			}
//			{
//				EffectElementMove move = new EffectElementMove(worldIn, ee);
//				move.setVelocity(ss.subtract(ee).scale(0.02));
//				move.xDecay = move.yDecay = move.zDecay = 1;
//				move.prevScale = move.scale = 0.02f;
//				move.setColor(0xff0000);
////				move.isGlow = true;
//				Effect.addEffect(move);
//			}
//		}

		RayTraceResult retResult = null;
		double minSquLength = Double.MAX_VALUE;

		for (CheckModelRenderer.CheckData checkData : checkList) {
			AxisAlignedBB box = checkData.box;
			RayTraceResult raytraceresult = box.calculateIntercept(dStartVec, dEndVec);
			if (raytraceresult != null) {
				if (raytraceresult.sideHit == EnumFacing.DOWN) raytraceresult.sideHit = EnumFacing.UP;
				else if (raytraceresult.sideHit == EnumFacing.UP) raytraceresult.sideHit = EnumFacing.DOWN;
				else if (raytraceresult.sideHit == EnumFacing.SOUTH) raytraceresult.sideHit = EnumFacing.NORTH;
				else if (raytraceresult.sideHit == EnumFacing.NORTH) raytraceresult.sideHit = EnumFacing.SOUTH;
				Vec3d hitVec = raytraceresult.hitVec;
				hitVec = transform2Origin(hitVec);
				hitVec = hitVec.add(rotationPoint);
				if (isRevser) hitVec = new Vec3d(hitVec.x, -hitVec.y, -hitVec.z);
				raytraceresult.hitVec = hitVec;
				raytraceresult.subHit = checkData.id;
				raytraceresult.hitInfo = (double) hitVec.squareDistanceTo(startVec);
				if (((Double) raytraceresult.hitInfo) < minSquLength) {
					minSquLength = (Double) raytraceresult.hitInfo;
					retResult = raytraceresult;
				}
			}
		}

		return retResult;
	}

	public static RayTraceResult rayTraceByModel(ModelBase modelBase, Vec3d startVec, Vec3d endVec, boolean isRevser) {
		RayTraceResult retResult = null;
		double checkSquLength = Double.MAX_VALUE;

		for (ModelRenderer renderer : modelBase.boxList) {
			if (!(renderer instanceof CheckModelRenderer)) continue;
			CheckModelRenderer checker = (CheckModelRenderer) renderer;
			RayTraceResult result = checker.rayTrace(startVec, endVec, isRevser);
			if (result == null) continue;
			if (((Double) result.hitInfo) < checkSquLength) {
				checkSquLength = (Double) result.hitInfo;
				retResult = result;
			}
		}

		return retResult;
	}

}
