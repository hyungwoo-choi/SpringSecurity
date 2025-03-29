package csieReserve.util;

import java.util.Random;

public class RandomPasswordGenerator {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";

    public static String generatePassword(int length) {
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}

