package frsf.isi.died.guia08.problema01.modelo;

import java.time.LocalDateTime;

public class Tarea {

	private Integer id;
	private String descripcion;
	private Integer duracionEstimada;
	private Empleado empleadoAsignado;
	private LocalDateTime fechaInicio;
	private LocalDateTime fechaFin;
	private Boolean facturada;
	
	
	
	public void asignarEmpleado(Empleado e) throws TareaNoAsignableException {
		// si la tarea ya tiene un empleado asignado
		// y tiene fecha de finalizado debe lanzar una excepcion
		boolean empAsignado = this.empleadoAsignado!=null, tareaFinalizada = this.fechaFin!=null;
		String errEmpAsignado ="", errTareaFinalizada="";
		if(empAsignado) {
			errEmpAsignado += "La tarea ya posee un empleado asignado. ";
		}
		if(tareaFinalizada) {
			errTareaFinalizada+="La tarea ya ha sido finalizada. ";
		}
		String error = errEmpAsignado + errTareaFinalizada;
		if(error!="") {
			throw new TareaNoAsignableException(error + "La tarea ya posee un empleado asignado." );
		}			
		else {
			this.empleadoAsignado=e;
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getDuracionEstimada() {
		return duracionEstimada;
	}

	public void setDuracionEstimada(Integer duracionEstimada) {
		this.duracionEstimada = duracionEstimada;
	}

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDateTime fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDateTime fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Boolean getFacturada() {
		return facturada;
	}

	public void setFacturada(Boolean facturada) {
		this.facturada = facturada;
	}

	public Empleado getEmpleadoAsignado() {
		return empleadoAsignado;
	}
	
	
}
