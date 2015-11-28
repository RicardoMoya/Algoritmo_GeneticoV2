package Main;

import java.text.DecimalFormat;


public class Utils {
	
	/**
	 * Metodo para pasar un numero binario a entero
	 */
	public static int binario_a_entero(int[] Bit_Vector) {
		int resultado = 0; /* Resultado en entero */
		int lon = Bit_Vector.length - 1;

		for (int i = 0; i < Bit_Vector.length; i++) {
			if (Bit_Vector[i] == 1) {
				resultado += Math.pow(2, lon - i);
			}
		}
		return resultado;
	}
	
	/**
	 * Metodo que devuelve el valor de la funcion 
	 */
	public static double funcion (double x){
		return Math.abs((x-5)/(2+Math.sin(x)));
	}
	
	
	/*
	 * Imprime una matriz con formato
	 */
	public static String toString(int [][] m) {
		StringBuffer sb = new StringBuffer();
		sb.append("\t");
		for (int i = 0; i < m[0].length; ++i) {
			sb.append("C"+(i+1)).append("\t");
		}
		sb.append("\n\n");
		for (int r = 0; r < m.length; ++r) {
			sb.append("I"+(r+1)).append("\t");
			for (int c = 0; c < m[0].length; ++c) {
				sb.append(m[r][c]).append("\t");
			}
			sb.append("Valor del Individuo "+(r+1)+" = "+binario_a_entero(m[r])+"\n");
		}
		return sb.toString();
	}
	
	
	/*
	 * Imprime una matriz con formato
	 */
	public static String toString(double [][] m) {
		StringBuffer sb = new StringBuffer();
		sb.append("\t");
		for (int i = 0; i < m[0].length; ++i) {
			sb.append("C"+(i+1)).append("\t");
		}
		sb.append("\n\n");
		for (int r = 0; r < m.length; ++r) {
			sb.append("I"+(r+1)).append("\t");
			for (int c = 0; c < m[0].length; ++c) {
				sb.append(customFormat("###.###",m[r][c])).append("\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/*
	 * Da formato a la matriz que se imprime
	 */
	public static String customFormat(String pattern, double value) {
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		String output = myFormatter.format(value);
		return output;
	}

	

}
