package advisor;

public class Main {
    public static void main(String[] args) {
        if (args.length >= 2) {
            for (int i = 0; i < args.length; i++) {
                if ("-access".equals(args[i])) {
                    new App(args[i + 1]).run();
                }
            }
        } else {
            new App().run();
        }
    }
}
