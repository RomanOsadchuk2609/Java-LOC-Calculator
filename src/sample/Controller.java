package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class Controller {

	private static final FileChooser FILE_CHOOSER = new FileChooser();

	private static final List<String> PRIMITIVE_TYPES = Arrays.asList("byte", "short", "int", "long", "float", "double", "char", "boolean");
	private static final List<String> ACCESS_MODIFIERS = Arrays.asList("public", "protected", "private", "static", "final", "volatile");

	private final ObservableList<Result> results = FXCollections.observableArrayList();
	@FXML
	private Button bntOpen;

	@FXML
	private Label labelFilename;

	@FXML
	private Button btnSave;

	@FXML
	private TableView<Result> tableView;

	@FXML
	private TableColumn<String, Result> columnKey;

	@FXML
	private TableColumn<String, Result> columnValue;

	@FXML
	public void initialize() {
		FILE_CHOOSER.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java files", "*.java"));
		columnKey.setCellValueFactory(new PropertyValueFactory<>("key"));
		columnKey.setResizable(true);
		columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));

		columnValue.setCellFactory(tc -> {
			TableCell<String, Result> cell = new TableCell<>();
			Text text = new Text();
			cell.setGraphic(text);
			cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
			text.wrappingWidthProperty().bind(columnValue.widthProperty());
			if (cell.itemProperty().asString() != null) {
				text.textProperty().bind(cell.itemProperty().asString());
			}
			return cell;
		});
		columnKey.setResizable(true);
		tableView.setItems(results);
		tableView.setFixedCellSize(50);
	}

	@FXML
	void onClickBtnOpen(ActionEvent event) {
		File file = FILE_CHOOSER.showOpenDialog(bntOpen.getScene().getWindow());
		if (file != null) {
			try {
				labelFilename.setText(file.getName());
				results.clear();
				EnumMap<LineType, List<String>> lineTypes = Files.lines(Paths.get(file.getPath()))
						.peek(System.out::println)
						.map(line -> line.replaceAll("\t", ""))
						.collect(groupingBy(this::getLineType, () -> new EnumMap<>(LineType.class), toList()));

				lineTypes.entrySet()
						.stream()
						.map(entry -> new Result(entry.getKey().getName(), String.valueOf(entry.getValue().size())))
						.forEach(results::add);

				List<String> varTypes = getVarTypes(lineTypes.get(LineType.CODE));
				results.add(new Result("Кількість констант", String.valueOf(countConstants(lineTypes.get(LineType.CODE)))));
				results.add(new Result("Використані змінні", String.join(", ", varTypes)));

				int amountOfLines = lineTypes.values()
						.stream()
						.mapToInt(List::size)
						.sum();
				results.add(new Result("Всього рядків", String.valueOf(amountOfLines)));


			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	void onClickBtnSave(ActionEvent event) {
		FileChooser fileSaver = new FileChooser();
		fileSaver.getExtensionFilters().add(new FileChooser.ExtensionFilter("Microsoft Word file", "*.docx"));
		String initialFileName = labelFilename.getText();
		fileSaver.setInitialFileName(initialFileName.substring(0, initialFileName.lastIndexOf('.')) + "_LOC_results.docx");
		File file = fileSaver.showSaveDialog(btnSave.getScene().getWindow());
		if (file != null) {
			DocxFileWriter.writeResultsToFile(results, initialFileName, file);
		}
	}

	private LineType getLineType(String line) {
		return Stream.of(LineType.values())
				.filter(lineType -> lineType.test(line))
				.findFirst()
				.orElse(LineType.CODE);
	}

	private List<String> getVarTypes(List<String> codeLines) {
		return codeLines.stream()
				.map(this::removeAccessModifiers)
				.map(String::trim)
				.filter(this::containsVarDefinition)
				.map(line -> line.substring(0, line.indexOf(' ')))
				.distinct()
				.sorted()
				.collect(toList());
	}

	private boolean containsVarDefinition(String line) {
		return PRIMITIVE_TYPES.stream().anyMatch(line::startsWith) || line.matches("^[A-Z][A-z<>\\[\\]]+\\s*[a-z]*.+");
	}

	private String removeAccessModifiers(String line) {
		for (String accessModifier : ACCESS_MODIFIERS) {
			line = line.replaceAll(accessModifier, "");
		}
		return line;
	}

	private long countConstants(List<String> codeLines) {
		return codeLines.stream()
				.map(String::trim)
				.filter(line -> line.contains(" final ") || line.startsWith("final "))
				.count();
	}


}
