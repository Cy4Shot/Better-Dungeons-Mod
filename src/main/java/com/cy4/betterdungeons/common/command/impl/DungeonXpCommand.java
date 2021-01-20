package com.cy4.betterdungeons.common.command.impl;

import com.cy4.betterdungeons.common.command.BaseCommand;
import com.cy4.betterdungeons.core.network.data.PlayerDungeonData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

public class DungeonXpCommand extends BaseCommand {

	public DungeonXpCommand(String command, int permissionLevel, boolean enabled) {
		super(command, permissionLevel, enabled);
	}

	@Override
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return builder.then(Commands.argument("player", EntityArgument.player())
				.then(Commands.argument("xp", LongArgumentType.longArg()).executes(context -> execute(context.getSource(),
						EntityArgument.getPlayer(context, "player"), LongArgumentType.getLong(context, "xp")))));
	}

	private int execute(CommandSource source, PlayerEntity p, long amount) throws CommandSyntaxException {
		PlayerDungeonData statsData = PlayerDungeonData.get((ServerWorld) p.getEntityWorld());
		statsData.addDungeonExp((ServerPlayerEntity) p, (int) amount);
		return Command.SINGLE_SUCCESS;
	}
}