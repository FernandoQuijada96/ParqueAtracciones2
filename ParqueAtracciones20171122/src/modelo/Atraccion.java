package modelo;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Atraccion {

	private static final byte[] POSIBLES_TAM_GRUPOS = { 2, 4, 8 };

	private final String nombre;

	public Semaphore semaforoGrupo;
	public Semaphore semaforoVerColaBinario;

	public ArrayList<Persona> personasEnCola;

	public Atraccion(String nombre) {
		this.nombre = nombre;
		inicializarSemaforoGrupo();
		semaforoVerColaBinario = new Semaphore(1);
		personasEnCola = new ArrayList<>();
	}

	public boolean isColaLlena() {
		return semaforoGrupo.availablePermits() == 0;
	}

	private void inicializarSemaforoGrupo() {
		semaforoGrupo = new Semaphore(tamGrupoAleatorio());
	}

	private byte tamGrupoAleatorio() {
		byte tam = POSIBLES_TAM_GRUPOS[(int) (Math.random() * POSIBLES_TAM_GRUPOS.length)];
		System.out.println(nombre + ": " + tam);
		return tam;
	}

	public void entraEnCola(Persona persona) throws InterruptedException {

		semaforoGrupo.acquire();
		personasEnCola.add(persona);
		semaforoVerColaBinario.release();
		if (isColaLlena())
			new EnAtraccion(this).start();

		persona.continuar.acquire();
		
	}

	public void finalizar() {
		int tamColaPersona = personasEnCola.size();
		for (Persona persona : personasEnCola)
			persona.salirDeAtraccion();
		personasEnCola.get(0).personasBuscandoCola.release(tamColaPersona);
		semaforoGrupo.release(personasEnCola.size());
		personasEnCola.removeAll(personasEnCola);
		
	}

	public void sacarDeCola() {
		for (Persona persona : personasEnCola) 
			persona.salirDeCola();
		semaforoGrupo.release(personasEnCola.size());
		personasEnCola.removeAll(personasEnCola);
	}

	public String getNombre() {
		return nombre;
	}

}
