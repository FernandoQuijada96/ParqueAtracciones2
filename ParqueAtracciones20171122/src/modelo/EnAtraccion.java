package modelo;

public class EnAtraccion extends Thread {
	
	Atraccion atraccion;
	
	public EnAtraccion(Atraccion atraccion) {
		this.atraccion = atraccion;
	}
	private static final long TIEMPO_EN_ATRACCION = 3000;
	public void run() {
		
		try {
			atraccion.personasEnCola.get(0).personasBuscandoCola.acquire(atraccion.personasEnCola.size());
			System.out.println(" ----------EMPEZANDO ATRACION ----------" + atraccion.getNombre());
			Thread.sleep(TIEMPO_EN_ATRACCION);
			atraccion.finalizar();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
