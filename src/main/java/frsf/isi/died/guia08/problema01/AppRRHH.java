package frsf.isi.died.guia08.problema01;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

import frsf.isi.died.guia08.problema01.modelo.Empleado;
import frsf.isi.died.guia08.problema01.modelo.Empleado.Tipo;
import frsf.isi.died.guia08.problema01.modelo.Tarea;
import frsf.isi.died.guia08.problema01.modelo.TareaNoAsignableException;
import frsf.isi.died.guia08.problema01.modelo.TareaNoExisteException;
import frsf.isi.died.guia08.problema01.modelo.TareaPreviamenteComenzadaFinalizadaException;

public class AppRRHH {

	private List<Empleado> empleados;
	
	public static void main(String[] args) {
        System.out.println("Hello World!");
    }
	
	public void agregarEmpleadoContratado(Integer cuil,String nombre,Double costoHora) {
		// crear un empleado
		// agregarlo a la lista
		Empleado emp = new Empleado(cuil, nombre, Tipo.CONTRATADO, costoHora.doubleValue(), LocalDate.now());
		Function<Tarea,Double> calculoPagoPorTarea = 
				(t)->{
						double costoParticularHora = emp.getCostoHora();
						long days = ChronoUnit.DAYS.between(t.getFechaInicio(), t.getFechaFin());
						long horasReales = days*4;
						int duracionEstimada = t.getDuracionEstimada().intValue();
						if(horasReales < duracionEstimada) {
							costoParticularHora*=1.30;
						}
						else if(horasReales>duracionEstimada+8) {
							costoParticularHora*=0.75;
						}
						return costoParticularHora*horasReales;
				};
		Predicate<Tarea> puedeAsignarTarea = (t) -> {
			return emp.getTareasAsignadas().stream()
				.filter(tarea->tarea.getFechaFin()==null)
				.count() < 5;
		};
		emp.setPuedeAsignarTarea(puedeAsignarTarea);
		emp.setCalculoPagoPorTarea(calculoPagoPorTarea);
		this.empleados.add(emp);
	}
	
	public void agregarEmpleadoEfectivo(Integer cuil,String nombre,Double costoHora) {
		// crear un empleado
		// agregarlo a la lista		
		Empleado emp = new Empleado(cuil, nombre, Tipo.EFECTIVO, costoHora.doubleValue(), LocalDate.now());
		Function<Tarea,Double> calculoPagoPorTarea = 
				(t)->{
						double costoParticularHora = emp.getCostoHora();
						long days = ChronoUnit.DAYS.between(t.getFechaInicio(), t.getFechaFin());
						long horasReales = days*4;
						int duracionEstimada = t.getDuracionEstimada().intValue();
						if(horasReales < duracionEstimada) {
							costoParticularHora*=1.20;
						}
						return costoParticularHora*horasReales;
				};
		Predicate<Tarea> puedeAsignarTarea = (t) -> {
			return emp.getTareasAsignadas().stream()
				.filter(tarea->tarea.getFechaFin()==null)
				.mapToInt(tarea->tarea.getDuracionEstimada())
				.sum() + t.getDuracionEstimada().intValue() <=15;
		};
		emp.setPuedeAsignarTarea(puedeAsignarTarea);
		emp.setCalculoPagoPorTarea(calculoPagoPorTarea);
		this.empleados.add(emp);
	}
	
	public void asignarTarea(Integer cuil,Integer idTarea,String descripcion,Integer duracionEstimada) {
		// busca un empleado
		// con el método buscarEmpleado() de esta clase
		// agregarla a la lista	
		Predicate<Empleado> p = (e) -> (e.getCuil()==cuil);
		Optional<Empleado> emp = buscarEmpleado(p);
		if(emp.isPresent()) {
			Tarea tarea = new Tarea();
			tarea.setId(idTarea);
			tarea.setDescripcion(descripcion);
			tarea.setDuracionEstimada(duracionEstimada);
			try {
				if(emp.get().asignarTarea(tarea).booleanValue()) {
					tarea.asignarEmpleado(emp.get());
				}
			}catch(TareaNoAsignableException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public void empezarTarea(Integer cuil,Integer idTarea) {
		// busca el empleado por cuil en la lista de empleados
		// con el método buscarEmpleado() actual de esta clase
		// e invoca al método comenzar tarea
		Predicate<Empleado> p = (e) -> (e.getCuil()==cuil);
		Optional<Empleado> emp = buscarEmpleado(p);
		if(emp.isPresent()) {
			try {
				emp.get().comenzar(idTarea);
			}catch(TareaNoExisteException e1) {
				System.err.println(e1.getMessage());
			}
			catch(TareaPreviamenteComenzadaFinalizadaException e2) {
				System.err.println(e2.getMessage());
			}
		}
	}
	
	public void terminarTarea(Integer cuil,Integer idTarea) {
		// crear un empleado
		// agregarlo a la lista
		Predicate<Empleado> p = (e) -> (e.getCuil()==cuil);
		Optional<Empleado> emp = buscarEmpleado(p);
		if(emp.isPresent()) {
			try {
				emp.get().finalizar(idTarea);
			}catch(TareaNoExisteException e1) {
				System.err.println(e1.getMessage());
			}catch(TareaPreviamenteComenzadaFinalizadaException e2) {
				System.err.println(e2.getMessage());
			}
		}
	}

	public void cargarEmpleadosContratadosCSV(String nombreArchivo) throws FileNotFoundException, IOException {
		// leer datos del archivo
		// por cada fila invocar a agregarEmpleadoContratado
		try (Reader fileReader = new FileReader(nombreArchivo)) {
			try(BufferedReader bufferedReader = new BufferedReader(fileReader)){
				String line = null;
				while((line = bufferedReader.readLine()) != null) {
					String[] row = line.split(";");
					if(row[0]!=null && row[0]!="") {
						this.agregarEmpleadoContratado(Integer.valueOf(row[0]), new String(row[2]), Double.valueOf(row[2]));
					}
					else {
						System.err.println("[ADVERTENCIA] Se ha detectado un error en el archivo, empleado con CUIL nulo o vacío, este empleado no será cargado");
					}
				}
			}
		}
		
	}

	public void cargarEmpleadosEfectivosCSV(String nombreArchivo) throws FileNotFoundException, IOException {
		// leer datos del archivo
		// por cada fila invocar a agregarEmpleadoContratado		
		try (Reader fileReader = new FileReader(nombreArchivo)) {
			try(BufferedReader bufferedReader = new BufferedReader(fileReader)){
				String line = null;
				while((line = bufferedReader.readLine()) != null) {
					String[] row = line.split(";");
					if(row[0]!=null && row[0]!="") {
						this.agregarEmpleadoEfectivo(Integer.valueOf(row[0]), new String(row[2]), Double.valueOf(row[2]));
					}
					else {
						System.err.println("[ADVERTENCIA] Se ha detectado un error en el archivo, empleado con CUIL nulo o vacío, este empleado no será cargado");
					}
				}
			}
		}
	}

	public void cargarTareasCSV(String nombreArchivo) throws FileNotFoundException, IOException {
		// leer datos del archivo
		// cada fila del archivo tendrá:
		// cuil del empleado asignado, numero de la taera, descripcion y duración estimada en horas.
		try (Reader fileReader = new FileReader(nombreArchivo)) {
			try(BufferedReader bufferedReader = new BufferedReader(fileReader)){
				String line = null;
				while((line = bufferedReader.readLine()) != null) {
					String[] row = line.split(";");
					if(row[0]!=null && row[0]!="") {
						this.asignarTarea(Integer.valueOf(row[3]), Integer.valueOf(row[0]), new String(row[1]), Integer.valueOf(row[2]));
					}
					else {
						System.err.println("[ADVERTENCIA] Se ha detectado un error en el archivo, tarea con ID nulo o vacío, esta tarea no será cargada");
					}
				}
			}
		}
	}
	
	private void guardarTareasTerminadasCSV() throws IOException {
		// guarda una lista con los datos de la tarea que fueron terminadas
		// y todavía no fueron facturadas
		// y el nombre y cuil del empleado que la finalizó en formato CSV 
		try (Writer fileWriter = new FileWriter("tareasTerminadas.csv", true)) {
			try(BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)){
				StringBuffer row = new StringBuffer("");
				List<Tarea> tareasTerminadas = this.empleados.stream()
					.flatMap(e->e.getTareasAsignadas().stream())
					.filter(t->t.getFechaFin()!=null && t.getFacturada().booleanValue()==false)
					.collect(Collectors.toList());
				
				if(tareasTerminadas.size()>0) {
				
					for(Tarea tarea : tareasTerminadas) {
						
							row.append(tarea.getId().toString());
							row.append(';');
							row.append(tarea.getDescripcion());
							row.append(';');
							row.append(tarea.getDuracionEstimada().toString());
							row.append(';');
							row.append(String.valueOf(tarea.getEmpleadoAsignado().getCuil()));
							bufferedWriter.write(row.toString());
							bufferedWriter.newLine();
							row.setLength(0);
					}
				}
			}
		}
	}
	
	private Optional<Empleado> buscarEmpleado(Predicate<Empleado> p){
		return this.empleados.stream().filter(p).findFirst();
	}

	public Double facturar() {
		try {
			this.guardarTareasTerminadasCSV();
		} catch (IOException e1) {
			System.err.println("Error al facturar, no se pudieron guardar las tareas terminadas");
		}
		return this.empleados.stream()				
				.mapToDouble(e -> e.salario())
				.sum();
	}
}
