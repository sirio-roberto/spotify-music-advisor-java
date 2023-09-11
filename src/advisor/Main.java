package advisor;

public class Main {
    public static void main(String[] args) {
        if (args.length >= 2) {
            for (int i = 0; i < args.length; i++) {
                if ("-access".equals(args[i])) {
                    Config.ACCESS_SERVER_POINT = args[i + 1];
                }
            }
        }

        new App().advise();
    }
}
