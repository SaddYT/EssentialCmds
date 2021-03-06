/*
 * This file is part of EssentialCmds, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 - 2015 HassanS6000
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.hsyyid.essentialcmds.listeners;

import io.github.hsyyid.essentialcmds.EssentialCmds;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class PlayerInteractListener
{
	@Listener
	public void onPlayerInteractBlock(InteractBlockEvent event, @First Player player)
	{
		if (EssentialCmds.frozenPlayers.contains(player.getUniqueId()))
		{
			player.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot interact while frozen."));
			event.setCancelled(true);
			return;
		}

		if (EssentialCmds.jailedPlayers.contains(player.getUniqueId()))
		{
			player.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot interact while jailed."));
			event.setCancelled(true);
			return;
		}

		Optional<Location<World>> optLocation = event.getTargetBlock().getLocation();

		if (optLocation.isPresent() && optLocation.get().getTileEntity().isPresent())
		{
			Location<World> location = optLocation.get();
			TileEntity clickedEntity = location.getTileEntity().get();

			if (event.getTargetBlock().getState().getType().equals(BlockTypes.STANDING_SIGN) || event.getTargetBlock().getState().getType().equals(BlockTypes.WALL_SIGN))
			{
				Optional<SignData> signData = clickedEntity.getOrCreate(SignData.class);

				if (signData.isPresent())
				{
					SignData data = signData.get();
					CommandManager cmdService = Sponge.getGame().getCommandManager();
					String line0 = data.getValue(Keys.SIGN_LINES).get().get(0).toPlain();
					String line1 = data.getValue(Keys.SIGN_LINES).get().get(1).toPlain();
					String command = "warp " + line1;

					if (line0.equals("[Warp]"))
					{
						if (player.hasPermission("essentialcmds.warps.use.sign"))
						{
							cmdService.process(player, command);
						}
						else
						{
							player.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You do not have permission to use Warp Signs!"));
						}
					}
				}
			}
		}
	}
}
