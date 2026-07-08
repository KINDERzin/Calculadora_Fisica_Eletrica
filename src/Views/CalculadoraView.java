package Views;

/*
 * ══════════════════════════════════════════════════════════════════════
 *  ECLIPSE — ANTES DE RODAR:
 *  Run → Run Configurations → Arguments → VM arguments:
 *  --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls
 * ══════════════════════════════════════════════════════════════════════
 */

import Controllers.CalculadoraController;
import Models.DadosEntrada;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CalculadoraView extends Application {

    // ── Paleta de cores (tema carbon/terminal) ───────────────────────────
    private static final String BG_CARBON  = "#0a0e08";
    private static final String BG_PANEL   = "#14241c";
    private static final String BG_CLUSTER = "#0d1a13";
    private static final String BG_INPUT   = "#050a07";
    private static final String C_BORDER   = "#1e3028";
    private static final String C_TEAL     = "#1fb88a";
    private static final String C_LIME     = "#c6ff33";
    private static final String C_DIM      = "#6b8c7a";
    private static final String C_MID      = "#9bbdaa";
    private static final String C_BRIGHT   = "#c8ddd5";

    // ── Campos de entrada ────────────────────────────────────────────────
    private final TextField tf_qtdEspiras    = inputField("120");
    private final TextField tf_resistencia   = inputField("5.3");
    private final TextField tf_espirasPorM   = inputField("22000");
    private final TextField tf_diametroSol   = inputField("0.032");
    private final TextField tf_cInicial      = inputField("1.5");
    private final TextField tf_cFinal        = inputField("0.0");
    private final TextField tf_intervaloTemp = inputField("0.025");

    // ── Labels de resultado ──────────────────────────────────────────────
    private final Label lbl_B    = resultLabel("—");
    private final Label lbl_A    = resultLabel("—");
    private final Label lbl_dPhi = resultLabel("—");
    private final Label lbl_FEM  = resultLabel("—");
    private final Label lbl_i    = new Label("—");   // estilo aplicado em buildFinalCard()

    // ── Status e contadores ──────────────────────────────────────────────
    private final Label lbl_status   = monoLabel("● IDLE",           C_DIM, 10);
    private final Label lbl_runCount = monoLabel("RUN: 000",         C_DIM, 10);
    private final Label lbl_mu       = monoLabel("μ₀ = 4π×10⁻⁷ T·m/A", C_DIM, 10);
    private int runCount = 0;

    // ════════════════════════════════════════════════════════════════════
    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_CARBON + ";");

        root.setTop(buildNavBar());
        root.setLeft(buildSidebar());
        root.setCenter(buildCanvas());
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 960, 640);

        stage.setTitle("InductoCalc — Indução Eletromagnética");
        stage.setMinWidth(820);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();
    }

    // ════════════════════════════════════════════════════════════════════
    //  NAV BAR — topo
    // ════════════════════════════════════════════════════════════════════
    private HBox buildNavBar() {
        Label appName  = monoLabel("⚡ INDUCTOCALC", C_TEAL, 13);
        appName.setStyle(appName.getStyle() + "-fx-font-weight: bold;");

        Label sep      = monoLabel("|", C_BORDER, 11);
        Label subtitle = monoLabel("// electromagnetic induction calculator", C_DIM, 10);

        Region spacer  = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label version  = monoLabel("v1.0", C_DIM, 10);

        HBox nav = new HBox(10, appName, sep, subtitle, spacer, lbl_runCount, version);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(9, 16, 9, 16));
        nav.setStyle(
            "-fx-background-color: #070b06;" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-width: 0 0 1 0;"
        );
        return nav;
    }

    // ════════════════════════════════════════════════════════════════════
    //  SIDEBAR — coluna esquerda com inputs
    // ════════════════════════════════════════════════════════════════════
    private VBox buildSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(12));
        sidebar.setPrefWidth(252);
        sidebar.setStyle(
            "-fx-background-color: " + BG_PANEL + ";" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-width: 0 1 0 0;"
        );

        sidebar.getChildren().addAll(
            buildCluster("SOLENOIDE",
                buildRow("d_sol", "Diâmetro (m)",    "m",  tf_diametroSol),
                buildRow("n",     "Espiras/metro",   "/m", tf_espirasPorM)
            ),
            buildCluster("BOBINA",
                buildRow("N",     "Total espiras",   "T",  tf_qtdEspiras),
                buildRow("R",     "Resistência",     "Ω",  tf_resistencia)
            ),
            buildCluster("CONDIÇÕES",
                buildRow("I₀",   "Corrente inicial", "A",  tf_cInicial),
                buildRow("I₁",   "Corrente final",   "A",  tf_cFinal),
                buildRow("Δt",   "Intervalo",        "s",  tf_intervaloTemp)
            ),
            buildButtons()
        );

        return sidebar;
    }

    // ── Cluster com borda e label estilo // NOME ─────────────────────────
    private VBox buildCluster(String titulo, HBox... rows) {
        Label label = monoLabel("// " + titulo, C_DIM, 9);
        label.setPadding(new Insets(-8, 0, 4, 4));

        VBox body = new VBox(7);
        body.getChildren().addAll(rows);

        VBox box = new VBox(4, label, body);
        box.setPadding(new Insets(10, 8, 10, 8));
        box.setStyle(
            "-fx-background-color: " + BG_CLUSTER + ";" +
            "-fx-border-color: "     + C_BORDER   + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 3;" +
            "-fx-background-radius: 3;"
        );
        return box;
    }

    // ── Linha de campo: [SYM] [nome] [TextField] [unidade] ──────────────
    private HBox buildRow(String sym, String name, String unit, TextField tf) {
        Label symLbl = monoLabel(sym, C_TEAL, 10);
        symLbl.setMinWidth(26);
        symLbl.setAlignment(Pos.CENTER);
        symLbl.setStyle(symLbl.getStyle() +
            "-fx-font-weight: bold;" +
            "-fx-background-color: #0d2018;" +
            "-fx-padding: 2 4 2 4;" +
            "-fx-background-radius: 3;"
        );

        Label nameLbl = monoLabel(name, C_DIM, 9);
        nameLbl.setMinWidth(90);

        HBox.setHgrow(tf, Priority.ALWAYS);

        Label unitLbl = monoLabel(unit, C_DIM, 9);
        unitLbl.setMinWidth(18);

        HBox row = new HBox(6, symLbl, nameLbl, tf, unitLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ── Botões Calcular e Resetar ─────────────────────────────────────────
    private VBox buildButtons() {
        Button btnCalc = new Button("▶  CALCULAR");
        btnCalc.setMaxWidth(Double.MAX_VALUE);
        btnCalc.setStyle(
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0a0e08;" +
            "-fx-background-color: " + C_LIME + ";" +
            "-fx-padding: 9 12 9 12;" +
            "-fx-background-radius: 3;" +
            "-fx-cursor: hand;"
        );
        btnCalc.setOnAction(e -> handleCalcular());

        Button btnReset = new Button("↺  RESETAR");
        btnReset.setMaxWidth(Double.MAX_VALUE);
        btnReset.setStyle(
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 11px;" +
            "-fx-text-fill: " + C_DIM + ";" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: "    + C_BORDER + ";" +
            "-fx-border-width: 1;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-background-radius: 3;" +
            "-fx-border-radius: 3;" +
            "-fx-cursor: hand;"
        );
        btnReset.setOnAction(e -> handleReset());

        VBox box = new VBox(6, btnCalc, btnReset);
        VBox.setMargin(box, new Insets(4, 0, 0, 0));
        return box;
    }

    // ════════════════════════════════════════════════════════════════════
    //  CANVAS — área central com resultados
    // ════════════════════════════════════════════════════════════════════
    private VBox buildCanvas() {
        Label canvasLbl = monoLabel("RESULTADO DO CÁLCULO", C_DIM, 9);
        Region spacer   = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label formulas  = monoLabel("ε = N·ΔΦ/Δt  |  i = ε/R", C_DIM, 9);

        HBox toolbar = new HBox(canvasLbl, spacer, formulas);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(7, 14, 7, 14));
        toolbar.setStyle(
            "-fx-background-color: #070b06;" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-width: 0 0 1 0;"
        );

        // Passos 1 a 4
        VBox steps = new VBox(8,
            buildResultRow("1", "Campo magnético",      "B = μ₀ · n · I",    lbl_B,    "Tesla"),
            buildResultRow("2", "Área do solenoide",    "A = π · r²",         lbl_A,    "m²"),
            buildResultRow("3", "Variação do fluxo",    "ΔΦ = ΔB · A",        lbl_dPhi, "Weber"),
            buildResultRow("4", "FEM — Lei de Faraday", "ε = N · ΔΦ / Δt",   lbl_FEM,  "Volt")
        );
        VBox.setVgrow(steps, Priority.ALWAYS);

        // Passo 5 — resultado final em destaque
        HBox finalCard = buildFinalCard();

        VBox content = new VBox(8, steps, finalCard);
        content.setPadding(new Insets(14));
        VBox.setVgrow(content, Priority.ALWAYS);

        VBox canvas = new VBox(toolbar, content);
        canvas.setStyle("-fx-background-color: " + BG_CARBON + ";");
        VBox.setVgrow(canvas, Priority.ALWAYS);
        return canvas;
    }

    // ── Linha de resultado: [num] [desc + fórmula] [valor + unidade] ────
    private HBox buildResultRow(String num, String desc, String formula,
                                Label valLbl, String unit) {
        // Badge numerado
        Label numBadge = monoLabel(num, C_TEAL, 11);
        numBadge.setMinWidth(24);
        numBadge.setMinHeight(24);
        numBadge.setAlignment(Pos.CENTER);
        numBadge.setStyle(numBadge.getStyle() +
            "-fx-font-weight: bold;" +
            "-fx-background-color: #0d2018;" +
            "-fx-background-radius: 12; -fx-border-radius: 12;"
        );

        // Texto descritivo e fórmula
        Label descLbl = monoLabel(desc, C_DIM, 9);
        Label fmlLbl  = monoLabel(formula, C_MID, 11);
        fmlLbl.setStyle(fmlLbl.getStyle() + "-fx-font-weight: bold;");
        VBox textBox  = new VBox(2, descLbl, fmlLbl);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        // Valor e unidade
        Label unitLbl = monoLabel(unit, C_DIM, 9);
        VBox valBox   = new VBox(2, valLbl, unitLbl);
        valBox.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(12, numBadge, textBox, valBox);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(9, 12, 9, 12));
        row.setStyle(
            "-fx-background-color: " + BG_CLUSTER + ";" +
            // Borda esquerda em teal, as demais em carbon
            "-fx-border-color: " + C_BORDER + " " + C_BORDER + " " + C_BORDER + " " + C_TEAL + ";" +
            "-fx-border-width: 1 1 1 3;" +
            "-fx-background-radius: 3; -fx-border-radius: 3;"
        );
        return row;
    }

    // ── Card do resultado final ──────────────────
    private HBox buildFinalCard() {
        Label numBadge = monoLabel("5", BG_CARBON, 13);
        numBadge.setMinWidth(38);
        numBadge.setMinHeight(38);
        numBadge.setAlignment(Pos.CENTER);
        numBadge.setStyle(numBadge.getStyle() +
            "-fx-font-weight: bold;" +
            "-fx-background-color: " + C_TEAL + ";" +
            "-fx-background-radius: 19; -fx-border-radius: 19;"
        );

        Label descLbl = monoLabel("Corrente induzida — Lei de Ohm", C_TEAL, 9);
        Label fmlLbl  = monoLabel("i = ε / R", C_BRIGHT, 12);
        fmlLbl.setStyle(fmlLbl.getStyle() + "-fx-font-weight: bold;");
        VBox textBox  = new VBox(2, descLbl, fmlLbl);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        // Valor em lime neon (grande)
        lbl_i.setStyle(
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 30px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + C_LIME + ";"
        );
        Label unitLbl = monoLabel("mA", C_TEAL, 12);
        VBox valBox   = new VBox(2, lbl_i, unitLbl);
        valBox.setAlignment(Pos.CENTER_RIGHT);

        HBox card = new HBox(14, numBadge, textBox, valBox);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle(
            "-fx-background-color: #0d2018;" +
            "-fx-border-color: " + C_TEAL + ";" +
            "-fx-border-width: 1 1 1 3;" +
            "-fx-background-radius: 3; -fx-border-radius: 3;"
        );
        return card;
    }

    // ════════════════════════════════════════════════════════════════════
    //  STATUS BAR — rodapé
    // ════════════════════════════════════════════════════════════════════
    private HBox buildStatusBar() {
        Label course = monoLabel("Física II — Indução Eletromagnética", C_DIM, 9);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(12, lbl_status, spacer, course, lbl_mu);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(5, 14, 5, 14));
        bar.setStyle(
            "-fx-background-color: #070b06;" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-width: 1 0 0 0;"
        );
        return bar;
    }

    // ════════════════════════════════════════════════════════════════════
    //  LÓGICA — calcular e resetar
    // ════════════════════════════════════════════════════════════════════
    private void handleCalcular() {
        try {
            setStatus("● RUNNING...", C_LIME);

            // r_sol é derivado do diâmetro — não precisa de campo separado na tela
            double d_sol = Double.parseDouble(tf_diametroSol.getText().trim());
            double r_sol = d_sol / 2.0;

            /*
             * Ordem dos parâmetros do construtor de DadosEntrada:
             * (resistencia, c_final, r_sol, c_inicial, d_sol, intervalo, qtdEspiras, espirasPorM)
             */
            DadosEntrada dados = new DadosEntrada(
                Double.parseDouble(tf_resistencia.getText().trim()),
                Double.parseDouble(tf_cFinal.getText().trim()),
                r_sol,
                Double.parseDouble(tf_cInicial.getText().trim()),
                d_sol,
                Double.parseDouble(tf_intervaloTemp.getText().trim()),
                Integer.parseInt(tf_qtdEspiras.getText().trim()),
                Integer.parseInt(tf_espirasPorM.getText().trim())
            );

            CalculadoraController ctrl = new CalculadoraController(dados);

            // Atualiza os labels com os resultados calculados
            lbl_B.setText(   String.format("%.5f",  ctrl.getCampo_mag_sol()));
            lbl_A.setText(   String.format("%.3e",  ctrl.getA_sol()));
            lbl_dPhi.setText(String.format("%.3e",  Math.abs(ctrl.getVariacao_fluxo())));
            lbl_FEM.setText( String.format("%.4f",  Math.abs(ctrl.getFEM_induzida())));

            // Corrente induzida em mA (controller retorna em Amperes)
            double i_mA = Math.abs(ctrl.getCorrente_induzida()) * 1000;
            lbl_i.setText(String.format("%.2f", i_mA));

            runCount++;
            lbl_runCount.setText(String.format("RUN: %03d", runCount));
            setStatus("● DONE", C_TEAL);

        } catch (NumberFormatException ex) {
            setStatus("● ERROR", "#e05252");
            showAlert("Formato inválido",
                "Verifique os campos.\n" +
                "Use ponto como separador decimal. Exemplo: 0.025");

        } catch (IllegalArgumentException ex) {
            setStatus("● ERROR", "#e05252");
            showAlert("Validação falhou", ex.getMessage());
        }
    }

    private void handleReset() {
        tf_diametroSol.setText("");   tf_espirasPorM.setText("");
        tf_qtdEspiras.setText("");    tf_resistencia.setText("");
        tf_cInicial.setText("");      tf_cFinal.setText("");
        tf_intervaloTemp.setText("");

        lbl_B.setText("—"); lbl_A.setText("—");
        lbl_dPhi.setText("—"); lbl_FEM.setText("—"); lbl_i.setText("—");
        setStatus("● IDLE", C_DIM);
    }

    private void setStatus(String texto, String cor) {
        lbl_status.setText(texto);
        lbl_status.setStyle(
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 10px;" +
            "-fx-text-fill: " + cor + ";"
        );
    }

    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.getDialogPane().setStyle(
            "-fx-background-color: " + BG_PANEL + ";" +
            "-fx-font-family: 'Consolas', monospace;"
        );
        alert.showAndWait();
    }

    // ════════════════════════════════════════════════════════════════════
    //  HELPERS DE ESTILO
    // ════════════════════════════════════════════════════════════════════

    // TextField com tema dark e borda teal no foco
    private TextField inputField(String valor) {
        TextField tf = new TextField(valor);

        String normalStyle =
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 12px;" +
            "-fx-text-fill: "       + C_BRIGHT  + ";" +
            "-fx-background-color: " + BG_INPUT + ";" +
            "-fx-border-color: "    + C_BORDER  + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 3;" +
            "-fx-background-radius: 3;" +
            "-fx-padding: 4 8 4 8;" +
            "-fx-alignment: center-right;";

        String focusStyle =
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 12px;" +
            "-fx-text-fill: "       + C_BRIGHT + ";" +
            "-fx-background-color: " + BG_INPUT + ";" +
            "-fx-border-color: "    + C_TEAL   + ";" +    // borda teal no foco
            "-fx-border-width: 1;" +
            "-fx-border-radius: 3;" +
            "-fx-background-radius: 3;" +
            "-fx-padding: 4 8 4 8;" +
            "-fx-alignment: center-right;";

        tf.setStyle(normalStyle);
        tf.focusedProperty().addListener((obs, old, focused) ->
            tf.setStyle(focused ? focusStyle : normalStyle)
        );
        return tf;
    }

    // Label para valores de resultado
    private Label resultLabel(String texto) {
        Label lbl = new Label(texto);
        lbl.setStyle(
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + C_TEAL + ";"
        );
        return lbl;
    }

    // Label com fonte monospace genérico
    private static Label monoLabel(String texto, String cor, int tamanho) {
        Label lbl = new Label(texto);
        lbl.setStyle(
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: " + tamanho + "px;" +
            "-fx-text-fill: " + cor + ";"
        );
        return lbl;
    }

    public static void main(String[] args) {
        launch(args);
    }
}