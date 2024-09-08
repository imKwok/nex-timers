package net.runelite.client.plugins.nextimers;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import java.awt.*;

class NexTimersOverlay extends OverlayPanel
{
    private final NexTimersPlugin nexTimersPlugin;
    private final NexTimersConfig nexTimersConfig;
    private final Client client;

    @Inject
    NexTimersOverlay(NexTimersPlugin nexTimersPlugin, NexTimersConfig nexTimersConfig, Client client)
    {
        super(nexTimersPlugin);
        this.nexTimersPlugin = nexTimersPlugin;
        this.nexTimersConfig = nexTimersConfig;
        this.client = client;
    }

    public String format(int ticks)
    {
        if (ticks <= 0) {
            if(!nexTimersConfig.displayTimePrecise()) {
                return "--:--";
            } else {
                return "--:--.--";
            }
        }
        int mins = (ticks / 100);
        float secondsReal = (ticks % 100) * 0.6f;
        int seconds = (int) secondsReal;
        int tenths = Math.round((secondsReal - seconds) * 10f);

        if(!nexTimersConfig.displayTimePrecise()) {
            return String.format("%d:%02d", mins, seconds);
        } else {
            return String.format("%d:%02d.%d0", mins, seconds, tenths);
        }
    }

    private static final int REGION_NEX_0 = 11345;
    private static final int REGION_NEX_1 = 11601;
    boolean inPrison = false;

    @Override
    public Dimension render(Graphics2D graphics)
    {
        // check if player is currently in the Ancient Prison
        LocalPoint lp = client.getLocalPlayer().getLocalLocation();
        int region = lp == null ? -1 : WorldPoint.fromLocalInstance(client, lp).getRegionID();
        inPrison = (region == REGION_NEX_1 || region == REGION_NEX_0);

        int currentSplit;
        int currentPhase = nexTimersPlugin.currentPhaseNum;
        int currentPhaseStartTick;
        int currentPhaseEndTick;
        int lastPhaseStartTick;
        int lastPhaseEndTick;

        if(currentPhase == 0) { // fight has started
            currentPhaseStartTick   = nexTimersPlugin.startTicks[currentPhase];
            currentPhaseEndTick     = nexTimersPlugin.endTicks[currentPhase];
            lastPhaseStartTick      = 0;
            lastPhaseEndTick        = 0;
            currentSplit = currentPhaseEndTick - currentPhaseStartTick;
        } else if(currentPhase > 0){
            currentPhaseStartTick   = nexTimersPlugin.startTicks[currentPhase];
            currentPhaseEndTick     = nexTimersPlugin.endTicks[currentPhase];
            lastPhaseStartTick      = nexTimersPlugin.startTicks[currentPhase - 1];
            lastPhaseEndTick        = nexTimersPlugin.endTicks[currentPhase - 1];
            if(!nexTimersConfig.displayTimePrecise())
            {
                currentSplit = currentPhaseEndTick - lastPhaseStartTick;
            } else {
                currentSplit = currentPhaseEndTick - currentPhaseStartTick;
            }
        } else {
            currentSplit = 0;
        }

        if(inPrison) { // Display overlay only in Ancient Prison
            if(nexTimersConfig.displayOverlay()) {

                final String title = "Nex Splits";
                panelComponent.getChildren().add(
                        TitleComponent.builder()
                                .text(title)
                                .build());

                if (nexTimersConfig.combineTime()) { // Display in combined time mode
                    // Currently in P1
                    if(nexTimersPlugin.currentPhaseNum == 0) {
                        panelComponent.getChildren().add(
                                LineComponent.builder()
                                        .left("P1 (Smoke)")
                                        .right(format(currentSplit))
                                        .build());
                    } else {
                        panelComponent.getChildren().add(
                                LineComponent.builder()
                                        .left("P1 (Smoke)")
                                        // combine both P1 (Nex) and P1 (Fumus) times
                                        .right(format(nexTimersPlugin.endTicks[1] - nexTimersPlugin.startTicks[0]))
                                        .build());
                    }
                    // Currently in P2
                    if(nexTimersPlugin.currentPhaseNum == 2) {
                        panelComponent.getChildren().add(
                                LineComponent.builder()
                                        .left("P2 (Shadow)")
                                        .right(format(currentSplit))
                                        .build());
                    } else {
                        panelComponent.getChildren().add(
                                LineComponent.builder()
                                        .left("P2 (Shadow)")
                                        // combine both P2 (Nex) and P2 (Umbra) times
                                        .right(format(nexTimersPlugin.endTicks[3] - nexTimersPlugin.startTicks[2]))
                                        .build());
                    }
                    // Currently in P3
                    if(nexTimersPlugin.currentPhaseNum == 4) {
                        panelComponent.getChildren().add(
                                LineComponent.builder()
                                        .left("P3 (Blood)")
                                        .right(format(currentSplit))
                                        .build());
                    } else {
                        panelComponent.getChildren().add(
                                LineComponent.builder()
                                        .left("P3 (Blood)")
                                        // combine both P3 (Nex) and P3 (Cruor) times
                                        .right(format(nexTimersPlugin.endTicks[5] - nexTimersPlugin.startTicks[4]))
                                        .build());
                    }
                    // Currently in P4
                    if(nexTimersPlugin.currentPhaseNum == 6) {
                        panelComponent.getChildren().add(
                                LineComponent.builder()
                                        .left("P4 (Ice)")
                                        .right(format(currentSplit))
                                        .build());
                    } else {
                        panelComponent.getChildren().add(
                                LineComponent.builder()
                                        .left("P4 (Ice)")
                                        // combine both P4 (Nex) and P4 (Glacies)
                                        .right(format(nexTimersPlugin.endTicks[7] - nexTimersPlugin.startTicks[6]))
                                        .build());
                    }
                    // Currently in P5
                    if(nexTimersPlugin.currentPhaseNum == 8) {
                        panelComponent.getChildren().add(
                                LineComponent.builder()
                                        .left("P5 (Zaros)")
                                        .right(format(currentSplit))
                                        .build());
                    } else {
                        panelComponent.getChildren().add(
                                LineComponent.builder()
                                        .left("P5 (Zaros)")
                                        .right(format(nexTimersPlugin.endTicks[8] - nexTimersPlugin.startTicks[7]))
                                        .build());
                    }
                    // Total Time
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("Total")
                                    .right(format(nexTimersPlugin.totalTime))
                                    .build());

                } else { // Display Nex and minion splits separately

                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("P1 (Nex)")
                                    .right(format(nexTimersPlugin.endTicks[0] - nexTimersPlugin.startTicks[0]))
                                    .build());
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("P1 (Fumus)")
                                    .right(format(nexTimersPlugin.endTicks[1] - nexTimersPlugin.startTicks[1]))
                                    .build());
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("P2 (Nex)")
                                    .right(format(nexTimersPlugin.endTicks[2] - nexTimersPlugin.startTicks[2]))
                                    .build());
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("P2 (Umbra)")
                                    .right(format(nexTimersPlugin.endTicks[3] - nexTimersPlugin.startTicks[3]))
                                    .build());
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("P3 (Nex)")
                                    .right(format(nexTimersPlugin.endTicks[4] - nexTimersPlugin.startTicks[4]))
                                    .build());
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("P3 (Cruor)")
                                    .right(format(nexTimersPlugin.endTicks[5] - nexTimersPlugin.startTicks[5]))
                                    .build());
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("P4 (Nex)")
                                    .right(format(nexTimersPlugin.endTicks[6] - nexTimersPlugin.startTicks[6]))
                                    .build());
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("P4 (Glacies)")
                                    .right(format(nexTimersPlugin.endTicks[7] - nexTimersPlugin.startTicks[7]))
                                    .build());
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("P5 (Nex)")
                                    .right(format(nexTimersPlugin.endTicks[8] - nexTimersPlugin.startTicks[8]))
                                    .build());
                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left("Total")
                                    .right(format(nexTimersPlugin.totalTime))
                                    .build());

                }
            }
        }
        return super.render(graphics);
    }
}
