package Main;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SampleController implements Initializable {
    
	
	public static int numIndividuos = 4;
	public static int numCromosomas = 4;
	public static int numGeneraciones = 50;
	public static int [][] individuos = new int [numIndividuos][numCromosomas];
	public static double [] valorIndividuos = new double [numIndividuos];
	public static double [] valorFuncionCalidadIndividuos = new double [numIndividuos];
	public static double pEmparejamiento = 0.7;
	public static double pMutacion = 0.3;
	public static double [][] mutaciones = new double [numIndividuos][numCromosomas];
	public static double [][] tramosSeleccion = new double [numIndividuos][2];
	public static double [][] tramosCorte = new double [numCromosomas-1][2];
	
	
    @FXML public LineChart<Double, Double> graph;
    @FXML public NumberAxis x;
    @FXML public NumberAxis y;
    @FXML public Button startBT;
    @FXML public TextArea textArea;
    @FXML public ChoiceBox chIndividuos;
    @FXML public ChoiceBox cdCromosomas;
    @FXML public ChoiceBox chEmparejamiento;
    @FXML public ChoiceBox chMutacion;
    @FXML public TextField tfGeneraciones;
    @FXML public TextField tfGeneracion;
    @FXML public TextField tfMejorIndividuo;
    @FXML public TextField tfFuncionCalidad;
    
    
    /**
     * Método que se ejecuta al pulsar el boton start
     * @param event
     */
    @FXML
    private void accionStart(ActionEvent event) {
    	
        numIndividuos = Integer.parseInt((String) chIndividuos.getValue());
        numCromosomas = Integer.parseInt((String) cdCromosomas.getValue());
        numGeneraciones = Integer.parseInt(tfGeneraciones.getText());
        pEmparejamiento = Double.parseDouble((String) chEmparejamiento.getValue());
        pMutacion = Double.parseDouble((String) chMutacion.getValue());
        
        individuos = new int [numIndividuos][numCromosomas];
    	valorIndividuos = new double [numIndividuos];
    	valorFuncionCalidadIndividuos = new double [numIndividuos];
    	mutaciones = new double [numIndividuos][numCromosomas];
    	tramosSeleccion = new double [numIndividuos][2];
    	tramosCorte = new double [numCromosomas-1][2];
        
        
        // Creo la gráfica
    	inicializarGrafica();
        inicializarTramosCorte();
		generarIndividuos();
		textArea.setText("");
		textArea.setText(textArea.getText()+"\n/======== INICIALIZACIÓN DE INDIVIDUOS ========/\n");
		textArea.setText(textArea.getText()+Utils.toString(individuos)+"\n\n");
		
		for (int i=0; i<numGeneraciones; i++){
			textArea.setText(textArea.getText()+"\n/======== GENERACIÓN "+(i+1)+" ========/\n");
			textArea.setText(textArea.getText()+"\n/*** 1.- Individuos de la generación "+(i+1)+" ***/\n");
			textArea.setText(textArea.getText()+Utils.toString(individuos)+"\n");
			// Aplicación de la función de calidad
			textArea.setText(textArea.getText()+"\n/*** 2.- Aplicamos la función de calidad a los individuos ***/\n");
			setValorIndividuo();
			for(int j=0; j<numIndividuos; j++){
				double valor = Utils.funcion((int) valorIndividuos[j]);
				textArea.setText(textArea.getText()+"I"+(j+1)+" -> f("+valorIndividuos[j]+") = "+valor+"\n");
				valorFuncionCalidadIndividuos[j] = valor;
			}
			// Calculo de las probabilidades para que un individuo sea seleccionado
			calcularTramosSelección ();
			textArea.setText(textArea.getText()+"\n/*** 3.- Seleccion de un individuo ***/\n");
			textArea.setText(textArea.getText()+"Calculamos las probabilidades de seleccón de cada individuo\n");
			
			for (int j = 0; j < tramosSeleccion.length; ++j) {
				textArea.setText(textArea.getText()+"I"+(j+1)+" -> ["+tramosSeleccion[j][0]+" , "+tramosSeleccion[j][1]+")\n");
			}
			
			// Seleccionamos al candidato
			double random = Math.random();
			textArea.setText(textArea.getText()+"\nSeleccionamos un Individuo. \nNº Aleatorio obtenido = "+random+"\n");
			int seleccionado = getIndividuoSeleccionado(random);
			textArea.setText(textArea.getText()+"Individuo seleccionado: "+seleccionado+"\n");
			
			// Operación de emparejamiento
			textArea.setText(textArea.getText()+"\n/*** 4.- Operación de emparejamiento ***/\n");
			random = Math.random();
			textArea.setText(textArea.getText()+"\n¿Hay emparejamiento en esta generación?\nNº Aleatorio obtenido = "+random+" es menor que 0.7 \n");
			if (random<pEmparejamiento){
				textArea.setText(textArea.getText()+"SI => Hay emparejamiento en esta generación\n");
				random = Math.random();
				textArea.setText(textArea.getText()+"Calculo la posición del corte. Nº Aleatorio para el corte = "+random+"\n");
				int corte = getCorte(random);
				textArea.setText(textArea.getText()+"Realizo el corte en el cromosoma numero "+corte+"\n");
				emparejar(seleccionado, corte);
				textArea.setText(textArea.getText()+"Nuevos individuos despues del emparejamiento:\n\n");
				textArea.setText(textArea.getText()+Utils.toString(individuos)+"\n");
			}else{
				textArea.setText(textArea.getText()+"NO => No hay emparejamiento en esta generación\n");
			}
			
			// Operación de mutación
			textArea.setText(textArea.getText()+"\n/*** 5.- Operación de mutación ***/\n");
			textArea.setText(textArea.getText()+"\nGeneramos numeros aleatorios para las mutaciones. \nLos cromosomas que tengan numeros aleatorios menores que 0.3 mutaran\n\n");
			generarMutaciones();
			textArea.setText(textArea.getText()+Utils.toString(mutaciones)+"\n");
			mutar();
			textArea.setText(textArea.getText()+"\nIndividuos despues de sufrir las mutaciones\n");
			textArea.setText(textArea.getText()+Utils.toString(individuos)+"\n");
			setValorIndividuo();
			textArea.setText(textArea.getText()+"\nEl mejor individuo de la generación "+(i+1)+" es el individuo numero "+getMejorIndividuo()+"\n");
			textArea.setText(textArea.getText()+"\nEl valor de su función de calidad es: "+Utils.funcion((int) valorIndividuos[getMejorIndividuo()-1])+"\n");
		
			tfGeneracion.setText(""+(i+1));
			tfMejorIndividuo.setText(""+valorIndividuos[getMejorIndividuo()-1]);
			tfFuncionCalidad.setText(""+Utils.funcion((int) valorIndividuos[getMejorIndividuo()-1]));
			
		}
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	// Pongo valores por defecto para aplicar el algoritmo
        chIndividuos.setValue("4");
        cdCromosomas.setValue("4");
        tfGeneraciones.setText("10");
        chEmparejamiento.setValue("0.7");
        chMutacion.setValue("0.3");
    }  
    
    

    
    
    /**
	 * Método para generar el conjunto inicial de individuos
	 */
	public static void generarIndividuos(){
		for (int i=0; i<numIndividuos; i++){
			for (int j=0; j<numCromosomas; j++){
				double aleatorio = Math.random();
				if (aleatorio < 0.5)
					individuos[i][j] = 0;
				else
					individuos[i][j] = 1;
			}
		}
	}
	
	
	/**
	 * Método para calcular el valor de cada individuo
	 */
	public static void setValorIndividuo (){
		for (int i=0; i<numIndividuos; i++){
			valorIndividuos [i] = Utils.binario_a_entero(individuos[i]);
		}
	}
	
	
	/**
	 * Método para calcular los tramos de seleccion de cada individuo
	 */
	public static void calcularTramosSelección (){
		double sum = 0;
		
		// Calculamos la suma de todos los valores de los individuos
		for (int i=0; i<numIndividuos; i++){
			sum += Utils.funcion((int) valorIndividuos[i]);
		}
		
		double contador = 0;
		// Normalizamos los valores para hacer la seleccion
		for (int i=0; i<numIndividuos; i++){
			double val = Utils.funcion((int) valorIndividuos[i])/(double) sum;
			tramosSeleccion[i][0] = contador;
			contador += val;
			tramosSeleccion[i][1] = contador;
		}
		
	}
	
	
	/**
	 * Método que nos da el individuo seleccionado en cada generacion
	 * @param random
	 * @return individuo seleccionado
	 */
	public static int getIndividuoSeleccionado (double random){
		int seleccion = 0;
		boolean encontrado = false;
		
		while (!encontrado){
			if (tramosSeleccion[seleccion][0]<=random & tramosSeleccion[seleccion][1]>random)
				encontrado = true;
			else
				seleccion ++;			
		}
		return seleccion+1;
	}
	
	
	/**
	 * Metodo para crear los tramos de corte para el emparejamiento
	 */
	public static void inicializarTramosCorte (){
		double x = 1/(double) (numCromosomas-1);
		double contador = 0;
		for (int i=0; i<numCromosomas-1; i++){
			tramosCorte[i][0] = contador;
			contador += x;
			tramosCorte[i][1] = contador;
		}
	}
	
	
	/**
	 * Método que calcula el punto en el que hay que hacer el corte para el emparejamiento
	 * @param random
	 * @return numero del corte
	 */
	public static int getCorte (double random){
		int corte = 0;
		boolean encontrado = false;
		
		while (!encontrado){
			if (tramosCorte[corte][0]<=random & tramosCorte[corte][1]>random)
				encontrado = true;
			else
				corte ++;			
		}
		return corte+1;
	}
	
	
	/**
	 * Metodo para realizar los emparejamientos
	 * @param mejorIndividuo
	 * @param numCromosoma
	 */
	public static void emparejar (int mejorIndividuo, int numCromosoma){
		int [] bestIndividuo = new int [numCromosomas];
		// Copio el mejor individuo
		for (int i=0; i<numCromosomas; i++){
			bestIndividuo[i] = individuos[mejorIndividuo-1][i];
		}
		
		// Emparejo el mejor individuo con el resto
		for (int i=0; i<numIndividuos; i++){
			for (int j=numCromosoma; j<numCromosomas; j++){
				individuos[i][j] = bestIndividuo[j];
			}
		}
		
	}
	
	
	/**
	 * Método que genera una matriz para ver que cromosomas mutar
	 */
	public static void generarMutaciones(){
		for (int i=0; i<numIndividuos; i++){
			for (int j=0; j<numCromosomas; j++){
				mutaciones[i][j] = Math.random();
			}
		}
	}
	
	
	/**
	 * Método que realiza las mutaciones
	 */
	public static void mutar(){
		for (int i=0; i<numIndividuos; i++){
			for (int j=0; j<numCromosomas; j++){
				double mutacion = mutaciones[i][j];
				if (mutacion < pMutacion){
					if (individuos[i][j] == 0)
						individuos[i][j] = 1;
					else
						individuos[i][j] = 0;
				}
			}
		}
	}
	
	
	/**
	 * Metodo que calcula el mejor individuo de los que hay
	 * @return mejor individuo
	 */
	public static int getMejorIndividuo (){
		int individuo = 0;
		double fun = Double.MIN_VALUE;
		
		for (int i=0; i<numIndividuos; i++){
			double valorFuncion = Utils.funcion((int) valorIndividuos[i]);
			if (valorFuncion > fun){
				fun = valorFuncion;
				individuo = i;
			}
		}
		return individuo+1;
	}
	
	
	/**
	 * Método para inicializar la gráfica
	 * 
	 * @param cont
	 */
	private void inicializarGrafica() {
		ObservableList<XYChart.Series<Double, Double>> lineChartData = FXCollections
				.observableArrayList();
		LineChart.Series<Double, Double> series = new LineChart.Series<Double, Double>();
		series.setName("f(x)");
		for (double i = 0.0; i < getRango(numCromosomas); i = i + 0.5) {
			series.getData().add(new XYChart.Data<Double, Double>(i, Utils.funcion(i)));
		}
		lineChartData.add(series);

		graph.setData(lineChartData);
		graph.createSymbolsProperty();

	}
	
	
	public static int getRango(int numCromosomas) {
		switch (numCromosomas) {
		case 4:
			return 15;
		case 5:
			return 31;
		case 6:
			return 63;
		case 7:
			return 127;
		case 8:
			return 255;
		default:
			return 15;
		}
	}
	
}
