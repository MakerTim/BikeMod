package nl.makertim.bikemod.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nl.makertim.bikemod.*;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		OBJLoader.INSTANCE.addDomain(ModInfo.MOD_ID);
		ModelLoader.setCustomModelResourceLocation(Bikes.item, 0,
			new ModelResourceLocation(new ResourceLocation(ModInfo.MOD_ID, "bike"), "inventory"));
		new ClientBikeHandler();
	}

	@Override
	public void registerEntity() {
		super.registerEntity();
		RenderingRegistry.registerEntityRenderingHandler(BikeEntity.class, new BikeRender.Factory());
	}

	@Override
	public void registerItems() {
		super.registerItems();
	}
}
