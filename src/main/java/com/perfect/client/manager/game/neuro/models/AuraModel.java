package com.perfect.client.manager.game.neuro.models;

import com.perfect.client.manager.game.neuro.NeuroModel;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;

import java.util.Map;
import java.util.SplittableRandom;


public class AuraModel extends NeuroModel {

	private final SplittableRandom rng = new SplittableRandom();

	public AuraModel(String path) {
		super(path);
	}

	public float[] predict(
			float[] context,
			float[][] playerState,
			float[][] targetState,
			float[] minDeltaToTarget,
			float[][] deltasToTarget,
			float[][] lastDeltas
	) {
		try (TFloat32 contextTensor = TFloat32.tensorOf(NdArrays.ofFloats(Shape.of(1, context.length)));
		     TFloat32 playerStateTensor = TFloat32.tensorOf(NdArrays.ofFloats(Shape.of(1, playerState.length, 6)));
		     TFloat32 targetStateTensor = TFloat32.tensorOf(NdArrays.ofFloats(Shape.of(1, targetState.length, 6)));
		     TFloat32 minDeltaTensor = TFloat32.tensorOf(NdArrays.ofFloats(Shape.of(1, minDeltaToTarget.length)));
		     TFloat32 pointsTensor = TFloat32.tensorOf(NdArrays.ofFloats(Shape.of(1, deltasToTarget.length, 2)));
		     TFloat32 lastDeltasTensor = TFloat32.tensorOf(NdArrays.ofFloats(Shape.of(1, lastDeltas.length, 2)));
		) {
			for (int i = 0; i < context.length; i++) {
				contextTensor.setFloat(context[i], 0, i);
			}
			for (int i = 0; i < playerState.length; i++) {
				playerStateTensor.setFloat(playerState[i][0], 0, i, 0);
				playerStateTensor.setFloat(playerState[i][1], 0, i, 1);
				playerStateTensor.setFloat(playerState[i][2], 0, i, 2);
				playerStateTensor.setFloat(playerState[i][3], 0, i, 3);
				playerStateTensor.setFloat(playerState[i][4], 0, i, 4);
				playerStateTensor.setFloat(playerState[i][5], 0, i, 5);
			}
			for (int i = 0; i < targetState.length; i++) {
				targetStateTensor.setFloat(targetState[i][0], 0, i, 0);
				targetStateTensor.setFloat(targetState[i][1], 0, i, 1);
				targetStateTensor.setFloat(targetState[i][2], 0, i, 2);
				targetStateTensor.setFloat(targetState[i][3], 0, i, 3);
				targetStateTensor.setFloat(targetState[i][4], 0, i, 4);
				targetStateTensor.setFloat(targetState[i][5], 0, i, 5);
			}
			for (int i = 0; i < minDeltaToTarget.length; i++) {
				minDeltaTensor.setFloat(minDeltaToTarget[i], 0, i);
			}
			for (int i = 0; i < deltasToTarget.length; i++) {
				pointsTensor.setFloat(deltasToTarget[i][0], 0, i, 0);
				pointsTensor.setFloat(deltasToTarget[i][1], 0, i, 1);
			}
			for (int i = 0; i < lastDeltas.length; i++) {
				lastDeltasTensor.setFloat(lastDeltas[i][0], 0, i, 0);
				lastDeltasTensor.setFloat(lastDeltas[i][1], 0, i, 1);
			}

			try (TFloat32 output = (TFloat32) model.function("serving_default")
			                                       .call(Map.of(
					                                       "history_input", lastDeltasTensor,
					                                       "points_input", pointsTensor,
					                                       "player_state", playerStateTensor,
					                                       "target_state", targetStateTensor,
					                                       "min_point_input", minDeltaTensor,
					                                       "context_input", contextTensor
			                                       ))
			                                       .get("output_0")
			                                       .get()
			) {
				return new float[]{
						output.getFloat(0, 0),
						output.getFloat(0, 1)
				};
			}
		}
	}
}
