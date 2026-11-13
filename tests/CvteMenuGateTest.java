import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import local.codex.u4diagnostico.CvteMenuGate;

public class CvteMenuGateTest {
    private static int count;
    private static void check(boolean valid, String name) {
        if (!valid) throw new AssertionError(name); count++;
    }
    public static void main(String[] args) throws Exception {
        Properties observed = new Properties();
        try (Reader reader = Files.newBufferedReader(Paths.get(args[0]), StandardCharsets.UTF_8)) { observed.load(reader); }
        String version = observed.getProperty("version");
        String signer = observed.getProperty("signer");
        check(CvteMenuGate.accepts(version, signer, true, true, null), "Allows the reviewed exported CVTE service");
        check(!CvteMenuGate.accepts(version, signer, false, true, null), "Blocks a non-exported service");
        check(!CvteMenuGate.accepts(version, signer, true, false, null), "Blocks a disabled service");
        check(!CvteMenuGate.accepts(version, signer, true, true, "vendor.signature.PERMISSION"), "Blocks an unreviewed service permission");
        check(!CvteMenuGate.accepts("1.2.71", signer, true, true, null), "Blocks an unreviewed menu version");
        check(!CvteMenuGate.accepts(version, signer.replace('8','7'), true, true, null), "Blocks a different signer");
        check(!CvteMenuGate.accepts(null, signer, true, true, null), "Blocks incomplete metadata");
        check(CvteMenuGate.EXTRA.equals(observed.getProperty("extra")), "Matches the observed service extra contract");
        check(CvteMenuGate.OPEN.equals(observed.getProperty("open")), "Emits the reviewed open command");
        check(CvteMenuGate.CLOSE.equals(observed.getProperty("close")), "Emits the reviewed close command");
        check(CvteMenuGate.SERVICE.equals(observed.getProperty("service")), "Targets the reviewed component");
        System.out.println("Passed: " + count + " CVTE firmware identity and command checks.");
    }
}
