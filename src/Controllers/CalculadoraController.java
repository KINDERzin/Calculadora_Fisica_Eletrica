package Controllers;

import Models.DadosEntrada; 
import Models.Configuracoes; 

public class CalculadoraController {
	private Double a_sol;             // Área do solenóide
	private Double FEM_induzida;      //
	private Double c_mag_sol;     // campo magnético
	private Double variacao_fluxo;    // varicação do fluxo
	private Double corrente_induzida; // corrente induzida
	
	private Configuracoes configs;
	private DadosEntrada dados;
	
	public CalculadoraController() {
		Initialize();
	}
	
	public void Initialize( ) {
		/* 
	 	Passo 1 - Campo magnético dentro do solenoide
			O campo dentro de um solenoide ideal é:
				B = μ0 * n * I
		*/
		setCampo_mag_sol(dados.getC_inicial());
		/*
	 	Passo 2 — Área relevante para o fluxo
			Asol = π  * rsol²​
		*/
		setVariacao_fluxo();
		/*
	 	Passo 3 — Variação do fluxo em cada espira
			A corrente cai de 1,5 A para zero, então B cai de B₀ para zero:
				ΔΦ = ΔB * Asol => (0 − B0) * Asol
		*/
		setVariacao_fluxo();
		/*
		Passo 4 — FEM induzida (Lei de Faraday)
			∣ε∣ = N2 * ∣ΔΦ∣ / Δt
		*/
		setFEM_induzida();
		/*
	  	Passo 5 — Corrente induzida (Lei de Ohm)
			i = ε / R
		*/
		setCorrente_induzida();
	}
	
	// Corrente induzida -  i = R / ε​
	public Double getCorrente_induzida() { return corrente_induzida; }
	public void setCorrente_induzida() {
		double corrente_i = FEM_induzida / dados.getResistencia();
		
		// Acrescentar validação 
		if(corrente_i < configs.CORRENTE_INDUZIDA_MIN || corrente_i > configs.CORRENTE_INDUZIDA_MAX)
			throw new IllegalArgumentException("A corrente induzida deve ter um valor entre " + configs.CORRENTE_INDUZIDA_MIN + " e " + configs.CORRENTE_INDUZIDA_MAX + "!");
		this.corrente_induzida = corrente_induzida;
	}
	// Área do solenóide - a_sol​ = π * r²_sol
	public Double getA_sol() { return a_sol; }
	public void setA_sol(Double a_sol) {
		double r_sol = dados.getD_solenoide() / 2.0;
		// Acrescentar validação
		if(r_sol < 0.1)
			throw new IllegalArgumentException("O valor do raio do solenóide deve ser maior que 0!");

		double area_solenoide = (configs.PI * Math.pow(r_sol, 2));
		// Acrescentar validação
		if(area_solenoide < 0.1)
			throw new IllegalArgumentException("A área do solenóide deve ser maior que 0!");

		this.a_sol = area_solenoide;
	}
	// FEM induzida - |ε| = N₂ * (Δt / |ΔΦ|)​
	public Double getFEM_induzida() { return FEM_induzida; }
	public void setFEM_induzida() {
		double fem = dados.getQtd_espiras() * (this.variacao_fluxo * dados.getIntervalo_tempo());
		
		// Acrescentar validação
		
		this.FEM_induzida = fem; 
	}
	// Campo magnético dentro do solenóide - B = μ_0 ​* n * I
	public Double getCampo_mag_sol() { return c_mag_sol; }
	public void setCampo_mag_sol(Double corrente) {
		double campo_magnetico = configs.MI * dados.getEspiras_m() * corrente;
		
		// Acrescentar validação
		
		this.c_mag_sol = campo_magnetico;
	}
	// Variação do fluxo (espira) - ΔΦ = ΔB * a_sol​ => (0 − B_0​) * a_sol​
	public Double getVariacao_fluxo() { return variacao_fluxo; }
	public void setVariacao_fluxo() {
		double b_0 = getCampo_mag_sol(); // Inicial
		setCampo_mag_sol(dados.getC_final()); 	
		double b_1 = getCampo_mag_sol(); // Final
		
		double delta_b = b_1 - b_0;
		double var_fluxo = delta_b * this.a_sol;
		
		// Acrecentar validação
		
		this.variacao_fluxo = var_fluxo;
	}
}