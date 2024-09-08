package net.runelite.client.plugins.nextimers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("nextimers")
public interface NexTimersConfig extends Config
{
	@ConfigItem(
		keyName = "combineTime",
		name = "Combine Boss & Minion Times",
		description = "Combines the splits of Nex and her minions."
	)
	default boolean combineTime()
	{
		return false;
	}

	@ConfigItem(
			keyName = "displayOverlay",
			name = "Display Overlay",
			description = "Displays splits overlay."
	)
	default boolean displayOverlay()
	{
		return true;
	}

	@ConfigItem(
			keyName = "displayTimePrecise",
			name = "Precise Time",
			description = "Sets timer to be precise."
	)
	default boolean displayTimePrecise()
	{
		return true;
	}

	@ConfigItem(
			keyName = "sendChatMessage",
			name = "Game Chat Report",
			description = "Sends a message of all splits in game chat."
	)
	default boolean sendChatMessage()
	{
		return true;
	}
}
