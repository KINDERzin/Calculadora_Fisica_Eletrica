package Models;

public class DadosEntrada {
	private Double  resistencia_bob; // Resistencia
	private Double  c_final;		 // Corrente final
	private Double  r_bob;           // Raio da bobina
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
		setC_final(c_final);
		setR_bobina(r_bobina);
		setC_inicial(c_inicial);
		setD_solenoide(d_bobina);
		setIntervalo_tempo(intervalo_tempo);
		setQtd_espiras(qtd_espiras);
		setEspiras_m(espiras_m);
	}
	// Resistênia das bobinas - Ω
	public Double getResistencia() { return resistencia_bob ; }
	public void setResistencia(Double resistencia) {
		//if() Erro de numero (resistencia >= x, < x....) 
		this.resistencia_bob = resistencia;
	}
	// Corrente final - I
	public Double getC_final() { return c_final; }
	public void setC_final(Double c_final) {
		if(c_final < 0.0)
			throw new IllegalArgumentException("A corrente final deve ser maio ou igual a 0!");
		
		this.c_final = c_final;
	}
	// Raio da bobina - r_bob
	public Double getR_bobina() { return r_bob; }
	public void setR_bobina(Double r_bobina) {
		if(r_bob < 0.1)
			throw new IllegalArgumentException("O raio da bobina deve ser maior que 0!");

		this.r_bob = r_bobina;
	}
	// Corrente inicial - I
	public Double getC_inicial() { return c_inicial; }
	public void setC_inicial(Double c_inicial) {
		if(c_inicial < 0.0)
			throw new IllegalArgumentException("A corrente inicial deve ser maior ou igual a 0!");
		
		this.c_inicial = c_inicial;
	}
	// Diâmetro solenóide - d_sol
	public Double getD_solenoide() { return d_sol; }
	public void setD_solenoide(Double d_bobina) {
		if(d_bobina < 0.1)
			throw new IllegalArgumentException("O raio da bobina deve ser maio que 0!");
		
		this.d_sol = d_bobina; 
	}
	// Intervalo de tempo - Δt
	public Double getIntervalo_tempo() { return intervalo_temp ; }
	public void setIntervalo_tempo(Double intervalo_tempo) {
		if(intervalo_tempo < 0.0)
			throw new IllegalArgumentException("O intervalo de tempo deve ser maior que 0!");

		this.intervalo_temp = intervalo_tempo;
	}
	// Espiras da bobina - N₂
	public Integer getQtd_espiras() { return qtd_espiras; }
	public void setQtd_espiras(Integer qtd_espiras) {
		if(qtd_espiras <= 0)
			throw new IllegalArgumentException("A quantidade total de espiras deve ser maior que 0!");
		
		this.qtd_espiras = qtd_espiras;
	}
	// Densidade de espiras do solenóide - n
	public Integer getEspiras_m() { return espiras_m; }
	public void setEspiras_m(Integer espiras_m) {
		if(espiras_m <= 0)
			throw new IllegalArgumentException("A densidade de espiras por metro deve ser maior que 0!");
		
		this.espiras_m = espiras_m;
	}
}
