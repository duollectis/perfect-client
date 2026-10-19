package com.perfect.client.functional.function.combat.attackAura.rotate;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.entity.AttackEntityEvent;
import com.perfect.client.core.functional.function.combatVersion.impl.NewCombatVersion;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.LoggerUtils;
import com.perfect.client.core.utils.files.FileUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.rotation.Angle;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.combat.attackAura.AttackAura;
import com.perfect.client.functional.function.combat.attackAura.AttackAuraConfig;
import com.perfect.client.functional.function.combat.attackAura.data.AttackInfo;
import com.perfect.client.functional.function.combat.attackAura.data.AttackPoint;
import com.perfect.client.functional.function.combat.attackAura.data.AttackPointsFindResult;
import com.perfect.client.functional.function.combat.attackAura.utils.RotateHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NeuroHandler extends RotateHandler {

	private final File file = new File(PerfectClient.FOLDER, "data.jsonl");
	private final Gson gson = new Gson();

	private final List<TrainingData> trainingData = new ArrayList<>();
	private final List<Angle> lastDeltas = new ArrayList<>();

	private final List<MovementState> playerStates = new ArrayList<>();
	private final List<MovementState> targetStates = new ArrayList<>();

	private int timing;

	private int prevTargetId;
	private Vec3d velocity = Vec3d.ZERO;

	public NeuroHandler(AttackAura function, AttackAuraConfig config) {
		super(function, config);
	}

	@Override
	public void onEnable() {
		reset();
	}

	@Override
	public void onRespawn() {
		reset();
	}

	private void reset() {
		lastDeltas.clear();

		for (int i = 0; i < 8; i++) {
			lastDeltas.add(new Angle(0.0f, 0.0f));
		}

		playerStates.clear();

		for (int i = 0; i < 10; i++) {
			playerStates.add(new MovementState());
		}

		resetTargetStates();

		FileUtils.create(file);
	}

	private void resetTargetStates() {
		targetStates.clear();

		for (int i = 0; i < 10; i++) {
			targetStates.add(new MovementState());
		}
	}

	private int i;

	@Override
	public void onPreTick() {
		if (function.getCombatVersion().getCurrentMode() instanceof NewCombatVersion<?, ?> version) {
			if (version.shouldAttack(null)) {
				timing++;
			}
		}
	}

	@Override
	public void onAttackEntity(AttackEntityEvent event) {
		if (timing >= 0 && timing <= 7 && !trainingData.isEmpty()) {
			for (TrainingData data : offsetData(timing)) {
				writeTick(buildData(normalize(data)));
			}

			function.info("Write training data. (%s)".formatted(i));
			i++;
		}
		timing = -1;
		trainingData.clear();
	}

	private TrainingData normalize(TrainingData trainingData) {
		assert mc.player != null;

		if (trainingData.tickData.negate) {
			trainingData = negate(trainingData);
		}

		TickData data = trainingData.tickData;

		Quaternionf quaternion = new Quaternionf().rotateY((float) Math.toRadians(-mc.player.getYaw()));

		TickData normalized = data.copyContext();
		Angle action = trainingData.tickAction;

		List<MovementState> playerStates = new ArrayList<>();
		List<MovementState> targetStates = new ArrayList<>();

		for (MovementState state : data.playerStates) {
			playerStates.add(state.mul(quaternion));
		}

		for (MovementState state : data.targetStates) {
			targetStates.add(state.mul(quaternion));
		}

		normalized.setPlayerStates(playerStates);
		normalized.setTargetStates(targetStates);

		normalized.setLastDeltas(data.lastDeltas);
		normalized.setMinDeltaToTarget(data.minDeltaToTarget);
		normalized.setDeltasToTarget(data.deltasToTarget);

		return new TrainingData(normalized, action);
	}

	private TrainingData negate(TrainingData trainingData) {
		TickData original = trainingData.tickData;

		TickData negated = original.copyContext();

		negated.lastDeltas = negateAngles(original.lastDeltas);
		negated.deltasToTarget = negateAngles(original.deltasToTarget);
		negated.minDeltaToTarget = original.minDeltaToTarget.mul(-1.0f, 1.0f);

		negated.playerStates = negateMovement(original.playerStates);
		negated.targetStates = negateMovement(original.targetStates);

		return new TrainingData(negated, trainingData.tickAction.mul(-1.0f, 1.0f));
	}

	private List<MovementState> negateMovement(List<MovementState> original) {
		List<MovementState> list = new ArrayList<>();

		original.forEach(state -> list.add(state.mirror()));

		return list;
	}

	private List<Angle> negateAngles(List<Angle> original) {
		List<Angle> list = new ArrayList<>();

		original.forEach(angle -> list.add(angle.mul(-1.0f, 1.0f)));

		return list;
	}

	private List<TrainingData> offsetData(int offset) {
		List<TrainingData> list = new ArrayList<>();

		for (int i = trainingData.size() - 1; i >= offset; i--) {
			TrainingData timingTrainingData = trainingData.get(i);
			TrainingData offsetTrainingData = trainingData.get(i - offset);

			TickData timingData = timingTrainingData.getTickData();
			TickData offsetData = offsetTrainingData.getTickData();

			TickData tickData = new TickData();

			tickData.setPlayerStates(stateWithY(timingData.playerStates, offsetData.playerStates));
			tickData.setTargetStates(stateWithY(timingData.targetStates, offsetData.targetStates));

			tickData.timeToAttack = offsetData.timeToAttack;

			tickData.focusError = timingData.focusError;
			tickData.targetVisibility = timingData.targetVisibility;
			tickData.distanceFromTarget = timingData.distanceFromTarget;
			tickData.minDeltaToTarget = timingData.minDeltaToTarget;
			tickData.lastDeltas = timingData.lastDeltas;
			tickData.deltasToTarget = timingData.deltasToTarget;

			Angle tickAction = timingTrainingData.getTickAction();

			list.addFirst(new TrainingData(tickData, tickAction));
		}

		return list;
	}

	private List<MovementState> stateWithY(List<MovementState> timingStates, List<MovementState> offsetStates) {
		List<MovementState> list = new ArrayList<>();

		for (int i = 0; i < timingStates.size(); i++) {
			list.add(timingStates.get(i).withY(offsetStates.get(i)));
		}

		return list;
	}

	@Override
	public void onPostPlayerMove() {
		velocity = PlayerUtils.getVelocity();
	}

	@Override
	public boolean rotate(AttackInfo attackInfo) {
		if (mc.player == null) {
			return false;
		}

		TickData tickData = getTickData(attackInfo);
		Angle tickAction = getTickAction();

		TrainingData data = new TrainingData(tickData, tickAction);

//		Angle angle = predict(buildData(normalize(data)));
//
//		if (tickData.negate) {
//			angle = angle.mul(-1.0f, 1.0f);
//		}

		trainingData.add(data);

		/*float yawDelta = (Managers.ROTATION.getSentYawDelta() + angle.getYaw() * 180.0f) * 0.8f;
		float pitchDelta = (Managers.ROTATION.getSentPitchDelta() + angle.getPitch() * 180.0f) * 0.8f;

		float yaw = Managers.ROTATION.getSentYaw() + yawDelta;
		float pitch = Math.clamp(Managers.ROTATION.getSentPitch() + pitchDelta, -90.0f, 90.0f);*/

//		float yaw = Managers.ROTATION.getSentYaw() + angle.getYaw() * 180.0f;
//		float pitch = Managers.ROTATION.getSentPitch() + angle.getPitch() * 180.0f;
//
//		Managers.ROTATION.setRotate(Managers.SENSITIVITY.gcdYaw(yaw), Managers.SENSITIVITY.gcdPitch(pitch));
//
//		return function.getRotateHelper().isRotated(attackInfo.getEntity());
		return false;
	}

	private Angle getTickAction() {
		assert mc.player != null;

		float yawDelta = mc.player.getYaw() - Managers.ROTATION.getSentYaw();
		float pitchDelta = mc.player.getPitch() - Managers.ROTATION.getSentPitch();

		return normalizeAngle(new Angle(yawDelta, pitchDelta));
	}

	private TickData getTickData(AttackInfo attackInfo) {
		assert mc.player != null;

		TickData tickData = new TickData();

		if (function.getCombatVersion().getCurrentMode() instanceof NewCombatVersion<?, ?> version) {
			lastDeltas.add(normalizeAngle(new Angle(
					Managers.ROTATION.getSentYawDelta(),
					Managers.ROTATION.getSentPitchDelta()
			)));

			lastDeltas.removeFirst();

			LivingEntity target = attackInfo.getEntity();

			if (prevTargetId != target.getId()) {
				resetTargetStates();
				prevTargetId = target.getId();
			}

			playerStates.add(new MovementState(velocity, playerStates.getLast()));
			targetStates.add(new MovementState(EntityUtils.getVelocity(target), targetStates.getLast()));

			playerStates.removeFirst();
			targetStates.removeFirst();

			tickData.setPlayerStates(playerStates);
			tickData.setTargetStates(targetStates);

			tickData.setTimeToAttack(Math.min(version.getAttackCooldownProgress(0), 0.5f));

			double distance = PositionMath.sqrtDistance(
					mc.player.getEyePos(),
					PositionMath.closestVec3d(mc.player.getEyePos(), target.getBoundingBox())
			);

			tickData.setDistanceFromTarget((float) Math.clamp(distance / config.getAttackRange().get(), 0.0, 1.0));

			AttackPointsFindResult attackPoints = function.getTargetHelper().getAttackPoints(
					target,
					target.getBoundingBox()
			);

			//tickData.setNegate(RotationUtils.getYaw(target.getPos()) < Managers.ROTATION.getSentYaw());

			tickData.setTargetVisibility(attackPoints.getVisibility());

			Angle minDelta = new Angle();
			float minDeltaSquared = 0.0f;

			List<Angle> deltasToTarget = new ArrayList<>();

			for (AttackPoint point : attackPoints.getPoints()) {
				Vec3d pos = point.toWorldVec3d(target.getBoundingBox());

				if (distance < 1.0 && PositionMath.isOutOfRange(pos, config.getAttackRange().get())) {
					continue;
				}

				float yaw = RotationUtils.getYaw(pos);
				float pitch = RotationUtils.getPitch(pos);

				float yawDelta = yaw - Managers.ROTATION.getSentYaw();
				float pitchDelta = pitch - Managers.ROTATION.getSentPitch();

				float deltaSquared = yawDelta * yawDelta + pitchDelta * pitchDelta;
				Angle delta = normalizeAngle(new Angle(yawDelta, pitchDelta));

				if (!minDelta.isSet() || deltaSquared < minDeltaSquared) {
					minDelta = delta;
					minDeltaSquared = deltaSquared;
				}

				deltasToTarget.add(delta);
			}

			tickData.setMinDeltaToTarget(minDelta);

			tickData.setDeltasToTarget(deltasToTarget);
			tickData.setFocusError(getFocusError(deltasToTarget));

			tickData.setLastDeltas(lastDeltas);

			tickData.setPlayerStates(playerStates);
			tickData.setTargetStates(targetStates);
		}

		return tickData;
	}

	private float getFocusError(List<Angle> deltasToTarget) {
		float size = deltasToTarget.size() * 2.0f;
		float error = 0.0f;

		for (Angle delta : deltasToTarget) {
			error += delta.getYaw() + delta.getPitch();
		}

		return Math.clamp(error / size, 0.0f, 1.0f);
	}

	private Angle predict(BuiltData data) {
		float[] angle = Managers.NEURO.auraModel.predict(
				data.context,
				data.playerStates,
				data.targetStates,
				data.minDeltaToTarget,
				data.deltasToTarget,
				data.lastDeltas
		);
		return new Angle(angle[0], angle[1]);
	}

	public BuiltData buildData(TrainingData trainingData) {
		TickData data = trainingData.tickData;
		Angle action = trainingData.tickAction;

		float[] context = {
				data.focusError,
				data.targetVisibility,
				data.timeToAttack,
				data.distanceFromTarget
		};

		float[][] deltasToTarget = new float[data.deltasToTarget.size()][2];

		for (int i = 0; i < data.deltasToTarget.size(); i++) {
			Angle angle = data.deltasToTarget.get(i);

			deltasToTarget[i][0] = angle.getYaw();
			deltasToTarget[i][1] = angle.getPitch();
		}

		float[][] lastDeltas = new float[data.lastDeltas.size()][2];

		for (int i = 0; i < data.lastDeltas.size(); i++) {
			Angle angle = data.lastDeltas.get(i);

			lastDeltas[i][0] = angle.getYaw();
			lastDeltas[i][1] = angle.getPitch();
		}

		float[][] playerStates = new float[data.playerStates.size()][6];

		for (int i = 0; i < data.playerStates.size(); i++) {
			MovementState state = data.playerStates.get(i);
			playerStates[i] = state.toFloatArray();
		}

		float[][] targetStates = new float[data.targetStates.size()][6];

		for (int i = 0; i < data.targetStates.size(); i++) {
			MovementState state = data.targetStates.get(i);
			targetStates[i] = state.toFloatArray();
		}

		return new BuiltData(
				context,
				playerStates,
				targetStates,
				data.minDeltaToTarget.toFloatArray(),
				deltasToTarget,
				lastDeltas,
				action.toFloatArray()
		);
	}

	public void writeTick(BuiltData data) {
		synchronized (file) {
			Managers.ASYNC.run(() -> {
				String jsonLine = gson.toJson(data);

				try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
					writer.write(jsonLine);
					writer.newLine();
				}
				catch (IOException e) {
					LoggerUtils.warning(e);
				}
			});
		}
	}

	private static Angle normalizeAngle(Angle angle) {
		float yaw = Math.clamp(angle.getYaw() / 180.0f, -1.0f, 1.0f);
		float pitch = Math.clamp(angle.getPitch() / 180.0f, -1.0f, 1.0f);

		return new Angle(yaw, pitch);
	}

	private static Vector3f normalizeSpeed(Vec3d speed) {
		return new Vector3f(
				(float) Math.clamp(speed.x * 2.0, -1.0, 1.0),
				(float) Math.clamp(speed.y * 2.0, -1.0, 1.0),
				(float) Math.clamp(speed.z * 2.0, -1.0, 1.0)
		);
	}

	@AllArgsConstructor
	public static class BuiltData {

		@SerializedName("context")
		public final float[] context;

		@SerializedName("playerStates")
		public final float[][] playerStates;

		@SerializedName("targetStates")
		public final float[][] targetStates;

		@SerializedName("minDeltaToTarget")
		public final float[] minDeltaToTarget;

		@SerializedName("deltasToTarget")
		public final float[][] deltasToTarget;

		@SerializedName("lastDeltas")
		public final float[][] lastDeltas;

		@SerializedName("action")
		public final float[] action;
	}

	@Getter
	@AllArgsConstructor
	public static class TrainingData {

		public final TickData tickData;
		public final Angle tickAction;
	}

	@Setter
	public static class TickData {

		public boolean negate;

		public List<MovementState> playerStates;
		public List<MovementState> targetStates;

		public Angle minDeltaToTarget;

		public float focusError;
		public float targetVisibility;
		public float timeToAttack;
		public float distanceFromTarget;

		public List<Angle> deltasToTarget;
		public List<Angle> lastDeltas;

		public void setPlayerStates(List<MovementState> playerStates) {
			this.playerStates = new ArrayList<>(playerStates);
		}

		public void setTargetStates(List<MovementState> targetStates) {
			this.targetStates = new ArrayList<>(targetStates);
		}

		public TickData copyContext() {
			TickData tickData = new TickData();
			tickData.focusError = focusError;
			tickData.targetVisibility = targetVisibility;
			tickData.timeToAttack = timeToAttack;
			tickData.distanceFromTarget = distanceFromTarget;
			return tickData;
		}
	}

	@Setter
	public static class MovementState {

		public Vector3f velocity = new Vector3f();
		public Vector3f acceleration = new Vector3f();

		public MovementState() {}

		public MovementState(Vec3d velocity, MovementState prevState) {
			this.velocity = normalizeSpeed(velocity);
			this.acceleration = this.velocity.sub(prevState.velocity);
		}

		public MovementState withY(MovementState y) {
			MovementState state = new MovementState();

			state.velocity = new Vector3f(velocity.x, y.velocity.y, velocity.z);
			state.acceleration = new Vector3f(acceleration.x, y.acceleration.y, acceleration.z);

			return state;
		}

		public MovementState mirror() {
			MovementState state = new MovementState();

			state.velocity = new Vector3f(velocity.z, velocity.y, velocity.x);
			state.acceleration = new Vector3f(acceleration.z, acceleration.y, acceleration.x);

			return state;
		}

		public MovementState mul(Quaternionf quaternionf) {
			MovementState state = new MovementState();

			state.velocity = quaternionf.transform(state.velocity);
			state.acceleration = quaternionf.transform(state.acceleration);

			return state;
		}

		public float[] toFloatArray() {
			return new float[]{
					velocity.x,
					velocity.y,
					velocity.z,
					acceleration.x,
					acceleration.y,
					acceleration.z
			};
		}
	}
}
