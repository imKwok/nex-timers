package net.runelite.client.plugins.nextimers;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.nextimers.NexTimersOverlay;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Nex Timers",
	description = "Split timer for the Nex boss fight",
	tags = {"nex", "prison", "timer", "splits", "minion"}
)

public class NexTimersPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private NexTimersConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NexTimersOverlay nexTimersOverlay;

	public int currentPhaseNum = -1;
    public int[] startTicks = {0, 0, 0, 0 ,0 ,0 ,0 ,0 ,0 ,0};
	public int[] endTicks 	= {0, 0, 0, 0 ,0 ,0 ,0 ,0 ,0 ,0};
	public int totalTime = -1;

	public void resetSplits() {
		currentPhaseNum = -1;
		startTicks 	= new int[]{0, 0, 0, 0 ,0 ,0 ,0 ,0 ,0 ,0};
		endTicks 	= new int[]{0, 0, 0, 0 ,0 ,0 ,0 ,0 ,0 ,0};
		totalTime = 0;
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(nexTimersOverlay);
		resetSplits();
		//log.info("Plugin started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(nexTimersOverlay);
		//log.info("Plugin stopped!");
	}

	public void setPhaseTimes() {
		currentPhaseNum++;
		startTicks[currentPhaseNum] = client.getTickCount();
		if(currentPhaseNum > 0) {
			endTicks[currentPhaseNum - 1] = client.getTickCount();
			//log.info("Phase #: " + (currentPhaseNum - 1) + " | Start Tick: " + startTicks[currentPhaseNum - 1] + " | End Tick: " + endTicks[currentPhaseNum - 1]);
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if(chatMessage.getMessage().contains("Fill my soul with smoke!") && currentPhaseNum == -1) {
			resetSplits();
			setPhaseTimes(); // 0
			log.info("Nex fight has started.");
		}
		if(chatMessage.getMessage().contains("Fumus, don't fail me!") && currentPhaseNum == 0) {
			setPhaseTimes(); // 1
			log.info("Fumus phase has started.");
		}
		if(chatMessage.getMessage().contains("Darken my shadow!") && currentPhaseNum == 1) {
			setPhaseTimes(); // 2
			log.info("Nex SHADOW phase has started.");
		}
		if(chatMessage.getMessage().contains("Umbra, don't fail me!") && currentPhaseNum == 2) {
			setPhaseTimes(); // 3
			log.info("Umbra phase has started.");
		}
		if(chatMessage.getMessage().contains("Flood my lungs with blood!") && currentPhaseNum == 3) {
			setPhaseTimes(); // 4
			log.info("Nex BLOOD phase has started.");
		}
		if(chatMessage.getMessage().contains("Cruor, don't fail me!") && currentPhaseNum == 4) {
			setPhaseTimes(); // 5
			log.info("Cruor phase has started.");
		}
		if(chatMessage.getMessage().contains("Infuse me with the power of ice!") && currentPhaseNum == 5) {
			setPhaseTimes(); // 6
			log.info("Nex ICE phase has started.");
		}
		if(chatMessage.getMessage().contains("Glacies, don't fail me!") && currentPhaseNum == 6) {
			setPhaseTimes(); // 7
			log.info("Glacies phase has started.");
		}
		if(chatMessage.getMessage().contains("NOW, THE POWER OF ZAROS!") && currentPhaseNum == 7) {
			setPhaseTimes(); // 8
			log.info("Nex ZAROS phase has started.");
		}
		if(chatMessage.getMessage().contains("Taste my wrath!")) {
			currentPhaseNum = -1;
			log.info("Fight has ended.");

			if(config.sendChatMessage()) {
				if (!config.combineTime()) {
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
						"P1 (Nex): " 	+ nexTimersOverlay.format((endTicks[0] - startTicks[0]))
						+ " | P1 (Fumus): " 	+ nexTimersOverlay.format((endTicks[1] - startTicks[1]))
						+ " | P2 (Nex): " 		+ nexTimersOverlay.format((endTicks[5] - startTicks[4]))
						+ " | P2 (Umbra): " 	+ nexTimersOverlay.format((endTicks[7] - startTicks[6]))
						+ " | P3 (Nex): " 		+ nexTimersOverlay.format((endTicks[8] - startTicks[7]))
						+ " | P3 (Cruor): " 	+ nexTimersOverlay.format((endTicks[1] - startTicks[0]))
						+ " | P4 (Nex): " 		+ nexTimersOverlay.format((endTicks[3] - startTicks[2]))
						+ " | P4 (Glacies): " 	+ nexTimersOverlay.format((endTicks[5] - startTicks[4]))
						+ " | P5 (Zaros): " 	+ nexTimersOverlay.format((endTicks[7] - startTicks[6])), null);
					/*
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P1 (Nex):        " + nexTimersOverlay.format((endTicks[0] - startTicks[0])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P1 (Fumus):    " + nexTimersOverlay.format((endTicks[1] - startTicks[1])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P2 (Nex):        " + nexTimersOverlay.format((endTicks[5] - startTicks[4])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P2 (Umbra):   " + nexTimersOverlay.format((endTicks[7] - startTicks[6])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P3 (Nex):        " + nexTimersOverlay.format((endTicks[8] - startTicks[7])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P3 (Cruor):     " + nexTimersOverlay.format((endTicks[1] - startTicks[0])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P4 (Nex):        " + nexTimersOverlay.format((endTicks[3] - startTicks[2])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P4 (Glacies):  " + nexTimersOverlay.format((endTicks[5] - startTicks[4])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P5 (Zaros):    " + nexTimersOverlay.format((endTicks[7] - startTicks[6])), null);*/
				} else {
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
				"P1 (Smoke): " + nexTimersOverlay.format((endTicks[1] - startTicks[0]))
						+ " | P2 (Shadow): " + nexTimersOverlay.format((endTicks[3] - startTicks[2]))
						+ " | P3 (Blood): " + nexTimersOverlay.format((endTicks[5] - startTicks[4]))
						+ " | P4 (Ice): " + nexTimersOverlay.format((endTicks[7] - startTicks[6]))
						+ " | P5 (Zaros): " + nexTimersOverlay.format((endTicks[8] - startTicks[7])), null);
					/*
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P1 (Smoke):   " + nexTimersOverlay.format((endTicks[1] - startTicks[0])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P2 (Shadow): " + nexTimersOverlay.format((endTicks[3] - startTicks[2])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P3 (Blood):     " + nexTimersOverlay.format((endTicks[5] - startTicks[4])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P4 (Ice):          " + nexTimersOverlay.format((endTicks[7] - startTicks[6])), null);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "P5 (Zaros):    " + nexTimersOverlay.format((endTicks[8] - startTicks[7])), null); */
				}
			}
			//TO DO: send to a readable file?
		}
	}

	@Subscribe
	public void onGameTick(GameTick e)
	{
        int currentTick = client.getTickCount() + 1;
		if(currentPhaseNum >= 0 && currentPhaseNum != 9) {
			endTicks[currentPhaseNum] = currentTick;
			totalTime++;
			//log.info("Number of ticks after start: " + totalTime + " | Current Phase End Tick: " + endTicks[currentPhaseNum]);
		}
	}

	/*
	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}*/

	@Provides
	NexTimersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NexTimersConfig.class);
	}
}