package frsf.isi.died.guia08.problema01.modelo;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import frsf.isi.died.guia08.problema01.modelo.Empleado.Tipo;

public class TareaTest {
	
	public Tarea t1, t2;
	public Empleado e1, e2;

	@Before
	public void before() {
		e1 = new Empleado(2041048938, "Juan Perez", Tipo.CONTRATADO, 250.00, LocalDate.now());
		e2 = new Empleado(2041048939, "Pedro Valdivia", Tipo.EFECTIVO, 300.00, LocalDate.now());
		t1 = new Tarea();
		t2 = new Tarea();
		try {
			t2.asignarEmpleado(e2);
		} catch (TareaNoAsignableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void asignarEmpleadoTest() throws TareaNoAsignableException {
		t1.asignarEmpleado(e1);
		assertEquals(e1, t1.getEmpleadoAsignado());
	}
	
	@Test(expected = TareaNoAsignableException.class)
	public void asignarEmpleadoTestYaTieneEmpleadoAsignado() throws TareaNoAsignableException {
		t2.asignarEmpleado(e1);
		assertNotEquals(t2.getEmpleadoAsignado(), e1);
	}

}
