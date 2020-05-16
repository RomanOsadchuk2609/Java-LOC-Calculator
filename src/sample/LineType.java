package sample;

import java.util.function.Predicate;

public enum LineType implements Predicate<String> {

	EMPTY(line -> line.replaceAll("\n", "").trim().isEmpty(), "Порожні рядки"),

	LINE_COMMENT(line -> line.trim().startsWith("//"), "Коментарі"),

	BLOCK_COMMENT(line -> line.trim().startsWith("/*") || line.trim().endsWith("*/"), "Коментар (блочний)"),

	CODE(line -> !EMPTY.test(line)
			&& !LINE_COMMENT.test(line)
			&& !BLOCK_COMMENT.test(line), "Код");

	private final Predicate<String> testFunction;

	private final String name;

	LineType(Predicate<String> testFunction, String name) {
		this.testFunction = testFunction;
		this.name = name;
	}

	@Override
	public boolean test(String line) {
		return testFunction.test(line);
	}

	public String getName() {
		return name;
	}
}
