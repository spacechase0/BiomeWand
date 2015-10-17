package com.spacechase0.minecraft.biomewand;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.spacechase0.minecraft.biomewand.item.SampleItem;
import com.spacechase0.minecraft.biomewand.item.WandItem;

public class WandAttunementRecipes implements IRecipe
{

	@Override
	public boolean matches( InventoryCrafting inv, World world )
	{
		return ( getCraftingResult( inv ) != null );
	}

	@Override
	public ItemStack getCraftingResult( InventoryCrafting inv )
	{
		ItemStack wand = null, sample = null;
		for ( int i = 0; i < inv.getSizeInventory(); ++i )
		{
			ItemStack stack = inv.getStackInSlot( i );
			if ( stack == null ) continue;
			
			if ( stack.getItem() == BiomeWandMod.items.wand && wand == null )
			{
				wand = stack;
			}
			else if ( stack.getItem() == BiomeWandMod.items.sample && sample == null &&
			          stack.getTagCompound().hasKey( SampleItem.SAMPLED_BIOME_TAG ) )
			{
				sample = stack;
			}
			else
			{
				return null;
			}
		}
		if ( wand == null || sample == null ) return null;
		
		ItemStack ret = wand.copy();
		
		NBTTagCompound tag = ret.getTagCompound();
		if ( tag == null ) tag = new NBTTagCompound();
		tag.setTag( WandItem.SAMPLED_BIOME_TAG, sample.getTagCompound().getTag( SampleItem.SAMPLED_BIOME_TAG ).copy() );
		ret.setTagCompound( tag );
		
		return ret;
	}

	@Override
	public int getRecipeSize()
	{
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems( InventoryCrafting inv )
	{
		ItemStack[] stacks = new ItemStack[ inv.getSizeInventory() ];
		for ( int i = 0; i < stacks.length; ++i )
		{
			ItemStack stack = inv.getStackInSlot( i );
			if ( stack == null ) continue;
			
			if ( stack.getItem() == BiomeWandMod.items.sample )
			{
				stacks[ i ] = stack;
			}
		}
		
		return stacks;
	}
}
