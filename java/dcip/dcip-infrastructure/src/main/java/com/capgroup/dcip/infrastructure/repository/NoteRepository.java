package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.canvas.QCanvasItem;
import com.capgroup.dcip.domain.note.Note;
import com.capgroup.dcip.domain.note.QNote;
import com.capgroup.dcip.infrastructure.querydsl.QuerydslPredicateProjectionExecutor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.stereotype.Repository;

/**
 * API for accessing Notes from the DB
 */
@Repository
public interface NoteRepository extends TemporalEntityRepository<Note>, QuerydslPredicateProjectionExecutor<Note> {
    /**
     * Builder class for creating Note expressions
     */
    class ExpressionBuilder extends EntityRepository.ExpressionBuilder {
        QNote note;

        public ExpressionBuilder() {
            this(QNote.note);
        }

        public ExpressionBuilder(QNote note) {
            super(note._super._super);
            this.note = note;
        }

        public BooleanExpression hasCanvasItem(Long canvasId) {
            return canvasId == null ? Expressions.TRUE.eq(true)
                    : note.id.in(JPAExpressions.select(QCanvasItem.canvasItem.entityId).from(QCanvasItem.canvasItem)
                    .where(QCanvasItem.canvasItem.canvas.id.eq(canvasId)));
        }
    }
}
