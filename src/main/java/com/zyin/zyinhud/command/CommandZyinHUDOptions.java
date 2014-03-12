package com.zyin.zyinhud.command;

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
    public String getCommandUsage(ICommandSender iCommandSender)
    {
        return "commands.zyinhud.usage";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] parameters)
    {
        mc.displayGuiScreen(new GuiZyinHUDOptions(null));
    }

    @Override
    public int compareTo(Object t)
    {
        return ( (CommandZyinHUDOptions) t ).getCommandName().compareTo( getCommandName() );
    }
}
