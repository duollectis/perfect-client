package com.perfect.client.core.event.system;

import com.perfect.client.PerfectClient;
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventManager implements IEventListener {

	private final List<IEventListener> listeners = new CopyOnWriteArrayList<>();

	public void subscribe(IEventListener eventListener) {
		listeners.add(eventListener);
	}

	public void unsubscribe(IEventListener eventListener) {
		// Удаление объекта по ссылке
		listeners.removeIf(obj -> obj == eventListener);
	}

	@Override
	public void onChar(CharEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onChar(event));
	}

	@Override
	public void onGlobalKey(KeyEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onGlobalKey(event));
	}

	@Override
	public void onKey(KeyEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onBindManagerKey(event));
		onCancellableEvent(event, eventListener -> eventListener.onKey(event));
	}

	@Override
	public void onMovementInput(MovementInputEvent event) {
		onEvent(eventListener -> eventListener.onMovementInput(event));
		onEvent(eventListener -> eventListener.onPostMovementInput(event));
	}

	@Override
	public void onGlobalMouseButton(MouseButtonEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onGlobalMouseButton(event));
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onBindManagerMouseButton(event));
		onCancellableEvent(event, eventListener -> eventListener.onMouseButton(event));
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onMouseMove(event));
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onMouseScroll(event));
	}

	@Override
	public void onClientPlayerUpdate() {
		onEvent(IEventListener::onClientPlayerUpdate);
	}

	@Override
	public void onClickSlot() {
		onEvent(IEventListener::onClickSlot);
	}

	@Override
	public void onMultiTask(Cancellable event) {
		onCancellableEvent(event, eventListener -> eventListener.onMultiTask(event));
	}

	@Override
	public void onResourcePacksReloaded() {
		onEvent(IEventListener::onResourcePacksReloaded);
	}

	@Override
	public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateEvent event) {
		onEvent(eventListener -> eventListener.onScreenHandlerSlotUpdate(event));
	}

	@Override
	public void onShutdown() {
		onEvent(IEventListener::onShutdown);
	}

	@Override
	public void onEntityPush(EntityPushEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onEntityPush(event));
	}

	@Override
	public void onEntityRemove(EntityEvent event) {
		onEvent(eventListener -> eventListener.onCrystalManagerEntityRemove(event));
		onEvent(eventListener -> eventListener.onEntityRemove(event));
	}

	@Override
	public void onEntitySpawn(EntityEvent event) {
		onEvent(eventListener -> eventListener.onCrystalManagerEntitySpawn(event));
		onEvent(eventListener -> eventListener.onProjectileManagerEntitySpawn(event));
		onEvent(eventListener -> eventListener.onEntitySpawn(event));
	}

	@Override
	public void onPlayerDeath(PlayerEvent event) {
		onEvent(eventListener -> eventListener.onPlayerDeath(event));
	}

	@Override
	public void onPlayerTotemPop(PlayerEvent event) {
		onEvent(eventListener -> eventListener.onPlayerTotemPop(event));
	}

	@Override
	public void onTickEntities() {
		onEvent(IEventListener::onTickEntities);
	}

	@Override
	public void onBlockUpdate(BlockUpdateEvent event) {
		onEvent(eventListener -> eventListener.onBlockUpdate(event));
	}

	@Override
	public void onBlockInteract(BlockUpdateEvent event) {
		onEvent(eventListener -> eventListener.onBlockInteract(event));
	}

	@Override
	public void onServerBlockUpdate(ServerBlockUpdateEvent event) {
		onEvent(eventListener -> eventListener.onServerBlockUpdate(event));
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onPrePacketReceive(event));
	}

	@Override
	public void onPostPacketReceive(PacketEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onPostPacketReceive(event));
	}

	@Override
	public void onPrePacketSend(PacketEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onAntiAcDetectPrePacketSend(event));
		onCancellableEvent(event, eventListener -> eventListener.onPrePacketSend(event));
		onCancellableEvent(event, eventListener -> eventListener.onRotationManagerPrePacketSend(event));
	}

	@Override
	public void onPostPacketSend(PacketEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onPostPacketSend(event));
	}

	@Override
	public void onGameJoin() {
		onEvent(IEventListener::onGameJoin);
	}

	@Override
	public void onGameLeft() {
		onEvent(IEventListener::onGameLeft);
	}

	@Override
	public void onRespawn() {
		onEvent(IEventListener::onRespawn);
	}

	@Override
	public void onHandleInput() {
		onEvent(IEventListener::onAttackAuraTick);
		onEvent(IEventListener::onHandleInput);
	}

	@Override
	public void onPreTick() {
		onEvent(IEventListener::onPreTick);
	}

	@Override
	public void onPostTick() {
		onEvent(IEventListener::onPostTick);
	}

	@Override
	public void onPacketMine(PacketMineEvent event) {
		onEvent(eventListener -> eventListener.onPacketMine(event));
	}

	@Override
	public void onAttackBlock(BlockEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onAttackBlock(event));
	}

	@Override
	public void onStartBreakingBlock(BlockEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onStartBreakingBlock(event));
	}

	@Override
	public void onAttackEntity(AttackEntityEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onAttackEntity(event));
	}

	@Override
	public void onBlockBreakingProgress(BlockEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onBlockBreakingProgress(event));
	}

	@Override
	public void onBlockPush(Cancellable event) {
		onCancellableEvent(event, eventListener -> eventListener.onBlockPush(event));
	}

	@Override
	public void onClipAtEdge(ClipAtEdgeEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onClipAtEdge(event));
	}

	@Override
	public void onFinishUsingItem(FinishUsingItemEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onFinishUsingItem(event));
	}

	@Override
	public void onFluidPush(Cancellable event) {
		onCancellableEvent(event, eventListener -> eventListener.onFluidPush(event));
	}

	@Override
	public void onInteractBlock(InteractBlockEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onInteractBlock(event));
	}

	@Override
	public void onPreJump(Cancellable event) {
		onCancellableEvent(event, eventListener -> eventListener.onPreJump(event));
	}

	@Override
	public void onPostJump() {
		onEvent(IEventListener::onPostJump);
	}

	@Override
	public void onJumpVelocityMultiplier(JumpVelocityMultiplierEvent event) {
		onEvent(eventListener -> eventListener.onJumpVelocityMultiplier(event));
	}

	@Override
	public void onItemSlowDown(Cancellable event) {
		onCancellableEvent(event, eventListener -> eventListener.onItemSlowDown(event));
	}

	@Override
	public void onSneakSlowDown(Cancellable event) {
		onCancellableEvent(event, eventListener -> eventListener.onSneakSlowDown(event));
	}

	@Override
	public void onCrawlSlowDown(Cancellable event) {
		onCancellableEvent(event, eventListener -> eventListener.onCrawlSlowDown(event));
	}

	@Override
	public void onMovementForCollisions(MovementForCollisionsEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onMovementForCollisions(event));
	}

	@Override
	public void onPrePlayerMove(PlayerMoveEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onHoleSnapPrePlayerMove(event));
		onCancellableEvent(event, eventListener -> eventListener.onPrePlayerMove(event));
		onCancellableEvent(event, eventListener -> eventListener.onHoleSnapPostPlayerMove(event));
	}

	@Override
	public void onPostPlayerMove() {
		onEvent(IEventListener::onPostPlayerMove);
	}

	@Override
	public void onMovementIteration(MovementIterationEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onMovementIteration(event));
	}

	@Override
	public void onRotate() {
		onEvent(IEventListener::onRotationManagerPreRotate);

		onEvent(IEventListener::onRotationFunctionRotate);
		onEvent(IEventListener::onRotate);

		onEvent(IEventListener::onAutoCrystalRotate);
		onEvent(IEventListener::onAutoWebRotate);
		onEvent(IEventListener::onAutoMineRotate);
		onEvent(IEventListener::onPlaceFunctionRotate);
		onEvent(IEventListener::onPhaseRotate);
	}

	@Override
	public void onPreSendMovement() {
		onEvent(IEventListener::onPreSendMovement);
		onEvent(IEventListener::onRotationManagerPreSendMovement);
	}

	@Override
	public void onPostSendMovement() {
		onEvent(IEventListener::onPostSendMovement);
		onEvent(IEventListener::onAutoCrystalPostSendMovement);
		onEvent(IEventListener::onRotationManagerPostSendMovement);
	}

	@Override
	public void onPreTravel(TravelEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onPreTravel(event));
	}

	@Override
	public void onPostTravel(TravelEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onPostTravel(event));
	}

	@Override
	public void onPreUpdateVelocity(UpdateVelocityEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onRotationManagerPreUpdateVelocity(event));
		onCancellableEvent(event, eventListener -> eventListener.onPreUpdateVelocity(event));
	}

	@Override
	public void onPostUpdateVelocity() {
		onEvent(IEventListener::onRotationManagerPostUpdateVelocity);
		onEvent(IEventListener::onPostUpdateVelocity);
	}

	@Override
	public void onChunkOcclusion(Cancellable event) {
		onCancellableEvent(event, eventListener -> eventListener.onChunkOcclusion(event));
	}

	@Override
	public void onHeldItemRenderer(HeldItemRendererEvent event) {
		onEvent(eventListener -> eventListener.onHeldItemRenderer(event));
	}

	@Override
	public void onHudRender(HudRenderEvent event) {
		onEvent(eventListener -> eventListener.onHudRender(event));
	}

	@Override
	public void onParticle(ParticleEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onParticle(event));
	}

	@Override
	public void onPostProcess() {
		onEvent(IEventListener::onPostProcess);
	}

	@Override
	public void onGuiRender(Render2DEvent event) {
		onEvent(eventListener -> eventListener.onGuiRender(event));
	}

	@Override
	public void onRenderTooltip(TooltipRenderEvent event) {
		onEvent(eventListener -> eventListener.onRenderTooltip(event));
	}

	@Override
	public void onRender2D(Render2DEvent event) {
		onEvent(eventListener -> eventListener.onRender2D(event));
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		onEvent(eventListener -> eventListener.onRender3D(event));
		PerfectClient.EVENT_MANAGER.onPostRender3D();
	}

	@Override
	public void onPostRender3D() {
		onEvent(IEventListener::onPostRender3D);
	}

	@Override
	public void onScreenChanged(ScreenChangedEvent event) {
		onCancellableEvent(event, eventListener -> eventListener.onScreenChanged(event));
		onCancellableEvent(event, eventListener -> eventListener.onAutoRespawnScreenChanged(event));
	}

	@Override
	public void onPostScreenChanged(ScreenChangedEvent event) {
		onEvent(eventListener -> eventListener.onPostScreenChanged(event));
	}

	@Override
	public void onRenderSlot(RenderHandledScreenEvent event) {
		onEvent(eventListener -> eventListener.onRenderSlot(event));
	}

	@Override
	public void onUpdateCrosshairTarget(Cancellable event) {
		onCancellableEvent(event, eventListener -> eventListener.onUpdateCrosshairTarget(event));
	}

	@Override
	public void onChunkData(ChunkDataEvent event) {
		onEvent(eventListener -> eventListener.onChunkData(event));
	}

	@Override
	public void onStartRenderTick() {
		onEvent(IEventListener::onStartRenderTick);
	}

	@Override
	public void onPreUsingItem() {
		onEvent(IEventListener::onPreUsingItem);
	}

	@Override
	public void onPostUsingItem() {
		onEvent(IEventListener::onPostUsingItem);
	}

	@Override
	public void onScreenMouse(Cancellable event) {
		onCancellableEvent(event, eventListener -> eventListener.onScreenMouse(event));
	}

	@Override
	public void onScreenKey(Cancellable event) {
		onCancellableEvent(event, eventListener -> eventListener.onScreenKey(event));
	}

	@Override
	public void onMessageReceive(MessageReceiveEvent event) {
		onEvent(eventListener -> eventListener.onMessageReceive(event));
	}

	@Override
	public void onMessageSend(MessageSendEvent event) {
		onEvent(eventListener -> eventListener.onMessageSend(event));
	}

	@Override
	public void onSound(SoundEvent event) {
		onEvent(eventListener -> eventListener.onSound(event));
	}

	private void onCancellableEvent(Cancellable event, Consumer<IEventListener> eventListenerConsumer) {
		for (IEventListener eventListener : listeners) {
			if (event.isBreakEvents()) {
				break;
			}
			eventListenerConsumer.accept(eventListener);
		}
	}

	private void onEvent(Consumer<IEventListener> eventListenerConsumer) {
		listeners.forEach(eventListenerConsumer);
	}
}
