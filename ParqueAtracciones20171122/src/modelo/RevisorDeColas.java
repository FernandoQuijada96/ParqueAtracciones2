package modelo;

import java.util.concurrent.Semaphore;

public class RevisorDeColas extends Thread {

	private byte numPersonas;
	private Semaphore personasBuscandoCola;
	private static final long TIEMPO_ENTRE_REVISIONES = 1000;
	private Atraccion[] arrayAtracciones;

	public RevisorDeColas(final byte numPersonas, Semaphore personasBuscandoCola, Atraccion[] arrayAtracciones) {
		this.numPersonas = numPersonas;
		this.personasBuscandoCola = personasBuscandoCola;
		this.arrayAtracciones = arrayAtracciones;
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(TIEMPO_ENTRE_REVISIONES);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Revisor despierto");
			if (personasBuscandoCola.availablePermits() == numPersonas) {
				try {
					System.out.println("Revisor entra en el if");
					personasBuscandoCola.acquire(numPersonas);
					System.out.println("Revisor va a sacar a gente");
					sacarPersonasDeLaMenorCola();

					personasBuscandoCola.release(numPersonas);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}
	}

	private void sacarPersonasDeLaMenorCola() {
		Atraccion atraccionConMenorCola = null;
		byte tamMinimo = Byte.MAX_VALUE;
		byte personasEnCola;
		for (byte i = 0; i < arrayAtracciones.length; i++) {
			personasEnCola = (byte) arrayAtracciones[i].personasEnCola.size();
			if (personasEnCola < tamMinimo && personasEnCola > 0) {
				atraccionConMenorCola = arrayAtracciones[i];
				tamMinimo = personasEnCola;
			}
		}

		if (atraccionConMenorCola != null) {
			System.out.println("Revisor echa gente de " + atraccionConMenorCola.getNombre());

			atraccionConMenorCola.sacarDeCola();
		}

	}
}
