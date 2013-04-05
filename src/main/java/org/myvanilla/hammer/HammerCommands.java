package org.myvanilla.hammer;

import org.myvanilla.hammer.converter.ConverterException;

import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.exception.CommandException;

import org.spout.vanilla.world.generator.VanillaGenerator;
import org.spout.vanilla.world.generator.VanillaGenerators;

public class HammerCommands {
	@Command(aliases = "convert", usage = "[world folder] [Generator]", desc = "Convert a minecraft world to the spout format", min = 1, max = 2)
	@CommandPermissions("hammer.convert")
	public void convertCommand(CommandContext args, CommandSource source) throws CommandException {
		try {
			VanillaGenerator generator = VanillaGenerators.byName(args.getString(1));
			HammerPlugin.getInstance().initializeConverter(args.getString(0));
			source.sendMessage("The converter has been initialized. Please type /confirmconvert to continue!");
		} catch (ConverterException e) {
			throw new CommandException(e.getMessage());
		}
	}

	@Command(aliases = "confirmconvert", desc = "Start the convertion process.", max = 0)
	@CommandPermissions("hammer.convert")
	public void confirmConvertCommand(CommandContext args, CommandSource source) throws CommandException {

	}
}
