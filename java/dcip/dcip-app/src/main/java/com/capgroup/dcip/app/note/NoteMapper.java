package com.capgroup.dcip.app.note;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.jsoup.Jsoup;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.app.entity.TemporalEntityMapper;
import com.capgroup.dcip.domain.canvas.WorkbenchResource.WorkbenchResourceId;
import com.capgroup.dcip.domain.note.Note;

/**
 * Mapping between the domain entity Note and DTO NoteModel
 */
@Mapper(config = TemporalEntityMapper.class)
public abstract class NoteMapper {
	private static final int SUMMARY_TEXT_LENGTH = 50;

	@Autowired
	private CanvasService canvasService;

	/**
	 * Updates a Note from a NoteModel
	 */
	@InheritConfiguration(name = "map")
	public abstract Note update(NoteModel model, @MappingTarget Note note);

	/**
	 * Converts a NoteModel to a Note
	 */
	public abstract Note map(NoteModel note);

	/**
	 * Converts a collection of Notes to a collection of NoteModels using the
	 * provided canvasId
	 */
	public Iterable<NoteModel> map(Iterable<Note> notes, Long canvasId) {
		return canvasId == null ? map(notes) : StreamSupport.stream(doMap(notes).spliterator(), false).map(x -> {
			x.setCanvasId(canvasId);
			return x;
		}).collect(Collectors.toList());
	}

	/**
	 * Converts a Collection of Notes to NoteModel - the canvas Id is retrieved from
	 * the DB for each Note
	 */
	public Iterable<NoteModel> map(Iterable<Note> notes) {
		return StreamSupport.stream(doMap(notes).spliterator(), false).map(x -> {
			canvasService.findByCanvasItemEntityId(WorkbenchResourceId.NOTE, x.getId()).ifPresent(canvas -> {
				x.setCanvasId(canvas.getId());
			});
			return x;
		}).collect(Collectors.toList());
	}

	/**
	 * Converts a Note to a NoteModel, retrieving the canvasId from the canvas
	 * service
	 */
	public NoteModel map(Note note) {
		NoteModel result = doMap(note);
		canvasService.findByCanvasItemEntityId(WorkbenchResourceId.NOTE, note.getId()).ifPresent(canvas -> {
			result.setCanvasId(canvas.getId());
		});
		return result;
	}

	public NoteModel map(Note note, long canvasId) {
		NoteModel result = map(note);
		result.setCanvasId(canvasId);
		return result;
	}

	protected Iterable<NoteModel> doMap(Iterable<Note> notes) {
		return StreamSupport.stream(notes.spliterator(), false).map(this::doMap).collect(Collectors.toList());
	}

	protected abstract NoteModel doMap(Note note);

	@AfterMapping
	protected void afterMapping(@MappingTarget Note note) {
		String summary = Jsoup.parseBodyFragment(note.getDetail()).text();
		if (summary.length() > 0)
			note.setSummary(summary.substring(0, Integer.min(SUMMARY_TEXT_LENGTH, summary.length())));
	}
}
