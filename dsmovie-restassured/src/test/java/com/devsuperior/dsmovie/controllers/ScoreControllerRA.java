package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ScoreControllerRA {
	
	private String clientToken, adminToken, invalidToken;
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	
	private Integer nonExistsMovieId;
	
	private Map<String, Object> scoreInstance;
	
	@BeforeEach
	public void setUp() throws Exception{
		baseURI = "http://localhost:8080";
		
		nonExistsMovieId = 100;
		
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";

		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		
		scoreInstance = new HashMap<>();
		scoreInstance.put("movieId", 1);
		scoreInstance.put("score", 4);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		scoreInstance.put("movieId", nonExistsMovieId);
		JSONObject newScore = new JSONObject(scoreInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newScore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(404)
			.body("error", equalTo("Recurso não encontrado"))
			.body("path", equalTo("/scores"));	
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		scoreInstance.put("movieId", "");
		JSONObject newScore = new JSONObject(scoreInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newScore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422)
			.body("error", equalTo("Dados inválidos"))
			.body("path", equalTo("/scores"))	
			.body("errors.fieldName", hasItem("movieId"))
			.body("errors.message", hasItem("Campo requerido"));		
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		scoreInstance.put("score", -1);
		JSONObject newScore = new JSONObject(scoreInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newScore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422)
			.body("error", equalTo("Dados inválidos"))
			.body("path", equalTo("/scores"))	
			.body("errors.fieldName", hasItem("score"))
			.body("errors.message", hasItem("Valor mínimo 0"));
	}
}
