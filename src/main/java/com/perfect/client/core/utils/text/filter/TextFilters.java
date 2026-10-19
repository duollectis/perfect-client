package com.perfect.client.core.utils.text.filter;

public class TextFilters {

	public static final ITextFilter LETTERS = (state, c) -> Character.isLetter(c);
	public static final ITextFilter POSITIVE_INT = (state, c) -> Character.isDigit(c);
	public static final ITextFilter INT = (state, c) -> FilterUtils.isNumber(false, state, c);
	public static final ITextFilter DOUBLE = (state, c) -> FilterUtils.isNumber(true, state, c);
}
