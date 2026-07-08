package Views;

/*
 * ══════════════════════════════════════════════════════════════════════
 *  ECLIPSE — ANTES DE RODAR:
 *  Run → Run Configurations → Arguments → VM arguments:
 *  --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls
 * ══════════════════════════════════════════════════════════════════════
 */

import Controllers.CalculadoraController;
import Models.Configuracoes;
import Models.DadosEntrada;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import javafx.util.Duration;

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

    // ── Labels do "passo a passo" (substituição de valores) ──────────────
    // Preenchidos em handleCalcular(), exibidos ao expandir cada card
    private final Label lbl_detalhe1 = detailLabel();
    private final Label lbl_detalhe2 = detailLabel();
    private final Label lbl_detalhe3 = detailLabel();
    private final Label lbl_detalhe4 = detailLabel();
    private final Label lbl_detalhe5 = detailLabel();

    // ── Containers expansíveis (um por passo) e seus indicadores ─────────
    private final VBox  box_detalhe1 = detailBox(lbl_detalhe1);
    private final VBox  box_detalhe2 = detailBox(lbl_detalhe2);
    private final VBox  box_detalhe3 = detailBox(lbl_detalhe3);
    private final VBox  box_detalhe4 = detailBox(lbl_detalhe4);
    private final VBox  box_detalhe5 = detailBox(lbl_detalhe5);

    private final Label chevron1 = chevronLabel();
    private final Label chevron2 = chevronLabel();
    private final Label chevron3 = chevronLabel();
    private final Label chevron4 = chevronLabel();
    private final Label chevron5 = chevronLabel();

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

        // Passos 1 a 4 — cada um clicável, expande e mostra a substituição de valores
        VBox steps = new VBox(8,
            buildResultRow("1", "Campo magnético",      "B = μ₀ · n · I",    lbl_B,    "Tesla",
                           chevron1, box_detalhe1),
            buildResultRow("2", "Área do solenoide",    "A = π · r²",         lbl_A,    "m²",
                           chevron2, box_detalhe2),
            buildResultRow("3", "Variação do fluxo",    "ΔΦ = ΔB · A",        lbl_dPhi, "Weber",
                           chevron3, box_detalhe3),
            buildResultRow("4", "FEM — Lei de Faraday", "ε = N · ΔΦ / Δt",   lbl_FEM,  "Volt",
                           chevron4, box_detalhe4)
        );
        VBox.setVgrow(steps, Priority.ALWAYS);

        // Passo 5 — resultado final em destaque
        VBox finalCard = buildFinalCard();

        VBox content = new VBox(8, steps, finalCard);
        content.setPadding(new Insets(14));
        VBox.setVgrow(content, Priority.ALWAYS);

        VBox canvas = new VBox(toolbar, content);
        canvas.setStyle("-fx-background-color: " + BG_CARBON + ";");
        VBox.setVgrow(canvas, Priority.ALWAYS);
        return canvas;
    }

    // ── Linha de resultado: [num] [desc + fórmula] [valor + unidade] [▸] ─
    //    Clicável — expande/recolhe o box de detalhe abaixo, mostrando o
    //    cálculo com os valores substituídos na fórmula.
    private VBox buildResultRow(String num, String desc, String formula,
                                Label valLbl, String unit,
                                Label chevron, VBox detailBox) {
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

        HBox header = new HBox(12, numBadge, textBox, valBox, chevron);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(9, 12, 9, 12));
        header.setCursor(Cursor.HAND);

        VBox card = new VBox(0, header, detailBox);
        card.setStyle(
            "-fx-background-color: " + BG_CLUSTER + ";" +
            // Borda esquerda em teal, as demais em carbon
            "-fx-border-color: " + C_BORDER + " " + C_BORDER + " " + C_BORDER + " " + C_TEAL + ";" +
            "-fx-border-width: 1 1 1 3;" +
            "-fx-background-radius: 3; -fx-border-radius: 3;"
        );

        header.setOnMouseClicked(e -> toggleDetalhe(detailBox, chevron));
        return card;
    }

    // ── Alterna a exibição do bloco de cálculo detalhado ────────────────
    private void toggleDetalhe(VBox detailBox, Label chevron) {
        boolean expandindo = !detailBox.isVisible();
        detailBox.setVisible(expandindo);
        detailBox.setManaged(expandindo);
        chevron.setText(expandindo ? "▾" : "▸");

        if (expandindo) {
            detailBox.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(160), detailBox);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    // ── Card do resultado final (clicável — expande o cálculo) ──────────
    private VBox buildFinalCard() {
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

        chevron5.setStyle(chevron5.getStyle() + "-fx-text-fill: " + C_TEAL + ";");

        HBox header = new HBox(14, numBadge, textBox, valBox, chevron5);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 16, 14, 16));
        header.setCursor(Cursor.HAND);

        VBox card = new VBox(0, header, box_detalhe5);
        card.setStyle(
            "-fx-background-color: #0d2018;" +
            "-fx-border-color: " + C_TEAL + ";" +
            "-fx-border-width: 1 1 1 3;" +
            "-fx-background-radius: 3; -fx-border-radius: 3;"
        );

        header.setOnMouseClicked(e -> toggleDetalhe(box_detalhe5, chevron5));
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

            preencherDetalhes(dados, ctrl);

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

    // ── Monta o texto de cada passo com os valores efetivamente usados ──
    //    Os valores finais aqui SEMPRE batem com os exibidos nos cards
    //    fechados (lbl_B, lbl_A, lbl_dPhi, lbl_FEM, lbl_i), inclusive o
    //    uso de módulo (Math.abs) em ΔΦ, ε e i — o sinal físico (Lei de
    //    Lenz) é mostrado como informação extra, não substitui o valor.
    private void preencherDetalhes(DadosEntrada dados, CalculadoraController ctrl) {
        double n   = dados.getEspiras_m();
        double i0  = dados.getC_inicial();
        double i1  = dados.getC_final();
        double d   = dados.getD_solenoide();
        double r   = d / 2.0;
        double N   = dados.getQtd_espiras();
        double dt  = dados.getIntervalo_tempo();
        double R   = dados.getResistencia();
        double mu0 = Configuracoes.MI;

        double B0 = ctrl.getCampo_mag_sol();     // igual ao valor do card 1 (usa corrente inicial)
        double B1 = mu0 * n * i1;                // campo com a corrente final (auxiliar do passo 3)
        double dB = B1 - B0;
        double A  = ctrl.getA_sol();             // igual ao valor do card 2

        double dPhiSinal = ctrl.getVariacao_fluxo();   // ΔΦ com sinal, igual ao usado internamente
        double femSinal  = ctrl.getFEM_induzida();     // ε com sinal
        double corrSinal = ctrl.getCorrente_induzida();// i com sinal

        double dPhiAbs = Math.abs(dPhiSinal);          // igual ao valor do card 3
        double femAbs  = Math.abs(femSinal);           // igual ao valor do card 4
        double corrAbs = Math.abs(corrSinal);          // igual ao valor do card 5

        // Passo 1 — Campo magnético (com a corrente inicial)
        lbl_detalhe1.setText(
            "μ₀ = 4π × 10⁻⁷ ≈ " + fmt(mu0, "%.5e") + " T·m/A\n" +
            "n  = " + fmt(n, "%.0f")  + " espiras/m\n" +
            "I  = " + fmt(i0, "%.3f") + " A  (corrente inicial)\n\n" +
            "B = μ₀ × n × I\n" +
            "B = " + fmt(mu0, "%.5e") + " × " + fmt(n, "%.0f") + " × " + fmt(i0, "%.3f") + "\n" +
            "B = " + fmt(mu0 * n, "%.5e") + " × " + fmt(i0, "%.3f") + "\n" +
            "B = " + fmt(B0, "%.5f") + " T  ← valor exibido no card acima"
        );

        // Passo 2 — Área do solenoide
        lbl_detalhe2.setText(
            "d = " + fmt(d, "%.4f") + " m\n" +
            "r = d / 2 = " + fmt(r, "%.4f") + " m\n\n" +
            "A = π × r²\n" +
            "A = π × (" + fmt(r, "%.4f") + ")²\n" +
            "A = " + fmt(Math.PI, "%.5f") + " × " + fmt(r * r, "%.3e") + "\n" +
            "A = " + fmt(A, "%.3e") + " m²  ← valor exibido no card acima"
        );

        // Passo 3 — Variação do fluxo (precisa do campo com I inicial e final)
        lbl_detalhe3.setText(
            "B(I₀) = μ₀ × n × I₀ = " + fmt(B0, "%.5f") + " T   (mesmo do passo 1)\n" +
            "B(I₁) = μ₀ × n × I₁ = " + fmt(mu0, "%.5e") + " × " + fmt(n, "%.0f") + " × " + fmt(i1, "%.3f") +
                " = " + fmt(B1, "%.5f") + " T\n\n" +
            "ΔB = B(I₁) - B(I₀)\n" +
            "ΔB = " + fmt(B1, "%.5f") + " - " + fmt(B0, "%.5f") + " = " + fmt(dB, "%.5f") + " T\n\n" +
            "ΔΦ = ΔB × A\n" +
            "ΔΦ = " + fmt(dB, "%.5f") + " × " + fmt(A, "%.3e") + "\n" +
            "ΔΦ = " + fmt(dPhiSinal, "%.3e") + " Wb  (o sinal indica o sentido — Lei de Lenz)\n" +
            "|ΔΦ| = " + fmt(dPhiAbs, "%.3e") + " Wb  ← valor exibido no card acima"
        );

        // Passo 4 — FEM induzida
        lbl_detalhe4.setText(
            "N  = " + fmt(N, "%.0f")  + " espiras\n" +
            "ΔΦ = " + fmt(dPhiSinal, "%.3e") + " Wb  (com sinal, do passo 3)\n" +
            "Δt = " + fmt(dt, "%.3f") + " s\n\n" +
            "ε = N × (ΔΦ / Δt)\n" +
            "ε = " + fmt(N, "%.0f") + " × (" + fmt(dPhiSinal, "%.3e") + " / " + fmt(dt, "%.3f") + ")\n" +
            "ε = " + fmt(N, "%.0f") + " × " + fmt(dPhiSinal / dt, "%.3e") + "\n" +
            "ε = " + fmt(femSinal, "%.4f") + " V  (o sinal indica o sentido — Lei de Lenz)\n" +
            "|ε| = " + fmt(femAbs, "%.4f") + " V  ← valor exibido no card acima"
        );

        // Passo 5 — Corrente induzida (Lei de Ohm)
        lbl_detalhe5.setText(
            "ε = " + fmt(femSinal, "%.4f") + " V  (com sinal, do passo 4)\n" +
            "R = " + fmt(R, "%.3f")  + " Ω\n\n" +
            "i = ε / R\n" +
            "i = " + fmt(femSinal, "%.4f") + " / " + fmt(R, "%.3f") + "\n" +
            "i = " + fmt(corrSinal, "%.5f") + " A  (o sinal indica o sentido — Lei de Lenz)\n" +
            "|i| = " + fmt(corrAbs, "%.5f") + " A  =  " + fmt(corrAbs * 1000, "%.2f") +
                " mA  ← valor exibido no card acima"
        );
    }

    private static String fmt(double valor, String pattern) {
        return String.format(pattern, valor);
    }

    private void handleReset() {
        tf_diametroSol.setText("");   tf_espirasPorM.setText("");
        tf_qtdEspiras.setText("");    tf_resistencia.setText("");
        tf_cInicial.setText("");      tf_cFinal.setText("");
        tf_intervaloTemp.setText("");

        lbl_B.setText("—"); lbl_A.setText("—");
        lbl_dPhi.setText("—"); lbl_FEM.setText("—"); lbl_i.setText("—");

        // Recolhe e limpa os cards de detalhe do passo a passo
        for (VBox box : new VBox[]{ box_detalhe1, box_detalhe2, box_detalhe3, box_detalhe4, box_detalhe5 }) {
            box.setVisible(false);
            box.setManaged(false);
        }
        for (Label chev : new Label[]{ chevron1, chevron2, chevron3, chevron4, chevron5 }) {
            chev.setText("▸");
        }
        lbl_detalhe1.setText(""); lbl_detalhe2.setText(""); lbl_detalhe3.setText("");
        lbl_detalhe4.setText(""); lbl_detalhe5.setText("");

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

    // Label multi-linha com a substituição de valores na fórmula (estilo "código")
    private Label detailLabel() {
        Label lbl = new Label("");
        lbl.setWrapText(true);
        lbl.setStyle(
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 11px;" +
            "-fx-text-fill: " + C_MID + ";" +
            "-fx-line-spacing: 3px;"
        );
        return lbl;
    }

    // Container expansível que envolve o label de detalhe (inicia oculto/recolhido)
    private VBox detailBox(Label conteudo) {
        VBox box = new VBox(conteudo);
        box.setPadding(new Insets(2, 14, 12, 46)); // alinhado à direita do badge numerado
        box.setStyle(
            "-fx-background-color: " + BG_INPUT + ";" +
            "-fx-border-color: " + C_BORDER + " transparent transparent transparent;" +
            "-fx-border-width: 1 0 0 0;"
        );
        box.setVisible(false);
        box.setManaged(false);
        return box;
    }

    // Indicador de expandir/recolher (seta)
    private Label chevronLabel() {
        Label lbl = monoLabel("▸", C_DIM, 11);
        lbl.setMinWidth(16);
        lbl.setAlignment(Pos.CENTER);
        return lbl;
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