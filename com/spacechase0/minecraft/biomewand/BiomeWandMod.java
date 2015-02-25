package com.spacechase0.minecraft.biomewand;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;

import com.spacechase0.minecraft.biomewand.item.Items;
import com.spacechase0.minecraft.spacecore.BaseMod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

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
		initRecipes();
	}
	
	private void initRecipes()
	{
		if ( config.get( "general", "cheaperRecipe", true ).getBoolean( true ) )
		{
			GameRegistry.addRecipe( new ItemStack( items.biomeWand ),
					                "  d",
					                " e ",
					                "G  ",
					                'd', diamond,
					                'e', emerald,
					                'G', gold_ingot );
		}
		else
		{
			GameRegistry.addRecipe( new ItemStack( items.biomeWand ),
					                "  d",
					                " e ",
					                "G  ",
					                'd', diamond,
					                'e', emerald,
					                'G', gold_block );
		}
	}
	
	public static Configuration config;
	public static Items items;
}
