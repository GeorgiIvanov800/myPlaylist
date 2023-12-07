package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.model.entity.UserActivationLinkEntity;
import org.myplaylist.myplaylist.model.event.UserRegisterEvent;
import org.myplaylist.myplaylist.repository.UserActivationLinkRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.service.EmailService;
import org.myplaylist.myplaylist.service.UserActivationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;

@Service
public class UserActivationServiceImpl implements UserActivationService {
    private static final String ACTIVATION_LINK_SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ACTIVATION_CODE_LENGTH = 20;
    private final EmailService emailService;
    private final UserActivationLinkRepository userActivationLinkRepository;
    private final UserRepository userRepository;

    public UserActivationServiceImpl(EmailService emailService,
                                     UserActivationLinkRepository userActivationLinkRepository,
                                     UserRepository userRepository) {

        this.emailService = emailService;
        this.userActivationLinkRepository = userActivationLinkRepository;
        this.userRepository = userRepository;
    }

    @Override
    @EventListener(UserRegisterEvent.class)
    public void userRegistered(UserRegisterEvent event) {
        String activationLink = createActivationLink(event.getUserEmail());
        emailService.sendRegistrationEmail(
                event.getUserEmail(),
                event.getUserName(),
                activationLink);
    }

    @Override
    public void cleanUpObsoleteActivationLinks() {
        //TODO: Implement
    }

    @Override
    public String createActivationLink(String userEmail) {
        String activationLink = generateActivationLink();

        UserActivationLinkEntity userActivationLinkEntity = new UserActivationLinkEntity();
        userActivationLinkEntity.setActivationLink(activationLink);
        userActivationLinkEntity.setCreated(Instant.now());
        userActivationLinkEntity.setUser(userRepository.findByEmail(userEmail).orElseThrow());

        userActivationLinkRepository.save(userActivationLinkEntity);

        return activationLink;
    }

    @Override
    public UserActivationLinkEntity getToken(String token) {
        return userActivationLinkRepository.findByActivationLink(token);
    }


    private static String generateActivationLink() {

        StringBuilder activationLink = new StringBuilder();
        Random random = new SecureRandom();

        for (int i = 0; i < ACTIVATION_CODE_LENGTH; i++) {
            int randInx = random.nextInt(ACTIVATION_LINK_SYMBOLS.length());
            activationLink.append(ACTIVATION_LINK_SYMBOLS.charAt(randInx));
        }

        return activationLink.toString();
    }

}
