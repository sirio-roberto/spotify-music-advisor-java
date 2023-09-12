package advisor;

public class Main {
    public static void main(String[] args) {
        if (args.length >= 2) {
            for (int i = 0; i < args.length; i++) {
                if ("-access".equals(args[i])) {
                    Config.AUTH_SERVER_PATH = args[i + 1];
                }

                if ("-resource".equals(args[i])) {
                    Config.API_SERVER_PATH = args[i + 1];
                }
            }
        }

        new App().advise();
    }
}
