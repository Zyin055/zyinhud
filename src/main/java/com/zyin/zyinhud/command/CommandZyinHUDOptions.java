package com.zyin.zyinhud.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import com.zyin.zyinhud.gui.GuiZyinHUDOptions;

public class CommandZyinHUDOptions extends CommandBase
{
	private static Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public String getCommandName()
	{
		return "zyinhud";
	}
	
	@Override
    public List getCommandAliases()
    {
		List list = new ArrayList();
		list.add("zh");
		list.add("zyin");
		list.add("zyinshud");
		list.add("zyinoptions");
		list.add("zyinhudoptions");
		list.add("zyinshudoptions");
        return list;
    }

	@Override
	public String getCommandUsage(ICommandSender iCommandSender)
	{
		return "commands.zyinhudoptions.usage";
	}

	@Override
	public void processCommand(ICommandSender iCommandSender, String[] parameters)
	{
		mc.displayGuiScreen(new GuiZyinHUDOptions(null));
	}
	
	@Override
	public int compareTo(Object t)
	{
		return ((CommandBase)t).getCommandName().compareTo(getCommandName());
	}
}
