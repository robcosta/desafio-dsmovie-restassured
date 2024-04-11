package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class MovieControllerRA {
	
	private Integer existsId, nonExistsId;
	
	private String clientToken, adminToken, invalidToken;
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	
	private Map<String, Object> movieInstance;
	
	@BeforeEach
	public void setUp() throws Exception{
		baseURI = "http://localhost:8080";
		
		existsId = 1;
		nonExistsId = 100;
		
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";

		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken = adminToken + "xpto";
		
		movieInstance = new HashMap<>();
		movieInstance.put("title", "Test Movie");
		movieInstance.put("score", 0.0);
		movieInstance.put("count", 0);
		movieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		
		
		
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given()
		.when()
			.get("/movies")
		.then()
			.statusCode(200)
			.body("content.id", hasItems(1,2,3,4,5))
			.body("content.title", hasItems("Matrix Resurrections", "O Espetacular Homem-Aranha 2: A Ameaça de Electro", "The Witcher"))
			.body("content.score", hasItems(4.33F,0.0F, 3.3F))
			.body("content.image", hasItems("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/6t8ES1d12OzWyCGxBeDYLHoaDrT.jpg", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/vIgyYkXkg6NC2whRbYjBD7eb3Er.jpg"));
			
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {	
		given()
		.when()
			.get("/movies?title=Star Wars")
		.then()
			.statusCode(200)
			.body("content.id", hasItems(10,12))
			.body("content.title", hasItems("Rogue One: Uma História Star Wars", "Star Wars: Episódio I - A Ameaça Fantasma"))
			.body("content.score", hasItem(0.0F))
			.body("content.image", hasItem("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/36LnijfQCOC89rCMOhn2OINXROI.jpg"))
			.body("totalPages", is(1))
			.body("totalElements", is(3));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		given()
		.when()
			.get("/movies/{id}", existsId)
		.then()
			.statusCode(200)
			.body("id", is(existsId))
			.body("title", equalTo("The Witcher"))
			.body("score", is(4.33F))
			.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		given()
		.when()
			.get("/movies/{id}", nonExistsId)
		.then()
			.statusCode(404)
			.body("error", equalTo("Recurso não encontrado"))
			.body("path", equalTo("/movies/" + nonExistsId));
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		movieInstance.put("title", "");
		JSONObject newMovie = new JSONObject(movieInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(422)
			.body("error", equalTo("Dados inválidos"))
			.body("path", equalTo("/movies"))	
			.body("errors.fieldName", hasItem("title"))
			.body("errors.message", hasItem("Campo requerido"));
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject newMovie = new JSONObject(movieInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(403);	
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject newMovie = new JSONObject(movieInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(401);
	}
}
