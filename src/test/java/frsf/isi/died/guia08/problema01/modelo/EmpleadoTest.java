package frsf.isi.died.guia08.problema01.modelo;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;



public class EmpleadoTest {
	
	Empleado e1, e2, e3;
	Tarea t1, t2, t3, t4, t5;
	
	
	public void setCalculoPagoPorTareaTest(Empleado emp) {
		Function<Tarea,Double> calculoPagoPorTarea = (t)->{
						double costoParticularHora = emp.getCostoHora();
						long days = ChronoUnit.DAYS.between(t.getFechaInicio(), t.getFechaFin());
						long horasReales = days*4;
						int duracionEstimada = t.getDuracionEstimada().intValue();
						if(horasReales < duracionEstimada) {
							costoParticularHora*=1.20;
						}
						return costoParticularHora*duracionEstimada;
				};
		
		emp.setCalculoPagoPorTarea(calculoPagoPorTarea);
	}
	
	public void setPuedeAsignarTareaTrue(Empleado emp) {
		emp.setPuedeAsignarTarea((t->true));
	}
	
	public void setPuedeAsignarTareaTestHoras(Empleado emp) {
		
		Predicate<Tarea> puedeAsignarTareaH = (t) -> {
			return emp.getTareasAsignadas().stream()
				.filter(tarea->tarea.getFechaFin()==null)
				.mapToInt(tarea->tarea.getDuracionEstimada())
				.sum() + t.getDuracionEstimada().intValue() <=15;
		};
		emp.setPuedeAsignarTarea(puedeAsignarTareaH);
	}
	
	public void setPuedeAsignarTareaTestCantTareas(Empleado emp) {
		Predicate<Tarea> puedeAsignarTarea = (t) -> {
			long contadorTareas = emp.getTareasAsignadas().stream()
				.filter(tarea->tarea.getFechaFin()==null)
				.count();
			return contadorTareas < 3;
		};
		emp.setPuedeAsignarTarea(puedeAsignarTarea);
	}

	@Before
	public void before() {
		e1 = new Empleado(2041048938, "Juan Perez", null, 250.00, LocalDate.now());
		e2 = new Empleado(2041048939, "Pedro Valdivia", null, 300.00, LocalDate.now());
		e3 = new Empleado(2041048939, "James Olivares", null, 300.00, LocalDate.now());
		t1 = new Tarea();
		t1.setId(1);
		t1.setDuracionEstimada(2);
		t1.setFacturada(Boolean.FALSE);
		t2 = new Tarea();
		t2.setId(2);
		t2.setDuracionEstimada(4);
		t2.setFacturada(Boolean.FALSE);
		t3 = new Tarea();
		t3.setId(3);
		t3.setDuracionEstimada(6);
		t3.setFacturada(Boolean.FALSE);
		t4 = new Tarea();
		t4.setId(4);
		t4.setDuracionEstimada(2);
		t4.setFacturada(Boolean.FALSE);
		t5 = new Tarea();
		t5.setId(5);
		t5.setDuracionEstimada(4);
		t5.setFacturada(Boolean.FALSE);
		this.setPuedeAsignarTareaTrue(e1);
		this.setCalculoPagoPorTareaTest(e1);
		this.setPuedeAsignarTareaTestHoras(e2);
		this.setPuedeAsignarTareaTestCantTareas(e3);
		
		//
		
		//
	}
	
	@Test
	public void testSalario() throws TareaNoAsignableException, TareaNoExisteException {
		e1.asignarTarea(t1);
		e1.asignarTarea(t2);
		e1.asignarTarea(t3);
		e1.asignarTarea(t4);
		e1.asignarTarea(t5);
		e1.comenzar(1);
		e1.comenzar(2);
		e1.comenzar(3);
		e1.finalizar(1);
		e1.finalizar(2);
		e1.finalizar(3);
		
		assertEquals(e1.salario().doubleValue(), 3600.0, 0.0);
	}
	
	@Test
	public void testCostoTarea() throws TareaNoAsignableException, TareaNoExisteException {
		e1.asignarTarea(t1);
		e1.comenzar(1);
		e1.finalizar(1);
		assertEquals(e1.costoTarea(t1), 600.0 ,0.0);
	}
	
	@Test
	public void testAsignarTareaExito() throws TareaNoAsignableException {
		e2.asignarTarea(t1);
		e3.asignarTarea(t2);
		assertTrue(e2.getTareasAsignadas().contains(t1));
		assertTrue(e3.getTareasAsignadas().contains(t2));
	}
	

	@Test(expected = TareaNoAsignableException.class)
	public void testAsignarTareaFracasoCantidad() throws TareaNoAsignableException {
		e3.asignarTarea(t1);
		e3.asignarTarea(t2);
		e3.asignarTarea(t3);
		e3.asignarTarea(t4);
	}

	@Test(expected = TareaNoAsignableException.class)
	public void testAsignarTareaFracasoHoras() throws TareaNoAsignableException {
		e2.asignarTarea(t1);
		e2.asignarTarea(t2);
		e2.asignarTarea(t3);
		e2.asignarTarea(t4);
		e2.asignarTarea(t5);
	}
	

	@Test
	public void testComenzarIntegerExito() throws TareaNoAsignableException, TareaNoExisteException {
		e1.asignarTarea(t1);
		e1.comenzar(t1.getId());
		assertNotNull(t1.getFechaInicio());
	}
	
	@Test(expected = TareaNoExisteException.class)
	public void testComenzarIntegerTareaNoExiste() throws TareaNoAsignableException, TareaNoExisteException {
		e1.comenzar(Integer.valueOf(50));
	}
	
	
	@Test
	public void testFinalizarIntegerExito() throws TareaNoAsignableException, TareaNoExisteException {
		e1.asignarTarea(t1);
		e1.comenzar(t1.getId());
		e1.finalizar(t1.getId());
		assertNotNull(t1.getFechaFin());
	}
	
	@Test(expected = TareaNoExisteException.class)
	public void testFinalizarIntegerNoExiste() throws TareaNoAsignableException, TareaNoExisteException {
		e1.finalizar(90);
	}

	@Test
	public void testComenzarIntegerStringExito() throws TareaNoAsignableException, TareaNoExisteException {
		e1.asignarTarea(t1);
		e1.comenzar(t1.getId(), "20-12-2004 12:00");
		assertTrue(t1.getFechaInicio().getDayOfMonth()==20);
		assertTrue(t1.getFechaInicio().getMonth()==Month.DECEMBER);
		assertTrue(t1.getFechaInicio().getYear()==2004);
		assertTrue(t1.getFechaInicio().getHour()==12);
		assertTrue(t1.getFechaInicio().getMinute()==0);
	}
	
	@Test(expected = TareaNoExisteException.class)
	public void testComenzarIntegerStringNoExiste() throws TareaNoAsignableException, TareaNoExisteException {
		e1.asignarTarea(t1);
		e1.comenzar(44, "20-12-2004 12:00");
	}

	@Test
	public void testFinalizarIntegerStringExito() throws TareaNoAsignableException, TareaNoExisteException {
		e1.asignarTarea(t1);
		e1.comenzar(t1.getId(), "20-12-2004 12:00");
		e1.comenzar(t1.getId(), "30-01-2005 11:59");
		assertTrue(t1.getFechaInicio().getDayOfMonth()==30);
		assertTrue(t1.getFechaInicio().getMonth()==Month.JANUARY);
		assertTrue(t1.getFechaInicio().getYear()==2005);
		assertTrue(t1.getFechaInicio().getHour()==11);
		assertTrue(t1.getFechaInicio().getMinute()==59);
	}
	
	@Test(expected = TareaNoExisteException.class)
	public void testFinalizarIntegerStringNoExiste() throws TareaNoAsignableException, TareaNoExisteException {
		e1.asignarTarea(t1);
		e1.finalizar(31, "20-12-2004 12:00");
	}

}
