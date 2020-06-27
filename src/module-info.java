module detector {
	requires javafx.graphics;
	requires javafx.controls;
	opens controller to javafx.graphics;
	
	requires client.java.api;
	requires client.java;
	
	requires java.sql;
	requires javafx.base;
	
	requires okhttp3;
	requires gson;
	requires ejml.simple;
	requires javafx.fxml;
	requires jdk.httpserver;
}