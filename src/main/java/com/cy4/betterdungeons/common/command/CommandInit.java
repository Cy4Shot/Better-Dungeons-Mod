package com.cy4.betterdungeons.common.command;

import java.util.ArrayList;

import com.cy4.betterdungeons.common.command.impl.TemplateCommand;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;

public class CommandInit {
	
	private static final ArrayList<BaseCommand> commands = new ArrayList<>();
	
	public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        commands.add(new TemplateCommand("template", 4, true));

        commands.forEach((cmd) -> {
            if (cmd.isEnabled() && cmd.setExecution() != null) {
                dispatcher.register(cmd.getBuilder());
            }
        });
    }

}
