
package odisseo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
//import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class Progetto {
	static String[][] matrice;
	
	
	public static int trovaNodoMax(String nomeFile) {// preso un file nel nostro standard trova il numero massimo tra i numeri che compaiono (escluso il numero di rotte)
		// apro il file
		
		File file = new File(nomeFile);
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("errore " + e);
		}
		StringTokenizer simboli;
		int max = 0;
		int j = 0;
		String riga = scan.nextLine();
		int numero_percorsi = Integer.parseInt(riga);
		while (scan.hasNextLine() && j < numero_percorsi) {//"tokenizzo" una riga alla volta e cerco il massimo tra il primo e il terzo simbolo
			j++;
			riga = scan.nextLine();
			// System.out.println(riga);
			simboli = new StringTokenizer(riga, " ");
			max = Math.max(max, Integer.parseInt(simboli.nextToken()));
			simboli.nextToken();
			max = Math.max(max, Integer.parseInt(simboli.nextToken()));
		}

		return max;

	}
	public static String trovaPercorso(String nomeFile) {//legge all'interno del file il percorso indicato da Hermes
		File file = new File(nomeFile);
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("errore " + e);
		}
		int j = 0;
		String riga = scan.nextLine();
		int numero_percorsi = Integer.parseInt(riga);
		while (scan.hasNextLine() && j < numero_percorsi) {
			j++;
			riga = scan.nextLine();
		}
		
		if(scan.hasNextLine()){
			
			riga = scan.nextLine();
			if(riga.isEmpty()){
				return "'\n'";
			}
			if (riga.charAt(0)=='\n') {
				return "'\n'"; //se non trova nessuna parola alla fine del file come messaggio utilizza "\n" che non sarà mai un messaggio giusto
			}
			else {
				return riga;
			}
		}
		else{
			return "'\n'";
		}
	}
	public static void modificaMatrice(String nomeFile, int max) {   //modifica la matrice in modo da mettere all'elemento i,j il nume che collega i a j mette 0 se non c'è nessun nume
		for(int i=0; i<max; i++){                                    //la complessità di questa funzione è O(n^2) (n è la grnadezza della matrice in questo caso max, dove max è il valore del nodo più alto es: se i nodi sono {1,2,100} max sarà 100) ed è data dai primi due for che riempiono la matrice di caratteri vuoti 
			for(int j=0; j<max; j++){
				matrice[i][j]=" ";
			}
		}
		
		File file = new File(nomeFile);
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("errore " + e);
		}
		StringTokenizer simboli;
		String riga = scan.nextLine();
		int numero_percorsi = Integer.parseInt(riga);
		int count=0;
		int i,j;
		while (scan.hasNextLine() && count < numero_percorsi) {//riempio la matrice come detto sopra
			count++;
			riga = scan.nextLine();
			simboli = new StringTokenizer(riga, " ");
			i=Integer.parseInt(simboli.nextToken());
			riga=simboli.nextToken();
			j=Integer.parseInt(simboli.nextToken());
			//System.out.println("riga:"+ riga + ", i=" + i + ", j=" + j + "matrice[" + i + "][" + j +"]="+ matrice[i][j]);
			if(matrice[i][j].equals(" "))matrice[i][j]=riga;//controllo se la cella è piena i.e. se il percorso ha già un nume
			else matrice[i][j]=matrice[i][j]+riga;
		}
	}
	public static Vector <Integer> trovaArrivi(int p, int max, char nume){//preso in imput il numero di una riga p e il nome di un nume ritorna tutte le colonne che nella riga 'p' hanno quel nume 
		Vector <Integer> arrivi = new Vector<>();
		int c=0;
		//String nume1= String.valueOf(nume);
		for(int i=0; i<max; i++){
			int lung=matrice[p][i].length();
			for(int z=0; z<lung; z++) //essendo che in una cella ci possono essere più caratteri e quindi più numi(ho supposto che il nome di un nume è formato da un solo carattere) scorro tutti i caratteri della cella
			if(matrice[p][i].charAt(z)==nume){
				arrivi.add(c,i);
				c++;
			}
		}
		if(c==0)arrivi.clear();//se non trovo nessun possibile arrivo allora azzero i possibili arrivi
		return arrivi;
	}
	public static Vector <Integer> terminal(Vector <Integer> pa, String percorso, int max, int k, int stop){//terminal prende in input un vettore di punti di partenza (pa), un percorso(percorso), a che punto siamo arrivati del percorso(k)
		int i=pa.size();																					//la funzione procede salvando in pa le possibili posizioni raggiunte seguendo il percorso dato da Hermes al passo k
		Vector <Integer> tmp = new Vector<>();																//INV il percorso minimo tra il nodo 0 e i nodi contenuti in pa è sempre minore uguale a k, quindi, quando termina la funzione avremo che la distanza tra il nodo di partenza e quelli contenuti in pa sarà sempre minore uguale a k
		tmp.clear();																						//INV k sarà sempre minore-uguale della lunghezza della parola che indica il percorso dato da Hermes
		for(int j=0; j<i; j++){
			tmp.addAll(trovaArrivi(pa.get(j), max, percorso.charAt(k) ));
		}
		tmp=new Vector <Integer> (new LinkedHashSet<Integer>(tmp)); //rimuove eventuali duplicati
		if(k<stop){
			return terminal(tmp, percorso,max,k+1, stop);//applica la ricorsione per calcolare le destinazioni possibili del passo sucessivo (k+1)
		}
			else return tmp;
	}
	public static boolean raggiungibile(String nomeFile) {//complessità: n^a dove n è il numero di nodi e a il numero di passi (prova tutti i percorsi possibili, forse è leggermente più efficente perchè nel caso in cui due percorsi all'iesimo passo si trovano nella stessa posizione allora vengono uniti)
		// TODO Auto-generated method stub
		//File file = new File(nomeFile);
		int max = trovaNodoMax(nomeFile);
		matrice = new String[max+1][max+1];
		String percorso=trovaPercorso(nomeFile);
		int lungPercorso=percorso.length();
		modificaMatrice(nomeFile,max+1);
		
		/*for(int i=0; i<max+1; i++){		//stampo la matrice		
			for(int j=0; j<max+1; j++){
				System.out.print("|");
				System.out.print(matrice[i][j]);
			}
			.
		System.out.print('\n');
		}*/
		
		
		Vector <Integer> pa = new Vector<>();
		pa.add(0,0); //pa mi dice in che nodo mi trovo
		pa=terminal(pa, percorso, max+1, 0,lungPercorso-1);
		
		
		boolean result=false;
		int c=pa.size();
		for(int i=0;i<c;i++){
			if(pa.get(i)==1) result=true;
		}
		return result;

	}
	public static void main(String[] args) {
		String f = "C:/Users/pop corn/workspace/odisseo/src/odisseo/test1.txt"; // "/home/zunino/java/progetto/test1.txt";
																					// //
																					// o
																					// altro
																					// nome
																					// di
																					// file
																					// nella
																					// vostra
																					// home
		System.out.println(raggiungibile(f));
	}
}