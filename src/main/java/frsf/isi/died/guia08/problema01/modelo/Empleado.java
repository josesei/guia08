package frsf.isi.died.guia08.problema01.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class Empleado {

	public enum Tipo { CONTRATADO,EFECTIVO}; 
	
	private Integer cuil;
	private String nombre;
	private Tipo tipo;
	private Double costoHora;
	private LocalDate fechaContratacion;
	private List<Tarea> tareasAsignadas;
	
	private Function<Tarea, Double> calculoPagoPorTarea;		
	private Predicate<Tarea> puedeAsignarTarea;

	public Empleado(int cuil, String nombre, Tipo tipo, double costoHora, LocalDate fechaContratacion) {
		super();
		this.cuil=cuil;
		this.nombre=nombre;
		this.tipo=tipo;
		this.costoHora=costoHora;
		this.fechaContratacion=fechaContratacion;
		this.tareasAsignadas=new ArrayList<Tarea>();
	}
	
	public void setCalculoPagoPorTarea(Function<Tarea, Double> func) {
		this.calculoPagoPorTarea = func;
	}
	
	public void setPuedeAsignarTarea(Predicate<Tarea> pred) {
		this.puedeAsignarTarea=pred;
	}
	
	public int getCuil() {
		return this.cuil.intValue();
	}
	
	public Double salario() {
		// cargar todas las tareas no facturadas
		// calcular el costo
		// marcarlas como facturadas.
		return this.tareasAsignadas.stream()
			.filter(t->t.getFacturada().booleanValue()==false && t.getFechaFin().compareTo(LocalDateTime.now())>0)
			.mapToDouble(t->calculoPagoPorTarea.apply(t))
			.sum();
	}
	
	/**
	 * Si la tarea ya fue terminada nos indica cuaal es el monto según el algoritmo de calculoPagoPorTarea
	 * Si la tarea no fue terminada simplemente calcula el costo en base a lo estimado.
	 * @param t
	 * @return
	 */
	public double getCostoHora() {
		return this.costoHora.doubleValue();
	}
	
	public List<Tarea> getTareasAsignadas(){
		return this.tareasAsignadas;
	}
	
	public Double costoTarea(Tarea t) {		
		return calculoPagoPorTarea.apply(t);
	}
		
	public Boolean asignarTarea(Tarea t) throws TareaNoAsignableException{
		if(this.puedeAsignarTarea.test(t)) {
			this.tareasAsignadas.add(t);
			return true;
		}
		return false;
	}
	
	public void comenzar(Integer idTarea) throws TareaNoExisteException, TareaPreviamenteComenzadaFinalizadaException {
		// busca la tarea en la lista de tareas asignadas 
		// si la tarea no existe lanza una excepción
		Optional<Tarea> tarea;
		try {
		 tarea = this.tareasAsignadas.stream()
			.filter(t->t.getId().intValue()==idTarea.intValue())
			.findFirst();
		}catch(NullPointerException npe) {
			throw new TareaNoExisteException("La Tarea no existe en la lista de Tareas del empleado");
		}
		if(tarea.isPresent()) {
			if(tarea.get().getFechaFin()==null) {
				tarea.get().setFechaInicio(LocalDateTime.now());
			}
			else {
				throw new TareaPreviamenteComenzadaFinalizadaException("No puede finalizarse la tarea. La tarea ya había sido comenzada anteriormente.");
			}
		}
	}
	
	public void finalizar(Integer idTarea) throws TareaNoExisteException, TareaPreviamenteComenzadaFinalizadaException {
		// busca la tarea en la lista de tareas asignadas 
		// si la tarea no existe lanza una excepción
		// si la tarea existe indica como fecha de finalizacion la fecha y hora actual
		Optional<Tarea> tarea;
		try {
		 tarea = this.tareasAsignadas.stream()
			.filter(t->t.getId().intValue()==idTarea.intValue())
			.findFirst();
		}catch(NullPointerException npe) {
			throw new TareaNoExisteException("La Tarea no existe en la lista de Tareas del empleado");
		}
		if(tarea.isPresent()) {
			if(tarea.get().getFechaFin()==null) {
				tarea.get().setFechaFin(LocalDateTime.now());
			}
			else {
				throw new TareaPreviamenteComenzadaFinalizadaException("No puede finalizarse la tarea. La tarea ya había sido finalizada anteriormente.");
			}
			
		}
	}

	public void comenzar(Integer idTarea,String fecha) throws TareaNoExisteException, TareaPreviamenteComenzadaFinalizadaException  {
		// busca la tarea en la lista de tareas asignadas 
		// si la tarea no existe lanza una excepción
		// si la tarea existe indica como fecha de finalizacion la fecha y hora actual
		Optional<Tarea> tarea;
		try {
			 tarea = this.tareasAsignadas.stream()
				.filter(t->t.getId().intValue()==idTarea.intValue())
				.findFirst();
		}catch(NullPointerException npe) {
			throw new TareaNoExisteException("La Tarea no existe en la lista de Tareas del empleado");
		}
		if(tarea.isPresent()) {
			if(tarea.get().getFechaInicio()==null) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD-MM-YYYY HH:mm");
				tarea.get().setFechaInicio(LocalDateTime.parse(fecha, formatter));
			}else {
				throw new TareaPreviamenteComenzadaFinalizadaException("No puede comenzarse la tarea. La tarea ya había sido comenzada anteriormente.");
			}
			
		}
	}
	
	public void finalizar(Integer idTarea,String fecha) throws TareaNoExisteException, TareaPreviamenteComenzadaFinalizadaException {
		// busca la tarea en la lista de tareas asignadas 
		// si la tarea no existe lanza una excepción
		// si la tarea existe indica como fecha de finalizacion la fecha y hora actual
		Optional<Tarea> tarea;
		try {
		 tarea = this.tareasAsignadas.stream()
			.filter(t->t.getId().intValue()==idTarea.intValue())
			.findFirst();
		}catch(NullPointerException npe) {
			throw new TareaNoExisteException("La Tarea no existe en la lista de Tareas del empleado");
		}
		if(tarea.isPresent()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD-MM-YYYY HH:mm");
			tarea.get().setFechaFin(LocalDateTime.parse(fecha, formatter));
		}
	}
	
}
