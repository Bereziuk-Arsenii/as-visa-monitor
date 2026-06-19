package com.example.visa.captcha;

import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.ReCaptcha;
import org.springframework.stereotype.Component;

@Component
public class CaptchaSolver {
    public String solve(String captchaApiKey, String siteKey, String siteUrl) {
        TwoCaptcha solver = new TwoCaptcha(captchaApiKey);
        ReCaptcha captcha = new ReCaptcha();
        captcha.setSiteKey(siteKey);
        captcha.setUrl(siteUrl);
        try {
            solver.solve(captcha);
            System.out.println("Captcha solved: " + captcha.getCode());
            return captcha.getCode();
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            return null;
        }
    }
}
