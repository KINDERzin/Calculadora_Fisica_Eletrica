package Models;

public final class Configuracoes {
	private Configuracoes() {
		throw new UnsupportedOperationException("A classe das configurações NÃO PODE/DEVE ser INSTANCIADA!");
	}
	
	// Valor de MI
	public static final Double MI = 4 * Math.PI * Math.pow(10, -7);
	// Valor de PI
	public static final Double PI = Math.PI;	
	// Resistência mínima e máxima
	public static final Double RESISTENCIA_MIN = 0.1; 
	public static final Double RESISTENCIA_MAX = 1000.0;
	// Corrente mínima e máxima
	public static final Double CORRENTE_MIN = 0.0;
	public static final Double CORRENTE_MAX = 100.0;
	// Raio mínimo e máximo da bobina 
	public static final Double RAIO_SOLENOIDE_MIN = 0.001;
	public static final Double RAIO_SOLENOIDE_MAX = 1.0;
	// Diametro mínimo e máximo da bobina
	public static final Double DIAMETRO_SOLENOIDE_MIN = 0.001;
	public static final Double DIAMETRO_SOLENOIDE_MAX = 2.0;
	// Intervalo mínimo e máximo de tempo
	public static final Double TEMPO_MIN = 0.001;
	public static final Double TEMPO_MAX = 3600.0;
	// Quantidade mínima e máxima de espiras
	public static final Integer ESPIRAS_MIN = 1;
	public static final Integer ESPIRAS_MAX = 10000;
	// Quantidade mínima e máxima de espiras por metro
	public static final Integer DENSIDADE_MIN = 1;
	public static final Integer DENSIDADE_MAX = 100000;
	
	// Área mínima e máxima do solenóide
	public static final Double AREA_SOLENOIDE_MIN = 3.14 * Math.pow(10, -6);
	public static final Double AREA_SOLENOIDE_MAX = 3.14;
	// Campo magnético mínimo e máximo do solenóide
	public static final Double CAMPO_MAGNETICO_MIN = 0.0;
	public static final Double CAMPO_MAGNETICO_MAX = 12.57;
	// Variação mínima e máxima do fluxo
	public static final Double VARIACAO_FLUXO_MIN = 0.0;
	public static final Double VARIACAO_FLUXO_MAX = 39.5;
	// FEM induzida mínima e máxima
	public static final Double FEM_INDUZIDA_MIN = 0.0;
	public static final Double FEM_INDUZIDA_MAX = 100000.0;
	// Corrente induzida mínima e máxima
	public static final Double CORRENTE_INDUZIDA_MIN= 0.0;
	public static final Double CORRENTE_INDUZIDA_MAX= 1000.0;
	
}
