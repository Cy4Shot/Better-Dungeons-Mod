package com.cy4.betterdungeons.common.command.impl;

import java.util.Collection;

import com.cy4.betterdungeons.common.command.BaseCommand;
import com.cy4.betterdungeons.common.item.MerchantItem;
import com.cy4.betterdungeons.common.merchant.MerchantNameGenerator;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

public class GiveMerchantCommand extends BaseCommand {

	public GiveMerchantCommand(String command, int permissionLevel, boolean enabled) {
		super(command, permissionLevel, enabled);
	}

	@Override
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return builder.then(Commands.argument("player", EntityArgument.players())
				.executes(context -> execute(context.getSource(), EntityArgument.getPlayers(context, "player"))));
	}

	private int execute(CommandSource source, Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
		for (ServerPlayerEntity player : players) {
			player.addItemStackToInventory(MerchantItem.generate(MerchantNameGenerator.getName()));
		}

		return Command.SINGLE_SUCCESS;
	}
}