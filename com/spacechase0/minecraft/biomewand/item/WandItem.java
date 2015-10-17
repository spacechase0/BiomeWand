package com.spacechase0.minecraft.biomewand.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.spacechase0.minecraft.biomewand.BiomeWandMod;
import com.spacechase0.minecraft.spacecore.util.TranslateUtils;

public class WandItem extends Item
{
	public WandItem()
	{
		maxStackSize = 1;
		setMaxDamage( BiomeWandMod.config.get( "general", "useCount", 490 ).getInt( 490 ) - 1 );
		
		setUnlocalizedName( "wand" );
		setCreativeTab( CreativeTabs.tabTools );
	}

	@Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
		NBTTagCompound tag = stack.getTagCompound();
		int biomeId = -1;
		if ( tag != null )
		{
			NBTTagByte biome = ( NBTTagByte ) tag.getTag( SAMPLED_BIOME_TAG );
			if ( biome != null )
			{
				biomeId = biome.getByte() & 255;
			}
		}
		
		if ( pass == 1 )
		{
			return biomeId >= 0 ? BiomeGenBase.getBiomeGenArray()[ biomeId ].color : 0;
		}
		
        return 0xFFFFFFFF;
    }
	
	@Override
    public boolean onItemUse( ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float par8, float par9, float par10 )
    {
		NBTTagCompound tag = stack.getTagCompound();
		if ( tag == null )
		{
			tag = new NBTTagCompound();
		}
		
		if ( player.isSneaking() )
		{
			if ( world.isRemote ) return true;
			
			int size = tag.hasKey( ACTION_SIZE_TAG ) ? tag.getInteger( ACTION_SIZE_TAG ) : 7;
			switch ( size )
			{
				case 1:  size = 3; break;
				case 3:  size = 5; break;
				case 5:  size = 7; break;
				case 7:  size = 9; break;
				case 9:  size = 1; break;
				default: size = 7; break;
			}
			tag.setInteger( ACTION_SIZE_TAG, size );
			
			TranslateUtils.chat( player, "item.wand.chat.actionSizeChanged", size );
		}
		else if ( tag.hasKey( SAMPLED_BIOME_TAG ) )
		{
			int size = tag.getInteger( ACTION_SIZE_TAG );
			int rad = size / 2;
			byte biome = tag.getByte( SAMPLED_BIOME_TAG );
			
			int totalDamage = 0;
			for ( int ix = pos.getX() - rad; ix <= pos.getX() + rad; ++ix )
			{
				for ( int iz = pos.getZ() - rad; iz <= pos.getZ() + rad; ++iz )
				{
					int relBlockX = ix & 0xF;
					int relBlockZ = iz & 0xF;

					Chunk chunk = world.getChunkFromBlockCoords( new BlockPos( ix, pos.getY(), iz ) );
					chunk.getBiomeArray()[ relBlockZ << 4 | relBlockX ] = biome;
					chunk.setModified( true );

					BiomeWandMod.proxy.updateRendererAt( ix, iz );
					++totalDamage;
				}
			}

			if ( world.isRemote ) return true;
			if ( !player.capabilities.isCreativeMode )
			{
				stack.damageItem( totalDamage, player );
			}
		}
		else
		{
			if ( world.isRemote ) return false;
			
			TranslateUtils.chat( player, "item.wand.chat.needBiomeSample" );
			return false;
		}
		
		stack.setTagCompound( tag );
		
		return true;
    }
	
	@Override
    public void addInformation( ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		List< String > lines = new ArrayList< String >();
		for ( String str : TranslateUtils.translate( "item.wand.tooltip.info1" ).split( "\\\\n" ) ) lines.add( str );
		for ( String str : TranslateUtils.translate( "item.wand.tooltip.info2" ).split( "\\\\n" ) ) lines.add( str );
		for ( String str : TranslateUtils.translate( "item.wand.tooltip.info3" ).split( "\\\\n" ) ) lines.add( str );
		lines.add( "" );
		for ( int i = 0; i < lines.size(); ++i )
		{
			lines.set( i, lines.get( i ).replace( "\\t", "    " ) );
		}
		list.addAll( lines );
		
		NBTTagCompound tag = stack.getTagCompound();
		if ( tag == null )
		{
			tag = new NBTTagCompound();
		}

		int size = tag.hasKey( "actionSize" ) ? tag.getInteger( "actionSize" ) : 7;
		list.add( TranslateUtils.translate( "item.wand.tooltip.size", size ) );
		
		String biomeStr = TranslateUtils.translate( "item.sample.tooltip.none" );
		if ( tag.hasKey( SAMPLED_BIOME_TAG ) )
		{
			BiomeGenBase b = BiomeGenBase.getBiomeGenArray()[ tag.getByte( SAMPLED_BIOME_TAG ) & 255 ];
			if ( b == null )
			{
				biomeStr = TranslateUtils.translate( "item.sample.tooltip.unknown" );
			}
			else
			{
				biomeStr = b.biomeName;
			}
		}
		list.add( TranslateUtils.translate( "item.sample.tooltip.biome", biomeStr ) );
		
	}
	
	public static final String SAMPLED_BIOME_TAG = "sampledBiome";
	public static final String ACTION_SIZE_TAG = "actionSize";
}
