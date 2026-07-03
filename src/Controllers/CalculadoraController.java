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
	
	public CalculadoraController() {
//		dados = new DadosEntrada();
		Initialize();
	}
	
	public void Initialize( ) {
		/* 
	 	Passo 1 - Campo magnético dentro do solenoide
			O campo dentro de um solenoide ideal é:
				B = μ0 * n * I
		*/
		setCampo_mag_sol(dados.getEspiras_m(), dados.getC_inicial());
		/*
	 	Passo 2 — Área relevante para o fluxo
			Asol = π  * rsol²​
		*/
		setA_sol(dados.getD_solenoide());
		/*
	 	Passo 3 — Variação do fluxo em cada espira
			A corrente cai de 1,5 A para zero, então B cai de B₀ para zero:
				ΔΦ = ΔB * Asol => (0 − B0) * Asol
		*/
		setVariacao_fluxo(getA_sol());
		/*
		Passo 4 — FEM induzida (Lei de Faraday)
			∣ε∣ = N2 * ∣ΔΦ∣ / Δt
		*/
		setFEM_induzida(dados.getQtd_espiras(), getVariacao_fluxo(), dados.getIntervalo_tempo());
		/*
	  	Passo 5 — Corrente induzida (Lei de Ohm)
			i = ε / R
		*/
		setCorrente_induzida(getFEM_induzida(), dados.getResistencia());
	}
	
	// Corrente induzida -  i = R / ε​
	public Double getCorrente_induzida() { return corrente_induzida; }
	public void setCorrente_induzida(Double fem, Double resistencia) {
		double corrente_i = fem / resistencia;
		
		// Acrescentar validação 
		if(corrente_i < Configuracoes.CORRENTE_INDUZIDA_MIN || corrente_i > Configuracoes.CORRENTE_INDUZIDA_MAX)
			throw new IllegalArgumentException("A corrente induzida deve ser entre " + Configuracoes.CORRENTE_INDUZIDA_MIN + " e " + Configuracoes.CORRENTE_INDUZIDA_MAX + "!");
		this.corrente_induzida = corrente_i;
	}
	// Área do solenóide - a_sol​ = π * r²_sol
	public Double getA_sol() { return a_sol; }
	public void setA_sol(Double d_sol) {
		double r_sol = d_sol / 2.0;
		// Acrescentar validação
		if(r_sol < Configuracoes.RAIO_SOLENOIDE_MIN || r_sol > Configuracoes.RAIO_SOLENOIDE_MAX)
			throw new IllegalArgumentException("O raio do solenóide deve ser entre " + Configuracoes.RAIO_SOLENOIDE_MIN + " e " + Configuracoes.RAIO_SOLENOIDE_MAX + "metro(s)!");

		double area_solenoide = (Configuracoes.PI * Math.pow(r_sol, 2));
		// Acrescentar validação
		if(area_solenoide < Configuracoes.AREA_SOLENOIDE_MIN || area_solenoide > Configuracoes.AREA_SOLENOIDE_MAX)
			throw new IllegalArgumentException("A área do solenóide deve ser entre " + Configuracoes.AREA_SOLENOIDE_MIN + " e " + Configuracoes.AREA_SOLENOIDE_MAX + " metro(s)²!");

		this.a_sol = area_solenoide;
	}
	// FEM induzida - |ε| = N₂ * (Δt / |ΔΦ|)​
	public Double getFEM_induzida() { return FEM_induzida; }
	public void setFEM_induzida(Integer qtd_espiras, Double variacao, Double tempo) {
		double fem = qtd_espiras * (variacao / tempo);
		
		if(fem < Configuracoes.FEM_INDUZIDA_MIN || fem > Configuracoes.FEM_INDUZIDA_MAX)
			throw new IllegalArgumentException("A FEM induzida deve ser entre " + Configuracoes.FEM_INDUZIDA_MIN + " e " + Configuracoes.FEM_INDUZIDA_MAX + "volts!");
		
		this.FEM_induzida = fem; 
	}
	// Campo magnético dentro do solenóide - B = μ_0 ​* n * I
	public Double getCampo_mag_sol() { return c_mag_sol; }
	public void setCampo_mag_sol(Integer densidade_esp, Double corrente) {
		double campo_magnetico = Configuracoes.MI * densidade_esp * corrente;
		
		if(campo_magnetico < Configuracoes.CAMPO_MAGNETICO_MIN || campo_magnetico > Configuracoes.CAMPO_MAGNETICO_MAX)
			throw new IllegalArgumentException("O campo magnético deve ser entre " + Configuracoes.CAMPO_MAGNETICO_MIN + " e " + Configuracoes.CAMPO_MAGNETICO_MAX + " Tesla!");
		
		this.c_mag_sol = campo_magnetico;
	}
	// Variação do fluxo (espira) - ΔΦ = ΔB * a_sol​ => (0 − B_0​) * a_sol​
	public Double getVariacao_fluxo() { return variacao_fluxo; }
	public void setVariacao_fluxo(Double area_sol) {
		double b_0 = getCampo_mag_sol(); // Inicial
		setCampo_mag_sol(dados.getEspiras_m(), dados.getC_final()); 	
		double b_1 = getCampo_mag_sol(); // Final
		
		if(b_0 == b_1)
			throw new IllegalArgumentException("O campo magnético inicial não pode ser igual ao final!");
		
		double delta_b = b_1 - b_0;
		double var_fluxo = delta_b * area_sol;
		
		if(var_fluxo < Configuracoes.VARIACAO_FLUXO_MIN || var_fluxo > Configuracoes.VARIACAO_FLUXO_MAX)
			throw new IllegalArgumentException("A variação do fluxo deve estar entre " + Configuracoes.VARIACAO_FLUXO_MIN+ " e " + Configuracoes.VARIACAO_FLUXO_MAX + " Webers!");
		
		this.variacao_fluxo = var_fluxo;
	}
}