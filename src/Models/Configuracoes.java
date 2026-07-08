package Models;

public final class Configuracoes {
	private Configuracoes() {
		throw new UnsupportedOperationException("A classe das configurações NÃO PODE/DEVE ser INSTANCIADA!");
	}
	
	// ── Constantes físicas ───────────────────────────────────────────────
    public static final Double MI = 4 * Math.PI * Math.pow(10, -7); // Permeabilidade do vácuo
    public static final Double PI = Math.PI;
 
    // ── Entradas do usuário ──────────────────────────────────────────────
 
    // Resistência da bobina (Ω)
    public static final Double RESISTENCIA_MIN = 0.1;
    public static final Double RESISTENCIA_MAX = 1000.0;
 
    // Corrente elétrica (A) — usada para inicial e final
    public static final Double CORRENTE_MIN = 0.0;
    public static final Double CORRENTE_MAX = 100.0;
 
    // Diâmetro do solenóide (m)
    public static final Double DIAMETRO_SOLENOIDE_MIN = 0.002; // 2 mm
    public static final Double DIAMETRO_SOLENOIDE_MAX = 2.0;   // 2 m
 
    // Raio do solenóide (m) — derivado do diâmetro
    public static final Double RAIO_SOLENOIDE_MIN = 0.001; // 1 mm
    public static final Double RAIO_SOLENOIDE_MAX = 1.0;   // 1 m
 
    // Intervalo de tempo (s)
    public static final Double TEMPO_MIN = 0.001; // 1 ms
    public static final Double TEMPO_MAX = 3600.0; // 1 hora
 
    // Espiras da bobina
    public static final Integer ESPIRAS_MIN = 1;
    public static final Integer ESPIRAS_MAX = 10000;
 
    // Densidade de espiras do solenóide (espiras/metro)
    public static final Integer DENSIDADE_MIN = 1;
    public static final Integer DENSIDADE_MAX = 100000;
 
    // ── Resultados calculados ────────────────────────────────────────────
 
    // Área do solenóide (m²) — derivada do raio
    public static final Double AREA_SOLENOIDE_MIN = 3.14e-6; // raio mínimo de 1 mm
    public static final Double AREA_SOLENOIDE_MAX = 3.14;    // raio máximo de 1 m
 
    // Campo magnético do solenóide (T) — B = μ₀ × n × I
    // Máximo: μ₀ × 100000 × 100 ≈ 12.57 T
    public static final Double CAMPO_MAGNETICO_MIN = 0.0;
    public static final Double CAMPO_MAGNETICO_MAX = 12.57;
 
    // Variação do fluxo magnético (Wb) — pode ser negativa (corrente diminui)
    // Máximo: B_max × A_max = 12.57 × 3.14 ≈ 39.5 Wb
    public static final Double VARIACAO_FLUXO_MIN = -39.5;
    public static final Double VARIACAO_FLUXO_MAX =  39.5;
 
    // FEM induzida (V) — pode ser negativa (depende do sinal de ΔΦ)
    // ⚠️ MIN deve ser negativo — FEM é negativa quando corrente diminui!
    public static final Double FEM_INDUZIDA_MIN = -100000.0;
    public static final Double FEM_INDUZIDA_MAX =  100000.0;
 
    // Corrente induzida (A) — pode ser negativa (segue sinal da FEM)
    public static final Double CORRENTE_INDUZIDA_MIN = -1000.0;
    public static final Double CORRENTE_INDUZIDA_MAX =  1000.0;
	
}
