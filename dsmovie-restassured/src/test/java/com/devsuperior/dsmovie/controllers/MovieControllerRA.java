package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MovieControllerRA {
	
	private Integer existsId, nonExistsId;
	
	
	@BeforeEach
	public void setUp() throws Exception{
		baseURI = "http://localhost:8080";
		
		existsId = 1;
		nonExistsId = 100;
		
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
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {		
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
	}
}
