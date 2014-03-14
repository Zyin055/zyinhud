package com.zyin.zyinhud.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import com.zyin.zyinhud.mods.Fps;

public class CommandFps extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "fps";
	}

	@Override
	public String getCommandUsage(ICommandSender iCommandSender)
	{
		return "commands.fps.usage";
	}

	@Override
	public void processCommand(ICommandSender iCommandSender, String[] parameters)
	{
		Fps.ToggleEnabled();
	}
	
	@Override
	public int compareTo(Object t)
	{
		return ((CommandBase)t).getCommandName().compareTo(getCommandName());
	}
}
