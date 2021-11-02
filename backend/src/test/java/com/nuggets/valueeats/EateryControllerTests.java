package com.nuggets.valueeats;

import com.nuggets.valueeats.entity.User;
import com.nuggets.valueeats.repository.UserRepository;
import com.nuggets.valueeats.repository.voucher.RepeatVoucherRepository;
import com.nuggets.valueeats.repository.voucher.VoucherRepository;
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
public class EateryControllerTests {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private VoucherRepository voucherRepository;

  @Autowired
  private RepeatVoucherRepository repeatVoucherRepository;

	@Autowired
	private MockMvc mockMvc;

  // Test create a one-off voucher with valid input.
  @Test
  void eateryCreateVoucherTest1() throws Exception {
    this.userRepository.deleteAll();
    this.voucherRepository.deleteAll();;
    this.repeatVoucherRepository.deleteAll();
    Map<String, String> eatery = new HashMap<>();
		eatery.put("alias", "eatery1");
		eatery.put("email", "eatery1@gmail.com");
		eatery.put("address", "Sydney");
		eatery.put("password", "12rwqeDsad@");

    String result = this.mockMvc.perform(
			post("/register/eatery")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token = data.getJSONObject("data").getString("token");

    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","0");
    voucher1.put("eatingStyle","DineIn");
    voucher1.put("discount","0.8");
    voucher1.put("quantity","15");
    voucher1.put("isRecurring","false");
    long currenttime = System.currentTimeMillis() + Long.valueOf(36000000);
    voucher1.put("date", Long.toString(currenttime));
    voucher1.put("startMinute","10");
    voucher1.put("endMinute","50");

    this.mockMvc.perform(
      post("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(String.valueOf(new JSONObject(voucher1)))
    )
    .andExpect(status().isOk());
  }

  // Test create a repeated voucher with valid input.
  @Test
  void eateryCreateVoucherTest2() throws Exception {
    this.userRepository.deleteAll();
    this.voucherRepository.deleteAll();;
    this.repeatVoucherRepository.deleteAll();
    Map<String, String> eatery = new HashMap<>();
		eatery.put("alias", "eatery1");
		eatery.put("email", "eatery1@gmail.com");
		eatery.put("address", "Sydney");
		eatery.put("password", "12rwqeDsad@");

    String result = this.mockMvc.perform(
			post("/register/eatery")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token = data.getJSONObject("data").getString("token");

    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","0");
    voucher1.put("eatingStyle","DineIn");
    voucher1.put("discount","0.8");
    voucher1.put("quantity","15");
    voucher1.put("isRecurring","true");
    long currenttime = System.currentTimeMillis() + Long.valueOf(36000000);
    voucher1.put("date", Long.toString(currenttime));
    voucher1.put("startMinute","10");
    voucher1.put("endMinute","50");

    this.mockMvc.perform(
      post("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(String.valueOf(new JSONObject(voucher1)))
    )
    .andExpect(status().isOk());
  }

    // Test create a voucher with invalid date.
    @Test
    void eateryCreateVoucherTest3() throws Exception {
      this.userRepository.deleteAll();
      this.voucherRepository.deleteAll();;
      this.repeatVoucherRepository.deleteAll();
      Map<String, String> eatery = new HashMap<>();
      eatery.put("alias", "eatery1");
      eatery.put("email", "eatery1@gmail.com");
      eatery.put("address", "Sydney");
      eatery.put("password", "12rwqeDsad@");
  
      String result = this.mockMvc.perform(
        post("/register/eatery")
          .contentType(MediaType.APPLICATION_JSON)
          .content(String.valueOf(new JSONObject(eatery)))
      )
      .andReturn()
      .getResponse()
      .getContentAsString();
  
      JSONObject data = new JSONObject(result);
      String token = data.getJSONObject("data").getString("token");
  
      Map<String,String> voucher1 = new HashMap<>();
      voucher1.put("eateryId","0");
      voucher1.put("eatingStyle","DineIn");
      voucher1.put("discount","0.8");
      voucher1.put("quantity","15");
      voucher1.put("isRecurring","true");
      long currenttime = System.currentTimeMillis() - Long.valueOf(36000000);
      voucher1.put("date", Long.toString(currenttime));
      voucher1.put("startMinute","10");
      voucher1.put("endMinute","50");
  
      this.mockMvc.perform(
        post("/eatery/voucher")
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", token)
          .content(String.valueOf(new JSONObject(voucher1)))
      )
      .andExpect(status().is4xxClientError());
    }
  
  // Test create a voucher with invalid duration.
  @Test
  void eateryCreateVoucherTest4() throws Exception {
    this.userRepository.deleteAll();
    this.voucherRepository.deleteAll();;
    this.repeatVoucherRepository.deleteAll();
    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

    String result = this.mockMvc.perform(
      post("/register/eatery")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token = data.getJSONObject("data").getString("token");

    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","0");
    voucher1.put("eatingStyle","DineIn");
    voucher1.put("discount","0.8");
    voucher1.put("quantity","15");
    voucher1.put("isRecurring","true");
    long currenttime = System.currentTimeMillis() + Long.valueOf(36000000);
    voucher1.put("date", Long.toString(currenttime));
    voucher1.put("startMinute","10");
    voucher1.put("endMinute","20");

    this.mockMvc.perform(
      post("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(String.valueOf(new JSONObject(voucher1)))
    )
    .andExpect(status().is4xxClientError());
  }

  // Test delete a voucher with valid input.
  @Test
  void eateryDeleteVoucherTest1() throws Exception {
    this.userRepository.deleteAll();
    this.voucherRepository.deleteAll();;
    this.repeatVoucherRepository.deleteAll();
    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

    String result = this.mockMvc.perform(
      post("/register/eatery")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token = data.getJSONObject("data").getString("token");

    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","0");
    voucher1.put("eatingStyle","DineIn");
    voucher1.put("discount","0.8");
    voucher1.put("quantity","15");
    voucher1.put("isRecurring","true");
    long currenttime = System.currentTimeMillis() + Long.valueOf(36000000);
    voucher1.put("date", Long.toString(currenttime));
    voucher1.put("startMinute","10");
    voucher1.put("endMinute","40");

    this.mockMvc.perform(
      post("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(String.valueOf(new JSONObject(voucher1)))
    );

    this.mockMvc.perform(
      MockMvcRequestBuilders
      .delete("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .param("id", "0")
    )
    .andExpect(status().isOk());
  }

  // Test delete a voucher with invalid token.
  @Test
  void eateryDeleteVoucherTest2() throws Exception {
    this.userRepository.deleteAll();
    this.voucherRepository.deleteAll();;
    this.repeatVoucherRepository.deleteAll();
    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

    String result = this.mockMvc.perform(
      post("/register/eatery")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token = data.getJSONObject("data").getString("token");

    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","0");
    voucher1.put("eatingStyle","DineIn");
    voucher1.put("discount","0.8");
    voucher1.put("quantity","15");
    voucher1.put("isRecurring","true");
    long currenttime = System.currentTimeMillis() + Long.valueOf(36000000);
    voucher1.put("date", Long.toString(currenttime));
    voucher1.put("startMinute","10");
    voucher1.put("endMinute","40");

    this.mockMvc.perform(
      post("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(String.valueOf(new JSONObject(voucher1)))
    );

    this.mockMvc.perform(
      MockMvcRequestBuilders
      .delete("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "InvalidToken")
        .param("id", "0")
    )
    .andExpect(status().is4xxClientError());
  }

  // Test delete a voucher with invalid voucher id.
  @Test
  void eateryDeleteVoucherTest3() throws Exception {
    this.userRepository.deleteAll();
    this.voucherRepository.deleteAll();;
    this.repeatVoucherRepository.deleteAll();
    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

    String result = this.mockMvc.perform(
      post("/register/eatery")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token = data.getJSONObject("data").getString("token");

    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","0");
    voucher1.put("eatingStyle","DineIn");
    voucher1.put("discount","0.8");
    voucher1.put("quantity","15");
    voucher1.put("isRecurring","true");
    long currenttime = System.currentTimeMillis() + Long.valueOf(36000000);
    voucher1.put("date", Long.toString(currenttime));
    voucher1.put("startMinute","10");
    voucher1.put("endMinute","40");

    this.mockMvc.perform(
      post("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(String.valueOf(new JSONObject(voucher1)))
    );

    this.mockMvc.perform(
      MockMvcRequestBuilders
      .delete("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .param("id", "10")
    )
    .andExpect(status().is4xxClientError());
  }

  @Test
  void listRepeatedVouchersTest1() throws Exception {
    this.userRepository.deleteAll();
    this.voucherRepository.deleteAll();;
    this.repeatVoucherRepository.deleteAll();
    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

    String result = this.mockMvc.perform(
      post("/register/eatery")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token = data.getJSONObject("data").getString("token");

    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","0");
    voucher1.put("eatingStyle","DineIn");
    voucher1.put("discount","0.7");
    voucher1.put("quantity","15");
    voucher1.put("isRecurring","true");
    long currenttime = System.currentTimeMillis() + Long.valueOf(36000000);
    voucher1.put("date", Long.toString(currenttime));
    voucher1.put("startMinute","10");
    voucher1.put("endMinute","40");

    this.mockMvc.perform(
      post("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(String.valueOf(new JSONObject(voucher1)))
    );

    Map<String,String> voucher2 = new HashMap<>();
    voucher2.put("eateryId","0");
    voucher2.put("eatingStyle","DineIn");
    voucher2.put("discount","0.8");
    voucher2.put("quantity","15");
    voucher2.put("isRecurring","true");
    currenttime = System.currentTimeMillis() + Long.valueOf(36000000);
    voucher2.put("date", Long.toString(currenttime));
    voucher2.put("startMinute","10");
    voucher2.put("endMinute","40");

    this.mockMvc.perform(
      post("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(String.valueOf(new JSONObject(voucher1)))
    );

    this.mockMvc.perform(
      MockMvcRequestBuilders
      .get("/health/list/repeatVoucher")
        .contentType(MediaType.APPLICATION_JSON)
    )
    .andExpect(status().isOk());
  }

  @Test
  void listOneOffVouchersTest1() throws Exception {
    this.userRepository.deleteAll();
    this.voucherRepository.deleteAll();;
    this.repeatVoucherRepository.deleteAll();
    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

    String result = this.mockMvc.perform(
      post("/register/eatery")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token = data.getJSONObject("data").getString("token");

    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","0");
    voucher1.put("eatingStyle","DineIn");
    voucher1.put("discount","0.7");
    voucher1.put("quantity","15");
    voucher1.put("isRecurring","true");
    long currenttime = System.currentTimeMillis() + Long.valueOf(36000000);
    voucher1.put("date", Long.toString(currenttime));
    voucher1.put("startMinute","10");
    voucher1.put("endMinute","40");

    this.mockMvc.perform(
      post("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(String.valueOf(new JSONObject(voucher1)))
    );

    Map<String,String> voucher2 = new HashMap<>();
    voucher2.put("eateryId","0");
    voucher2.put("eatingStyle","DineIn");
    voucher2.put("discount","0.8");
    voucher2.put("quantity","15");
    voucher2.put("isRecurring","true");
    currenttime = System.currentTimeMillis() + Long.valueOf(36000000);
    voucher2.put("date", Long.toString(currenttime));
    voucher2.put("startMinute","10");
    voucher2.put("endMinute","40");

    this.mockMvc.perform(
      post("/eatery/voucher")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(String.valueOf(new JSONObject(voucher1)))
    );

    this.mockMvc.perform(
      MockMvcRequestBuilders
      .get("/health/list/voucher")
        .contentType(MediaType.APPLICATION_JSON)
    )
    .andExpect(status().isOk());
  }

}
