package com.perfect.client.gui.widgets.function;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.ScissorStack;
import com.perfect.client.gui.utils.Rounding;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Predicate;

import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_SEMI_BOLD;


public class CategoryWidget extends Widget {

	public static final float INDENT = 2.25f;

	public static final float WIDTH = 72f;
	public static final float HEIGHT = 170f;
	private static final float ROUNDING = 4f;
	private static final float HEADER_HEIGHT = 13.9f;
	private static final float OUTlINE_THICKNESS = 0.6f;

	private final Category category;

	private final FunctionListWidget functionsList;

	@Setter
	private Predicate<Function> visiblePredicate = function -> true;

	public CategoryWidget(Category category) {
		this.category = category;
		functionsList = addChild(new FunctionListWidget(category));
	}

	@Override
	public void onPreTick() {
		functionsList.setIndent(INDENT);

		functionsList.getChildren().forEach(widget -> {
			if (widget instanceof FunctionWidget functionWidget) {
				boolean active = visiblePredicate.test(functionWidget.getFunction());
				widget.setVisible(active);
				widget.setActive(active);
			}
		});
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setWidth(WIDTH);
		setHeight(HEIGHT);

		setRounding(4.0f);

		float _height = height - HEADER_HEIGHT - INDENT;

		functionsList.setBoundOffset(_height);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		functionsList.setX(INDENT);
		functionsList.setY(HEADER_HEIGHT + OUTlINE_THICKNESS);
		functionsList.setTopOffset(INDENT - OUTlINE_THICKNESS);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {

		ScissorStack.expand(matrices, 0, 0, 0, OUTlINE_THICKNESS);

		// Background
		Renderer2D.blurredRound()
		          .pos(drawX, drawY, width, height + OUTlINE_THICKNESS)
		          .rounding(rounding)
		          .thickness(OUTlINE_THICKNESS)
		          .blur(2.0f)
		          .alpha(0.9f)
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.DEFAULT_OUTLINE))
		          .color(Managers.COLOR.get(Managers.COLOR.BACKGROUND))
		          .draw(matrices);

		ScissorStack.setLast(matrices);

		// Header

		Renderer2D.round()
		          .pos(drawX + OUTlINE_THICKNESS, drawY + OUTlINE_THICKNESS)
		          .size(width - OUTlINE_THICKNESS * 2f, HEADER_HEIGHT - OUTlINE_THICKNESS)
		          .rounding(Rounding.top(ROUNDING - OUTlINE_THICKNESS))
		          .color(Managers.COLOR.get(Managers.COLOR.BACKGROUND))
		          .draw(matrices);

		// Separator

		Renderer2D.round()
		          .pos(drawX + 0.4f, drawY + HEADER_HEIGHT)
		          .size(width - 0.8f, 0.4f)
		          .softness(0.15f)
		          .color(Managers.COLOR.get(Managers.COLOR.DEFAULT_OUTLINE))
		          .draw(matrices);

		MONTSERRAT_SEMI_BOLD.drawCentered(
				matrices,
				category.toString(),
				4.75f,
				drawX + width / 2f,
				drawY + HEADER_HEIGHT / 2f,
				Color.WHITE
		);
	}

	public void resetList() {
		functionsList.reset();
	}
}
