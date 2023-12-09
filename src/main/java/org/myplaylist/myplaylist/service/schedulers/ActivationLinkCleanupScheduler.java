package org.myplaylist.myplaylist.service.schedulers;

import org.myplaylist.myplaylist.service.UserActivationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Component
public class ActivationLinkCleanupScheduler {

    private final UserActivationService userActivationService;

    public ActivationLinkCleanupScheduler(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivationLinkCleanupScheduler.class);
    @Scheduled(cron = "0 0 */12 * * ?") // clean the activations links every 12 hours
    public void cleanUp() {
        LOGGER.info("Clean up activation links triggered {}", LocalDateTime.now());
        userActivationService.cleanUpObsoleteActivationLinks();
    }

}
