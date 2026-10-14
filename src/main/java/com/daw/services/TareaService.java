package com.daw.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.daw.persistence.entities.TareaEntity;
import com.daw.persistence.entities.enums.Estado;
import com.daw.persistence.repositories.TareaRepository;
import com.daw.services.exceptions.TareaException;
import com.daw.services.exceptions.TareaNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TareaService {

	private final TareaRepository tareaRepository;

	public List<TareaEntity> findAll() {

		return this.tareaRepository.findAll();
	}

	public TareaEntity findById(long idTarea) {

		return this.tareaRepository.findById(idTarea)
				.orElseThrow(() -> new TareaNotFoundException(String.format("La tarea con id %d no existe", idTarea)));
	}

	public void deleteById(long idTarea) {

		if (!this.tareaRepository.existsById(idTarea)) {

			throw new TareaNotFoundException(String.format("La tarea con id %d no existe", idTarea));
		}

		this.tareaRepository.deleteById(idTarea);
	}

	public TareaEntity create(TareaEntity t) {
		if (t.getFechaCreacion() != null) {

			throw new TareaException("No se puede indicar la fecha de creacion de una tarea");
		}

		if (t.getEstado() != null) {

			throw new TareaException("No se puede indicar el estado inicial de una tarea");
		}

		if (t.getFechaVencimiento() == null) {

			throw new TareaException("La fecha de vencimiento es obligatoria");
		}

		if (t.getFechaVencimiento().isBefore(LocalDate.now())) {

			throw new TareaException("La fecha de vencimiento no puede ser anterior a la fecha actual");
		}

		t.setId(0L);
		t.setFechaCreacion(LocalDate.now());
		t.setEstado(Estado.PENDIENTE);

		return this.tareaRepository.save(t);
	}

	public TareaEntity update(long idTarea, TareaEntity t) {

		TareaEntity tareaExistente = this.findById(idTarea);

		if (t.getFechaCreacion() != null) {

			throw new TareaException("No se puede modificar la fecha de creacion de una tarea");
		}

		if (t.getEstado() != null) {

			throw new TareaException("No se puede modificar el estado de una tarea");
		}

		if (t.getFechaVencimiento() != null && t.getFechaVencimiento().isBefore(LocalDate.now())) {

			throw new TareaException("La fecha de vencimiento no puede ser anterior a la fecha actual");
		}

		t.setId(idTarea);
		t.setFechaCreacion(tareaExistente.getFechaCreacion());
		t.setEstado(tareaExistente.getEstado());

		return this.tareaRepository.save(t);
	}

	public TareaEntity iniciarTarea(long idTarea) {

		TareaEntity tarea = this.findById(idTarea);

		if (!tarea.getEstado().equals(Estado.PENDIENTE)) {
			throw new TareaException("No se puede iniciar una tarea en progreso o completada");
		}
		tarea.setEstado(Estado.EN_PROGRESO);

		return this.tareaRepository.save(tarea);
	}

	public TareaEntity completarTarea(long idTarea) {

		TareaEntity tarea = this.findById(idTarea);

		if (!tarea.getEstado().equals(Estado.EN_PROGRESO)) {
			throw new TareaException("No se puede iniciar una tarea en progreso o completada");
		}
		tarea.setEstado(Estado.EN_PROGRESO);

		return this.tareaRepository.save(tarea);
	}
}