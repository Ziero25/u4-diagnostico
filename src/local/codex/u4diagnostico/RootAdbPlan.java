package local.codex.u4diagnostico;

/** Fixed ADB-only commands. Values from the device are validated before shell use. */
public final class RootAdbPlan {
    private RootAdbPlan() {}
    public static final String READ = "id; printf '\\nU4_ADB_ENABLED='; settings get global adb_enabled; "
            + "printf 'U4_TCP_PORT='; getprop service.adb.tcp.port; "
            + "printf 'U4_SERVICE='; getprop init.svc.adbd";
    public static final String ENABLE = "setprop service.adb.tcp.port 5555 && "
            + "settings put global adb_enabled 1 && setprop ctl.restart adbd";

    public static final class State {
        public final String enabled;
        public final String port;
        public final String service;
        public State(String enabled, String port, String service) {
            if (!enabled.matches("0|1|null")) throw new IllegalArgumentException("Estado adb_enabled desconhecido");
            if (!validPort(port)) throw new IllegalArgumentException("Porta ADB inválida");
            if (!service.matches("running|stopped")) throw new IllegalArgumentException("Serviço ADB ausente ou em transição");
            this.enabled = enabled; this.port = port; this.service = service;
        }
        public String restoreCommand() {
            String setting = enabled.equals("null") ? "settings delete global adb_enabled"
                    : "settings put global adb_enabled " + enabled;
            return "setprop service.adb.tcp.port '" + port + "' && " + setting
                    + " && setprop ctl." + (service.equals("running") ? "restart" : "stop") + " adbd";
        }
        public boolean same(State other) {
            return enabled.equals(other.enabled) && port.equals(other.port) && service.equals(other.service);
        }
    }

    private static boolean validPort(String value) {
        if (value.isEmpty() || value.equals("-1")) return true;
        if (!value.matches("[0-9]{1,5}")) return false;
        int port = Integer.parseInt(value); return port >= 0 && port <= 65535;
    }

    public static State parse(String value) {
        if (!value.matches("(?s).*\\buid=0(?:\\(|\\s).*")) throw new IllegalArgumentException("Root não confirmado");
        return new State(field(value, "U4_ADB_ENABLED"), field(value, "U4_TCP_PORT"), field(value, "U4_SERVICE"));
    }

    private static String field(String value, String key) {
        for (String line : value.split("\\r?\\n", -1)) {
            if (line.startsWith(key + "=")) return line.substring(key.length() + 1).trim();
        }
        throw new IllegalArgumentException("Diagnóstico root incompleto: " + key);
    }
}
