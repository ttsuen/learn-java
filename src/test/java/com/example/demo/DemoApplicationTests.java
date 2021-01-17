package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class DemoApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private CatRepository catRepository;

	@BeforeEach()
	public void before() throws Exception {
		Stream.of("Felix", "Garfield", "Whiskers")
				.forEach(name -> catRepository.save(new Cat(name)));
	}

	@Test
	public void catsReflectedInRead() throws Exception {
		MediaType halJson = MediaType.parseMediaType("application/hal+json");

		this.mvc
				.perform(get("/cats"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(halJson))
				.andExpect(mvcResult -> {
					String contentAsString = mvcResult.getResponse().getContentAsString();
					assertTrue(contentAsString.split("totalElements")[1].split(":")[1].trim().split(",")[0].equals("3"));
				});
	}
}
