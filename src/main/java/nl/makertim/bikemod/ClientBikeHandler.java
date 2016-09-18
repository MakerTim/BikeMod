package nl.makertim.bikemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientBikeHandler {
	@SideOnly(Side.CLIENT)
	private static final KeyBinding fwd = Minecraft.getMinecraft().gameSettings.keyBindForward;
	@SideOnly(Side.CLIENT)
	private static final KeyBinding bck = Minecraft.getMinecraft().gameSettings.keyBindBack;

	public ClientBikeHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		EntityPlayerSP ep = Minecraft.getMinecraft().thePlayer;
		if (ep != null && ep.getRidingEntity() instanceof BikeEntity) {
			BikeEntity bike = ((BikeEntity) ep.getRidingEntity());
			bike.updateForward(fwd.isKeyDown());
			bike.updateBrake(bck.isKeyDown());
		}
	}
}
