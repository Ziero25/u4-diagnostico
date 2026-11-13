package local.codex.u4diagnostico;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Offline helper. Existing root is used only after an explicit local action, for ADB only. */
public class MainActivity extends Activity {
    private static final String SECURE = "android.permission.WRITE_SECURE_SETTINGS";
    private static final long FILE_LIMIT = 40L * 1024 * 1024;
    private static final long TOTAL_LIMIT = 150L * 1024 * 1024;
    private TextView status;
    private Button exportButton;
    private String settingsOwner;
    private boolean exporting;
    private boolean rootBusy;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        ResolveInfo owner = getPackageManager().resolveActivity(new Intent(Settings.ACTION_SETTINGS), 0);
        settingsOwner = owner == null ? "" : owner.activityInfo.packageName;
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 18, 32, 18);
        layout.setBackgroundColor(Color.rgb(17, 24, 39));
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.addView(layout);
        setContentView(scroll);
        TextView title = text("U4 Diagnóstico 1.2 — acesso CVTE", 28);
        layout.addView(title);
        layout.addView(text("Aplicativo local criado para este diagnóstico. Não é da BYINTEK.\n"
                + "Não acessa a internet. Não troca firmware nem remove aplicativos.", 16));
        layout.addView(text(Build.MODEL + " • Android " + Build.VERSION.RELEASE + " • " + Build.BOARD
                + "\nBuild: " + Build.DISPLAY, 15));
        layout.addView(text("Caminho identificado no APK deste projetor:\n"
                + "Abrir menu CVTE → Debug → ADB Enable → On → Voltar.\n"
                + "Use somente a opção ADB. As demais opções técnicas incluem reset e calibração.", 17));
        Button cvteOpen = button("Abrir menu CVTE identificado no seu projetor");
        layout.addView(cvteOpen);
        cvteOpen.setOnClickListener(v -> confirmCvteMenu());
        Button cvteClose = button("Fechar menu CVTE");
        layout.addView(cvteClose);
        cvteClose.setOnClickListener(v -> sendCvteMenu(false));
        Button direct = button("1. Tentar ativar ADB");
        if (checkSelfPermission(SECURE) == PackageManager.PERMISSION_GRANTED) layout.addView(direct);
        direct.setOnClickListener(v -> tryEnableAdb());
        Button developer = button("2. Abrir tela de depuração disponível");
        // The CVTE route above replaces the generic Settings redirect for this firmware.
        developer.setOnClickListener(v -> openDeveloper());
        exportButton = button("3. Gerar diagnóstico para análise");
        layout.addView(exportButton);
        exportButton.setOnClickListener(v -> requestExport());
        Button disableAdb = button("Desativar ADB ao terminar (se permitido)");
        if (checkSelfPermission(SECURE) == PackageManager.PERMISSION_GRANTED) layout.addView(disableAdb);
        disableAdb.setOnClickListener(v -> setAdb(false));
        Button undoRoot = button("Desfazer alterações do modo avançado");
        if (rootPrefs().getBoolean("pending", false)) layout.addView(undoRoot);
        undoRoot.setOnClickListener(v -> confirmRoot(true));
        status = text("Pronto. Use Abrir menu CVTE e altere somente Debug → ADB Enable → On.\n"
                + "O app chama o serviço original de fábrica; não exige root e não ativa ADB automaticamente. "
                + "Se o menu não abrir ou não mostrar ADB, use Gerar diagnóstico.", 16);
        layout.addView(status);
        cvteOpen.requestFocus();
    }

    private TextView text(String value, int size) {
        TextView view = new TextView(this);
        view.setText(value); view.setTextSize(size); view.setTextColor(Color.WHITE);
        view.setPadding(0, 5, 0, 5); return view;
    }

    private Button button(String value) {
        Button b = new Button(this); b.setText(value); b.setTextSize(17);
        b.setFocusable(true); b.setFocusableInTouchMode(true);
        b.setAllCaps(false); b.setMinimumHeight(52); return b;
    }

    private void message(String value) {
        status.setText(value);
        status.post(() -> status.requestRectangleOnScreen(new android.graphics.Rect(
                0, 0, status.getWidth(), Math.min(status.getHeight(), 220)), true));
    }

    private void tryEnableAdb() {
        setAdb(true);
    }

    private void confirmCvteMenu() {
        if (rootBusy || exporting) { message("Aguarde a operação atual terminar."); return; }
        new AlertDialog.Builder(this).setTitle("Abrir menu técnico original da CVTE?")
                .setMessage("O APK enviado contém Debug → ADB Enable. O app abrirá o menu original para você. "
                        + "Altere somente ADB Enable para On e saia com Voltar. "
                        + "Não selecione Shipping Init, Board Init, Aging Mode, painel, motores ou calibração: essas opções podem resetar ou desconfigurar o projetor.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Abrir menu", (d, w) -> sendCvteMenu(true)).show();
    }

    @SuppressWarnings("deprecation")
    private void sendCvteMenu(boolean open) {
        if (rootBusy || exporting) { message("Aguarde a operação atual terminar."); return; }
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo(CvteMenuGate.PACKAGE, PackageManager.GET_SIGNATURES);
            ComponentName component = new ComponentName(CvteMenuGate.PACKAGE, CvteMenuGate.SERVICE);
            ServiceInfo service = pm.getServiceInfo(component, 0);
            boolean trusted = false;
            if (info.signatures != null) for (android.content.pm.Signature signature : info.signatures) {
                byte[] digest = MessageDigest.getInstance("SHA-256").digest(signature.toByteArray());
                StringBuilder hex = new StringBuilder();
                for (byte b : digest) hex.append(String.format(Locale.ROOT, "%02x", b & 255));
                trusted |= CvteMenuGate.accepts(info.versionName, hex.toString(), service.exported,
                        service.enabled && service.applicationInfo.enabled, service.permission);
            }
            if (!trusted) {
                message("A versão, assinatura ou acesso do menu CVTE difere do APK analisado. Nenhum comando foi enviado. Gere um novo diagnóstico."); return;
            }
            if (open && "1".equals(property("sys.factorymenu"))) {
                message("O sistema já indica um menu técnico aberto. Use Voltar no controle ou o botão Fechar menu CVTE antes de tentar abrir novamente."); return;
            }
            // Only these two reviewed display commands are sent. No reset/calibration extras.
            ComponentName started = startService(new Intent().setComponent(component)
                    .putExtra(CvteMenuGate.EXTRA, open ? CvteMenuGate.OPEN : CvteMenuGate.CLOSE));
            if (started == null) throw new IOException("O Android não iniciou o serviço");
            if (open) {
                message("Comando enviado ao menu CVTE original. Se a janela abrir, use somente Debug → ADB Enable → On, "
                        + "depois saia com Voltar e avise na conversa para testar o ADB.\n"
                        + "O envio do comando ainda não confirma que a janela foi exibida.");
            } else message("Comando de fechar enviado. Isso fecha a janela técnica; não desativa o ADB que você tenha ligado.");
        } catch (Exception e) {
            message("O Android não permitiu chamar o menu CVTE: " + e.getClass().getSimpleName() + ": " + e.getMessage()
                    + "\nNenhum comando de reset ou calibração foi enviado.");
        }
    }

    private void setAdb(boolean enable) {
        if (rootBusy || exporting) { message("Aguarde a operação atual terminar."); return; }
        if (rootPrefs().getBoolean("pending", false)) {
            message("Há uma cópia dos valores anteriores do modo avançado. Use Desfazer alterações do modo avançado para restaurar esses valores."); return;
        }
        if (checkSelfPermission(SECURE) != PackageManager.PERMISSION_GRANTED) {
            message("O Android não concedeu a este APK a permissão de alterar a depuração. Nada foi alterado.\n"
                    + "Tente o botão 2 para abrir uma tela de depuração. Se ela não existir, o botão 3 reúne os arquivos para investigar o menu deste firmware.");
            return;
        }
        new AlertDialog.Builder(this).setTitle(enable ? "Ativar depuração ADB?" : "Desativar depuração ADB?")
                .setMessage(enable ? "O firmware concedeu permissão. Esta ação liga apenas a opção adb_enabled. "
                        + "Se o firmware já configurar ADB pela rede, o computador poderá tentar conectar. "
                        + "Nenhuma outra configuração será alterada."
                        : "Esta ação desliga a opção adb_enabled. Ela pode encerrar a conexão com o computador. "
                        + "Nenhuma outra configuração será alterada.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton(enable ? "Ativar" : "Desativar", (dialog, which) -> {
                    try {
                        int before = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0);
                        int requested = enable ? 1 : 0;
                        boolean written = Settings.Global.putInt(getContentResolver(), Settings.Global.ADB_ENABLED, requested);
                        int after = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0);
                        message("adb_enabled antes: " + before + "; depois: " + after
                                + ".\n" + (written && after == requested
                                ? (enable ? "Opção ADB ativada. Peça ao computador para testar a conexão; o acesso pela rede ainda depende do firmware."
                                : "Opção ADB desativada. O fechamento da porta de rede ainda depende do firmware; confirme o resultado com o computador.")
                                : "O sistema não confirmou a alteração. Gere o diagnóstico pelo botão 3."));
                    } catch (Exception e) { message("O sistema bloqueou a operação: " + e.getClass().getSimpleName()
                            + ". Gere o diagnóstico pelo botão 3."); }
                }).show();
    }

    private List<PackageInfo> packages() {
        List<PackageInfo> list = getPackageManager().getInstalledPackages(
                PackageManager.GET_ACTIVITIES | PackageManager.MATCH_DISABLED_COMPONENTS);
        Collections.sort(list, (a, b) -> a.packageName.compareTo(b.packageName));
        return list;
    }

    private boolean candidate(PackageInfo p) {
        String n = p.packageName.toLowerCase(Locale.ROOT);
        String label = String.valueOf(p.applicationInfo.loadLabel(getPackageManager())).toLowerCase(Locale.ROOT);
        if (n.equals(getPackageName()) || n.equals("com.android.providers.settings")
                || n.equals("com.android.settings.intelligence")) return false;
        boolean system = (p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        return n.equals(settingsOwner) || (system && (n.contains("settings") || n.contains("factory")
                || n.contains("engineer") || label.contains("configura") || label.contains("defini")
                || label.equals("ajustes") || label.contains("factory")));
    }

    private void openDeveloper() {
        List<ActivityInfo> choices = new ArrayList<>();
        for (PackageInfo p : packages()) {
            if (!candidate(p) || p.activities == null) continue;
            for (ActivityInfo a : p.activities) {
                String name = a.name.toLowerCase(Locale.ROOT);
                boolean specific = name.contains("developmentsettings") || name.contains("developer")
                        || name.contains("adbsettings") || name.contains("usbdebug");
                if (!specific || !a.exported || !a.enabled || !a.applicationInfo.enabled) continue;
                if (a.permission != null && checkSelfPermission(a.permission) != PackageManager.PERMISSION_GRANTED) continue;
                choices.add(a);
            }
        }
        if (choices.isEmpty()) {
            // A generic vendor Settings redirect is deliberately not launched again.
            ResolveInfo r = getPackageManager().resolveActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS), 0);
            if (r != null && r.activityInfo.exported && r.activityInfo.enabled) {
                String name = r.activityInfo.name.toLowerCase(Locale.ROOT);
                if (name.contains("develop") || name.contains("adb") || name.contains("debug")) choices.add(r.activityInfo);
            }
        }
        if (choices.isEmpty()) {
            message("Não encontrei uma tela específica de depuração acessível a um app comum. "
                    + "O botão 3 permite analisar o aplicativo original de configurações para procurar o procedimento correto.\n"
                    + "Nenhum menu de calibração foi aberto."); return;
        }
        String[] names = new String[choices.size()];
        for (int i = 0; i < names.length; i++) names[i] = choices.get(i).packageName + "\n" + choices.get(i).name;
        new AlertDialog.Builder(this).setTitle("Telas de depuração encontradas")
                .setItems(names, (d, i) -> {
                    ActivityInfo a = choices.get(i);
                    try { startActivity(new Intent().setComponent(new ComponentName(a.packageName, a.name))); }
                    catch (Exception e) { message("O sistema não permitiu abrir: " + a.name + "\n" + e.getClass().getSimpleName()); }
                }).setNegativeButton("Voltar", null).show();
    }

    private void requestExport() {
        if (exporting || rootBusy) { message("Aguarde a operação atual terminar."); return; }
        if (Build.VERSION.SDK_INT > 28) {
            message("Este auxiliar foi preparado para Android 6 a 9. A exportação pública nesta versão do Android não é suportada."); return;
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7); return;
        }
        exporting = true; exportButton.setEnabled(false);
        message("Gerando arquivo… Aguarde. Os APKs são cópias dos programas, sem dados de contas ou senhas.");
        new Thread(() -> {
            try {
                File result = export();
                runOnUiThread(() -> message("Arquivo pronto:\n" + result.getAbsolutePath()
                        + "\n\nCopie U4-diagnostico.zip para o pendrive pelo gerenciador de arquivos e envie esse ZIP na conversa. "
                        + "Ele contém informações técnicas e cópias dos APKs de configurações. Nenhum dado de conta foi copiado."));
            } catch (Exception e) {
                runOnUiThread(() -> message("Não foi possível gerar o ZIP: " + e.getClass().getSimpleName()
                        + ": " + e.getMessage() + "\nNenhuma configuração foi alterada."));
            } finally {
                runOnUiThread(() -> { exporting = false; exportButton.setEnabled(true); });
            }
        }, "u4-export").start();
    }

    @Override public void onRequestPermissionsResult(int request, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(request, permissions, results);
        if (request == 7 && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) requestExport();
        else message("Sem permissão para salvar o ZIP. Os botões de diagnóstico continuam disponíveis. Nada foi alterado.");
    }

    private String property(String key) {
        Process process = null;
        try {
            process = new ProcessBuilder("/system/bin/getprop", key).start();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String value = r.readLine(); return value == null ? "" : value;
            }
        } catch (IOException e) { return "<indisponível>"; }
        finally { if (process != null) process.destroy(); }
    }

    private SharedPreferences rootPrefs() { return getSharedPreferences("root-adb-backup", MODE_PRIVATE); }

    private void confirmRoot(boolean undo) {
        if (rootBusy || exporting) { message("Aguarde a operação atual terminar."); return; }
        if (undo && !rootPrefs().getBoolean("pending", false)) {
            message("Não existe uma alteração pendente do modo avançado para desfazer."); return;
        }
        new AlertDialog.Builder(this).setTitle(undo ? "Restaurar ADB anterior?" : "Tentar root já existente?")
                .setMessage(undo ? "Restaura a opção ADB, a porta temporária e o estado do serviço salvos antes da tentativa. Exige o mesmo acesso root."
                        : "Procura um executável su já instalado e confirma uid=0. Se o gerenciador de root pedir autorização, você decide. "
                        + "Com root, salva os valores anteriores e tenta ligar somente ADB na porta 5555. "
                        + "Isso permite acesso de depuração pela rede local, dependendo do firmware. "
                        + "Não instala root, não explora falhas, não troca firmware nem altera calibração. Use Desfazer ao terminar.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton(undo ? "Restaurar" : "Tentar", (d, w) -> runRootAction(undo)).show();
    }

    private static final class CommandResult {
        final int code; final String output;
        CommandResult(int code, String output) { this.code = code; this.output = output; }
    }

    private CommandResult rootCommand(String su, String command) throws Exception {
        Process process = new ProcessBuilder(su, "-c", command).redirectErrorStream(true).start();
        try {
            long deadline = android.os.SystemClock.elapsedRealtime() + 20000;
            int code;
            while (true) {
                try { code = process.exitValue(); break; }
                catch (IllegalThreadStateException running) {
                    if (android.os.SystemClock.elapsedRealtime() >= deadline) throw new IOException("Tempo esgotado aguardando root");
                    Thread.sleep(100);
                }
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (InputStream in = process.getInputStream()) {
                byte[] buffer = new byte[1024]; int n;
                while (bytes.size() < 16384 && (n = in.read(buffer, 0, Math.min(buffer.length, 16384 - bytes.size()))) > 0) bytes.write(buffer, 0, n);
            }
            return new CommandResult(code, new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        } finally { process.destroy(); }
    }

    private String findRoot() throws Exception {
        LinkedHashSet<String> paths = new LinkedHashSet<>();
        String[] known = {"/system/bin/su", "/system/xbin/su", "/sbin/su", "/su/bin/su"};
        for (String path : known) {
            File file = new File(path);
            if (file.isFile() && file.canExecute()) paths.add(file.getCanonicalPath());
        }
        String pathValue = System.getenv("PATH");
        if (pathValue != null) for (String path : pathValue.split(":")) {
            if (!path.startsWith("/")) continue;
            File file = new File(path, "su");
            if (file.isFile() && file.canExecute()) paths.add(file.getCanonicalPath());
        }
        if (paths.isEmpty()) throw new IOException("Não há executável su acessível. O app não pode criar privilégios root.");
        String last = "Root não autorizado";
        int tried = 0;
        for (String path : paths) {
            if (++tried > 2) break;
            CommandResult test = rootCommand(path, "id");
            if (test.code == 0 && test.output.matches("(?s).*\\buid=0(?:\\(|\\s).*")) return path;
            last = "su encontrado, mas uid=0 não foi autorizado (código " + test.code + ")";
        }
        throw new IOException(last);
    }

    private RootAdbPlan.State readRootState(String su) throws Exception {
        CommandResult read = rootCommand(su, RootAdbPlan.READ);
        if (read.code != 0) throw new IOException("Não foi possível ler o estado ADB com root");
        return RootAdbPlan.parse(read.output);
    }

    private RootAdbPlan.State savedRootState() {
        SharedPreferences p = rootPrefs();
        return new RootAdbPlan.State(p.getString("enabled", ""), p.getString("port", ""), p.getString("service", ""));
    }

    private boolean restoreWithRoot(String su) throws Exception {
        RootAdbPlan.State previous = savedRootState();
        CommandResult restore = rootCommand(su, previous.restoreCommand());
        Thread.sleep(1200);
        boolean restored = restore.code == 0 && previous.same(readRootState(su));
        if (restored) rootPrefs().edit().putBoolean("pending", false).commit();
        return restored;
    }

    private void runRootAction(boolean undo) {
        rootBusy = true;
        message("Verificando root existente… Se aparecer uma solicitação no projetor, decida se deseja autorizar. Aguarde.");
        new Thread(() -> {
            String result;
            String su = null;
            boolean modified = false;
            try {
                su = findRoot();
                if (undo) {
                    modified = true;
                    result = restoreWithRoot(su) ? "Valores anteriores restaurados e conferidos. Peça ao computador para confirmar se a porta fechou."
                            : "A restauração não foi confirmada. A cópia dos valores anteriores foi mantida; gere o diagnóstico.";
                } else {
                    RootAdbPlan.State before = readRootState(su);
                    if (!rootPrefs().getBoolean("pending", false)) {
                        boolean saved = rootPrefs().edit().putString("enabled", before.enabled).putString("port", before.port)
                                .putString("service", before.service).putBoolean("pending", true).commit();
                        if (!saved) throw new IOException("Não foi possível salvar os valores anteriores; operação cancelada");
                    }
                    modified = true;
                    CommandResult enabled = rootCommand(su, RootAdbPlan.ENABLE);
                    Thread.sleep(1200);
                    RootAdbPlan.State after = readRootState(su);
                    if (enabled.code == 0 && after.enabled.equals("1") && after.port.equals("5555") && after.service.equals("running")) {
                        result = "Root confirmado. ADB ativado; adbd está em execução com porta configurada como 5555. "
                                + "Avise na conversa para testar a conexão. Isso ainda não confirma que a porta esteja acessível pela rede. "
                                + "Use Desfazer alterações do modo avançado ao terminar.";
                    } else {
                        result = restoreWithRoot(su) ? "O firmware não confirmou a ativação. Os valores anteriores foram restaurados. Gere o diagnóstico."
                                : "A ativação e a restauração não foram confirmadas. A cópia anterior foi mantida. Use Desfazer ou gere o diagnóstico.";
                    }
                }
            } catch (Exception e) {
                result = "Modo avançado: " + e.getMessage();
                if (modified && su != null) {
                    try { result += restoreWithRoot(su) ? "\nValores anteriores restaurados." : "\nRestauração pendente; a cópia anterior foi mantida."; }
                    catch (Exception restoreError) { result += "\nNão foi possível restaurar agora. Use Desfazer alterações do modo avançado; a cópia anterior foi mantida."; }
                } else result += "\nNenhuma alteração foi feita nesta tentativa. Gere o diagnóstico para analisar outro caminho.";
            }
            final String display = result;
            rootPrefs().edit().putString("lastResult", result).commit();
            runOnUiThread(() -> { rootBusy = false; message(display); });
        }, "u4-root-adb").start();
    }

    @SuppressWarnings("deprecation")
    private String certificateReport() {
        StringBuilder report = new StringBuilder("Certificados públicos SHA-256. Não são chaves privadas.\n");
        LinkedHashSet<String> names = new LinkedHashSet<>(); names.add("android");
        if (!settingsOwner.isEmpty()) names.add(settingsOwner);
        for (PackageInfo p : packages()) if (candidate(p)) names.add(p.packageName);
        for (String name : names) {
            try {
                PackageInfo p = getPackageManager().getPackageInfo(name, PackageManager.GET_SIGNATURES);
                report.append(name).append('\n');
                if (p.signatures != null) for (android.content.pm.Signature signature : p.signatures) {
                    byte[] digest = MessageDigest.getInstance("SHA-256").digest(signature.toByteArray());
                    for (byte b : digest) report.append(String.format(Locale.ROOT, "%02x", b & 255));
                    report.append('\n');
                }
            } catch (Exception e) { report.append(name).append(" <indisponível>\n"); }
        }
        return report.toString();
    }

    private void addText(ZipOutputStream zip, String name, String value) throws IOException {
        zip.putNextEntry(new ZipEntry(name)); zip.write(value.getBytes(StandardCharsets.UTF_8)); zip.closeEntry();
    }

    private File export() throws IOException {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "U4-Diagnostico");
        if (!dir.isDirectory() && !dir.mkdirs()) throw new IOException("Pasta Downloads indisponível");
        File result = new File(dir, "U4-diagnostico.zip");
        File temporary = new File(dir, "U4-diagnostico.zip.partial");
        StringBuilder report = new StringBuilder("U4 Diagnóstico 1.2\nArquivo criado localmente. Sem dados de contas.\n");
        String[] keys = {"ro.build.fingerprint", "ro.build.display.id", "ro.build.version.incremental", "ro.build.type", "ro.build.tags",
                "ro.build.version.release", "ro.build.version.sdk", "ro.product.board", "ro.product.device", "ro.product.name",
                "ro.product.manufacturer", "ro.product.model", "ro.product.rom.rommodelnumber", "ro.CVT_DEF_ADB_DEFAULT_VALUE",
                "ro.CVT_EN_ADB", "ro.adb.secure", "ro.debuggable", "service.adb.tcp.port", "persist.adb.tcp.port", "persist.sys.usb.config", "init.svc.adbd", "sys.factorymenu", "ro.cvte.config.path"};
        for (String key : keys) report.append(key).append('=').append(property(key)).append('\n');
        report.append("settingsOwner=").append(settingsOwner).append('\n');
        report.append("WRITE_SECURE_SETTINGS=").append(checkSelfPermission(SECURE) == PackageManager.PERMISSION_GRANTED).append('\n');
        report.append("adb_enabled=").append(Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, -1)).append('\n');
        report.append("rootAdbRestorePending=").append(rootPrefs().getBoolean("pending", false)).append('\n');
        report.append("rootAdbLastResult=").append(rootPrefs().getString("lastResult", "Não executado")).append('\n');
        if (rootPrefs().getBoolean("pending", false)) {
            report.append("rootAdbOriginalEnabled=").append(rootPrefs().getString("enabled", "")).append('\n');
            report.append("rootAdbOriginalPort=").append(rootPrefs().getString("port", "")).append('\n');
            report.append("rootAdbOriginalService=").append(rootPrefs().getString("service", "")).append('\n');
        }
        StringBuilder packagesReport = new StringBuilder();
        StringBuilder activitiesReport = new StringBuilder();
        StringBuilder copies = new StringBuilder();
        long total = 0;
        int copied = 0;
        try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(temporary)))) {
            for (PackageInfo p : packages()) {
                // Inventory only system packages and this helper; no user-app inventory.
                if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || p.packageName.equals(getPackageName())) {
                    packagesReport.append(p.packageName).append("\t").append(p.versionName).append("\t")
                            .append(p.applicationInfo.loadLabel(getPackageManager())).append('\n');
                }
                if (!candidate(p)) continue;
                activitiesReport.append("\nPACKAGE ").append(p.packageName).append(" version=").append(p.versionName)
                        .append(" enabled=").append(p.applicationInfo.enabled).append('\n');
                if (p.activities != null) for (ActivityInfo a : p.activities) {
                    activitiesReport.append(a.name).append(" exported=").append(a.exported).append(" enabled=").append(a.enabled)
                            .append(" permission=").append(a.permission).append('\n');
                }
                List<String> paths = new ArrayList<>(); paths.add(p.applicationInfo.sourceDir);
                if (p.applicationInfo.splitSourceDirs != null) paths.addAll(Arrays.asList(p.applicationInfo.splitSourceDirs));
                int part = 0;
                for (String path : paths) {
                    File apk = new File(path);
                    String entry = "apks/" + p.packageName + (part++ == 0 ? "-base" : "-split" + part) + ".apk";
                    if (!apk.isFile() || !apk.canRead() || apk.length() > FILE_LIMIT || total + apk.length() > TOTAL_LIMIT) {
                        copies.append("SKIPPED ").append(entry).append(" (inacessível ou limite de tamanho)\n"); continue;
                    }
                    zip.putNextEntry(new ZipEntry(entry));
                    try (InputStream in = new BufferedInputStream(new FileInputStream(apk))) {
                        byte[] buffer = new byte[65536]; int n;
                        while ((n = in.read(buffer)) != -1) zip.write(buffer, 0, n);
                    }
                    zip.closeEntry(); total += apk.length(); copied++;
                    copies.append("COPIED ").append(entry).append(" bytes=").append(apk.length()).append('\n');
                }
            }
            report.append("apksCopied=").append(copied).append('\n');
            addText(zip, "diagnostico.txt", report.toString());
            addText(zip, "pacotes-sistema.txt", packagesReport.toString());
            addText(zip, "atividades-configuracoes.txt", activitiesReport.toString());
            addText(zip, "arquivos-copiados.txt", copies.toString());
            addText(zip, "certificados-sistema.txt", certificateReport());
        } catch (IOException | RuntimeException e) { temporary.delete(); throw e; }
        if (result.exists() && !result.delete()) throw new IOException("Não foi possível substituir o ZIP anterior");
        if (!temporary.renameTo(result)) throw new IOException("Não foi possível concluir o arquivo");
        return result;
    }
}
