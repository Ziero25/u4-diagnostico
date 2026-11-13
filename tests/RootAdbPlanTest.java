import local.codex.u4diagnostico.RootAdbPlan;

public class RootAdbPlanTest {
    private static int count;
    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError(name); count++;
    }
    private static void reject(Runnable operation, String name) {
        try { operation.run(); } catch (IllegalArgumentException expected) { count++; return; }
        throw new AssertionError(name);
    }
    public static void main(String[] arguments) {
        RootAdbPlan.State stopped = RootAdbPlan.parse("uid=0(root) gid=0(root)\nU4_ADB_ENABLED=0\nU4_TCP_PORT=\nU4_SERVICE=stopped\n");
        check(stopped.port.isEmpty(), "Preserves an initially unset port");
        check(stopped.restoreCommand().contains("tcp.port ''"), "Clears a port that was originally unset");
        check(stopped.restoreCommand().contains("ctl.stop adbd"), "Restores originally stopped service");
        RootAdbPlan.State running = new RootAdbPlan.State("1", "4444", "running");
        check(running.restoreCommand().contains("tcp.port '4444'"), "Preserves a nondefault original port");
        check(running.restoreCommand().contains("ctl.restart adbd"), "Restarts the original running service");
        RootAdbPlan.State unset = new RootAdbPlan.State("null", "-1", "stopped");
        check(unset.restoreCommand().contains("settings delete global adb_enabled"), "Removes a previously absent setting");
        reject(() -> RootAdbPlan.parse("uid=2000(shell)\nU4_ADB_ENABLED=0\nU4_TCP_PORT=5555\nU4_SERVICE=stopped"), "Rejects nonroot output");
        reject(() -> RootAdbPlan.parse("uid=0(root)\nU4_ADB_ENABLED=0\nU4_SERVICE=stopped"), "Rejects incomplete snapshot");
        reject(() -> new RootAdbPlan.State("0", "5555'; reboot; '", "stopped"), "Rejects shell injection");
        reject(() -> new RootAdbPlan.State("0", "65536", "stopped"), "Rejects out of range port");
        reject(() -> new RootAdbPlan.State("0", "5555", "restarting"), "Rejects unstable snapshot");
        reject(() -> new RootAdbPlan.State("x", "5555", "running"), "Rejects malformed enabled state");
        check(!RootAdbPlan.ENABLE.contains("persist."), "Does not alter persistent TCP or USB properties");
        check(!running.same(stopped), "Detects failed restoration");
        check(running.same(new RootAdbPlan.State("1", "4444", "running")), "Confirms matching restoration");
        System.out.println("Passed: " + count + " ADB state and rollback checks.");
    }
}
