package com.cy4.betterdungeons.common.command.impl;

import com.cy4.betterdungeons.common.command.BaseCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.server.ServerWorld;

public class TemplateCommand extends BaseCommand {

	private static final ResourceLocation ROOM_TEMPLATE = new ResourceLocation("betterdungeons:template/room_template");
	private static final ResourceLocation TUNNEL_TEMPLATE = new ResourceLocation("betterdungeons:template/tunnel_template");
	private static final ResourceLocation START_TEMPLATE = new ResourceLocation("betterdungeons:template/start_template");

	public TemplateCommand(String command, int permissionLevel, boolean enabled) {
		super(command, permissionLevel, enabled);
	}

	@Override
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return builder
				.then(Commands.literal("room").then(Commands.argument("blockPos", BlockPosArgument.blockPos()).executes(
						(context) -> execute(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "blockPos"), ROOM_TEMPLATE))))
				.then(Commands.literal("tunnel")
						.then(Commands.argument("blockPos", BlockPosArgument.blockPos())
								.executes((context) -> execute(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "blockPos"),
										TUNNEL_TEMPLATE))))
				.then(Commands.literal("start")
						.then(Commands.argument("blockPos", BlockPosArgument.blockPos()).executes((context) -> execute(context.getSource(),
								BlockPosArgument.getLoadedBlockPos(context, "blockPos"), START_TEMPLATE))));
	}

	private int execute(CommandSource source, BlockPos pos, ResourceLocation loc) throws CommandSyntaxException {
		ServerWorld worldIn = source.getWorld();
		worldIn.getStructureTemplateManager().getTemplateDefaulted(loc).func_237144_a_(worldIn, pos, (new PlacementSettings())
				.setRandom(worldIn.getRandom()).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK), worldIn.getRandom());

		sendMessage(source.asPlayer(), "command.betterdungeons.tp.success");
		return Command.SINGLE_SUCCESS;
	}
}