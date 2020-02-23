package com.capgroup.dcip.infrastructure.repository;

import com.capgroup.dcip.domain.canvas.QCanvasItem;
import com.capgroup.dcip.domain.canvas_summary.CanvasSummary;
import com.capgroup.dcip.domain.canvas_summary.QCanvasSummary;
import com.capgroup.dcip.infrastructure.querydsl.QuerydslPredicateProjectionExecutor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.stereotype.Repository;


/**
 * API for accessing CanvasSummary from the DB
 */
@Repository
public interface CanvasSummaryRepository extends TemporalEntityRepository<CanvasSummary>, QuerydslPredicateProjectionExecutor<CanvasSummary> {
    /**
     * Builder class for creating Canvas Summary expressions
     */
    class ExpressionBuilder extends EntityRepository.ExpressionBuilder {
    	QCanvasSummary canvasSummary;

        public ExpressionBuilder() {
            this(QCanvasSummary.canvasSummary);
        }

        public ExpressionBuilder(QCanvasSummary canvasSummary) {
            super(canvasSummary._super._super);
            this.canvasSummary = canvasSummary;
        }

        public BooleanExpression hasCanvasItem(Long canvasId) {
            return canvasId == null ? Expressions.TRUE.eq(true)
                    : canvasSummary.id.in(JPAExpressions.select(QCanvasItem.canvasItem.entityId).from(QCanvasItem.canvasItem)
                    .where(QCanvasItem.canvasItem.canvas.id.eq(canvasId)));
        }
    }
}
