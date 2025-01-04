package server.services;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomService {
    public String getRandomString(int length) {
        String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";

        Random random = new Random();

        StringBuilder randomString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(lowercaseLetters.length());
            randomString.append(lowercaseLetters.charAt(index));
        }

        return randomString.toString();
    }
}
