package com.spacechase0.minecraft.biomewand;

import static net.minecraft.init.Blocks.vine;
import static net.minecraft.init.Items.emerald;
import static net.minecraft.init.Items.ender_pearl;
import static net.minecraft.init.Items.gold_ingot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.spacechase0.minecraft.biomewand.item.Items;
import com.spacechase0.minecraft.spacecore.BaseMod;

// 1.2 - Revamped to use new wand and sample system. Updated to Minecraft 1.8.
// 1.1.9 - Updated for SpaceCore 0.7.9.
// 1.1.8 - ...And made sure the fix from 1.1.6 is still there.
// 1.1.7 - Fixed server crash that I'm 99% sure was there.
// 1.1.6 - Fixed bug with having to do F3+A to see new foliage colors.
// 1.1.5 - (Hopefully) Fixed bug with biome IDs > 127.
// 1.1.4 - Added some config options.
// 1.1.3 - Updated for SpaceCore 0.6.0.
// 1.1.2 - Fixed crashing bug for creative mode with some mods.
// Website said 1.1.x while this was 0.3.x... Weird

@Mod( modid = "SC0_BiomeWand", useMetadata = true, dependencies="required-after:SC0_SpaceCore" )
public class BiomeWandMod extends BaseMod
{
	public static BiomeWandMod instance;
	
	@SidedProxy( clientSide = "com.spacechase0.minecraft.biomewand.client.ClientProxy",
		         serverSide = "com.spacechase0.minecraft.biomewand.CommonProxy" )
	public static CommonProxy proxy;
	
	public BiomeWandMod()
	{
		super( "biomewand" );
	}
	
	@Override
	@EventHandler
	public void init( FMLInitializationEvent event )
	{
		super.init( event );
		proxy.init();
		
		initRecipes();
	}
	
	private void initRecipes()
	{
		GameRegistry.addRecipe( new WandAttunementRecipes() );
		GameRegistry.addRecipe( new ItemStack( items.sample ),
                                " g ",
		                        "gOg",
		                        " g ",
		                        'g', gold_ingot,
		                        'O', ender_pearl );
		GameRegistry.addRecipe( new ItemStack( items.wand ),
                                "vve",
		                        " /v",
		                        "/ v",
		                        '/', gold_ingot,
		                        'v', vine,
		                        'e', emerald );
	}
	
	public static Configuration config;
	public static Items items;
}
