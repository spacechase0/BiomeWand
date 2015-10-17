package com.spacechase0.minecraft.biomewand.client;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import com.spacechase0.minecraft.biomewand.BiomeWandMod;
import com.spacechase0.minecraft.biomewand.CommonProxy;
import com.spacechase0.minecraft.biomewand.item.WandItem;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		RenderItem ri = Minecraft.getMinecraft().getRenderItem();
		ItemModelMesher imm = ri.getItemModelMesher();

		imm.register( BiomeWandMod.items.wand, 0, new ModelResourceLocation( "sc0_biomewand:wand", "inventory" ) );
		imm.register( BiomeWandMod.items.sample, 0, new ModelResourceLocation( "sc0_biomewand:sample", "inventory" ) );
		
		MinecraftForge.EVENT_BUS.register( this );
	}
	
	@Override
	public void updateRendererAt( int x, int z )
	{
		Minecraft.getMinecraft().renderGlobal.markBlockRangeForRenderUpdate( x, 0, z, x, 255, z );
	}
	
	@SubscribeEvent
	public void highlight( DrawBlockHighlightEvent event )
	{
		ItemStack held = event.player.getHeldItem();
		if ( held == null || held.getItem() != BiomeWandMod.items.wand )
		{
			return;
		}
		NBTTagCompound tag = held.getTagCompound();
		if ( tag == null ) tag = new NBTTagCompound();
		
		if ( !tag.hasKey( WandItem.SAMPLED_BIOME_TAG ) )
		{
			return;
		}
		
		int size = tag.hasKey( WandItem.ACTION_SIZE_TAG ) ? tag.getInteger( WandItem.ACTION_SIZE_TAG ) : 7;
		drawSelectionBox( size, event.context, event.player, event.target, 0, event.partialTicks );
		//event.setCanceled( true );
	}
	
	// From RenderGlobal, modified
    private static void drawSelectionBox( int size, RenderGlobal context, EntityPlayer p_72731_1_, MovingObjectPosition p_72731_2_, int p_72731_3_, float p_72731_4_)
    {
        if (p_72731_3_ == 0 && p_72731_2_.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
        	// MINE
        	World theWorld = p_72731_1_.worldObj;
        	int x = p_72731_2_.getBlockPos().getX();
        	int z = p_72731_2_.getBlockPos().getZ();
        	int rad = size / 2;
        	
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            float f1 = 0.002F;
            BlockPos blockpos = p_72731_2_.getBlockPos();
            Block block = theWorld.getBlockState(blockpos).getBlock();

            if (block.getMaterial() != Material.air && theWorld.getWorldBorder().contains(blockpos))
            {
                block.setBlockBoundsBasedOnState(theWorld, blockpos);
                double d0 = p_72731_1_.lastTickPosX + (p_72731_1_.posX - p_72731_1_.lastTickPosX) * (double)p_72731_4_;
                double d1 = p_72731_1_.lastTickPosY + (p_72731_1_.posY - p_72731_1_.lastTickPosY) * (double)p_72731_4_;
                double d2 = p_72731_1_.lastTickPosZ + (p_72731_1_.posZ - p_72731_1_.lastTickPosZ) * (double)p_72731_4_;
                AxisAlignedBB box = new AxisAlignedBB( x - rad, 0, z - rad, x + 1 + rad, 255, z + 1 + rad ).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2);
                context.drawOutlinedBoundingBox(box, -1);
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
}
