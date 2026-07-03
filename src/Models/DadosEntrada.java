package Models;

import Models.Configuracoes;

public class DadosEntrada {
	private Double  resistencia_bob; // Resistencia
	private Double  c_final;		 // Corrente final
	private Double  r_sol;           // Raio do solenoide
	private Double  c_inicial;       // Corrente inicial
	private Double  d_sol;           // Diâmetro da bobina
	private Double  intervalo_temp;  // Intervalo de tempo
	private Integer qtd_espiras;     // Quantidade total de espiras
	private Integer espiras_m;       // Quantidade de espiras por metro
	
	public DadosEntrada( Double resistencia,  Double c_final,
						 Double r_bobina,     Double c_inicial,
						 Double d_bobina,     Double intervalo_tempo,
						 Integer qtd_espiras, Integer espiras_m) {
		setResistencia(resistencia);
		setC_inicial(c_inicial);
		setC_final(c_final);
		setR_solenoide(r_bobina);
		setD_solenoide(d_bobina);
		setIntervalo_tempo(intervalo_tempo);
		setQtd_espiras(qtd_espiras);
		setEspiras_m(espiras_m);
	}
	// Resistênia das bobinas - Ω
	public Double getResistencia() { return resistencia_bob ; }
	public void setResistencia(Double resistencia) {
		if(resistencia < Configuracoes.RESISTENCIA_MIN || resistencia > Configuracoes.RESISTENCIA_MAX)
			throw new IllegalArgumentException("A resistência deve ser entre " + Configuracoes.RESISTENCIA_MIN + " e " + Configuracoes.RESISTENCIA_MAX + " Ohm's!");

		this.resistencia_bob = resistencia;
	}
	// Corrente final - I
	public Double getC_final() { return c_final; }
	public void setC_final(Double c_final) {
		if(c_final < Configuracoes.CORRENTE_MIN || c_final > Configuracoes.CORRENTE_MAX)
			throw new IllegalArgumentException("A corrente final deve ser um valor entre " + Configuracoes.CORRENTE_MIN + " e " + Configuracoes.CORRENTE_MAX + " Ampères!");

		this.c_final = c_final;
	}
	// Raio da bobina - r_bob
	public Double getR_solenoide() { return r_sol; }
	public void setR_solenoide(Double r_bobina) {
		if(r_bobina < Configuracoes.RAIO_SOLENOIDE_MIN || r_bobina > Configuracoes.RAIO_SOLENOIDE_MAX)
			throw new IllegalArgumentException("O raio da bobina deve ser um valor entre " + Configuracoes.RAIO_SOLENOIDE_MIN + " e " + Configuracoes.RAIO_SOLENOIDE_MAX + " metros!");

		this.r_sol = r_bobina;
	}
	// Corrente inicial - I
	public Double getC_inicial() { return c_inicial; }
	public void setC_inicial(Double c_inicial) {
		if(c_inicial < Configuracoes.CORRENTE_MIN || c_inicial > Configuracoes.CORRENTE_MAX)
			throw new IllegalArgumentException("A corrente inicial deve ser um valor entre " + Configuracoes.CORRENTE_MIN + " e " + Configuracoes.CORRENTE_MAX + " Ampères!");

		this.c_inicial = c_inicial;
	}
	// Diâmetro solenóide - d_sol
	public Double getD_solenoide() { return d_sol; }
	public void setD_solenoide(Double d_solenoide) {
		if(d_solenoide < Configuracoes.DIAMETRO_SOLENOIDE_MIN || d_solenoide > Configuracoes.DIAMETRO_SOLENOIDE_MAX)
			throw new IllegalArgumentException("O raio da bobina deve ser um valor entre " + Configuracoes.DIAMETRO_SOLENOIDE_MIN + " e " + Configuracoes.DIAMETRO_SOLENOIDE_MAX + " metros!");

		this.d_sol = d_solenoide;
	}
	// Intervalo de tempo - Δt
	public Double getIntervalo_tempo() { return intervalo_temp ; }
	public void setIntervalo_tempo(Double intervalo_tempo) {
		if(intervalo_tempo < Configuracoes.TEMPO_MIN || intervalo_tempo > Configuracoes.TEMPO_MAX)
			throw new IllegalArgumentException("O intervalo de tempo deve ser um valor entre " + Configuracoes.TEMPO_MIN + "e " + Configuracoes.TEMPO_MAX + " segundos!");

		this.intervalo_temp = intervalo_tempo;
	}
	// Espiras da bobina - N₂
	public Integer getQtd_espiras() { return qtd_espiras; }
	public void setQtd_espiras(Integer qtd_espiras) {
		if(qtd_espiras < Configuracoes.ESPIRAS_MIN || qtd_espiras > Configuracoes.ESPIRAS_MAX)
			throw new IllegalArgumentException("A quantidade total de espiras deve ser entre " + Configuracoes.ESPIRAS_MIN + " e " + Configuracoes.ESPIRAS_MAX + " unidades!");

		this.qtd_espiras = qtd_espiras;
	}
	// Densidade de espiras do solenóide - n
	public Integer getEspiras_m() { return espiras_m; }
	public void setEspiras_m(Integer espiras_m) {
		if(espiras_m < Configuracoes.DENSIDADE_MIN || espiras_m > Configuracoes.DENSIDADE_MAX)
			throw new IllegalArgumentException("A quantidade de espiras por metro deve ser entre " + Configuracoes.DENSIDADE_MIN + " e " + Configuracoes.DENSIDADE_MAX + " unidades por metro!");

		this.espiras_m = espiras_m;
	}
}
