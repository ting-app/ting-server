package ting.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ting.config.AwsSesConfig;

/**
 * The service that sends mails by Amazon Simple Email Service.
 */
@Service
public class AwsSesService {
    @Autowired
    private AwsSesConfig awsSesConfig;

    /**
     * Send a mail to the specified address.
     *
     * @param to      Recipient's mail address
     * @param title   Title of the mail
     * @param content Content of the mail
     */
    public void send(String to, String title, String content) {
        if (StringUtils.isBlank(to) || StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("to/title/content 都不能为空");
        }

        AmazonSimpleEmailService amazonSimpleEmailService =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(awsSesConfig.getRegion())
                        .build();
        Destination destination = new Destination().withToAddresses(to);
        Body body = new Body().withHtml(
                new Content().withCharset("UTF-8").withData(content));
        Content subject = new Content().withCharset("UTF-8")
                .withData(title);
        Message message = new Message().withBody(body)
                .withSubject(subject);
        SendEmailRequest sendEmailRequest = new SendEmailRequest()
                .withDestination(destination)
                .withMessage(message)
                .withSource(awsSesConfig.getFromAddress());
        amazonSimpleEmailService.sendEmail(sendEmailRequest);
    }
}
