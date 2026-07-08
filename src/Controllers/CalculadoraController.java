package Controllers;

import Models.DadosEntrada; 
import Models.Configuracoes; 

public class CalculadoraController {
	private Double a_sol;             // Área do solenóide
	private Double FEM_induzida;      //
	private Double c_mag_sol;     // campo magnético
	private Double variacao_fluxo;    // varicação do fluxo
	private Double corrente_induzida; // corrente induzida
	
	private DadosEntrada dados;
	
	public CalculadoraController(DadosEntrada dados) {
	    this.dados = dados;
	    Initialize();
	}
	
	public void Initialize( ) {
		/*  Passo 1 — Campo magnético: B = μ₀ · n · I  */
		setCampo_mag_sol(dados.getEspiras_m(), dados.getC_inicial());
		System.out.println(getCampo_mag_sol());
        /*  Passo 2 — Área do solenóide: A = π · r²  */
        setA_sol(dados.getD_solenoide());
 
        /*  Passo 3 — Variação do fluxo: ΔΦ = ΔB · A  */
        setVariacao_fluxo(getA_sol());
 
        /*  Passo 4 — FEM induzida: ε = N · ΔΦ / Δt  */
        setFEM_induzida(dados.getQtd_espiras(), getVariacao_fluxo(), dados.getIntervalo_tempo());
 
        /*  Passo 5 — Corrente induzida: i = ε / R  */
        setCorrente_induzida(getFEM_induzida(), dados.getResistencia());
	}
	
	 // ── Passo 1: Campo magnético — B = μ₀ · n · I ───────────────────────
    public Double getCampo_mag_sol() { return c_mag_sol; }
    public void setCampo_mag_sol(Integer densidade_esp, Double corrente) {
        double campo = Configuracoes.MI * densidade_esp * corrente;
 
        if (campo < Configuracoes.CAMPO_MAGNETICO_MIN || campo > Configuracoes.CAMPO_MAGNETICO_MAX)
            throw new IllegalArgumentException(
                "Campo magnético calculado fora do intervalo esperado: "
                + String.format("%.5f", campo) + " T.\n"
                + "Verifique: espiras/metro (" + densidade_esp
                + " esp/m) e corrente (" + corrente + " A)."
            );
 
        this.c_mag_sol = campo;
    }
 
    // ── Passo 2: Área do solenóide — A = π · r² ─────────────────────────
    public Double getA_sol() { return a_sol; }
    public void setA_sol(Double d_sol) {
        double r_sol = d_sol / 2.0;
 
        if (r_sol < Configuracoes.RAIO_SOLENOIDE_MIN || r_sol > Configuracoes.RAIO_SOLENOIDE_MAX)
            throw new IllegalArgumentException(
                "Raio do solenóide fora do intervalo: "
                + String.format("%.4f", r_sol) + " m.\n"
                + "O diâmetro informado (" + d_sol + " m) deve gerar um raio entre "
                + Configuracoes.RAIO_SOLENOIDE_MIN + " e " + Configuracoes.RAIO_SOLENOIDE_MAX + " m."
            );
 
        double area = Configuracoes.PI * Math.pow(r_sol, 2);
 
        if (area < Configuracoes.AREA_SOLENOIDE_MIN || area > Configuracoes.AREA_SOLENOIDE_MAX)
            throw new IllegalArgumentException(
                "Área do solenóide calculada fora do intervalo: "
                + String.format("%.3e", area) + " m².\n"
                + "Verifique o diâmetro informado: " + d_sol + " m."
            );
 
        this.a_sol = area;
    }
 
    // ── Passo 3: Variação do fluxo — ΔΦ = ΔB · A ───────────────────────
    public Double getVariacao_fluxo() { return variacao_fluxo; }
    public void setVariacao_fluxo(Double area_sol) {
        double b_0 = getCampo_mag_sol();                                // B inicial
        setCampo_mag_sol(dados.getEspiras_m(), dados.getC_final());
        double b_1 = getCampo_mag_sol();                                // B final
 
        this.c_mag_sol = b_0;
        
        // Correntes iguais → sem variação → sem indução
        if (Math.abs(b_0 - b_1) < 1e-12)
            throw new IllegalArgumentException(
                "A corrente inicial e final são iguais — não há variação de campo, "
                + "portanto não há indução eletromagnética."
            );
 
        double delta_b    = b_1 - b_0;
        double var_fluxo  = delta_b * area_sol;
 
        if (var_fluxo < Configuracoes.VARIACAO_FLUXO_MIN || var_fluxo > Configuracoes.VARIACAO_FLUXO_MAX)
            throw new IllegalArgumentException(
                "Variação de fluxo calculada fora do intervalo: "
                + String.format("%.3e", var_fluxo) + " Wb.\n"
                + "Revise: corrente inicial/final e diâmetro do solenóide."
            );
 
        this.variacao_fluxo = var_fluxo;
    }
 
    // ── Passo 4: FEM induzida — ε = N · ΔΦ / Δt ────────────────────────
    public Double getFEM_induzida() { return FEM_induzida; }
    public void setFEM_induzida(Integer qtd_espiras, Double variacao, Double tempo) {
        double fem = qtd_espiras * (variacao / tempo);
 
        if (fem < Configuracoes.FEM_INDUZIDA_MIN || fem > Configuracoes.FEM_INDUZIDA_MAX)
            throw new IllegalArgumentException(
                "FEM calculada fora do intervalo: "
                + String.format("%.4f", fem) + " V.\n"
                + "Revise: espiras da bobina ("
                + qtd_espiras
                + "), variação de fluxo e intervalo de tempo (" + tempo + " s)."
            );
 
        this.FEM_induzida = fem;
    }
 
    // ── Passo 5: Corrente induzida — i = ε / R ──────────────────────────
    public Double getCorrente_induzida() { return corrente_induzida; }
    public void setCorrente_induzida(Double fem, Double resistencia) {
        double corrente_i = fem / resistencia;
 
        if (corrente_i < Configuracoes.CORRENTE_INDUZIDA_MIN || corrente_i > Configuracoes.CORRENTE_INDUZIDA_MAX)
            throw new IllegalArgumentException(
                "Corrente induzida calculada fora do intervalo: "
                + String.format("%.4f", corrente_i) + " A.\n"
                + "Revise: FEM calculada (" + String.format("%.4f", fem)
                + " V) e resistência (" + resistencia + " Ω)."
            );
 
        this.corrente_induzida = corrente_i;
    }
}