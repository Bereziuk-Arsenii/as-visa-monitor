package com.example.visa;
import com.example.visa.captcha.CaptchaSolver;
import com.microsoft.playwright.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisaAutomationService implements CommandLineRunner {

    private final SiteConfig siteConfig;
    private final CaptchaSolver captchaSolver;

    @Override
    public void run(String ... args) throws Exception {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();

            Page page = browser.newPage();
            page.navigate(siteConfig.getUrl());

            String solvedToken = captchaSolver.solve(siteConfig.getCaptchaApiKey(),
                    siteConfig.getSiteKey(),
                    siteConfig.getUrl());

            if (solvedToken != null) {

                page.evaluate("document.getElementById('g-recaptcha-response').innerHTML = '"
                        + solvedToken + "';");

                Locator check = page.locator("button[type='submit']");
                check.click();

                page.waitForTimeout(2000);

                Locator alertBox = page.locator("p[class^='_successMessage']");

                if (alertBox.isVisible()) {
                    System.out.println(alertBox.innerText());
                } else {
                    System.out.println("No error alert found. Captcha likely passed!");
                }
            } else {
                System.out.println("Failed to obtain a valid token from 2Captcha.");
            }

            browser.close();
        }
    }
}

