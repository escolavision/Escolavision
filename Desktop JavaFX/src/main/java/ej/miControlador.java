package ej;

import ej.Tablas.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;

public class miControlador implements Initializable {

	@FXML
	private Button btnClearTest, btnClearQuest, btnClearAlum, btnClearTry, btnLogo, btnDelTest, btnDelQuest, btnDelAlum, btnDelTry, btnLogin, btnSaveTest, btnSaveQuest, btnSaveAlum, btnSaveTry, btnClearPxA, btnDelPxA, btnSavePxA, btnClearArea, btnDelArea, btnSaveArea, btnHelp, btnVisibleYes, btnVisibleNo;

	@FXML
	private ListView<String> listViewAlumnos, listViewArea, listViewIntentos, listViewPreguntas, listViewPxa, listViewTest, listViewPxAPreguntas;


	@FXML
	private ImageView imglogo, imgViewPicAlum, imgViewPicArea, imgDelTest, imgDelQuest, imgDelAlum, imgDelPxA, imgDelTry, imgViewlogIn, imgClearTest, imgClearQuest, imgClearAlum, imgClearPxA, imgClearTry, imgSaveTest, imgSaveQuest, imgSaveAlum, imgSavePxA, imgSaveTry, imgDelArea, imgSaveArea, imgClearArea;

	@FXML
	private Tab tabAlum, tabAr, tabHome, tabPxA, tabQuest, tabTest, tabTr;

	@FXML
	private TabPane tabPane;

	@FXML
	private TextField txtDNIAlm,  txtIdAlm, txtIdArea, txtAreaPxA, txtQuestPxA, txtIdQuest, txtIdTest, txtIdTry, txtNameAlm, txtNameArea, txtResTry, txtSurnameAlm, txtTimeTry, txtUser, txtIdPxA, txtDNITry, txtTituloQuest, txtAñoNacimiento;

	@FXML
	private TextArea txtDescripArea, txtEnunQuest, txtTestName, txtEnumPxA;

	@FXML
	private PasswordField txtPassword, txtPasswordAlm;

	@FXML
	private DatePicker txtDateTry;

	@FXML
	private HBox hbHeader, hboxTry;

	@FXML
	private Label lblEscolavisionDesktop;

	@FXML
	private StackPane miStackPane;

	@FXML
    private ComboBox<String> txtTestQuest, txtTestTry;

	private final Map<String, ListView<String>> listViewMap = new HashMap<>();
	private final ListView<String> listViewDesplegableQuest = new ListView<>();
	private final ListView<String> listViewDesplegableTry = new ListView<>();
	private final Map<Tab, Button> tabToButtonMap = new HashMap<>();

	public miControlador() {
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarMap();
		inicializarLogin();
		inicializarImagenes();
	}

	private void inicializarImagenes() {
		double imageSize = 20.0;

		setImageWithSize(imgDelTest, "delete.png", imageSize, imageSize);
		setImageWithSize(imgDelQuest, "delete.png", imageSize, imageSize);
		setImageWithSize(imgDelAlum, "delete.png", imageSize, imageSize);
		setImageWithSize(imgDelPxA, "delete.png", imageSize, imageSize);
		setImageWithSize(imgDelTry, "delete.png", imageSize, imageSize);
		setImageWithSize(imgDelArea, "delete.png", imageSize, imageSize);
		setImageWithSize(imgViewlogIn, "logIn.png", imageSize, imageSize);
		setImageWithSize(imgClearTest, "clear.png", imageSize, imageSize);
		setImageWithSize(imgClearQuest, "clear.png", imageSize, imageSize);
		setImageWithSize(imgClearAlum, "clear.png", imageSize, imageSize);
		setImageWithSize(imgClearPxA, "clear.png", imageSize, imageSize);
		setImageWithSize(imgClearTry, "clear.png", imageSize, imageSize);
		setImageWithSize(imgClearArea, "clear.png", imageSize, imageSize);
		setImageWithSize(imgSaveTest, "save.png", imageSize, imageSize);
		setImageWithSize(imgSaveQuest, "save.png", imageSize, imageSize);
		setImageWithSize(imgSaveAlum, "save.png", imageSize, imageSize);
		setImageWithSize(imgSavePxA, "save.png", imageSize, imageSize);
		setImageWithSize(imgSaveTry, "save.png", imageSize, imageSize);
		setImageWithSize(imgSaveArea, "save.png", imageSize, imageSize);
	}

	private void setImageWithSize(ImageView imageView, String imagePath, double width, double height) {
		Image image = new Image(imagePath);
		imageView.setImage(image);
		imageView.setFitWidth(width);
		imageView.setFitHeight(height);
		imageView.setPreserveRatio(true);
	}

	private void inicializarMap() {
		listViewMap.put("alumno", listViewAlumnos);
		listViewMap.put("area", listViewArea);
		listViewMap.put("intentos", listViewIntentos);
		listViewMap.put("pxa", listViewPxa);
		listViewMap.put("pregunta", listViewPreguntas);
		listViewMap.put("test", listViewTest);
	}

	private void inicializarLogin() {
		imglogo.setImage(new Image("escolavision.png"));
		pantallaPrincipal();
		inicializarEventos();
	}

	private void pantallaPrincipal() {
		tabPane.getTabs().remove(tabAlum);
		tabPane.getTabs().remove(tabAr);
		tabPane.getTabs().remove(tabQuest);
		tabPane.getTabs().remove(tabPxA);
		tabPane.getTabs().remove(tabTest);
		tabPane.getTabs().remove(tabTr);
		btnHelp.setVisible(false);
		btnLogo.setVisible(true);
		btnLogo.setManaged(true);
	}

	private void rolAdmin() {
		tabPane.getTabs().remove(tabHome);
		tabPane.getTabs().add(tabTest);
		tabPane.getTabs().add(tabQuest);
		tabPane.getTabs().add(tabAlum);
		tabPane.getTabs().add(tabAr);
		tabPane.getTabs().add(tabPxA);
		tabPane.getTabs().add(tabTr);
		btnLogo.setVisible(true);
		btnLogo.setManaged(true);
		btnHelp.setVisible(true);
		btnClearArea.setVisible(true);
		btnDelArea.setVisible(true);
		btnSaveArea.setVisible(true);
	}

	private void rolAlumno() {
		tabPane.getTabs().add(tabAlum);
		tabPane.getTabs().add(tabTr);
		tabPane.getTabs().remove(tabHome);
		btnLogo.setVisible(true);
		btnLogo.setManaged(true);
		btnHelp.setVisible(true);
		btnClearArea.setVisible(false);
		btnDelArea.setVisible(false);
		btnSaveArea.setVisible(false);
	}

	private void rolProfesor() {
		tabPane.getTabs().add(tabAlum);
		tabPane.getTabs().add(tabTr);
		tabPane.getTabs().remove(tabHome);
		btnLogo.setVisible(true);
		btnLogo.setManaged(true);
		btnHelp.setVisible(true);
		btnClearArea.setVisible(false);
		btnDelArea.setVisible(false);
		btnSaveArea.setVisible(false);
	}

	private void inicializarEventos() {

		btnLogin.setOnAction(e -> login());

		txtPassword.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				btnLogin.fire();
			}
		});

		tabPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
			if (newScene != null) {
				newScene.widthProperty().addListener((obsW, oldW, newW) -> {
					btnHelp.setLayoutX(newW.doubleValue() - btnHelp.getWidth() - 5);
				});
				newScene.heightProperty().addListener((obsH, oldH, newH) -> {
					btnHelp.setLayoutY(120);
				});
			}
		});


		tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
			if (newTab != null) {
				ProgressIndicator progressIndicator = new ProgressIndicator();
				progressIndicator.setPrefSize(70, 70);

				Region background = new Region();
				background.setStyle("-fx-background-color: rgba(174,214,241,0.8);");
				background.setPrefSize(miStackPane.getWidth(), miStackPane.getHeight());

				StackPane loadingPane = new StackPane();
				loadingPane.getChildren().addAll(background, progressIndicator);
				StackPane.setAlignment(progressIndicator, Pos.CENTER);

				javafx.application.Platform.runLater(() -> miStackPane.getChildren().add(loadingPane));

				long startTime = System.currentTimeMillis();

				new Thread(() -> {
					try {
						limpiar(newTab);
						cargar(newTab);
						cargarDatos(newTab);

						long elapsedTime = System.currentTimeMillis() - startTime;
						if (elapsedTime < 800) {
							Thread.sleep(800 - elapsedTime);
						}

						javafx.application.Platform.runLater(() -> miStackPane.getChildren().remove(loadingPane));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).start();
			}
		});


		txtQuestPxA.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && !newValue.trim().isEmpty()) {
				try {
					String[] parts = newValue.trim().split("\\s+");
					if (parts.length > 1) {
						int id = Integer.parseInt(parts[1]);
						Pregunta pregunta = buscarPreguntaPorId(id);
						if (pregunta != null) {
							txtEnumPxA.setText(pregunta.getEnunciado());
						} else {
							txtEnumPxA.setText("");
						}
					} else {
						txtEnumPxA.setText("");
					}
				} catch (NumberFormatException e) {
					txtEnumPxA.setText("");
				}
			} else {
				txtEnumPxA.setText("");
			}
		});


		listViewMap.forEach((tipo, listView) -> listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				//comprobarEditar(tipo);
				String[] parts = newValue.trim().split("\\s+", 2);
				switch (tipo) {
					case "test": {
						cambiarBotonEditar(btnSaveTest, "Actualizar", "test");
						if (parts.length > 1) {
							String[] parts2 = newValue.trim().split("\\s+");
							cargarTestPartido(parts2[1]);
						}
						break;
					}
					case "pregunta": {
						cambiarBotonEditar(btnSaveQuest, "Actualizar", "pregunta");
						cargarPregunta(parts[1]);
						break;
					}
					case "alumno": {
						cambiarBotonEditar(btnSaveAlum, "Actualizar", "alumno");
						cargarAlumno(newValue);
						break;
					}
					case "area": {
						cargarArea(newValue);
						break;
					}
					case "pxa": {
						cambiarBotonEditar(btnSavePxA, "Actualizar", "pxa");
						String[] parts2 = newValue.trim().split("\\s+");
						cargarPxA(parts2[1]);
						break;
					}
					case "intentos": {
						cambiarBotonEditar(btnSaveTry, "Actualizar", "intentos");
						cargarIntento(parts[1]);
						break;
					}
					default: {
					}
				}
			}
			listViewDesplegableQuest.setVisible(false);
			listViewDesplegableTry.setVisible(false);
		}));

		listViewPxAPreguntas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				String[] parts = newValue.trim().split("\\s+", 2);
				cargarPreguntaPxA(parts[1]);
			}
		});

		//AYUDA
		btnHelp.setOnAction(e -> {
			String helpMessage = "";
			switch (tabPane.getSelectionModel().getSelectedItem().getText()) {
				case "Test" -> {
					helpMessage = "En esta sección puedes crear y modificar tests, asignándoles un nombre. También puedes ver y editar los tests guardados en el sistema.";
				}
				case "Preguntas" -> {
					helpMessage = "En esta sección puedes añadir y modificar preguntas, asignándoles un título, un enunciado, y asociándolas a un test específico. Además, puedes ver y editar las preguntas guardadas en el sistema.";
				}
				case "Usuarios" -> {
					helpMessage = "En esta sección puedes introducir y modificar los datos del alumno, incluyendo nombre y apellidos, DNI, clave de acceso, foto y el profesor asignado. También puedes ver y editar los alumnos guardados en el sistema.";
				}
				case "Área" -> {
					helpMessage = "En esta sección puedes consultar las áreas de especialización disponibles. No se permite la modificación de las áreas guardadas en el sistema.";
				}
				case "Pregunta x Área" -> {
					helpMessage = "En esta sección puedes asociar y modificar preguntas al área correspondiente, facilitando su organización y asignación. También puedes ver y editar las asociaciones guardadas en el sistema.";
				}
				case "Intentos" -> {
					helpMessage = "En esta sección se muestran los intentos realizados por el alumno en cada test, incluyendo el nombre y apellidos del alumno, la fecha y hora de realización, y el resultado obtenido. Puedes ver y modificar los intentos guardados en el sistema.";
				}
				default -> {
				}
			}
			Dialog<Void> dialog = new Dialog<>();
			dialog.setTitle("Ayuda");
			dialog.setHeaderText("Información de la pestaña seleccionada");

			Label helpLabel = new Label(helpMessage);
			helpLabel.setStyle("-fx-text-alignment: justify;");
			helpLabel.setWrapText(true);

			VBox content = new VBox(10, helpLabel);
			content.setPrefWidth(Region.USE_PREF_SIZE);
			content.setMinHeight(Region.USE_PREF_SIZE);
			content.setAlignment(javafx.geometry.Pos.CENTER);

			dialog.getDialogPane().setContent(content);
			Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
			dialogStage.getIcons().add(new Image("escolavision.png"));
			dialog.getDialogPane().getButtonTypes().addAll(javafx.scene.control.ButtonType.CLOSE);

			dialog.showAndWait();
		});

		// LIMPIAR

		btnClearTest.setOnAction(e -> {
			limpiar(tabTest);
		});

		btnClearQuest.setOnAction(e -> {
			limpiar(tabQuest);
		});

		btnClearAlum.setOnAction(e -> {
			limpiar(tabAlum);
		});

		btnClearPxA.setOnAction(e -> {
			limpiar(tabPxA);
		});

		btnClearTry.setOnAction(e -> {
			limpiar(tabTr);
		});

		btnClearArea.setOnAction(e -> {
			limpiar(tabAr);
		});

		// ELIMINAR

		btnDelTest.setOnAction(e -> {
			if (!txtIdTest.getText().isEmpty()) borrar("tests", txtIdTest.getText());
			else mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al borrar", "Debe seleccionar un test.");

		});

		btnDelQuest.setOnAction(e -> {
			if (!txtIdQuest.getText().isEmpty()) borrar("preguntas", txtIdQuest.getText());
			else mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al borrar", "Debe seleccionar una pregunta.");
		});

		btnDelAlum.setOnAction(e -> {
			if (!txtIdAlm.getText().isEmpty()) borrar("usuarios", txtIdAlm.getText());
			else mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al borrar", "Debe seleccionar un usuario.");
		});


		btnDelPxA.setOnAction(e -> {
			if (!txtIdPxA.getText().isEmpty()) borrar("pxa", txtIdPxA.getText());
			else mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al borrar", "Debe seleccionar un PxA.");
		});

		btnDelTry.setOnAction(e -> {
			if (!txtIdTry.getText().isEmpty()) borrar("intentos", txtIdTry.getText());
			else mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al borrar", "Debe seleccionar un intento.");
		});

		btnDelArea.setOnAction(e -> {
            if (!txtIdArea.getText().isEmpty()) borrar("area", txtIdArea.getText());
            else mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al borrar", "Debe seleccionar un área.");
        });

		ValidationSupport vSnombreTest = new ValidationSupport();
		vSnombreTest.registerValidator(txtTestName, false, Validator.createEmptyValidator("El nombre no puede estar vacío"));

		ValidationSupport vSPregunta = new ValidationSupport();
		vSPregunta.registerValidator(txtEnunQuest, false, Validator.createEmptyValidator("El enunciado no puede estar vacío"));
		vSPregunta.registerValidator(txtTituloQuest, false, Validator.createEmptyValidator("El titulo no puede estar vacío"));
		vSPregunta.registerValidator(txtTestQuest, false, Validator.createEmptyValidator("Debe seleccionar un test"));

		ValidationSupport vSAlumno = new ValidationSupport();
		vSAlumno.registerValidator(txtNameAlm, false, Validator.createEmptyValidator("El nombre no puede estar vacío"));
		vSAlumno.registerValidator(txtSurnameAlm, false, Validator.createEmptyValidator("El email no puede estar vacío"));
		vSAlumno.registerValidator(txtAñoNacimiento, false, Validator.createEmptyValidator("Debe introducir un año de nacimiento"));
		vSAlumno.registerValidator(txtDNIAlm, false, Validator.createRegexValidator("El formato del DNI es inválido", "^\\d{8}[A-Za-z]$", Severity.ERROR));
		vSAlumno.registerValidator(txtPasswordAlm, false, Validator.createRegexValidator("""
				La contraseña debe tener al menos 8 caracteres,
				incluyendo una mayúscula, una minúscula,
				un número y un carácter especial
				(@ $ ! % * ? & . - _ #).
				""", "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&\\.\\-\\_\\#])[A-Za-z\\d@$!%*?&\\.\\-\\_\\#]{8,}$", Severity.ERROR));

		ValidationSupport vSArea = new ValidationSupport();
		vSArea.registerValidator(txtNameArea, false, Validator.createEmptyValidator("Debe introducir un nombre de area"));
		vSArea.registerValidator(txtDescripArea, false, Validator.createEmptyValidator("Debe introducir una descripción de area"));

		ValidationSupport vSPxA = new ValidationSupport();
		vSPxA.registerValidator(txtAreaPxA, false, Validator.createEmptyValidator("Debe seleccionar un area"));
		vSPxA.registerValidator(txtQuestPxA, false, Validator.createEmptyValidator("Debe seleccionar una pregunta"));

		ValidationSupport vSIntentos = new ValidationSupport();
		vSIntentos.registerValidator(txtTestTry, false, Validator.createEmptyValidator("Debe seleccionar un test"));
		vSIntentos.registerValidator(txtDNITry, false, Validator.createEmptyValidator("Debe seleccionar un usuario"));
		vSIntentos.registerValidator(txtDateTry, false, Validator.createEmptyValidator("El formato de la fecha es incorrect"));
		/*
		vSProfesor.registerValidator(txtDateTry, Validator.createRegexValidator("""
			Formato de fecha inválido. Use yyyy-MM-dd
		""", "^(\\d{4})-(\\d{2})-(\\d{2})$", Severity.ERROR));
		 */
		vSIntentos.registerValidator(txtTimeTry, false, Validator.createEmptyValidator("Debe seleccionar un test"));
		vSIntentos.registerValidator(txtResTry, false, Validator.createEmptyValidator("Debe seleccionar un test"));

		// INSERTAR
		btnSaveTest.setOnAction(e -> {
			ValidationResult resultado = vSnombreTest.getValidationResult();
			Collection<ValidationMessage> errores = resultado.getErrors();

			if (!errores.isEmpty()) {
				mostrarAlerta(Alert.AlertType.ERROR, "Error", "Datos incompletos o incorrectos", "Por favor, introduce un nombre para el test.");

				ValidationMessage primerError = errores.iterator().next();
				if (primerError.getTarget() instanceof Control control) {
					control.requestFocus();
				}
			} else {
				insertarYActualizar("test");
				btnClearTest.fire();
			}
		});


		btnSaveQuest.setOnAction(e -> {
			ValidationResult resultado = vSPregunta.getValidationResult();
			Collection<ValidationMessage> errores = resultado.getErrors();

			if (!errores.isEmpty()) {
				Alert alerta = new Alert(Alert.AlertType.ERROR);
				alerta.setTitle("Error");
				alerta.setHeaderText("Datos incompletos o incorrectos");
				alerta.setContentText("Por favor, corrige los errores antes de guardar.");
				alerta.showAndWait();

				ValidationMessage primerError = errores.iterator().next();
				if (primerError.getTarget() instanceof Control control) {
					control.requestFocus();
				}
			} else {
				insertarYActualizar("pregunta");
				btnClearQuest.fire();
			}
		});

		btnSaveAlum.setOnAction(e -> {
			ValidationResult resultado = vSAlumno.getValidationResult();
			Collection<ValidationMessage> errores = resultado.getErrors();

			if (!errores.isEmpty()) {
				Alert alerta = new Alert(Alert.AlertType.ERROR);
				alerta.setTitle("Error");
				alerta.setHeaderText("Datos incompletos o incorrectos");
				alerta.setContentText("Por favor, corrige los errores antes de guardar.");
				alerta.showAndWait();

				ValidationMessage primerError = errores.iterator().next();
				if (primerError.getTarget() instanceof Control control) {
					control.requestFocus();
				}
			} else {
				insertarYActualizar("usuario");
				btnClearAlum.fire();
			}
		});


		btnSavePxA.setOnAction(e -> {
			ValidationResult resultado = vSPxA.getValidationResult();
			Collection<ValidationMessage> errores = resultado.getErrors();

			if (!errores.isEmpty()) {
				Alert alerta = new Alert(Alert.AlertType.ERROR);
				alerta.setTitle("Error");
				alerta.setHeaderText("Datos incompletos o incorrectos");
				alerta.setContentText("Por favor, corrige los errores antes de guardar.");
				alerta.showAndWait();

				ValidationMessage primerError = errores.iterator().next();
				if (primerError.getTarget() instanceof Control control) {
					control.requestFocus();
				}
			} else {
				insertarYActualizar("pxa");
				btnClearPxA.fire();
			}
		});

		btnSaveTry.setOnAction(e -> {
			ValidationResult resultado = vSIntentos.getValidationResult();
			Collection<ValidationMessage> errores = resultado.getErrors();

			if (!errores.isEmpty()) {
				Alert alerta = new Alert(Alert.AlertType.ERROR);
				alerta.setTitle("Error");
				alerta.setHeaderText("Datos incompletos o incorrectos");
				alerta.setContentText("Por favor, corrige los errores antes de guardar.");
				alerta.showAndWait();

				ValidationMessage primerError = errores.iterator().next();
				if (primerError.getTarget() instanceof Control control) {
					control.requestFocus();
				}
			} else {
				insertarYActualizar("intentos");
				btnClearTry.fire();
			}
		});

		btnSaveArea.setOnAction(e -> {
			ValidationResult resultado = vSArea.getValidationResult();
			Collection<ValidationMessage> errores = resultado.getErrors();

			if (!errores.isEmpty()) {
				Alert alerta = new Alert(Alert.AlertType.ERROR);
				alerta.setTitle("Error");
				alerta.setHeaderText("Datos incompletos o incorrectos");
				alerta.setContentText("Por favor, corrige los errores antes de guardar.");
				alerta.showAndWait();

				ValidationMessage primerError = errores.iterator().next();
				if (primerError.getTarget() instanceof Control control) {
					control.requestFocus();
				}
			} else {
				insertarYActualizar("area");
				btnClearArea.fire();
			}
		});

		ajustarImagenes();
		habilitarArrastrarYSoltar();
		agregarEventListenersParaSeleccionarImagen();
		btnVisibleYes.setStyle("-fx-background-color: green; -fx-text-fill: white;");
		btnVisibleNo.setStyle("-fx-background-color: gray; -fx-text-fill: black;");

		btnVisibleYes.setOnAction(e -> {
			btnVisibleYes.setStyle("-fx-background-color: green; -fx-text-fill: white;");
			btnVisibleNo.setStyle("-fx-background-color: gray; -fx-text-fill: black;");
		});

		btnVisibleNo.setOnAction(e -> {
			btnVisibleYes.setStyle("-fx-background-color: gray; -fx-text-fill: black;");
			btnVisibleNo.setStyle("-fx-background-color: red; -fx-text-fill: white;");
		});

		Platform.runLater(() -> {
			vSnombreTest.initInitialDecoration();
			vSPregunta.initInitialDecoration();
			vSAlumno.initInitialDecoration();
			vSPxA.initInitialDecoration();
			vSIntentos.initInitialDecoration();
		});
	}

	public void cambiarBotonEditar(Button boton, String textobtn, String tab) {
		boton.setText(textobtn);
	}

	@FXML
	private void handleRegister(MouseEvent event) {
		System.out.println("Redirigir a la pantalla de registro");

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

		alert.setTitle("Registro de Profesor");
		alert.setHeaderText("Solo los profesores pueden registrarse.");
		alert.setContentText("Por favor, introduce los siguientes datos:");
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/escolavision.png")));

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10));
		grid.setHgap(10);
		grid.setVgap(10);

		TextField nombreField = new TextField();
		TextField apellidosField = new TextField();
		TextField dniField = new TextField();
		TextField emailField = new TextField();
		TextField centroField = new TextField();
		TextField anioNacimientoField = new TextField();

		grid.add(new Label("Nombre:"), 0, 0);
		grid.add(nombreField, 1, 0);
		grid.add(new Label("Apellidos:"), 0, 1);
		grid.add(apellidosField, 1, 1);
		grid.add(new Label("DNI:"), 0, 2);
		grid.add(dniField, 1, 2);
		grid.add(new Label("Email:"), 0, 3);
		grid.add(emailField, 1, 3);
		grid.add(new Label("Centro:"), 0, 4);
		grid.add(centroField, 1, 4);
		grid.add(new Label("Año de nacimiento:"), 0, 5);
		grid.add(anioNacimientoField, 1, 5);

		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.setContent(grid);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			String nombre = nombreField.getText();
			String apellidos = apellidosField.getText();
			String dni = dniField.getText();
			String email = emailField.getText();
			String centro = centroField.getText();
			String anioNacimiento = anioNacimientoField.getText();

			System.out.println("Registro:");
			System.out.println("Nombre: " + nombre);
			System.out.println("Apellidos: " + apellidos);
			System.out.println("DNI: " + dni);
			System.out.println("Email: " + email);
			System.out.println("Centro: " + centro);
			System.out.println("Año de nacimiento: " + anioNacimiento);

			// Aquí puedes agregar la lógica para enviar los datos a la API
		}
	}


	public void cambiarBotonSave(Button boton, String textobtn, String tab) {
		boton.setText(textobtn);
	}

	private void cargarTestPartido(String newValue) {
		Test test = buscarTestPorId(Integer.parseInt(newValue));
		txtIdTest.setText("" + test.getId());
		txtTestName.setText(test.getNombre());
		if (test.getIsVisible() == 1) {
			btnVisibleYes.setStyle("-fx-background-color: green; -fx-text-fill: white;");
			btnVisibleNo.setStyle("-fx-background-color: gray; -fx-text-fill: black;");
		} else {
			btnVisibleYes.setStyle("-fx-background-color: gray; -fx-text-fill: black;");
			btnVisibleNo.setStyle("-fx-background-color: red; -fx-text-fill: white;");
		}
	}

	private void cargarPregunta(String newValue) {
		Pregunta pregunta;
		if (newValue.contains("Pregunta")) {
			String[] parts = newValue.trim().split(" ", 2);
			pregunta = buscarPreguntaPorId(Integer.parseInt(parts[1]));
		} else {
			pregunta = buscarPreguntaPorId(Integer.parseInt(newValue));
		}
		txtIdQuest.setText("" + pregunta.getId());
		txtTestQuest.setValue(buscarTestPorId(pregunta.getIdTest()).getNombre());
		txtTituloQuest.setText(pregunta.getTitulo());
		txtEnunQuest.setText(pregunta.getEnunciado());
	}

	private void cargarPreguntaPxA(String newValue) {
		PxA pxa = buscarPxAPorPregunta(Integer.parseInt(newValue));
		Pregunta p = buscarPreguntaPorId(Integer.parseInt(newValue));
		String[] parts = listViewPxa.getSelectionModel().getSelectedItem().trim().split(" ");
		txtIdPxA.setText("" + pxa.getId());
		txtAreaPxA.setText("Área " + parts[1]);
		txtQuestPxA.setText("Pregunta " + p.getId());
		txtEnumPxA.setText(p.getEnunciado());
	}

	private void cargarAlumno(String nombre) {
		Alumno alumno = buscarAlumnoPorNombre(nombre);
		txtIdAlm.setText("" + alumno.getId());
		txtNameAlm.setText(alumno.getNombre());
		txtAñoNacimiento.setText(""+alumno.getEdad());
		txtSurnameAlm.setText(alumno.getEmail());
		txtDNIAlm.setText(alumno.getDni());
		txtPasswordAlm.setText(alumno.getContraseña());
		if (!Objects.equals(alumno.getFoto(), "")) {
			imgViewPicAlum.setFitWidth(123);
			imgViewPicAlum.setFitHeight(151);
			imgViewPicAlum.setImage(base64ToImage(alumno.getFoto()));
		} else {
			imgViewPicAlum.setImage(null);
		}
	}

	private void cargarArea(String newValue) {
		String[] parts = newValue.trim().split("\\s+", 4);
		Area area = buscarAreaPorNombre(parts[3]);
		txtNameArea.setText(area.getNombre());
		txtDescripArea.setText(area.getDescripcion());
		txtIdArea.setText("" + area.getId());
		if (!Objects.equals(area.getLogo(), "")) {
			imgViewPicArea.setImage(base64ToImage(area.getLogo()));
		} else {
			imgViewPicArea.setImage(null);
		}
	}

	private void cargarPxA(String newValue) {
		List<PxA> listpxa = buscarPxAPorArea(Integer.parseInt(newValue));
		List<String> preguntas = new ArrayList<>();
		for (PxA pxa : listpxa) {
			preguntas.add("Pregunta " + pxa.getPregunta().getId());
		}
		listViewPxAPreguntas.setItems(FXCollections.observableArrayList(preguntas));
	}

	private void cargarIntento(String newValue) {

		Intentos intento;
		if (newValue.contains("Intento")) {
			String[] parts = newValue.trim().split("\\s+", 2);
			intento = buscarIntentoPorId(Integer.parseInt(parts[1]));
		} else {
			intento = buscarIntentoPorId(Integer.parseInt(newValue));
		}
		txtIdTry.setText(String.valueOf(intento.getId()));
		txtTestTry.setValue(buscarTestPorId(intento.getTest().getId()).getNombre());
		Alumno a = buscarUsuarioPorId(intento.getAlumno().getId());
		txtDNITry.setText(a.getNombre());
		txtDateTry.setValue(intento.getFecha());
		txtTimeTry.setText(intento.getHora());
		txtResTry.setText(intento.getResultados());

		String[] valores = intento.getResultados().split(";");
		String[] areas = {"AREA 1", "AREA 2", "AREA 3", "AREA 4", "AREA 5"};

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < valores.length; i++) {
			dataset.addValue(Double.parseDouble(valores[i]), "Resultados", areas[i]);
		}

		JFreeChart chart = ChartFactory.createBarChart("Resultados por Área", "Áreas", "Resultados", dataset, PlotOrientation.VERTICAL, true, true, false);

		BufferedImage bufferedImage = chart.createBufferedImage(800, 600);

		Image fxImage = convertToFXImage(bufferedImage);

		ImageView imageView = new ImageView(fxImage);

		imageView.fitWidthProperty().bind(hboxTry.widthProperty());
		imageView.fitHeightProperty().bind(hboxTry.heightProperty());
		imageView.setPreserveRatio(true);

		hboxTry.getChildren().clear();
		hboxTry.setAlignment(Pos.CENTER);
		hboxTry.getChildren().add(imageView);

		imageView.setOnMouseEntered(event -> {
			imageView.setCursor(javafx.scene.Cursor.HAND);
			imageView.setEffect(new DropShadow(20, Color.GRAY));
		});

		imageView.setOnMouseExited(event -> {
			imageView.setCursor(javafx.scene.Cursor.DEFAULT);
			imageView.setEffect(null);
		});

		imageView.setOnMouseClicked(event -> {
			Stage imageStage = new Stage();
			imageStage.setTitle("Gráfico Ampliado");
			imageStage.getIcons().add(new Image(getClass().getResourceAsStream("/escolavision.png")));
			ImageView expandedImageView = new ImageView(fxImage);
			expandedImageView.setPreserveRatio(true);
			expandedImageView.setFitWidth(800);
			expandedImageView.setFitHeight(600);

			Scene scene = new Scene(new StackPane(expandedImageView), 800, 600);

			imageStage.setScene(scene);
			imageStage.show();
		});

		/*new Thread(() -> {
			try {


				javafx.application.Platform.runLater(() -> {

				javafx.application.Platform.runLater(() -> ;miStackPane.getChildren().remove(loadingPane))

			} catch (Exception e) {
				e.printStackTrace();
				javafx.application.Platform.runLater(() -> miStackPane.getChildren().remove(loadingPane));
			}
		}).start();*/
	}

	private Image convertToFXImage(BufferedImage bufferedImage) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
			byteArrayOutputStream.flush();
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			return new Image(new ByteArrayInputStream(byteArray));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void borrar(String tipo, String id) {
		Tab tab = getTabByTipo(tipo);

		if (tab != null) {
			// Crear alerta de confirmación
			Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
			confirmacion.setTitle("Confirmación de eliminación");
			confirmacion.setHeaderText("¿Está seguro de que desea eliminar este elemento?");
			if(tipo.equals("usuarios")){
				confirmacion.setContentText("Se eliminarán todos los datos del usuario, incluidos sus intentos. Esta acción será irreversible.");
			}


			Optional<ButtonType> resultado = confirmacion.showAndWait();

			if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
				try {
					// URL de la API para borrar
					String apiUrl = "http://servidor.ieshlanz.es:8000/crud/borrar.php";

					// Crear JSON con los datos
					JSONObject jsonDatos = new JSONObject();
					jsonDatos.put("tabla", tipo);
					jsonDatos.put("id", Integer.parseInt(id)); // Asegurar que el ID sea numérico

					// Convertir JSON a String
					String jsonString = jsonDatos.toString();

					// Configurar conexión HTTP
					URL url = new URL(apiUrl);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("DELETE");
					connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
					connection.setRequestProperty("Accept", "application/json");
					connection.setDoOutput(true);

					// Enviar JSON en el cuerpo de la solicitud
					try (OutputStream os = connection.getOutputStream()) {
						byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
						os.write(input, 0, input.length);
						os.flush();
					}

					// Obtener respuesta de la API
					int responseCode = connection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {
						limpiar(tab); // Limpiar el contenido de la pestaña
						cargar(tab);  // Recargar los datos
					} else {
						mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al borrar",
								"No se ha podido borrar el elemento seleccionado. Compruebe que no está siendo usado en otro registro.");
					}

				} catch (Exception e) {
					e.printStackTrace();
					mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al borrar",
							"Ocurrió un error al intentar eliminar el elemento: " + e.getMessage());
				}
			}
		} else {
			mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error", "Tipo desconocido: " + tipo);
		}
	}

	private void insertarYActualizar(String tipo) {
		boolean isUpdate = false;
		boolean resultado = false;

		try {
			// URL base para las operaciones
			String baseApiUrl = "http://servidor.ieshlanz.es:8000/crud/";
			String apiUrl;

			// Crear un objeto JSON con los datos
			JSONObject jsonData = new JSONObject();

			switch (tipo) {
				case "test": {
					jsonData.put("tabla", "tests");
					JSONObject datos = new JSONObject();

					datos.put("nombretest", txtTestName.getText());

					if (!txtIdTest.getText().isEmpty()) {
						jsonData.put("id", Integer.parseInt(txtIdTest.getText()));
						isUpdate = true;
					}

					// Verificar el estado de los botones de visibilidad
					if (btnVisibleYes.getStyle().contains("green")) { // Verifica si el fondo es verde
						datos.put("isVisible", 1); // El test es visible (Sí)
					} else if (btnVisibleNo.getStyle().contains("red")) { // Verifica si el fondo es rojo
						datos.put("isVisible", 0); // El test no es visible (No)
					}
					jsonData.put("datos", datos); // Agregar los datos al objeto principal

					break;
				}
				case "pregunta": {
					jsonData.put("tabla", "preguntas");

					JSONObject datos = new JSONObject();

					datos.put("idtest", 1);
					datos.put("enunciado", txtEnunQuest.getText());
					datos.put("titulo", txtTituloQuest.getText());
					Test t = buscarTestPorNombre(txtTestQuest.getValue());
					datos.put("idtest",t.getId());
					if (!txtIdQuest.getText().isEmpty()) {
						jsonData.put("id", Integer.parseInt(txtIdQuest.getText()));
						isUpdate = true;
					}
					jsonData.put("datos", datos); // Agregar los datos al objeto principal

					break;
				}
				case "usuario": {
					jsonData.put("tabla", "usuarios");

					JSONObject datos = new JSONObject();

					datos.put("nombre", txtNameAlm.getText());
					datos.put("email", txtSurnameAlm.getText());
					datos.put("dni", txtDNIAlm.getText());
					datos.put("fecha_nacimiento", txtAñoNacimiento.getText());
					datos.put("contrase\u00f1a", txtPasswordAlm.getText());
					if (imgViewPicAlum.getImage() != null) {
						datos.put("foto", imageToBase64(imgViewPicAlum.getImage()));
					}
					if (!txtIdAlm.getText().isEmpty()) {
						jsonData.put("id", Integer.parseInt(txtIdAlm.getText()));
						isUpdate = true;
					}
					datos.put("tipo_usuario", 1);
					jsonData.put("datos", datos); // Agregar los datos al objeto principal

					break;
				}
				case "area": {
					jsonData.put("tabla", "areas");
					JSONObject datos = new JSONObject();

					datos.put("nombre", txtNameArea.getText());
					datos.put("descripcion", txtDescripArea.getText());
					if (imgViewPicArea.getImage() != null) {
						datos.put("logo", imageToBase64(imgViewPicArea.getImage()));
					}
					if (!txtIdArea.getText().isEmpty()) {
						jsonData.put("id", Integer.parseInt(txtIdArea.getText()));
						isUpdate = true;
					}
					jsonData.put("datos", datos); // Agregar los datos al objeto principal

					break;
				}
				case "pxa": {
					jsonData.put("tabla", "pxa");
					JSONObject datos = new JSONObject();
					Area a = buscarAreaPorNombre(txtAreaPxA.getText());
					datos.put("idarea", a.getId());
					String pregunta = txtQuestPxA.getText();
					String[] partes = pregunta.split(" ");
					datos.put("idpregunta", partes[1]);
					if (!txtIdPxA.getText().isEmpty()) {
						jsonData.put("id", Integer.parseInt(txtIdPxA.getText()));
						isUpdate = true;
					}
					jsonData.put("datos", datos); // Agregar los datos al objeto principal
					break;
				}
				case "intentos": {
					jsonData.put("tabla", "intentos");
					JSONObject datos = new JSONObject();
					Test t = buscarTestPorNombre(txtTestTry.getValue());
					Alumno a = buscarUsuarioPorNombre(txtDNITry.getText());
					datos.put("idusuario", a.getId());
					datos.put("idtest", t.getId());
					LocalDate fecha = txtDateTry.getValue();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
					String fechaString = fecha.format(formatter);
					datos.put("fecha", fechaString);
					datos.put("hora", txtTimeTry.getText());
					datos.put("resultados", txtResTry.getText());
					if (!txtIdTry.getText().isEmpty()) {
						jsonData.put("id", Integer.parseInt(txtIdTry.getText()));
						isUpdate = true;
					}
					jsonData.put("datos", datos); // Agregar los datos al objeto principal
					System.out.println(jsonData);
					break;
				}
				default: {
					mostrarAlerta(Alert.AlertType.WARNING, "Warning", "Operación no válida", "El tipo especificado no es válido.");
					return;
				}
			}
			// Definir la URL y el método (insertar o actualizar)
			System.out.println(jsonData);
			apiUrl = baseApiUrl + (isUpdate ? "actualizar.php" : "insertar.php");
			// Enviar la solicitud HTTP
			resultado = enviarSolicitudApi(apiUrl, jsonData);
		} catch (Exception e) {
			e.printStackTrace();
			mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al procesar los datos", e.getMessage());
		}

		// Actualizar la interfaz gráfica según el resultado
		if (resultado) {
			limpiar(getTabByTipo(tipo));
			cargar(getTabByTipo(tipo));
		} else {
			String operacion = isUpdate ? "actualizar" : "insertar";
			mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al " + operacion, "No se ha podido " + operacion + " el elemento.");
		}
	}

	private boolean enviarSolicitudApi(String apiUrl, JSONObject jsonData) {
		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; utf-8");
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true);
			// Enviar datos JSON
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = jsonData.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			// Leer la respuesta
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				return true;
			} else {
				System.err.println("Error en la respuesta de la API: " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}



	private Tab getTabByTipo(String tipo) {
		return switch (tipo) {
			case "test", "tests" -> tabTest;
			case "pregunta", "preguntas" -> tabQuest;
			case "usuario", "usuarios" -> tabAlum;
            case "area" -> tabAr;
			case "pxa" -> tabPxA;
			case "intentos" -> tabTr;
			default -> null;
		};
	}


	private void ajustarImagenes() {
		imgViewPicAlum.setFitHeight(67);
		imgViewPicAlum.setFitWidth(74);
		imgViewPicArea.setFitHeight(67);
		imgViewPicArea.setFitWidth(74);

		imgViewPicAlum.setPreserveRatio(true);
		imgViewPicArea.setPreserveRatio(true);
	}

	private void agregarEventListenersParaSeleccionarImagen() {
		imgViewPicAlum.setOnMouseClicked(e -> abrirFileChooser(imgViewPicAlum));
		imgViewPicArea.setOnMouseClicked(e -> abrirFileChooser(imgViewPicArea));
	}

	private void abrirFileChooser(ImageView imageView) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg"));
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			Image image = new Image(file.toURI().toString());
			imageView.setImage(image);
		}
	}

	private void habilitarArrastrarYSoltar() {
		habilitarArrastreImagen(imgViewPicAlum);
		habilitarArrastreImagen(imgViewPicArea);
	}

	private void habilitarArrastreImagen(ImageView imageView) {
		imageView.setOnDragOver(event -> {
			if (event.getGestureSource() != imageView && event.getDragboard().hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY);
			}
			event.consume();
		});

		imageView.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();
			boolean success = false;

			if (db.hasFiles()) {
				File file = db.getFiles().getFirst();
				Image image = new Image(file.toURI().toString());
				imageView.setImage(image);
				success = true;
			}

			event.setDropCompleted(success);
			event.consume();
		});
	}

	private String imageToBase64(Image image) {
		if (image == null) {
			return null;
		}

		try {
			BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

			int maxWidth = 300;
			int maxHeight = 300;
			BufferedImage resizedImage = resizeImage(bufferedImage, maxWidth, maxHeight);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			float compressionQuality = 0.9f;
			String base64Image;

			do {
				baos.reset();
				ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
				ImageWriteParam param = writer.getDefaultWriteParam();

				if (param.canWriteCompressed()) {
					param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					param.setCompressionQuality(compressionQuality);
				}

				ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
				writer.setOutput(ios);
				writer.write(null, new IIOImage(resizedImage, null, null), param);
				writer.dispose();

				base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
				compressionQuality -= 0.1f;
			} while (base64Image.length() > 20000 && compressionQuality > 0.1f);

			if (base64Image.length() > 20000) {
				throw new IllegalArgumentException("La imagen no puede comprimirse lo suficiente para cumplir el límite.");
			}

			return base64Image;

		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	private BufferedImage resizeImage(BufferedImage originalImage, int maxWidth, int maxHeight) {
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();

		double scale = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
		int newWidth = (int) (originalWidth * scale);
		int newHeight = (int) (originalHeight * scale);

		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
		g.dispose();
		return resizedImage;
	}

	private Image base64ToImage(String base64Image) {
		if (base64Image == null || base64Image.isEmpty()) {
			return null;
		}
		try {
			byte[] imageBytes = Base64.getDecoder().decode(base64Image.replaceAll("\\s", ""));
			ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
			BufferedImage bufferedImage = ImageIO.read(bis);
			return SwingFXUtils.toFXImage(bufferedImage, null);
		} catch (IOException e) {
			return null;
		}
	}

	private void cargarDatos(Tab newTab) {
		if (newTab == tabTr) {
			cargarDatosUsuarios();
			cargarDatosTests();
		} else if (newTab == tabQuest) {
			cargarDatosTests();
		} else if (newTab == tabPxA) {
			cargarDatosAreas();
			cargarDatosPreguntas();
		}
	}

	private void cargarDatosPreguntas() {
		String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=preguntas";
		ObservableList<String> idList = FXCollections.observableArrayList();
		ObservableList<String> rawList = obtenerListaDesdeApi(apiUrl, "preguntas", "id");

		for (String id : rawList) {
			idList.add("Pregunta " + id);
		}

		configureAutocomplete(txtQuestPxA, idList);
	}

	private void cargarDatosUsuarios() {
		String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=usuarios";
		ObservableList<String> nombreList = obtenerListaDesdeApi(apiUrl, "usuarios", "nombre");

		TextFields.bindAutoCompletion(txtDNITry, nombreList);
	}

	private void cargarDatosTests() {
		String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=tests";
		ObservableList<String> originalList = obtenerListaDesdeApi(apiUrl, "tests", "nombretest");
		txtTestQuest.setItems(originalList);
		txtTestTry.setItems(originalList);
	}

	private void cargarDatosAreas() {
		String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=areas";
		ObservableList<String> areaList = obtenerListaDesdeApi(apiUrl, "areas", "nombre");

		configureAutocomplete(txtAreaPxA, areaList);
	}


	private ObservableList<String> obtenerListaDesdeApi(String apiUrl, String clave, String... campos) {
		ObservableList<String> lista = FXCollections.observableArrayList();

		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					response.append(line);
				}

				JSONObject jsonObject = new JSONObject(response.toString());
				JSONArray jsonArray = jsonObject.getJSONArray(clave);

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject objeto = jsonArray.getJSONObject(i);
					StringBuilder concatenado = new StringBuilder();

					for (String campo : campos) {
						Object valor = objeto.get(campo);
						concatenado.append(valor.toString()).append(" "); // Convierte el valor a String explícitamente
					}

					lista.add(concatenado.toString().trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			showErrorBD();
		}

		return lista;
	}


	private void configureAutocomplete(TextField textField, ObservableList<String> data) {
		TextFields.bindAutoCompletion(textField, data);
	}

	private void showErrorBD() {
		javafx.application.Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error de Base de Datos");
			alert.setHeaderText(null);
			alert.setContentText("Hubo un error al acceder a la base de datos.");
			alert.showAndWait();
		});
	}

	private void cargar(Tab newTab) {
		if (newTab == tabAlum) {
			cargarDatos("usuarios", listViewAlumnos);
		} else if (newTab == tabAr) {
			cargarDatos("areas", listViewArea);
		} else if (newTab == tabTr) {
			cargarDatos("intentos", listViewIntentos);
		} else if (newTab == tabQuest) {
			cargarDatos("preguntas", listViewPreguntas);
		} else if (newTab == tabPxA) {
			cargarDatos("areas", listViewPxa);
		} else if (newTab == tabTest) {
			cargarDatos("tests", listViewTest);
		}
	}

	private void limpiar(Tab newTab) {
		Platform.runLater(() -> {
			switch (newTab.getText()) {
				case "EscolaVision" -> {
					txtUser.clear();
					txtPassword.clear();
				}
				case "Test" -> {
					txtIdTest.clear();
					txtTestName.clear();
					listViewTest.getSelectionModel().clearSelection();
					btnVisibleYes.fire();
					cambiarBotonSave(btnSaveTest, "Guardar", "test");
				}
				case "Preguntas" -> {
					txtIdQuest.clear();
					txtTestQuest.setValue("Seleccione test...");
					txtEnunQuest.clear();
					txtTituloQuest.clear();
					listViewPreguntas.getSelectionModel().clearSelection();
					cambiarBotonSave(btnSaveQuest, "Guardar", "pregunta");
				}
				case "Usuarios" -> {
					txtIdAlm.clear();
					txtNameAlm.clear();
					txtSurnameAlm.clear();
					txtPasswordAlm.clear();
					txtAñoNacimiento.clear();
					txtDNIAlm.clear();
					imgViewPicAlum.setImage(null);
					listViewAlumnos.getSelectionModel().clearSelection();
					cambiarBotonSave(btnSaveAlum, "Guardar", "usuario");
				}
				case "Área" -> {
					txtIdArea.clear();
					txtNameArea.clear();
					txtDescripArea.clear();
					imgViewPicArea.setImage(null);
					listViewArea.getSelectionModel().clearSelection();
				}
				case "Pregunta x Área" -> {
					txtIdPxA.clear();
					txtAreaPxA.clear();
					txtQuestPxA.clear();
					txtEnumPxA.clear();
					listViewPxa.getSelectionModel().clearSelection();
					listViewPxAPreguntas.getSelectionModel().clearSelection();
					listViewPxAPreguntas.setItems(null);
					cambiarBotonSave(btnSavePxA, "Guardar", "pxa");
				}
				case "Intentos" -> {
					txtIdTry.clear();
					txtTestTry.setValue("Seleccione test...");
					txtDNITry.clear();
					txtDateTry.setValue(null);
					txtTimeTry.clear();
					txtResTry.clear();
					listViewIntentos.getSelectionModel().clearSelection();
					hboxTry.getChildren().clear();
					cambiarBotonSave(btnSaveTry, "Guardar", "intentos");
				}
			}
		});
	}


	public void login() {
		String user = txtUser.getText();
		String password = txtPassword.getText();

		String apiUrl = "http://servidor.ieshlanz.es:8000/crud/login.php";

		new Thread(() -> {
			try {
				// Crear conexión HTTP
				URL url = new URL(apiUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setDoOutput(true);

				// Crear el objeto JSON con los datos del login
				JSONObject loginData = new JSONObject();
				loginData.put("usuario", user);
				loginData.put("contrasena", password);

				// Enviar datos al servidor
				try (OutputStream os = connection.getOutputStream()) {
					byte[] input = loginData.toString().getBytes("utf-8");
					os.write(input, 0, input.length);
				}

				// Leer la respuesta del servidor
				int status = connection.getResponseCode();
				if (status == 200) {
					try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
						StringBuilder response = new StringBuilder();
						String responseLine;
						while ((responseLine = br.readLine()) != null) {
							response.append(responseLine.trim());
						}
						// Parsear la respuesta JSON
						JSONObject jsonResponse = new JSONObject(response.toString());
						String statusResponse = jsonResponse.optString("status", "error");
						String message = jsonResponse.optString("message", "Error desconocido");

						if ("success".equalsIgnoreCase(statusResponse)) {
							String nombre = jsonResponse.optString("nombre", "Nombre no disponible");
							String dni = jsonResponse.optString("dni", "DNI no disponibles");
							String tipo = jsonResponse.optString("tipo", "Tipo no disponible");

							if ("Profesor".equals(tipo)) {
								boolean isOrientador = jsonResponse.optInt("is_orientador", 0) == 1;
								Platform.runLater(() -> {
									if (isOrientador) {
										rolAdmin();
										configurarHeader(nombre + " - " + dni);
									} else {
										rolProfesor();
										configurarHeader(nombre + " - " + dni);
									}
								});
							} else {
								Platform.runLater(() -> {
									rolAlumno();
									configurarHeader(nombre + " - " + dni);
								});
							}

						} else {
							Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error", "Login fallido", message));
						}
					}
				} else {
					Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error en el servidor", "Código de error: " + status));
				}
			} catch (Exception e) {
				e.printStackTrace();
				Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error de conexión", "No se pudo conectar con el servidor."));
			}
		}).start();
	}


	private Label lblNombre = new Label();

	private void configurarHeader(String nombreCompleto) {
		hbHeader.getChildren().clear();

		hbHeader.getChildren().addAll(btnLogo, lblEscolavisionDesktop);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		lblNombre = new Label(nombreCompleto);
		lblNombre.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
		HBox.setMargin(lblNombre, new Insets(0, 10, 0, 0));

		Button btnCerrarSesion = new Button("Cerrar sesión");
		btnCerrarSesion.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-start-margin: 15px;");
		ImageView imageView = new ImageView(new Image("logOut.png"));
		imageView.setFitWidth(16);
		imageView.setFitHeight(16);
		btnCerrarSesion.setGraphic(imageView);
		btnCerrarSesion.setOnAction(event -> cerrarSesion(spacer, lblNombre, btnCerrarSesion));


		hbHeader.getChildren().addAll(spacer, lblNombre, btnCerrarSesion);
	}

	private void cerrarSesion(Region spacer, Label lblNombre, Button btnCerrarSesion) {
		Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
		confirmacion.setTitle("Confirmación de cierre de sesión");
		confirmacion.setHeaderText("¿Está seguro de que desea cerrar sesión?");
		Optional<ButtonType> resultado = confirmacion.showAndWait();

		if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
			hbHeader.getChildren().removeAll(spacer, lblNombre, btnCerrarSesion);
			tabPane.getTabs().removeAll(tabTest, tabQuest, tabAlum, tabAr, tabPxA, tabTr);
			tabPane.getTabs().add(tabHome);
		}
	}

	private void cargarDatos(String tabla, ListView<String> listView) {
		String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=" + tabla;

		new Thread(() -> {
			ObservableList<String> items = FXCollections.observableArrayList();

			try {
				// Conectar a la API
				URL url = new URL(apiUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "application/json");

				// Leer la respuesta de la API
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					StringBuilder response = new StringBuilder();
					String line;

					while ((line = reader.readLine()) != null) {
						response.append(line);
					}

					// Parsear la respuesta JSON
					JSONObject responseObject = new JSONObject(response.toString());

					// Verificar el tipo de tabla y manejar los datos correspondientes
					switch (tabla.toLowerCase()) {
						case "tests":
							if (responseObject.has("tests")) {
								JSONArray testArray = responseObject.getJSONArray("tests");
								for (int i = 0; i < testArray.length(); i++) {
									JSONObject testObject = testArray.getJSONObject(i);
									int testId = testObject.getInt("id");
									String testName = testObject.getString("nombretest");
									items.add("Test "+testId+" - "+testName);
								}
							}
							break;

						case "usuarios":
							if (responseObject.has("usuarios")) {
								JSONArray array = responseObject.has("usuarios") ? responseObject.getJSONArray("usuarios") : responseObject.getJSONArray("profesores");
								for (int i = 0; i < array.length(); i++) {
									JSONObject itemObject = array.getJSONObject(i);
									String name = itemObject.getString("nombre");
									items.add(name);
								}
							}
							break;

						case "areas":
							if (responseObject.has("areas")) {
								JSONArray areasArray = responseObject.getJSONArray("areas");
								for (int i = 0; i < areasArray.length(); i++) {
									JSONObject areaObject = areasArray.getJSONObject(i);
									int idArea = areaObject.getInt("id");
									String areaName = areaObject.getString("nombre");
									items.add("ÁREA "+idArea+" - " + areaName);
								}
							}
							break;

						case "preguntas":
							if (responseObject.has("preguntas")) {
								JSONArray preguntasArray = responseObject.getJSONArray("preguntas");
								for (int i = 0; i < preguntasArray.length(); i++) {
									JSONObject preguntaObject = preguntasArray.getJSONObject(i);
									String preguntaName = "Pregunta " + preguntaObject.getInt("id");
									items.add(preguntaName);
								}
							}
							break;

						case "pxa":
							if (responseObject.has("pxa")) {
								JSONArray pxaArray = responseObject.getJSONArray("pxa");
								for (int i = 0; i < pxaArray.length(); i++) {
									JSONObject pxaObject = pxaArray.getJSONObject(i);
									String pxaName = pxaObject.getInt("idpregunta") + " - " + pxaObject.getInt("idarea");
									items.add(pxaName);
								}
							}
							break;

						case "intentos":
							if (responseObject.has("intentos")) {
								JSONArray intentosArray = responseObject.getJSONArray("intentos");
								for (int i = 0; i < intentosArray.length(); i++) {
									JSONObject intentoObject = intentosArray.getJSONObject(i);
									String intentoName = "Intento " + intentoObject.getInt("id");
									items.add(intentoName);
								}
							}
							break;

						default:
							// Si no es una tabla conocida, muestra un mensaje por defecto
							items.add("Tabla desconocida");
							break;
					}
				}

				// Actualizar la ListView en el hilo de la interfaz gráfica
				Platform.runLater(() -> listView.setItems(items));

			} catch (Exception e) {
				e.printStackTrace();
				Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar datos", "No se pudo cargar los datos desde la API."));
			}
		}).start();
	}


	public Test buscarTestPorId(int id) {
		try {
			String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=tests";
			JSONArray testsArray = obtenerArrayDesdeApi(apiUrl);

			for (int i = 0; i < testsArray.length(); i++) {
				JSONObject testObject = testsArray.getJSONObject(i);
				if (testObject.getInt("id") == id) {
					return new Test(testObject.getInt("id"), testObject.getString("nombretest"), testObject.getInt("isVisible"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Test buscarTestPorNombre(String nombre) {
		try {
			String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=tests";
			JSONArray testsArray = obtenerArrayDesdeApi(apiUrl);

			for (int i = 0; i < testsArray.length(); i++) {
				JSONObject testObject = testsArray.getJSONObject(i);
				if (Objects.equals(testObject.getString("nombretest"), nombre)) {
					return new Test(testObject.getInt("id"), testObject.getString("nombretest"), testObject.getInt("isVisible"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Pregunta buscarPreguntaPorId(int id) {
		try {
			String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=preguntas";
			JSONArray preguntasArray = obtenerArrayDesdeApi(apiUrl);

			for (int i = 0; i < preguntasArray.length(); i++) {
				JSONObject preguntaObject = preguntasArray.getJSONObject(i);
				if (preguntaObject.getInt("id") == id) {
					return new Pregunta(preguntaObject.getInt("id"), preguntaObject.getString("enunciado"), preguntaObject.getString("titulo"), preguntaObject.getInt("idtest"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public Alumno buscarAlumnoPorNombre(String nombre) {
		try {
			String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=usuarios";
			JSONArray alumnosArray = obtenerArrayDesdeApi(apiUrl);

			for (int i = 0; i < alumnosArray.length(); i++) {
				JSONObject alumnoObject = alumnosArray.getJSONObject(i);

				if (alumnoObject.getString("nombre").equalsIgnoreCase(nombre)) {

					// Manejo seguro del campo 'foto'
					String foto = null;
					if (alumnoObject.has("foto") && !alumnoObject.isNull("foto")) {
						Object fotoObject = alumnoObject.get("foto");
						if (fotoObject instanceof String) {
							foto = (String) fotoObject;
						} else {
							System.out.println("El campo 'foto' no es un String. Tipo recibido: " + fotoObject.getClass().getSimpleName());
						}
					}

					int fechaNacimiento = 0000;
					if(alumnoObject.has("fecha_nacimiento") && !alumnoObject.isNull("fecha_nacimiento")) {
						fechaNacimiento = alumnoObject.getInt("fecha_nacimiento");
					}

					// Crear y retornar el objeto Alumno
					return new Alumno(
							alumnoObject.getInt("id"),
							alumnoObject.getString("nombre"),
							alumnoObject.getString("dni"),
							alumnoObject.getString("contraseña"),
							fechaNacimiento,
							foto,
							alumnoObject.getString("email")
					);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Alumno buscarUsuarioPorId(int id){
		try {
			String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=usuarios";
			JSONArray alumnosArray = obtenerArrayDesdeApi(apiUrl);

			for (int i = 0; i < alumnosArray.length(); i++) {
				JSONObject alumnoObject = alumnosArray.getJSONObject(i);
				if (alumnoObject.getInt("id")== id) {
					// Manejo seguro del campo 'foto'
					String foto = null;
					if (alumnoObject.has("foto") && !alumnoObject.isNull("foto")) {
						Object fotoObject = alumnoObject.get("foto");
						if (fotoObject instanceof String) {
							foto = (String) fotoObject;
						} else {
							System.out.println("El campo 'foto' no es un String. Tipo recibido: " + fotoObject.getClass().getSimpleName());
						}
					}

					int fechaNacimiento = 0000;
					if(alumnoObject.has("fecha_nacimiento") && !alumnoObject.isNull("fecha_nacimiento")) {
						fechaNacimiento = alumnoObject.getInt("fecha_nacimiento");
					}

					// Crear y retornar el objeto Alumno
					return new Alumno(
							alumnoObject.getInt("id"),
							alumnoObject.getString("nombre"),
							alumnoObject.getString("dni"),
							alumnoObject.getString("contraseña"),
							fechaNacimiento,
							foto,
							alumnoObject.getString("email")
					);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Alumno buscarUsuarioPorNombre(String nombre){
		try {
			String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=usuarios";
			JSONArray alumnosArray = obtenerArrayDesdeApi(apiUrl);

			for (int i = 0; i < alumnosArray.length(); i++) {
				JSONObject alumnoObject = alumnosArray.getJSONObject(i);
				if (Objects.equals(alumnoObject.getString("nombre"), nombre)) {
					return new Alumno(
							alumnoObject.getInt("id"),
							alumnoObject.getString("nombre"),
							"",
							"",
							0,
							"",
							""
					);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public Area buscarAreaPorNombre(String nombre) {
		try {
			String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=areas";
			JSONArray areasArray = obtenerArrayDesdeApi(apiUrl);

			for (int i = 0; i < areasArray.length(); i++) {
				JSONObject areaObject = areasArray.getJSONObject(i);
				if (areaObject.getString("nombre").equalsIgnoreCase(nombre)) {
					return new Area(
							areaObject.getInt("id"),
							areaObject.getString("nombre"),
							areaObject.getString("descripción"),
							areaObject.getString("logo")
					);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	public PxA buscarPxAPorPregunta(int id) {
		try {
			String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=pxa";
			JSONArray pxaArray = obtenerArrayDesdeApi(apiUrl);

			for (int i = 0; i < pxaArray.length(); i++) {
				JSONObject pxaObject = pxaArray.getJSONObject(i);
				if (pxaObject.getInt("idpregunta") == id) {
					return new PxA(
							pxaObject.getInt("id"),
							pxaObject.getInt("idpregunta"),
							pxaObject.getInt("idarea")
					);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public List<PxA> buscarPxAPorArea(int id) {
		List<PxA> listaPxA = new ArrayList<>();  // Crear una lista para almacenar las coincidencias

		try {
			String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=pxa";
			JSONArray pxaArray = obtenerArrayDesdeApi(apiUrl);

			for (int i = 0; i < pxaArray.length(); i++) {
				JSONObject pxaObject = pxaArray.getJSONObject(i);
				if (pxaObject.getInt("idarea") == id) {
					listaPxA.add(new PxA(
							pxaObject.getInt("id"),
							pxaObject.getInt("idpregunta"),
							pxaObject.getInt("idarea")
					));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listaPxA;  // Devolver la lista completa
	}



	public Intentos buscarIntentoPorId(int id) {
		try {
			String apiUrl = "http://servidor.ieshlanz.es:8000/crud/leer.php?tabla=intentos";
			JSONArray intentosArray = obtenerArrayDesdeApi(apiUrl);

			for (int i = 0; i < intentosArray.length(); i++) {
				JSONObject intentoObject = intentosArray.getJSONObject(i);
				if (intentoObject.getInt("id") == id) {
					return new Intentos(
							intentoObject.getInt("id"),
							intentoObject.getInt("idtest"),
							intentoObject.getInt("idusuario"),
							intentoObject.getString("fecha"),
							intentoObject.getString("hora"),
							intentoObject.getString("resultados")
					);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	private JSONArray obtenerArrayDesdeApi(String apiUrl) throws IOException, JSONException {
		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			JSONObject jsonResponse = new JSONObject(response.toString());
			// Asume que la respuesta tiene un array con el nombre de la tabla
			String rootKey = apiUrl.split("tabla=")[1]; // Obtener el nombre de la tabla
			return jsonResponse.getJSONArray(rootKey);
		}
	}



	public void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String contenido) {
		Platform.runLater(() -> {
			Alert alert = new Alert(tipo);
			alert.setTitle(titulo);
			alert.setHeaderText(cabecera);
			alert.setContentText(contenido);

			Image icon = new Image(getClass().getResourceAsStream("/escolavision.png"));
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(icon);

			alert.showAndWait();
		});
	}
	public void mostrarMenu(ActionEvent event) {
		Button sourceButton = (Button) event.getSource();

		ContextMenu contextMenu = new ContextMenu();

		MenuItem creditos = new MenuItem("Créditos");
		creditos.setOnAction(e -> {
			try {
				mostrarCreditos();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});

		MenuItem salir = new MenuItem("Salir");
		salir.setOnAction(e -> salirAplicacion());

		contextMenu.getItems().addAll(creditos, salir);

		contextMenu.show(sourceButton, Side.BOTTOM, 0, 0);
	}

	public void mostrarCreditos() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/creditos.fxml"));
		Parent creditosView = loader.load();

		Stage creditosStage = new Stage();
		creditosStage.setTitle("Créditos");

		Scene creditosScene = new Scene(creditosView);
		creditosStage.setScene(creditosScene);
		creditosStage.getIcons().add(new Image("escolavision.png"));
		creditosStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

		creditosStage.showAndWait();
	}

	public void salirAplicacion() {
		if (tabHome.isSelected()) {
			System.exit(0);
		}

		Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
		confirmacion.setTitle("Confirmación de salida");
		confirmacion.setHeaderText("¿Está seguro de que desea salir?");

		Optional<ButtonType> resultado = confirmacion.showAndWait();

		if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
			System.exit(0);
		}
	}
}
