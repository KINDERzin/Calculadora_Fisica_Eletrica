package application;
	
import Views.CalculadoraView;
import javafx.application.Application;

public class Main {
	
	public static void main(String[] args) {
        // O launch agora aponta diretamente para a sua tela principal
		Application.launch(CalculadoraView.class, args);
	}
}