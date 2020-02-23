package com.capgroup.dcip.webapi.controllers;

import java.util.EnumSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capgroup.dcip.app.note.NoteModel;
import com.capgroup.dcip.app.note.NoteService;
import com.capgroup.dcip.app.note.NoteViewOption;

/**
 * External API for creating/reading/updating notes 
 */
@RestController
@RequestMapping("api/dcip/notes")
public class NoteController {

	private NoteService noteService;

	@Autowired
	public NoteController(NoteService noteService) {
		this.noteService = noteService;
	}

	@GetMapping
	public Iterable<NoteModel> get(@RequestParam(name = "canvasId", required = false) Long canvasId,
			@RequestParam(name="profileId", required =false) Long profileId,
			@RequestParam(name = "view", required=false) List<NoteViewOption> viewOptions) {
		return noteService.findAll(canvasId, profileId, toEnumSet(viewOptions));
	}
	
	@GetMapping("/{noteId}")
	public NoteModel get(@PathVariable("noteId") long noteId) {
		return noteService.findById(noteId);
	}

	@PostMapping
	public NoteModel post(@RequestBody NoteModel noteModel) {
		return noteService.create(noteModel);
	}
	
	@PutMapping("/{noteId}")
	public NoteModel put(@PathVariable("noteId") long noteId, @RequestBody NoteModel noteModel) {
		return noteService.update(noteId, noteModel);
	}
	
	@DeleteMapping("/{noteId}")
	public NoteModel delete(@PathVariable("noteId") long noteId) {
			return noteService.delete(noteId);
	}
	
	protected EnumSet<NoteViewOption> toEnumSet(List<NoteViewOption> viewOptions){
		return (viewOptions == null || viewOptions.isEmpty()) ? EnumSet.allOf(NoteViewOption.class)
				: EnumSet.copyOf(viewOptions);
	}
}
