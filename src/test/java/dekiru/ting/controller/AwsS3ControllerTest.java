package dekiru.ting.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import dekiru.ting.BaseTest;
import dekiru.ting.service.AwsS3Service;

public class AwsS3ControllerTest extends BaseTest {
    @MockBean
    private AwsS3Service awsS3Service;

    @Test
    public void shouldReturn400WhenGetPresignedUrlAndParametersAreInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/s3/presignedUrl?permission=r"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/s3/presignedUrl?permission=a&fileName=abc.mp3"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldGetPresignedUrl() throws Exception {
        Mockito.when(awsS3Service.getPresignedUrl("r", "abc.mp3")).thenReturn("presigned.mp3");

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/s3/presignedUrl?permission=r&fileName=abc.mp3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals("presigned.mp3", body);
    }
}
