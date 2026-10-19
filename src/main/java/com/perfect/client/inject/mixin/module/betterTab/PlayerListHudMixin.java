package com.perfect.client.inject.mixin.module.betterTab;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;

import static com.perfect.client.manager.system.FunctionManager.betterTab;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

	@Shadow
	@Final
	private static Comparator<PlayerListEntry> ENTRY_ORDERING;

	@Shadow
	@Final
	private MinecraftClient client;

	@ModifyVariable(
			method = "render(Lnet/minecraft/client/gui/DrawContext;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
			at = @At(value = "STORE"),
			ordinal = 0
	)
	private static boolean perfect$modifyPlayerHeads(boolean oldValue) {
		return betterTab.getPlayerHeads();
	}

	@SuppressWarnings("DataFlowIssue")
	@Inject(
			method = "collectPlayerEntries",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$modifyPlayersCount(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
		cir.setReturnValue(client.player.networkHandler.getListedPlayerListEntries().stream().sorted(ENTRY_ORDERING).limit(betterTab.getTabSize(80)).toList());
	}

	@Inject(
			method = "getPlayerName",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$modifyNicknameColor(PlayerListEntry playerListEntry, CallbackInfoReturnable<Text> info) {
		if (betterTab.isEnabled()) {
			info.setReturnValue(betterTab.formatName(playerListEntry));
		}
	}
}