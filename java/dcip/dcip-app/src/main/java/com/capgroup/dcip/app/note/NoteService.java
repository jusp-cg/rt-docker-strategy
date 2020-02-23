package com.capgroup.dcip.app.note;

import java.util.EnumSet;

public interface NoteService {
	Iterable<NoteModel> findAll(Long canvasId, Long profileId, EnumSet<NoteViewOption> option);

	NoteModel findById(long id);
	
	NoteModel create(NoteModel option);
	
	NoteModel update(long id, NoteModel model);
	
	NoteModel delete(long id);
}
