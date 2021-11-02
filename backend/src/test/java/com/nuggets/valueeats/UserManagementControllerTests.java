package com.nuggets.valueeats;

import com.nuggets.valueeats.entity.User;
import com.nuggets.valueeats.repository.UserRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserManagementControllerTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserRepository<User> userRepository;

	@Test
	public void testRegisterDiner_validDiner() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "diner1");
		body.put("email", "diner1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/diner")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		)
			.andExpect(status().isOk());
	}

	@Test
	public void testRegisterDiner_aliasAlreadyExists() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "diner1");
		body.put("email", "diner1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/diner")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		);

		this.mockMvc.perform(
			post("/register/diner")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body)))
			)
			.andExpect(status().is4xxClientError());
	}

	@Test
	public void testRegisterDiner_invalidEmail() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "diner2");
		body.put("email", "diner2");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/diner")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		)
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testRegisterDiner() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "diner2");
		body.put("email", "diner2@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "1234");

		this.mockMvc.perform(
				post("/register/diner")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		)
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testRegisterEatery_success() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "eatery1");
		body.put("email", "eatery1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/eatery")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		)
				.andExpect(status().isOk());
	}

	@Test
	public void testRegisterEatery_aliasExists() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "eatery1");
		body.put("email", "eatery1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/eatery")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		);

		this.mockMvc.perform(
				post("/register/eatery")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		)
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testRegisterEatery_invalidEmail() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "eatery2");
		body.put("email", "eatery2");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/eatery")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		)
			.andExpect(status().is4xxClientError());
	}

	@Test
	public void testRegisterEatery_invalidPassword() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "eatery2");
		body.put("email", "eatery2@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "1234");

		this.mockMvc.perform(
				post("/register/eatery")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		)
		.andExpect(status().is4xxClientError());
	}

	@Test
	public void testDinerLogin_success() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "diner1");
		body.put("email", "diner1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/diner")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		);

		this.mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body)))
			)
			.andExpect(status().isOk());
	}

	@Test
	public void testDinerLogin_invalidDetails() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "diner1");
		body.put("email", "diner1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/diner")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		);

		body = new HashMap<>();
		body.put("alias", "diner1");
		body.put("email", "diner@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body)))
			)
			.andExpect(status().is4xxClientError());
	}

	@Test
	public void testEateryLogin_success() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "eatery1");
		body.put("email", "eatery1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/eatery")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		);

		this.mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body)))
			)
			.andExpect(status().isOk());
	}

	@Test
	public void testEateryLogin_invalidDetails() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "eatery1");
		body.put("email", "eatery1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/eatery")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		);

		body = new HashMap<>();
		body.put("alias", "eatery1");
		body.put("email", "eatery@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body)))
			)
			.andExpect(status().is4xxClientError());
	}

	@Test
	public void testUpdateDiner_success() throws Exception {
		this.userRepository.deleteAll();
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "diner1");
		body.put("email", "diner1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		String result = this.mockMvc.perform(
		post("/register/diner")
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(new JSONObject(body)))
		)
		.andReturn()
		.getResponse()
		.getContentAsString();
		JSONObject data = new JSONObject(result);
		String token = data.getJSONObject("data").getString("token");

		body = new HashMap<>();
		body.put("alias", "superman");

		this.mockMvc.perform(
				post("/update/diner")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", token)
						.content(String.valueOf(new JSONObject(body)))
		)
				.andExpect(status().isOk());
	}

	@Test
	public void testUpdateDiner_invalidInformation() throws Exception {
		this.userRepository.deleteAll();
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "diner1");
		body.put("email", "diner1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		String result = this.mockMvc.perform(
				post("/register/diner")
						.contentType(MediaType.APPLICATION_JSON)
						.content(String.valueOf(new JSONObject(body)))
		)
				.andReturn()
				.getResponse()
				.getContentAsString();
		JSONObject data = new JSONObject(result);
		String token = data.getJSONObject("data").getString("token");

		body = new HashMap<>();
		body.put("alias", "superman");
		body.put("password", "1234");

		this.mockMvc.perform(
				post("/update/diner")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", token)
						.content(String.valueOf(new JSONObject(body)))
		)
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testUpdateEatery_validInformation() throws Exception {
		this.userRepository.deleteAll();
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "eatery1");
		body.put("email", "eatery1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		String result = this.mockMvc.perform(
		post("/register/eatery")
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(new JSONObject(body)))
		)
		.andReturn()
		.getResponse()
		.getContentAsString();
		JSONObject data = new JSONObject(result);
		String token = data.getJSONObject("data").getString("token");

		body = new HashMap<>();
		body.put("alias", "superman");

		this.mockMvc.perform(
			post("/update/eatery")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token)
				.content(String.valueOf(new JSONObject(body)))
			)
			.andExpect(status().isOk());
	}

	@Test
	public void testUpdateEatery_invalidInformation() throws Exception {
		this.userRepository.deleteAll();
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "eatery1");
		body.put("email", "eatery1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		String result = this.mockMvc.perform(
				post("/register/eatery")
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(new JSONObject(body)))
		)
		.andReturn()
		.getResponse()
		.getContentAsString();
		JSONObject data = new JSONObject(result);
		String token = data.getJSONObject("data").getString("token");

		body = new HashMap<>();
		body.put("alias", "superman");
		body.put("email","1234");

		this.mockMvc.perform(
			post("/update/eatery")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token)
				.content(String.valueOf(new JSONObject(body)))
			)
			.andExpect(status().is4xxClientError());
	}

	@Test
	public void testDinerLogout_activeToken() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "diner1");
		body.put("email", "diner1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/diner")
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(new JSONObject(body)))
		);

		String result = this.mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body)))
			)
		.andReturn()
		.getResponse()
		.getContentAsString();

		JSONObject data = new JSONObject(result);
		String token = data.getJSONObject("data").getString("token");

		body = new HashMap<>();
		body.put("alias", "superman");

		this.mockMvc.perform(
			post("/logout")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token)
			)
			.andExpect(status().isOk());
	}

	@Test
	public void testDinerLogout_invalidToken() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "diner1");
		body.put("email", "diner1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/diner")
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(new JSONObject(body)))
		);

		this.mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body)))
			);

		String token = "invalidtoken";

		body = new HashMap<>();
		body.put("alias", "superman");

		this.mockMvc.perform(
			post("/logout")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token)
			)
			.andExpect(status().is4xxClientError());
	}

	@Test
	public void testEateryLogout_validToken() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "eatery1");
		body.put("email", "eatery1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/eatery")
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(new JSONObject(body)))
		);

		String result = this.mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body)))
			)
		.andReturn()
		.getResponse()
		.getContentAsString();

		JSONObject data = new JSONObject(result);
		String token = data.getJSONObject("data").getString("token");

		body = new HashMap<>();
		body.put("alias", "superman");

		this.mockMvc.perform(
			post("/logout")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token)
			)
			.andExpect(status().isOk());
	}

	@Test
	public void testEateryLogout_invalidToken() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "eatery1");
		body.put("email", "eatery1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
				post("/register/eatery")
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(new JSONObject(body)))
		);

		this.mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body)))
			);

		String token = "invalidtoken";

		body = new HashMap<>();
		body.put("alias", "superman");

		this.mockMvc.perform(
			post("/logout")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token)
			)
			.andExpect(status().is4xxClientError());
	}

	@Test
	void listDinersTest1() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "diner1");
		body.put("email", "diner1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
		post("/register/diner")
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(new JSONObject(body)))
		);

		String result = this.mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body)))
			)
		.andReturn()
		.getResponse()
		.getContentAsString();

		JSONObject data = new JSONObject(result);
		String token = data.getJSONObject("data").getString("token");
		
		body = new HashMap<>();
		body.put("alias", "superman");

		this.mockMvc.perform(
      MockMvcRequestBuilders
      .get("/health/list/diners")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk());
	}
		
	@Test
	void listEateriesTest1() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body = new HashMap<>();
		body.put("alias", "eatery1");
		body.put("email", "eatery1@gmail.com");
		body.put("address", "Sydney");
		body.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
		post("/register/eatery")
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(new JSONObject(body)))
		);

		String result = this.mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body)))
			)
		.andReturn()
		.getResponse()
		.getContentAsString();

		JSONObject data = new JSONObject(result);
		String token = data.getJSONObject("data").getString("token");
		
		body = new HashMap<>();
		body.put("alias", "superman");

		this.mockMvc.perform(
      MockMvcRequestBuilders
      .get("/health/list/eateries")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk());
	}

	@Test
	void listUsersTest1() throws Exception {
		this.userRepository.deleteAll();
		Map<String, String> body1 = new HashMap<>();
		body1.put("alias", "eatery1");
		body1.put("email", "eatery1@gmail.com");
		body1.put("address", "Sydney");
		body1.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
		post("/register/eatery")
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(new JSONObject(body1)))
		);

		String result = this.mockMvc.perform(
			post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(String.valueOf(new JSONObject(body1)))
			)
		.andReturn()
		.getResponse()
		.getContentAsString();

		Map<String, String> body2 = new HashMap<>();
		body2.put("alias", "diner1");
		body2.put("email", "diner1@gmail.com");
		body2.put("address", "Sydney");
		body2.put("password", "12rwqeDsad@");

		this.mockMvc.perform(
		post("/register/diner")
			.contentType(MediaType.APPLICATION_JSON)
			.content(String.valueOf(new JSONObject(body2)))
		);

		JSONObject data = new JSONObject(result);
		String token = data.getJSONObject("data").getString("token");
		
		body2 = new HashMap<>();
		body2.put("alias", "superman");

		this.mockMvc.perform(
      MockMvcRequestBuilders
      .get("/health/list/users")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk());
	}
}
