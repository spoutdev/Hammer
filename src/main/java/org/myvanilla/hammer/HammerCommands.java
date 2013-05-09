/*
 * This file is part of Hammer.
 *
 * Copyright (c) 2012-2013, Spout LLC <http://www.spout.org/>
 * Hammer is licensed under the Spout License Version 1.
 *
 * Hammer is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Hammer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
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
