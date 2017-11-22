package modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class Persona extends Thread {
	
	private byte id;
	private ArrayList<Atraccion> atraccionesVisitadas;
	private ArrayList<Atraccion> atraccionesPorVisitar;

	private Atraccion atraccionActual;
	public Semaphore continuar;
	public Semaphore personasBuscandoCola;

	public Persona(byte id, Atraccion[] arrayAtracciones, Semaphore personasBuscandoCola) {

		this.id = id;
		atraccionesVisitadas = new ArrayList<Atraccion>();
		atraccionesPorVisitar = new ArrayList<Atraccion>();
		atraccionesPorVisitar.addAll(Arrays.asList(arrayAtracciones));
		continuar = new Semaphore(0);
		this.personasBuscandoCola = personasBuscandoCola;
	}

	public void run() {

		while (atraccionesPorVisitar.size() != 0) {
			try {
				personasBuscandoCola.acquire();
				Thread.sleep(100);

				
				Atraccion atraccionAleatoria = seleccionaAtraccionAleatoria();

				System.out.println("Persona: " + id + " atraccionAleatoria " + atraccionAleatoria.getNombre());
				atraccionAleatoria.semaforoVerColaBinario.acquire();
				if (!atraccionAleatoria.isColaLlena()) {
					personasBuscandoCola.release();
					System.out.println("Persona: " + id + " encontrado atraccion " + atraccionAleatoria.getNombre());
					atraccionAleatoria.entraEnCola(this);
					atraccionActual = atraccionAleatoria;
				}
				else {
					atraccionAleatoria.semaforoVerColaBinario.release();
					personasBuscandoCola.release();
				}
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("**************************************Persona: **********************************" + id + " SALIENDO DEL PARQUE!!");
		Parque.getInstance().disminuirNumeroPersonas();
	}

	private Atraccion seleccionaAtraccionAleatoria() {
		System.out.println("Tamaño de tal: " + atraccionesPorVisitar.size());
		return atraccionesPorVisitar.get((int) (Math.random() * atraccionesPorVisitar.size()));
	}

	public void salirDeAtraccion() {		
		atraccionesVisitadas.add(atraccionActual);
		for(Atraccion a : atraccionesPorVisitar)
			System.out.println("Pre " + id + " " + a.getNombre());
		atraccionesPorVisitar.remove(atraccionActual);
		continuar.release();
		
		for(Atraccion a : atraccionesPorVisitar)
			System.out.println("Post " + id + " " + a.getNombre());
		System.out.println("Persona: " + id + " saliendo de atraccion");
	}
	
	public void salirDeCola() {
		continuar.release();
		System.out.println("Persona: " + id + " saliendo de cola");
	}

}
