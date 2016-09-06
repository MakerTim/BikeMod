package nl.makertim.bikemod;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nl.makertim.bikemod.proxy.CommonProxy;

@Mod(modid = ModInfo.MOD_ID, name = ModInfo.NAME)
public class Bikes {

	public static Item item;

	@SidedProxy(clientSide = "nl.makertim.bikemod.proxy.ClientProxy", serverSide = "nl.makertim.bikemod.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.registerItems();
	}
}
