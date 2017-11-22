package modelo;

import java.util.concurrent.Semaphore;

public class Parque {

	private final static String[] NOMBRE_ATRACCIONES = { "Dinosaur", "Mine Train", "Big Thunder Mountain" };
	private static final byte MIN_PERSONAS_EN_PARQUE = 8;
	/*
	 * , "Test Track", "Splash Mountain", "Soarin",
	 * "The Twilight Zone Tower of Terror", "Rock 'n' Roller Coaster", "Bataman",
	 * "Expedition Everest", "Space Mountain", "Star Wars Guided Tours",
	 * "Disney Magic Kingdom", "Universal Volcano Bay", "Coaster Express",
	 * "Superman", "Stunt Fall", "Coches Chocones", "Tom & Jerry",
	 * "PeterPan's Flight" };
	 */

	private Atraccion[] arrayAtracciones;
	private Persona[] arrayHilosPersona;
	private RevisorDeColas revisor;

	private Semaphore personasBuscandoCola;

	private byte numPersonasEnParque;
	private Semaphore semaforoNumPersonasEnParque;
	private static Parque parque;
	//Singleton
	private Parque(final byte numPersonas) {
		personasBuscandoCola = new Semaphore(numPersonas);
		inicializarAtracciones();
		inicializarRevisor(numPersonas);
		inicializarPersonas(numPersonas);
		numPersonasEnParque = numPersonas;
		semaforoNumPersonasEnParque = new Semaphore(1);
	}

	private void inicializarRevisor(byte numPersonas) {

		revisor = new RevisorDeColas(numPersonas, personasBuscandoCola, arrayAtracciones);
		revisor.start();

	}

	private void inicializarAtracciones() {
		setArrayAtracciones(new Atraccion[NOMBRE_ATRACCIONES.length]);
		for (byte i = 0; i < NOMBRE_ATRACCIONES.length; i++)
			arrayAtracciones[i] = new Atraccion(NOMBRE_ATRACCIONES[i]);
	}

	private void inicializarPersonas(byte numPersonas) {
		setArrayHilosPersona(new Persona[numPersonas]);
		for (byte i = 0; i < numPersonas; i++) {
			arrayHilosPersona[i] = new Persona(i, arrayAtracciones, personasBuscandoCola);
			arrayHilosPersona[i].start();
		}

		try {
			for (byte i = 0; i < numPersonas; i++)
				arrayHilosPersona[i].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static Parque getParque() {
		return parque;
	}

	//Sobrecarga instancia Singleton
	public static Parque getInstance(byte numPersonas) {		
		if(getParque() == null)
			parque = new Parque(numPersonas);
		return parque;		
	}
	
	public static Parque getInstance() {
		return getInstance((byte) 0);
	}

	public void disminuirNumeroPersonas() {
		try {
			semaforoNumPersonasEnParque.acquire();
			numPersonasEnParque--;
			if (numPersonasEnParque < MIN_PERSONAS_EN_PARQUE) {
				for (byte i = 0; i < arrayHilosPersona.length; i++) {
					arrayHilosPersona[i].interrupt();
				}
			}
			semaforoNumPersonasEnParque.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public Atraccion[] getArrayAtracciones() {
		return arrayAtracciones;
	}

	public void setArrayAtracciones(Atraccion[] arrayAtracciones) {
		this.arrayAtracciones = arrayAtracciones;
	}

	public Persona[] getArrayHilosPersona() {
		return arrayHilosPersona;
	}

	public void setArrayHilosPersona(Persona[] arrayHilosPersona) {
		this.arrayHilosPersona = arrayHilosPersona;
	}
}
