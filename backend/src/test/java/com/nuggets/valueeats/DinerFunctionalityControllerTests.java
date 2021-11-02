package com.nuggets.valueeats;

import com.nuggets.valueeats.entity.User;
import com.nuggets.valueeats.repository.BookingRecordRepository;
import com.nuggets.valueeats.repository.ReviewRepository;
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
public class DinerFunctionalityControllerTests {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  @Autowired
  private VoucherRepository voucherRepository;

  @Autowired
  private RepeatVoucherRepository repeatVoucherRepository;

  @Autowired
  private BookingRecordRepository bookingRecordRepository;

	@Autowired
	private MockMvc mockMvc;

  // Test create a review with valid input.

  @Test
  public void testBooking_ValidInput() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();

    Map<String, String> diner = new HashMap<>();
    diner.put("alias", "diner1");
    diner.put("email", "diner1@gmail.com");
    diner.put("address", "Sydney");
    diner.put("password", "12rwqeDsad@");

     String result = this.mockMvc.perform(
      post("/register/diner")
      .contentType(MediaType.APPLICATION_JSON)
      .content(String.valueOf(new JSONObject(diner)))
    )
      .andReturn()
      .getResponse()
      .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token_diner = data.getJSONObject("data").getString("token");

    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

     result = this.mockMvc.perform(
      post("/register/eatery")
              .contentType(MediaType.APPLICATION_JSON)
              .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

      data = new JSONObject(result);
    String token_eatery = data.getJSONObject("data").getString("token");
    
    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","1");
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
        .header("Authorization", token_eatery)
        .content(String.valueOf(new JSONObject(voucher1)))
    )
    .andExpect(status().isOk());

    this.mockMvc.perform(
      post("/diner/book")
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", token_diner)
      .param("id", "0")
    )
    .andExpect(status().isOk());
  }

  @Test
  public void testBooking_InvalidToken() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();

    Map<String, String> diner = new HashMap<>();
    diner.put("alias", "diner1");
    diner.put("email", "diner1@gmail.com");
    diner.put("address", "Sydney");
    diner.put("password", "12rwqeDsad@");

     String result = this.mockMvc.perform(
      post("/register/diner")
      .contentType(MediaType.APPLICATION_JSON)
      .content(String.valueOf(new JSONObject(diner)))
    )
      .andReturn()
      .getResponse()
      .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token_diner = data.getJSONObject("data").getString("token");

    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

     result = this.mockMvc.perform(
      post("/register/eatery")
              .contentType(MediaType.APPLICATION_JSON)
              .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

      data = new JSONObject(result);
    String token_eatery = data.getJSONObject("data").getString("token");
    
    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","1");
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
        .header("Authorization", token_eatery)
        .content(String.valueOf(new JSONObject(voucher1)))
    )
    .andExpect(status().isOk());

    this.mockMvc.perform(
      post("/diner/book")
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", "InvalidToken")
      .param("id", "0")
    )
    .andExpect(status().is4xxClientError());
  }

  @Test
  public void testBooking_InvalidVoucherId() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();

    Map<String, String> diner = new HashMap<>();
    diner.put("alias", "diner1");
    diner.put("email", "diner1@gmail.com");
    diner.put("address", "Sydney");
    diner.put("password", "12rwqeDsad@");

     String result = this.mockMvc.perform(
      post("/register/diner")
      .contentType(MediaType.APPLICATION_JSON)
      .content(String.valueOf(new JSONObject(diner)))
    )
      .andReturn()
      .getResponse()
      .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token_diner = data.getJSONObject("data").getString("token");

    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

     result = this.mockMvc.perform(
      post("/register/eatery")
              .contentType(MediaType.APPLICATION_JSON)
              .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

      data = new JSONObject(result);
    String token_eatery = data.getJSONObject("data").getString("token");
    
    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","1");
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
        .header("Authorization", token_eatery)
        .content(String.valueOf(new JSONObject(voucher1)))
    )
    .andExpect(status().isOk());

    this.mockMvc.perform(
      post("/diner/book")
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", token_diner)
      .param("id", "10")
    )
    .andExpect(status().is4xxClientError());
  }

  @Test
  public void testCreateReview_ValidInput() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();

    Map<String, String> diner = new HashMap<>();
    diner.put("alias", "diner1");
    diner.put("email", "diner1@gmail.com");
    diner.put("address", "Sydney");
    diner.put("password", "12rwqeDsad@");

     String result = this.mockMvc.perform(
      post("/register/diner")
      .contentType(MediaType.APPLICATION_JSON)
      .content(String.valueOf(new JSONObject(diner)))
    )
      .andReturn()
      .getResponse()
      .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token_diner = data.getJSONObject("data").getString("token");

    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

     result = this.mockMvc.perform(
      post("/register/eatery")
              .contentType(MediaType.APPLICATION_JSON)
              .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

      data = new JSONObject(result);
    String token_eatery = data.getJSONObject("data").getString("token");
    
    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","1");
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
        .header("Authorization", token_eatery)
        .content(String.valueOf(new JSONObject(voucher1)))
    )
    .andExpect(status().isOk());

    this.mockMvc.perform(
      post("/diner/book")
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", token_diner)
      .param("id", "0")
    )
    .andExpect(status().isOk());

    Map<String, String> review = new HashMap<>();
    review.put("dinerId", "0");
    review.put("eateryId", "1");
    review.put("message", "hello");
    review.put("rating", "2");

    this.mockMvc.perform(
            post("/diner/createreview")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", token_diner)
                    .content(String.valueOf(new JSONObject(review)))
    )
            .andExpect(status().isOk());
  }

  @Test
  public void testCreateReview_haventDinedAt() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();
    Map<String, String> diner = new HashMap<>();
    diner.put("alias", "diner1");
    diner.put("email", "diner1@gmail.com");
    diner.put("address", "Sydney");
    diner.put("password", "12rwqeDsad@");

    String result = this.mockMvc.perform(
        post("/register/diner")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(diner)))
    )
        .andReturn()
        .getResponse()
        .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token = data.getJSONObject("data").getString("token");

    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

    this.mockMvc.perform(
        post("/register/eatery")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(eatery)))
    )
    .andExpect(status().isOk());;

    Map<String, String> review = new HashMap<>();
    review.put("dinerId", "0");
    review.put("eateryId", "1");
    review.put("message", "hello");
    review.put("rating", "2");

    this.mockMvc.perform(
        post("/diner/createreview")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token)
        .content(String.valueOf(new JSONObject(review)))
    )
        .andExpect(status().is4xxClientError());
}

@Test
public void testCreateReview_invalidEateryId() throws Exception {
        this.userRepository.deleteAll();
        this.reviewRepository.deleteAll();
        this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
        this.bookingRecordRepository.deleteAll();

    Map<String, String> diner = new HashMap<>();
    diner.put("alias", "diner1");
    diner.put("email", "diner1@gmail.com");
    diner.put("address", "Sydney");
    diner.put("password", "12rwqeDsad@");

     String result = this.mockMvc.perform(
      post("/register/diner")
      .contentType(MediaType.APPLICATION_JSON)
      .content(String.valueOf(new JSONObject(diner)))
    )
      .andReturn()
      .getResponse()
      .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token_diner = data.getJSONObject("data").getString("token");

    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

     result = this.mockMvc.perform(
      post("/register/eatery")
              .contentType(MediaType.APPLICATION_JSON)
              .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

      data = new JSONObject(result);
    String token_eatery = data.getJSONObject("data").getString("token");
    
    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","1");
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
        .header("Authorization", token_eatery)
        .content(String.valueOf(new JSONObject(voucher1)))
    )
    .andExpect(status().isOk());

    this.mockMvc.perform(
      post("/diner/book")
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", token_diner)
      .param("id", "0")
    )
    .andExpect(status().isOk());

    Map<String, String> review = new HashMap<>();
    review.put("dinerId", "100");
    review.put("eateryId", "100");
    review.put("message", "hello");
    review.put("rating", "2");

    this.mockMvc.perform(
            post("/diner/createreview")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", token_diner)
                    .content(String.valueOf(new JSONObject(review)))
    )
            .andExpect(status().is4xxClientError());
  }

  @Test
  public void testCreateReview_invalidRating() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();

            Map<String, String> diner = new HashMap<>();
      diner.put("alias", "diner1");
      diner.put("email", "diner1@gmail.com");
      diner.put("address", "Sydney");
      diner.put("password", "12rwqeDsad@");

       String result = this.mockMvc.perform(
        post("/register/diner")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(diner)))
      )
        .andReturn()
        .getResponse()
        .getContentAsString();

      JSONObject data = new JSONObject(result);
      String token_diner = data.getJSONObject("data").getString("token");

      Map<String, String> eatery = new HashMap<>();
      eatery.put("alias", "eatery1");
      eatery.put("email", "eatery1@gmail.com");
      eatery.put("address", "Sydney");
      eatery.put("password", "12rwqeDsad@");

       result = this.mockMvc.perform(
        post("/register/eatery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(new JSONObject(eatery)))
      )
      .andReturn()
      .getResponse()
      .getContentAsString();

        data = new JSONObject(result);
      String token_eatery = data.getJSONObject("data").getString("token");
      
      Map<String,String> voucher1 = new HashMap<>();
      voucher1.put("eateryId","1");
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
          .header("Authorization", token_eatery)
          .content(String.valueOf(new JSONObject(voucher1)))
      )
      .andExpect(status().isOk());

      this.mockMvc.perform(
        post("/diner/book")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .param("id", "0")
      )
      .andExpect(status().isOk());

      Map<String, String> review = new HashMap<>();
      review.put("dinerId", "0");
      review.put("eateryId", "1");
      review.put("message", "hello");
      review.put("rating", "5.3");

      this.mockMvc.perform(
              post("/diner/createreview")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", token_diner)
                      .content(String.valueOf(new JSONObject(review)))
      )
              .andExpect(status().is4xxClientError());
  }

   @Test
   public void testRemoveReview_ValidInput() throws Exception {
        this.userRepository.deleteAll();
        this.reviewRepository.deleteAll();
        this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
        this.bookingRecordRepository.deleteAll();

             Map<String, String> diner = new HashMap<>();
      diner.put("alias", "diner1");
      diner.put("email", "diner1@gmail.com");
      diner.put("address", "Sydney");
      diner.put("password", "12rwqeDsad@");

       String result = this.mockMvc.perform(
        post("/register/diner")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(diner)))
      )
        .andReturn()
        .getResponse()
        .getContentAsString();

      JSONObject data = new JSONObject(result);
      String token_diner = data.getJSONObject("data").getString("token");

      Map<String, String> eatery = new HashMap<>();
      eatery.put("alias", "eatery1");
      eatery.put("email", "eatery1@gmail.com");
      eatery.put("address", "Sydney");
      eatery.put("password", "12rwqeDsad@");

       result = this.mockMvc.perform(
        post("/register/eatery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(new JSONObject(eatery)))
      )
      .andReturn()
      .getResponse()
      .getContentAsString();

        data = new JSONObject(result);
      String token_eatery = data.getJSONObject("data").getString("token");
      
      Map<String,String> voucher1 = new HashMap<>();
      voucher1.put("eateryId","1");
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
          .header("Authorization", token_eatery)
          .content(String.valueOf(new JSONObject(voucher1)))
      )
      .andExpect(status().isOk());

      this.mockMvc.perform(
        post("/diner/book")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .param("id", "0")
      )
      .andExpect(status().isOk());

       Map<String, String> review = new HashMap<>();
       review.put("id", "0");
       review.put("dinerId", "0");
       review.put("eateryId", "1");
       review.put("message", "hello");
       review.put("rating", "4");
       this.mockMvc.perform(
               post("/diner/createreview")
                       .contentType(MediaType.APPLICATION_JSON)
                       .header("Authorization", token_diner)
                       .content(String.valueOf(new JSONObject(review)))
       );
       this.mockMvc.perform(
               MockMvcRequestBuilders
                       .delete("/diner/removereview")
                       .contentType(MediaType.APPLICATION_JSON)
                       .header("Authorization", token_diner)
                       .content(String.valueOf(new JSONObject(review)))
       )
               .andExpect(status().isOk());
   }

  @Test
  public void testRemoveReview_invalidToken() throws Exception {
        this.userRepository.deleteAll();
        this.reviewRepository.deleteAll();
        this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
        this.bookingRecordRepository.deleteAll();

            Map<String, String> diner = new HashMap<>();
      diner.put("alias", "diner1");
      diner.put("email", "diner1@gmail.com");
      diner.put("address", "Sydney");
      diner.put("password", "12rwqeDsad@");

       String result = this.mockMvc.perform(
        post("/register/diner")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(diner)))
      )
        .andReturn()
        .getResponse()
        .getContentAsString();

      JSONObject data = new JSONObject(result);
      String token_diner = data.getJSONObject("data").getString("token");

      Map<String, String> eatery = new HashMap<>();
      eatery.put("alias", "eatery1");
      eatery.put("email", "eatery1@gmail.com");
      eatery.put("address", "Sydney");
      eatery.put("password", "12rwqeDsad@");

       result = this.mockMvc.perform(
        post("/register/eatery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(new JSONObject(eatery)))
      )
      .andReturn()
      .getResponse()
      .getContentAsString();

        data = new JSONObject(result);
      String token_eatery = data.getJSONObject("data").getString("token");
      
      Map<String,String> voucher1 = new HashMap<>();
      voucher1.put("eateryId","1");
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
          .header("Authorization", token_eatery)
          .content(String.valueOf(new JSONObject(voucher1)))
      )
      .andExpect(status().isOk());

      this.mockMvc.perform(
        post("/diner/book")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .param("id", "0")
      )
      .andExpect(status().isOk());

      Map<String, String> review = new HashMap<>();
      review.put("id", "0");
      review.put("dinerId", "0");
      review.put("eateryId", "1");
      review.put("message", "hello");
      review.put("rating", "4");

      this.mockMvc.perform(
              post("/diner/createreview")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", token_diner)
                      .content(String.valueOf(new JSONObject(review)))
      );

      this.mockMvc.perform(
              MockMvcRequestBuilders
                      .delete("/diner/removereview")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "InvalidToken")
                      .content(String.valueOf(new JSONObject(review)))
      )
              .andExpect(status().is4xxClientError());
  }

  @Test
  public void testRemoveReview_invalidMessageId() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();
      
      Map<String, String> diner = new HashMap<>();
      diner.put("alias", "diner1");
      diner.put("email", "diner1@gmail.com");
      diner.put("address", "Sydney");
      diner.put("password", "12rwqeDsad@");

       String result = this.mockMvc.perform(
        post("/register/diner")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(diner)))
      )
        .andReturn()
        .getResponse()
        .getContentAsString();

      JSONObject data = new JSONObject(result);
      String token_diner = data.getJSONObject("data").getString("token");

      Map<String, String> eatery = new HashMap<>();
      eatery.put("alias", "eatery1");
      eatery.put("email", "eatery1@gmail.com");
      eatery.put("address", "Sydney");
      eatery.put("password", "12rwqeDsad@");

       result = this.mockMvc.perform(
        post("/register/eatery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(new JSONObject(eatery)))
      )
      .andReturn()
      .getResponse()
      .getContentAsString();

        data = new JSONObject(result);
      String token_eatery = data.getJSONObject("data").getString("token");
      
      Map<String,String> voucher1 = new HashMap<>();
      voucher1.put("eateryId","1");
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
          .header("Authorization", token_eatery)
          .content(String.valueOf(new JSONObject(voucher1)))
      )
      .andExpect(status().isOk());

      this.mockMvc.perform(
        post("/diner/book")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .param("id", "0")
      )
      .andExpect(status().isOk());

      Map<String, String> review = new HashMap<>();
      review.put("id", "100");
      review.put("dinerId", "0");
      review.put("eateryId", "1");
      review.put("message", "hello");
      review.put("rating", "4");

      this.mockMvc.perform(
              post("/diner/createreview")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", token_diner)
                      .content(String.valueOf(new JSONObject(review)))
      );

      this.mockMvc.perform(
              MockMvcRequestBuilders
                      .delete("/diner/removereview")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", token_diner)
                      .content(String.valueOf(new JSONObject(review)))
      )
              .andExpect(status().is4xxClientError());
  }

  @Test
  public void testEditReview_validInput() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();

      Map<String, String> diner = new HashMap<>();
      diner.put("alias", "diner1");
      diner.put("email", "diner1@gmail.com");
      diner.put("address", "Sydney");
      diner.put("password", "12rwqeDsad@");

       String result = this.mockMvc.perform(
        post("/register/diner")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(diner)))
      )
        .andReturn()
        .getResponse()
        .getContentAsString();

      JSONObject data = new JSONObject(result);
      String token_diner = data.getJSONObject("data").getString("token");

      Map<String, String> eatery = new HashMap<>();
      eatery.put("alias", "eatery1");
      eatery.put("email", "eatery1@gmail.com");
      eatery.put("address", "Sydney");
      eatery.put("password", "12rwqeDsad@");

       result = this.mockMvc.perform(
        post("/register/eatery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(new JSONObject(eatery)))
      )
      .andReturn()
      .getResponse()
      .getContentAsString();

        data = new JSONObject(result);
      String token_eatery = data.getJSONObject("data").getString("token");
      
      Map<String,String> voucher1 = new HashMap<>();
      voucher1.put("eateryId","1");
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
          .header("Authorization", token_eatery)
          .content(String.valueOf(new JSONObject(voucher1)))
      )
      .andExpect(status().isOk());

      this.mockMvc.perform(
        post("/diner/book")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .param("id", "0")
      )
      .andExpect(status().isOk());
      
       Map<String, String> review1 = new HashMap<>();
      review1.put("dinerId", "0");
      review1.put("eateryId", "1");
      review1.put("message", "hello");
      review1.put("rating", "4");

       this.mockMvc.perform(
        post("/diner/createreview")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token_diner)
                .content(String.valueOf(new JSONObject(review1)))
      );

       Map<String, String> review2 = new HashMap<>();
      review2.put("id", "0");
      review2.put("dinerId", "0");
      review2.put("eateryId", "1");
      review2.put("message", "hahahaha");
      review2.put("rating", "2");

       this.mockMvc.perform(
        MockMvcRequestBuilders
        .post("/diner/editreview")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .content(String.valueOf(new JSONObject(review2)))
      )
        .andExpect(status().isOk());
  }

  @Test
  public void testEditReview_invalidToken() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();
      
      Map<String, String> diner = new HashMap<>();
      diner.put("alias", "diner1");
      diner.put("email", "diner1@gmail.com");
      diner.put("address", "Sydney");
      diner.put("password", "12rwqeDsad@");

       String result = this.mockMvc.perform(
        post("/register/diner")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(diner)))
      )
        .andReturn()
        .getResponse()
        .getContentAsString();

      JSONObject data = new JSONObject(result);
      String token_diner = data.getJSONObject("data").getString("token");

      Map<String, String> eatery = new HashMap<>();
      eatery.put("alias", "eatery1");
      eatery.put("email", "eatery1@gmail.com");
      eatery.put("address", "Sydney");
      eatery.put("password", "12rwqeDsad@");

       result = this.mockMvc.perform(
        post("/register/eatery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(new JSONObject(eatery)))
      )
      .andReturn()
      .getResponse()
      .getContentAsString();

        data = new JSONObject(result);
      String token_eatery = data.getJSONObject("data").getString("token");
      
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
          .header("Authorization", token_eatery)
          .content(String.valueOf(new JSONObject(voucher1)))
      )
      .andExpect(status().isOk());

      this.mockMvc.perform(
        post("/diner/book")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .param("id", "0")
      )
      .andExpect(status().isOk());

      Map<String, String> review1 = new HashMap<>();
      review1.put("id", "0");
      review1.put("dinerId", "0");
      review1.put("eateryId", "1");
      review1.put("message", "hello");
      review1.put("rating", "4");

      this.mockMvc.perform(
              post("/diner/createreview")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", token_diner)
                      .content(String.valueOf(new JSONObject(review1)))
      );

      Map<String, String> review2 = new HashMap<>();
      review2.put("id", "0");
      review2.put("dinerId", "0");
      review2.put("eateryId", "1");
      review2.put("message", "hahahaha");
      review2.put("rating", "2");

      this.mockMvc.perform(
              MockMvcRequestBuilders
                      .post("/diner/editreview")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "InvalidToken")
                      .content(String.valueOf(new JSONObject(review2)))
      )
              .andExpect(status().is4xxClientError());
  }

  @Test
  public void testEditReview_invalidEateryId() throws Exception {
  this.userRepository.deleteAll();
  this.reviewRepository.deleteAll();
  this.voucherRepository.deleteAll();
  this.repeatVoucherRepository.deleteAll();
  this.bookingRecordRepository.deleteAll();
      
      Map<String, String> diner = new HashMap<>();
      diner.put("alias", "diner1");
      diner.put("email", "diner1@gmail.com");
      diner.put("address", "Sydney");
      diner.put("password", "12rwqeDsad@");

       String result = this.mockMvc.perform(
        post("/register/diner")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(diner)))
      )
        .andReturn()
        .getResponse()
        .getContentAsString();

      JSONObject data = new JSONObject(result);
      String token_diner = data.getJSONObject("data").getString("token");

      Map<String, String> eatery = new HashMap<>();
      eatery.put("alias", "eatery1");
      eatery.put("email", "eatery1@gmail.com");
      eatery.put("address", "Sydney");
      eatery.put("password", "12rwqeDsad@");

       result = this.mockMvc.perform(
        post("/register/eatery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(new JSONObject(eatery)))
      )
      .andReturn()
      .getResponse()
      .getContentAsString();

        data = new JSONObject(result);
      String token_eatery = data.getJSONObject("data").getString("token");
      
      Map<String,String> voucher1 = new HashMap<>();
      voucher1.put("eateryId","1");
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
          .header("Authorization", token_eatery)
          .content(String.valueOf(new JSONObject(voucher1)))
      )
      .andExpect(status().isOk());

      this.mockMvc.perform(
        post("/diner/book")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .param("id", "0")
      )
      .andExpect(status().isOk());

      Map<String, String> review1 = new HashMap<>();
      review1.put("id", "0");
      review1.put("dinerId", "0");
      review1.put("eateryId", "1");
      review1.put("message", "hello");
      review1.put("rating", "4");

      this.mockMvc.perform(
              post("/diner/createreview")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", token_diner)
                      .content(String.valueOf(new JSONObject(review1)))
      );

      Map<String, String> review2 = new HashMap<>();
      review2.put("id", "0");
      review2.put("dinerId", "0");
      review2.put("eateryId", "10");
      review2.put("message", "hahahaha");
      review2.put("rating", "");

      this.mockMvc.perform(
              MockMvcRequestBuilders
                      .post("/diner/editreview")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", token_diner)
                      .content(String.valueOf(new JSONObject(review2)))
      )
              .andExpect(status().is4xxClientError());
  }

  @Test
  public void testEditReview_invalidRating() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();
      
      Map<String, String> diner = new HashMap<>();
      diner.put("alias", "diner1");
      diner.put("email", "diner1@gmail.com");
      diner.put("address", "Sydney");
      diner.put("password", "12rwqeDsad@");

       String result = this.mockMvc.perform(
        post("/register/diner")
        .contentType(MediaType.APPLICATION_JSON)
        .content(String.valueOf(new JSONObject(diner)))
      )
        .andReturn()
        .getResponse()
        .getContentAsString();

      JSONObject data = new JSONObject(result);
      String token_diner = data.getJSONObject("data").getString("token");

      Map<String, String> eatery = new HashMap<>();
      eatery.put("alias", "eatery1");
      eatery.put("email", "eatery1@gmail.com");
      eatery.put("address", "Sydney");
      eatery.put("password", "12rwqeDsad@");

       result = this.mockMvc.perform(
        post("/register/eatery")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(new JSONObject(eatery)))
      )
      .andReturn()
      .getResponse()
      .getContentAsString();

        data = new JSONObject(result);
      String token_eatery = data.getJSONObject("data").getString("token");
      
      Map<String,String> voucher1 = new HashMap<>();
      voucher1.put("eateryId","1");
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
          .header("Authorization", token_eatery)
          .content(String.valueOf(new JSONObject(voucher1)))
      )
      .andExpect(status().isOk());

      this.mockMvc.perform(
        post("/diner/book")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .param("id", "0")
      )
      .andExpect(status().isOk());

      Map<String, String> review1 = new HashMap<>();
      review1.put("id", "0");
      review1.put("dinerId", "0");
      review1.put("eateryId", "1");
      review1.put("message", "hello");
      review1.put("rating", "4");

      this.mockMvc.perform(
              post("/diner/createreview")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", token_diner)
                      .content(String.valueOf(new JSONObject(review1)))
      );

      Map<String, String> review2 = new HashMap<>();
      review2.put("id", "0");
      review2.put("dinerId", "0");
      review2.put("eateryId", "1");
      review2.put("message", "hahahaha");
      review2.put("rating", "20");

      this.mockMvc.perform(
              MockMvcRequestBuilders
                      .post("/diner/editreview")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", token_diner)
                      .content(String.valueOf(new JSONObject(review2)))
      )
              .andExpect(status().is4xxClientError());
  }

  @Test
  void listReviewsTest1() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();

        Map<String, String> diner = new HashMap<>();
        diner.put("alias", "diner1");
        diner.put("email", "diner1@gmail.com");
        diner.put("address", "Sydney");
        diner.put("password", "12rwqeDsad@");
  
         String result = this.mockMvc.perform(
          post("/register/diner")
          .contentType(MediaType.APPLICATION_JSON)
          .content(String.valueOf(new JSONObject(diner)))
        )
          .andReturn()
          .getResponse()
          .getContentAsString();
  
        JSONObject data = new JSONObject(result);
        String token_diner = data.getJSONObject("data").getString("token");
  
        Map<String, String> eatery = new HashMap<>();
        eatery.put("alias", "eatery1");
        eatery.put("email", "eatery1@gmail.com");
        eatery.put("address", "Sydney");
        eatery.put("password", "12rwqeDsad@");
  
         result = this.mockMvc.perform(
          post("/register/eatery")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(String.valueOf(new JSONObject(eatery)))
        )
        .andReturn()
        .getResponse()
        .getContentAsString();
  
          data = new JSONObject(result);
        String token_eatery = data.getJSONObject("data").getString("token");
        
        Map<String,String> voucher1 = new HashMap<>();
        voucher1.put("eateryId","1");
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
            .header("Authorization", token_eatery)
            .content(String.valueOf(new JSONObject(voucher1)))
        )
        .andExpect(status().isOk());
  
        this.mockMvc.perform(
          post("/diner/book")
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", token_diner)
          .param("id", "0")
        )
        .andExpect(status().isOk());

    Map<String, String> review1 = new HashMap<>();
    review1.put("id","0");
    review1.put("dinerId","0");
    review1.put("eateryId","1");
    review1.put("message","hello");
    review1.put("rating","4");

    this.mockMvc.perform(
      post("/diner/createreview")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .content(String.valueOf(new JSONObject(review1)))
    );

    this.mockMvc.perform(
        MockMvcRequestBuilders
        .get("/health/list/reviews")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
    )
    .andExpect(status().isOk());
  }

  @Test
  void listReviewsTest2() throws Exception {
    this.userRepository.deleteAll();
    this.reviewRepository.deleteAll();
    this.voucherRepository.deleteAll();
    this.repeatVoucherRepository.deleteAll();
    this.bookingRecordRepository.deleteAll();

    Map<String, String> diner = new HashMap<>();
    diner.put("alias", "diner1");
    diner.put("email", "diner1@gmail.com");
    diner.put("address", "Sydney");
    diner.put("password", "12rwqeDsad@");

     String result = this.mockMvc.perform(
      post("/register/diner")
      .contentType(MediaType.APPLICATION_JSON)
      .content(String.valueOf(new JSONObject(diner)))
    )
      .andReturn()
      .getResponse()
      .getContentAsString();

    JSONObject data = new JSONObject(result);
    String token_diner = data.getJSONObject("data").getString("token");

    Map<String, String> eatery = new HashMap<>();
    eatery.put("alias", "eatery1");
    eatery.put("email", "eatery1@gmail.com");
    eatery.put("address", "Sydney");
    eatery.put("password", "12rwqeDsad@");

     result = this.mockMvc.perform(
      post("/register/eatery")
              .contentType(MediaType.APPLICATION_JSON)
              .content(String.valueOf(new JSONObject(eatery)))
    )
    .andReturn()
    .getResponse()
    .getContentAsString();

      data = new JSONObject(result);
    String token_eatery = data.getJSONObject("data").getString("token");
    
    Map<String,String> voucher1 = new HashMap<>();
    voucher1.put("eateryId","1");
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
        .header("Authorization", token_eatery)
        .content(String.valueOf(new JSONObject(voucher1)))
    )
    .andExpect(status().isOk());

    this.mockMvc.perform(
      post("/diner/book")
      .contentType(MediaType.APPLICATION_JSON)
      .header("Authorization", token_diner)
      .param("id", "0")
    )
    .andExpect(status().isOk());

    Map<String, String> review1 = new HashMap<>();
    review1.put("id","0");
    review1.put("dinerId","0");
    review1.put("eateryId","1");
    review1.put("message","hello");
    review1.put("rating","4");

    this.mockMvc.perform(
      post("/diner/createreview")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .content(String.valueOf(new JSONObject(review1)))
    )
    .andExpect(status().isOk());;

    Map<String, String> review2 = new HashMap<>();
    review2.put("id","0");
    review2.put("dinerId","0");
    review2.put("eateryId","1");
    review2.put("message","hahahaha");
    review2.put("rating","20");

    this.mockMvc.perform(
      post("/diner/createreview")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .content(String.valueOf(new JSONObject(review2)))
    )
    .andExpect(status().is4xxClientError());;

    Map<String, String> review3 = new HashMap<>();
    review3.put("id","0");
    review3.put("dinerId","0");
    review3.put("eateryId","1");
    review3.put("message","review3yes!");
    review3.put("rating","20");

    this.mockMvc.perform(
      post("/diner/createreview")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .content(String.valueOf(new JSONObject(review3)))
    )
    .andExpect(status().is4xxClientError());;

    this.mockMvc.perform(
      MockMvcRequestBuilders
      .get("/health/list/reviews")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", token_diner)
        .content(String.valueOf(new JSONObject(review2)))
    )
    .andExpect(status().isOk());
  }
}
