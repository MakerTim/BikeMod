package nl.makertim.bikemod.proxy;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import nl.makertim.bikemod.BikeEntity;
import nl.makertim.bikemod.BikeItem;
import nl.makertim.bikemod.Bikes;

public class CommonProxy {

	public MinecraftServer server;

	public void preInit(FMLPreInitializationEvent event) {
	}

	public void registerItems() {
		Bikes.item = new BikeItem();
		GameRegistry.register(Bikes.item);
	}

	public void registerEntity() {
		EntityRegistry.registerModEntity(BikeEntity.class, "bike", 2304, Bikes.instance, 20, 20, true);
	}
}
