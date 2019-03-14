package ch.uzh.ifi.seal.soprafs19.service;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.uzh.ifi.seal.soprafs19.controller.UserController;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.writer.BeansMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;


import java.util.Date;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class WebMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;
    //private byte[] requestJson;


    @Test
    public void createUserShouldReturnURLFromService() throws Exception {
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setBirthDay(new Date());
        testUser.setPassword("testPassword");
        //ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        //ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        //String requestJson=ow.writeValueAsString(testUser);
        //when(service.createUser(testUser)).thenReturn(testUser);
       // this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON_UTF8).content()).andExpect((status().isCreated()));
                //.andExpect(content().string(containsString("http://%s:%s/users/%s")));
    }

}