package com.capgroup.dcip.app.note;

import com.capgroup.dcip.app.ResourceNotFoundException;
import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.app.event.application.DeletedApplicationEvent;
import com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews;
import com.capgroup.dcip.domain.canvas.WorkbenchResource.WorkbenchResourceId;
import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.domain.note.Note;
import com.capgroup.dcip.domain.note.QNote;
import com.capgroup.dcip.infrastructure.repository.NoteRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.EnumSet;

@Service
@EnableAllVirtualViews
public class NoteServiceImpl implements NoteService {

    private NoteRepository noteRepository;
    private NoteMapper noteMapper;
    private EntityManager entityManager;
    private CanvasService canvasService;
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, NoteMapper noteMapper, EntityManager entityManager,
                           CanvasService canvasService, ApplicationEventPublisher eventPublisher) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.entityManager = entityManager;
        this.canvasService = canvasService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<NoteModel> findAll(Long canvasId, Long profileId, EnumSet<NoteViewOption> options) {
        NoteRepository.ExpressionBuilder builder = new NoteRepository.ExpressionBuilder();
        BooleanExpression expression = builder.hasCanvasItem(canvasId).and(builder.hasProfile(profileId));
        Iterable<Note> results = Collections.emptyList();

        if (options.containsAll(EnumSet.allOf(NoteViewOption.class)))
            results = noteRepository.findAll(expression);
        else if (options.contains(NoteViewOption.SUMMARY))
            results = noteRepository.findAll(expression,
                    Projections.constructor(Note.class, QNote.note.id, QNote.note.entityType, QNote.note.event,
                            QNote.note.versionNo, QNote.note.versionId, QNote.note.validPeriod, QNote.note.summary,
                            QNote.note.status));
        else if (options.contains(NoteViewOption.DETAIL))
            results = noteRepository.findAll(expression,
                    Projections.constructor(Note.class, QNote.note.id, QNote.note.entityType, QNote.note.event,
                            QNote.note.versionNo, QNote.note.versionId, QNote.note.validPeriod, QNote.note.status,
                            QNote.note.detail));

        return noteMapper.map(results);
    }

    @Override
    @Transactional(readOnly = true)
    public NoteModel findById(long id) {
        return noteRepository.findById(id).map(note ->
                noteMapper.map(note)
        ).orElseThrow(() -> new ResourceNotFoundException("Note", String.valueOf(id)));
    }

    @Override
    @Transactional
    public NoteModel create(NoteModel noteModel) {
        Note note = noteMapper.map(noteModel);

        note = noteRepository.save(note);
        entityManager.flush();

        // associate the note with the Canvas - create a CanvasItem
        canvasService.addCanvasItem(noteModel.getCanvasId(), WorkbenchResourceId.NOTE, note.getId());

        return noteMapper.map(note, noteModel.getCanvasId());
    }

    @Override
    @Transactional
    public NoteModel update(long id, NoteModel model) {
        return noteRepository.findById(id).map(note -> {
            noteMapper.update(model, note);
            return noteMapper.map(note);
        }).orElse(null);
    }

    @Override
    @Transactional
    public NoteModel delete(long id) {
        return noteRepository.findById(id).map(note -> {
            // delete the canvas item
            canvasService.deleteCanvasItem(WorkbenchResourceId.NOTE, id);
            entityManager.flush();

            NoteModel result = noteMapper.map(note);

            // publish the event. If the event is not rejected - delete the note, otherwise mark it for deletion
            new DeletedApplicationEvent<>(result).onComplete(evnt ->
                    noteRepository.delete(note)
            ).onReject(evnt -> note.setStatus(TemporalEntity.Status.MARKED_FOR_DELETE)).publish(eventPublisher);

            return result;
        }).orElse(null);
    }
}
