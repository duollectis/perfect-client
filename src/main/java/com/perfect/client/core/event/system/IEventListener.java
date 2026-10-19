package com.perfect.client.core.event.system;

import com.perfect.client.core.event.events.block.BlockEvent;
import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import com.perfect.client.core.event.events.block.InteractBlockEvent;
import com.perfect.client.core.event.events.block.ServerBlockUpdateEvent;
import com.perfect.client.core.event.events.chat.MessageReceiveEvent;
import com.perfect.client.core.event.events.chat.MessageSendEvent;
import com.perfect.client.core.event.events.entity.*;
import com.perfect.client.core.event.events.function.PacketMineEvent;
import com.perfect.client.core.event.events.input.keyboard.CharEvent;
import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.event.events.input.mouse.MouseMoveEvent;
import com.perfect.client.core.event.events.input.mouse.MouseScrollEvent;
import com.perfect.client.core.event.events.movement.*;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.event.events.network.ScreenHandlerSlotUpdateEvent;
import com.perfect.client.core.event.events.render.*;
import com.perfect.client.core.event.events.world.ChunkDataEvent;
import com.perfect.client.core.event.events.world.SoundEvent;

public interface IEventListener {

	default void onChar(CharEvent event) {}
	default void onGlobalKey(KeyEvent event) {}

	default void onBindManagerKey(KeyEvent event) {}
	default void onKey(KeyEvent event) {}

	default void onMovementInput(MovementInputEvent event) {}
	default void onPostMovementInput(MovementInputEvent event) {}

	default void onGlobalMouseButton(MouseButtonEvent event) {}

	default void onBindManagerMouseButton(MouseButtonEvent event) {}
	default void onMouseButton(MouseButtonEvent event) {}

	default void onMouseMove(MouseMoveEvent event) {}
	default void onMouseScroll(MouseScrollEvent event) {}

	default void onClickSlot() {}

	default void onClientPlayerUpdate() {}

	default void onMultiTask(Cancellable event) {}

	default void onResourcePacksReloaded() {}

	default void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateEvent event) {}

	default void onShutdown() {}

	default void onEntityPush(EntityPushEvent event) {}

	default void onCrystalManagerEntityRemove(EntityEvent event) {}
	default void onEntityRemove(EntityEvent event) {}

	default void onCrystalManagerEntitySpawn(EntityEvent event) {}
	default void onProjectileManagerEntitySpawn(EntityEvent event) {}
	default void onEntitySpawn(EntityEvent event) {}

	default void onPlayerDeath(PlayerEvent event) {}
	default void onPlayerTotemPop(PlayerEvent event) {}

	default void onTickEntities() {}

	default void onBlockUpdate(BlockUpdateEvent event) {}
	default void onBlockInteract(BlockUpdateEvent event) {}
	default void onServerBlockUpdate(ServerBlockUpdateEvent event) {}

	default void onPrePacketReceive(PacketEvent event) {}

	default void onPostPacketReceive(PacketEvent event) {}

	default void onAntiAcDetectPrePacketSend(PacketEvent event) {}
	default void onPrePacketSend(PacketEvent event) {}
	default void onRotationManagerPrePacketSend(PacketEvent event) {}

	default void onPostPacketSend(PacketEvent event) {}

	default void onChunkData(ChunkDataEvent event) {}

	default void onGameJoin() {}
	default void onGameLeft() {}

	default void onRespawn() {}

	default void onAttackAuraTick() {}
	default void onHandleInput() {}

	default void onPreTick() {}
	default void onPostTick() {}

	default void onPacketMine(PacketMineEvent event) {}

	default void onAttackBlock(BlockEvent event) {}
	default void onStartBreakingBlock(BlockEvent event) {}

	default void onAttackEntity(AttackEntityEvent event) {}

	default void onBlockBreakingProgress(BlockEvent event) {}

	default void onBlockPush(Cancellable event) {}

	default void onClipAtEdge(ClipAtEdgeEvent event) {}

	default void onFinishUsingItem(FinishUsingItemEvent event) {}

	default void onFluidPush(Cancellable event) {}

	default void onInteractBlock(InteractBlockEvent event) {}

	default void onPreJump(Cancellable event) {}
	default void onPostJump() {}

	default void onJumpVelocityMultiplier(JumpVelocityMultiplierEvent event) {}

	default void onItemSlowDown(Cancellable event) {}

	default void onCrawlSlowDown(Cancellable event) {}

	default void onSneakSlowDown(Cancellable event) {}


	default void onMovementForCollisions(MovementForCollisionsEvent event) {}

	default void onHoleSnapPrePlayerMove(PlayerMoveEvent event) {}
	default void onPrePlayerMove(PlayerMoveEvent event) {}
	default void onHoleSnapPostPlayerMove(PlayerMoveEvent event) {}

	default void onPostPlayerMove() {}

	default void onMovementIteration(MovementIterationEvent event) {}

	default void onRotationManagerPreRotate() {}

	default void onRotationFunctionRotate() {}
	default void onRotate() {}
	default void onAutoCrystalRotate() {}
	default void onAutoWebRotate() {}
	default void onAutoMineRotate() {}
	default void onPlaceFunctionRotate() {}
	default void onPhaseRotate() {}

	default void onPreSendMovement() {}
	default void onRotationManagerPreSendMovement() {}

	default void onPostSendMovement() {}
	default void onAutoCrystalPostSendMovement() {}
	default void onRotationManagerPostSendMovement() {}

	default void onPreTravel(TravelEvent event) {}
	default void onPostTravel(TravelEvent event) {}

	default void onPreUpdateVelocity(UpdateVelocityEvent event) {}
	default void onRotationManagerPreUpdateVelocity(UpdateVelocityEvent event) {}

	default void onPostUpdateVelocity() {}
	default void onRotationManagerPostUpdateVelocity() {}

	default void onChunkOcclusion(Cancellable event) {}

	default void onHeldItemRenderer(HeldItemRendererEvent event) {}

	default void onHudRender(HudRenderEvent event) {}

	default void onParticle(ParticleEvent event) {}

	default void onPostProcess() {}

	default void onRender2D(Render2DEvent event) {}

	default void onGuiRender(Render2DEvent event) {}

	default void onRenderTooltip(TooltipRenderEvent event) {}

	default void onRender3D(Render3DEvent event) {}
	default void onPostRender3D() {}

	default void onRenderSlot(RenderHandledScreenEvent event) {}

	default void onScreenChanged(ScreenChangedEvent event) {}
	default void onPostScreenChanged(ScreenChangedEvent event) {}
	default void onAutoRespawnScreenChanged(ScreenChangedEvent event) {}

	default void onStartRenderTick() {}

	default void onPreUsingItem() {}
	default void onPostUsingItem() {}

	default void onUpdateCrosshairTarget(Cancellable event) {}

	default void onScreenMouse(Cancellable event) {}
	default void onScreenKey(Cancellable event) {}

	default void onMessageReceive(MessageReceiveEvent event) {}
	default void onMessageSend(MessageSendEvent event) {}

	default void onSound(SoundEvent event) {}
}
