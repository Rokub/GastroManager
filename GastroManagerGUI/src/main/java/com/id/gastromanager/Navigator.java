package com.id.gastromanager;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

import com.id.gastromanager.controller.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Navigator {
	private static final Map<Stage, Navigator> navigators = new IdentityHashMap<>();

	public static Navigator of(Stage stage) {
		return navigators.computeIfAbsent(stage, Navigator::new);
	}

	public static Stage createStage(Parent parent) {
		Stage stage = new Stage();
		stage.setScene(new Scene(parent));
		stage.setMinHeight(600);
		stage.setMinWidth(800);
		Navigator navigator = new Navigator(stage);
		navigators.put(stage, navigator);
		return stage;
	}

	public static Stage createStageNamed(String route, Object... args) throws IOException {
		return createStage(loadRoute(route, args));
	}

	private static Parent loadRoute(String route, Object... args) throws IOException {
		FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(route));
		try {
			Parent parent = loader.load();
			loader.<Controller>getController().init(args);
			return parent;
		} catch (IOException e) {
			throw new IOException("No such route: " + route);
		}
	}

	private final Stage stage;
	private final Stack<Parent> pageStack;

	private Navigator(Stage stage) {
		this.stage = stage;
		pageStack = new Stack<>();
		pageStack.push(this.stage.getScene().getRoot());
	}

	public void set(Parent parent) {
		pageStack.clear();
		pageStack.push(parent);
		stage.getScene().setRoot(parent);
	}

	public void setNamed(String route, Object... args) throws IOException {
		set(loadRoute(route, args));
	}

	public void push(Parent parent) {
		BorderPane borderPane = new BorderPane(parent);

		Button backButton = new Button();
		backButton.setText("←");
		backButton.setOnMouseClicked(e -> pop());
		borderPane.setTop(backButton);

		pageStack.push(borderPane);
		stage.getScene().setRoot(borderPane);
	}

	public void pushNamed(String route, Object... args) throws IOException {
		push(loadRoute(route, args));
	}

	public void pop() throws UnsupportedOperationException {
		pageStack.pop();
		if (pageStack.empty()) {
			stage.close();
		} else {
			stage.getScene().setRoot(pageStack.peek());
		}
	}
}
