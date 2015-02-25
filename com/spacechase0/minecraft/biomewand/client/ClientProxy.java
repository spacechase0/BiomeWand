package com.spacechase0.minecraft.biomewand.client;

import net.minecraft.client.Minecraft;

import com.spacechase0.minecraft.biomewand.CommonProxy;

public class ClientProxy extends CommonProxy
{
	@Override
	public void updateRendererAt( int x, int z )
	{
		Minecraft.getMinecraft().renderGlobal.markBlockRangeForRenderUpdate( x, 0, z, x, 255, z );
	}
}
