package util;


import java.util.Random;

final public class UsernameGenerator {
    private static final String[] DEFAULT_USERNAMES = {"Alpha", "Beta", "Gamma", "Delta", "Omega", "Sphinx", "Phoenix", "Vortex", "Nebula", "Aurora"};
    private static int userCounter = 1;

    public static String generateUniqueRandomUsername() {
        Random random = new Random();
        int index = random.nextInt(DEFAULT_USERNAMES.length);
        String username = DEFAULT_USERNAMES[index] + userCounter;
        userCounter++;
        return username;
    }
}
