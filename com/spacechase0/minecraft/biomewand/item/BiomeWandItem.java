package com.spacechase0.minecraft.biomewand.item;

import java.util.List;

import com.spacechase0.minecraft.biomewand.BiomeWandMod;
import com.spacechase0.minecraft.spacecore.util.TranslateUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomeWandItem extends Item
{
	public BiomeWandItem()
	{
		maxStackSize = 1;
		setMaxDamage( BiomeWandMod.config.get( "general", "useCount", 10 ).getInt( 10 ) - 1 );
		
		setUnlocalizedName( "biomeWand" );
		setCreativeTab( CreativeTabs.tabTools );
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void registerIcons( IIconRegister ir )
	{
		itemIcon = ir.registerIcon( "biomewand:wand" );
		gemIcon = ir.registerIcon( "biomewand:gem" );
	}

	@Override
    public IIcon getIconFromDamageForRenderPass(int par1, int pass )
    {
		if ( pass == 1 )
		{
			return gemIcon;
		}
		
        return super.getIconFromDamageForRenderPass(par1, pass);
    }

	@Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
		NBTTagCompound tag = stack.getTagCompound();
		int biomeId = -1;
		if ( tag != null )
		{
			NBTTagByte biome = ( NBTTagByte ) tag.getTag( "sampledBiome" );
			if ( biome != null )
			{
				biomeId = biome.func_150290_f() & 255;
			}
		}
		
		if ( pass == 1 )
		{
			return biomeId >= 0 ? BiomeGenBase.getBiomeGenArray()[ biomeId ].color : 0;
		}
		
        return 0xFFFFFFFF;
    }

	@Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

	@Override
    public int getRenderPasses(int metadata)
    {
        return 2;
    }

	/*
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
    	return ( stack.getTagCompound() != null && stack.getTagCompound().hasKey( "sampledBiome" ) );
    }
    */
	
	@Override
    public boolean onItemUse( ItemStack stack, EntityPlayer player, World world, int blockX, int blockY, int blockZ, int side, float par8, float par9, float par10 )
    {
		NBTTagCompound tag = stack.getTagCompound();
		
		boolean sample = true;
		if ( tag != null )
		{
			NBTTagByte biome = ( NBTTagByte ) tag.getTag( "sampledBiome" );
			if ( biome != null )
			{
				sample = false;
			}
		}
		
		if ( tag == null )
		{
			tag = new NBTTagCompound();
		}
		
		if ( sample )
		{
			int relBlockX = blockX & 0xF;
			int relBlockZ = blockZ & 0xF;
			
			Chunk chunk = world.getChunkFromBlockCoords( blockX, blockZ );
			byte biome = chunk.getBiomeArray()[ relBlockZ * 16 + relBlockX ];
			tag.setByte( "sampledBiome", biome );
		}
		else
		{
			byte biome = tag.getByte( "sampledBiome" );
			for ( int ix = blockX - 3; ix <= blockX + 3; ++ix )
			{
				for ( int iz = blockZ - 3; iz <= blockZ + 3; ++iz )
				{
					int relBlockX = ix & 0xF;
					int relBlockZ = iz & 0xF;

					Chunk chunk = world.getChunkFromBlockCoords( blockX, blockZ );
					chunk.getBiomeArray()[ relBlockZ << 4 | relBlockX ] = biome;
					chunk.isModified = true;

					BiomeWandMod.proxy.updateRendererAt( ix, iz );
				}
			}
			
			if ( !player.capabilities.isCreativeMode )
			{
				tag.removeTag( "sampledBiome" );
				stack.damageItem( 1, player );
			}
		}
		
		stack.setTagCompound( tag );
		
		return true;
    }
	
	@Override
    public void addInformation( ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		NBTTagCompound tag = stack.getTagCompound();
		NBTTagByte biome = ( tag == null ) ? null : ( ( NBTTagByte ) tag.getTag( "sampledBiome" ) );
		
		String str = TranslateUtils.translate( "biomewand.tooltip.biome" ) + ": ";
		if ( biome == null )
		{
			str += TranslateUtils.translate( "biomewand.tooltip.none" );
		}
		else if ( /*biome.data > 0 && biome.data < BiomeGenBase.biomeList.length*/true )
		{
			BiomeGenBase b = BiomeGenBase.getBiomeGenArray()[ biome.func_150290_f() & 255 ];
			if ( b == null )
			{
				str += TranslateUtils.translate( "biomewand.tooltip.unknown" );
			}
			else
			{
				str += b.biomeName;
			}
		}
		list.add( str );
	}
	
	@Override
    public void getSubItems( Item item, CreativeTabs tabs, List list)
    {
		list.add( new ItemStack( this, 1 ) );
		
		for ( BiomeGenBase biome : BiomeGenBase.getBiomeGenArray() )
		{
			if ( biome == null )
			{
				continue;
			}
			
			NBTTagCompound compound = new NBTTagCompound();
			compound.setByte( "sampledBiome", ( byte ) biome.biomeID );
			
			ItemStack wand = ( ItemStack ) new ItemStack( this, 1 );
			wand.setTagCompound( compound );
			list.add( wand );
		}
    }
	
	private IIcon gemIcon;
}
