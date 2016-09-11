package nl.makertim.bikemod;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class BikeRender extends Render<BikeEntity> {

	private static final float SCALE = 1F / 16F;

	public BikeRender(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(BikeEntity bike, double x, double y, double z, float yaw, float partialTicks) {
		GlStateManager.pushMatrix();
		int l = bike.getEntityWorld().getLight(bike.getPosition());
		GL11.glEnable(GL11.GL_LIGHTING);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 50 + ((240 - 50) * (l / 15)),
			50 + ((240 - 50) * (l / 15)));

		GlStateManager.translate(x, y, z);
		GL11.glRotated(90, 0, 1, 0);
		GL11.glRotated(bike.rotationYaw, 0, -1, 0);
		GlStateManager.translate(0, 0, 0.05);

		int angle = (int) System.currentTimeMillis() / 10;

		drawWheelAt(-12, 0, 0, angle);
		drawFrame(-10.5, 2, 0);
		drawWheelAt(8, 0, 0, angle);

		GlStateManager.popMatrix();
	}

	protected void drawFrame(double x, double y, double z) {
		GlStateManager.translate(x * SCALE, y * SCALE, z * SCALE);

		bindTexture(new ResourceLocation(ModInfo.MOD_ID, "textures/model/red.png"));
		GL11.glRotated(70, 0, 0, 1);

		// Bar front wheel
		drawPixels(0, 5, 0.5, 8, 0.5, 1);
		drawPixels(0, 5, -2, 8, 0.5, 1);
		drawPixels(7, 5, 0, 1, 2, 1);

		// Random part front wheel
		GL11.glRotated(15, 0, 0, -1);
		drawPixels(5.5, 6.6, -0.5, 2.75, 1, 1);
		GL11.glRotated(15, 0, 0, 1);

		// Steer
		GL11.glRotated(20, 0, 0, 1);
		drawPixels(11, 0.5, -0.5, 3.5, 1, 1);
		drawPixels(14, 0.5, 3, 1, 8, 1);
		bindTexture(new ResourceLocation(ModInfo.MOD_ID, "textures/model/black.png"));
		GL11.glRotated(35, -1, 0, 0);
		drawPixels(14.1, 3.1, -3.2, 0.8, 3, 0.8);
		GL11.glRotated(35, 1, 0, 0);
		GL11.glRotated(35, 1, 0, 0);
		drawPixels(14.1, 2, 4.4, 0.8, 3, 0.8);
		GL11.glRotated(35, -1, 0, 0);
		bindTexture(new ResourceLocation(ModInfo.MOD_ID, "textures/model/red.png"));
		GL11.glRotated(20, 0, 0, -1);

		// Chair bar
		drawPixels(5, -6, -0.5, 9.5, 1, 1);
		GL11.glRotated(70, 0, 0, -1);

		// Diagonal bar
		GL11.glRotated(45, 0, 0, -1);
		drawPixels(-8, 6, -0.5, 12, 1, 1);
		GL11.glRotated(45, 0, 0, 1);

		// Main bar
		drawPixels(-1.5, 10, -0.5, 13.25, 1, 1);

		// Bar back wheel
		GL11.glRotated(55, 0, 0, -1);
		drawPixels(-3.5, 13.25, 0.5, 10, 0.5, 1);
		drawPixels(-3.5, 13.25, -2, 10, 0.5, 1);
		drawPixels(-3.5, 13.25, 0, 1, 2, 1);
		GL11.glRotated(55, 0, 0, 1);

		// Back wheel extra bar
		drawPixels(8.5, 2, 0.5, 7, 0.5, 1);
		drawPixels(8.5, 2, -2, 7, 0.5, 1);
		drawPixels(8.5, 2, 0, 1, 2, 1);

		// Saddle
		bindTexture(new ResourceLocation(ModInfo.MOD_ID, "textures/model/black.png"));
		drawPixels(8.5, 11.5, 0.5, 3, 3, 0.5);

		// Chain drive
		bindTexture(new ResourceLocation(ModInfo.MOD_ID, "textures/model/gray.png"));
		drawPixels(5.5, 1, 0, 3, 2, 3);

		// Axis
		drawPixels(-5.5, 2, 0, 1, 2, 1);
		drawPixels(14.5, 2, 0, 1, 2, 1);

		GlStateManager.translate(-x * SCALE, -y * SCALE, -z * SCALE);
	}

	protected void drawWheelAt(double x, double y, double z, double angle) {
		angle %= 90;
		angle -= 45;
		GlStateManager.translate(x * SCALE, y * SCALE, z * SCALE);
		GlStateManager.translate(SCALE * -3.5, SCALE * 4.5, 0);
		GL11.glRotated(angle, 0, 0, 1);
		GlStateManager.translate(SCALE * 3.5, SCALE * -4.5, 0);
		bindTexture(new ResourceLocation(ModInfo.MOD_ID, "textures/model/black.png"));
		// Wheel
		drawPixels(-8, 2, 0, 1, 2, 5);
		drawPixels(-7, 1, 0, 1, 2, 1);
		drawPixels(-6, 0, 0, 5, 2, 1);
		drawPixels(-1, 1, 0, 1, 2, 1);
		drawPixels(0, 2, 0, 1, 2, 5);
		drawPixels(-7, 7, 0, 1, 2, 1);
		drawPixels(-6, 8, 0, 5, 2, 1);
		drawPixels(-1, 7, 0, 1, 2, 1);
		bindTexture(new ResourceLocation(ModInfo.MOD_ID, "textures/model/gray.png"));
		// Stripes
		drawPixels(-4, 1, -0.5, 1, 1, 7);
		drawPixels(-7, 4, -0.5, 7, 1, 1);
		GlStateManager.translate(SCALE * -3.5, SCALE * 4.5, 0);
		GL11.glRotated(angle, 0, 0, -1);
		GlStateManager.translate(SCALE * 3.5, SCALE * -4.5, 0);
		GlStateManager.translate(-x * SCALE, -y * SCALE, -z * SCALE);
	}

	protected void drawPixel(double offX, double offY, double offZ) {
		drawPixels(offX, offY, offZ, 1, 1, 1);
	}

	protected void drawPixels(double offX, double offY, double offZ, double xSize, double ySize, double zSize) {
		GlStateManager.translate(offX * SCALE, offY * SCALE, offZ * SCALE);
		// NORTH
		GL11.glRectd(0, 0, SCALE * xSize, SCALE * zSize);
		// EAST
		GlStateManager.rotate(-90, 0, 1, 0);
		GlStateManager.translate(SCALE * -ySize, 0, 0);
		GL11.glRectd(0, 0, SCALE * ySize, SCALE * zSize);
		GlStateManager.translate(SCALE * ySize, 0, 0);
		GlStateManager.rotate(90, 0, 1, 0);
		// SOUTH
		GlStateManager.rotate(-180, 0, 1, 0);
		GlStateManager.translate(SCALE * -xSize, 0, SCALE * ySize);
		GL11.glRectd(0, 0, SCALE * xSize, SCALE * zSize);
		GlStateManager.translate(SCALE * xSize, 0, SCALE * -ySize);
		GlStateManager.rotate(180, 0, 1, 0);
		// WEST
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.translate(0, 0, SCALE * xSize);
		GL11.glRectd(0, 0, SCALE * ySize, SCALE * zSize);
		GlStateManager.translate(0, 0, SCALE * -xSize);
		GlStateManager.rotate(-90, 0, 1, 0);
		// TOP
		GlStateManager.rotate(-90, 1, 0, 0);
		GlStateManager.translate(0, 0, SCALE * zSize);
		GL11.glRectd(0, 0, SCALE * xSize, SCALE * ySize);
		GlStateManager.translate(0, 0, SCALE * -zSize);
		GlStateManager.rotate(90, 1, 0, 0);
		// BOTTOM
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.translate(0, SCALE * -ySize, 0);
		GL11.glRectd(0, 0, SCALE * xSize, SCALE * ySize);
		GlStateManager.translate(0, SCALE * ySize, 0);
		GlStateManager.rotate(-90, 1, 0, 0);
		GlStateManager.translate(-offX * SCALE, -offY * SCALE, -offZ * SCALE);
	}

	@Override
	public void doRenderShadowAndFire(Entity bike, double x, double y, double z, float pitch, float yaw) {
	}

	@Override
	protected ResourceLocation getEntityTexture(BikeEntity bikeEntity) {
		return new ResourceLocation(ModInfo.MOD_ID, "bike");
	}

	public static class Factory implements IRenderFactory<BikeEntity> {

		@Override
		public Render<BikeEntity> createRenderFor(RenderManager renderManager) {
			return new BikeRender(renderManager);
		}
	}
}
