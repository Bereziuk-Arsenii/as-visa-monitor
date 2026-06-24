//package com.example.visa.captcha;
//
//import com.twocaptcha.TwoCaptcha;
//import com.twocaptcha.captcha.ReCaptcha;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CaptchaSolver {
//    public String solve(String captchaApiKey, String siteKey, String siteUrl) {
//        TwoCaptcha solver = new TwoCaptcha(captchaApiKey);
//        ReCaptcha captcha = new ReCaptcha();
//        captcha.setSiteKey(siteKey);
//        captcha.setUrl(siteUrl);
//        try {
//            solver.solve(captcha);
//            System.out.println("Captcha solved: " + captcha.getCode());
//            return captcha.getCode();
//        } catch (Exception e) {
//            System.out.println("Error occurred: " + e.getMessage());
//            return null;
//        }
//    }
//}


//String solvedToken = captchaSolver.solve(siteConfig.getCaptchaApiKey(),
//        siteConfig.getSiteKey(),
//        siteConfig.getUrl());
//
//            if (solvedToken != null) {
//
//        page.evaluate("document.getElementById('g-recaptcha-response').innerHTML = '"
//                              + solvedToken + "';");
//
//Locator check = page.locator("button[type='submit']");
//                check.click();
//
//                page.waitForTimeout(2000);
//
//Locator alertBox = page.locator("p[class^='_successMessage']");
//
//                if (alertBox.isVisible()) {
//        System.out.println(alertBox.innerText());
//        } else {
//        System.out.println("No error alert found. Captcha likely passed!");
//                }
//                        } else {
//                        System.out.println("Failed to obtain a valid token from 2Captcha.");
//            }