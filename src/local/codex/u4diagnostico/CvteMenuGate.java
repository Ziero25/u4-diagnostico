package local.codex.u4diagnostico;

/** Values verified against the factory-menu APK supplied by this projector's owner. */
public final class CvteMenuGate {
    public static final String PACKAGE = "com.cvte.fac.menu";
    public static final String SERVICE = "com.cvte.fac.menu.app.TvMenuWindowManagerService";
    public static final String EXTRA = "com.cvte.fac.menu.commmand";
    public static final String OPEN = "com.cvte.fac.menu.commmand.factory_menu";
    public static final String CLOSE = "com.cvte.fac.menu.commmand.close_factory_menu";
    private static final String VERSION = "1.2.70";
    private static final String SIGNER = "8152aa9954af235d7aa208b4b7df2d8cac8b2dc44a33d8d9c3ca3e05ca2000ca";
    private CvteMenuGate() {}
    public static boolean accepts(String version, String signer, boolean exported, boolean enabled, String permission) {
        return VERSION.equals(version) && SIGNER.equals(signer) && exported && enabled && permission == null;
    }
}
