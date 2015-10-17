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

public class SampleItem extends Item
{
	public SampleItem()
	{
		maxStackSize = 1;
		
		setUnlocalizedName( "sample" );
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
		if ( world.isRemote ) return true;
		
		NBTTagCompound tag = stack.getTagCompound();
		if ( tag == null )
		{
			tag = new NBTTagCompound();
		}
		
		if ( !tag.hasKey( SAMPLED_BIOME_TAG ) || player.isSneaking() )
		{
			int relBlockX = pos.getX() & 0xF;
			int relBlockZ = pos.getZ() & 0xF;
			
			Chunk chunk = world.getChunkFromBlockCoords( pos );
			byte biome = chunk.getBiomeArray()[ relBlockZ * 16 + relBlockX ];
			tag.setByte( SAMPLED_BIOME_TAG, biome );
		}
		else
		{
			return false;
		}
		
		stack.setTagCompound( tag );
		return true;
    }
	
	@Override
    public void addInformation( ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		List< String > lines = new ArrayList< String >();
		for ( String str : TranslateUtils.translate( "item.sample.tooltip.info1" ).split( "\\\\n" ) ) lines.add( str );
		for ( String str : TranslateUtils.translate( "item.sample.tooltip.info2" ).split( "\\\\n" ) ) lines.add( str );
		lines.add( "" );
		for ( int i = 0; i < lines.size(); ++i )
		{
			lines.set( i, lines.get( i ).replace( "\\t", "    " ) );
		}
		list.addAll( lines );
		
		NBTTagCompound tag = stack.getTagCompound();
		NBTTagByte biome = ( tag == null ) ? null : ( ( NBTTagByte ) tag.getTag( SAMPLED_BIOME_TAG ) );
		
		String biomeStr = TranslateUtils.translate( "item.sample.tooltip.none" );
		if ( biome != null )
		{
			BiomeGenBase b = BiomeGenBase.getBiomeGenArray()[ biome.getByte() & 255 ];
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
			compound.setByte( SAMPLED_BIOME_TAG, ( byte ) biome.biomeID );
			
			ItemStack wand = ( ItemStack ) new ItemStack( this, 1 );
			wand.setTagCompound( compound );
			list.add( wand );
		}
    }
	
	public static final String SAMPLED_BIOME_TAG = WandItem.SAMPLED_BIOME_TAG;
}
