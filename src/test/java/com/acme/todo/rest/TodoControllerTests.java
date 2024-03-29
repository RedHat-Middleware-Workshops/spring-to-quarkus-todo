package com.acme.todo.rest;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import com.acme.todo.domain.TodoEntity;
import com.acme.todo.repository.TodoRepository;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTests {
	private static final TodoEntity TODO = new TodoEntity(1L, "Go on vacation", false);

	@Autowired
	MockMvc mockMvc;

	@MockBean
	TodoRepository todoRepository;

	@BeforeEach
	public void beforeEach() {
		RestAssuredMockMvc.mockMvc(this.mockMvc);
	}

	@Test
	void findAll() {
		when(this.todoRepository.findAll(any(Sort.class)))
			.thenReturn(List.of(TODO));

		var todos = get("/todo").then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.extract().body()
			.jsonPath().getList(".", TodoEntity.class);

		assertThat(todos)
			.singleElement()
			.usingRecursiveComparison()
			.isEqualTo(TODO);

		verify(this.todoRepository).findAll(any(Sort.class));
		verifyNoMoreInteractions(this.todoRepository);
	}

	@Test
	void findById() {
		when(this.todoRepository.findById(eq(TODO.getId())))
			.thenReturn(Optional.of(TODO));

		var foundTodo = get("/todo/{id}", String.valueOf(TODO.getId())).then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.extract().as(TodoEntity.class);

		assertThat(foundTodo)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(TODO);

		verify(this.todoRepository).findById(eq(TODO.getId()));
		verifyNoMoreInteractions(this.todoRepository);
	}

	@Test
	void update() {
		when(this.todoRepository.save(any(TodoEntity.class)))
			.thenReturn(TODO);

		given()
			.body(TODO)
			.contentType(ContentType.JSON)
			.put("/todo").then()
			.statusCode(204);

		verify(this.todoRepository).save(any(TodoEntity.class));
		verifyNoMoreInteractions(this.todoRepository);
	}

	@Test
	void create() {
		when(this.todoRepository.save(any(TodoEntity.class)))
			.thenReturn(TODO);

		var createdTodo = given()
			.body(TODO)
			.contentType(ContentType.JSON)
			.post("/todo").then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.extract().as(TodoEntity.class);

		assertThat(createdTodo)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(TODO);

		verify(this.todoRepository).save(any(TodoEntity.class));
		verifyNoMoreInteractions(this.todoRepository);
	}

	@Test
	void delete() {
		doNothing()
			.when(this.todoRepository)
			.deleteById(eq(TODO.getId()));

		given().delete("/todo/{id}", String.valueOf(TODO.getId())).then()
			.statusCode(204);

		verify(this.todoRepository).deleteById(eq(TODO.getId()));
		verifyNoMoreInteractions(this.todoRepository);
	}
}