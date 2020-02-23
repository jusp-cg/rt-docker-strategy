package com.capgroup.dcip.domain.thesis;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

import com.capgroup.dcip.domain.entity.TemporalEntity;
import com.capgroup.dcip.util.CollectionUtils;

@Entity
@Table(name = "thesis_view")
@SQLInsert(callable = true, check = ResultCheckStyle.NONE, sql = "{call thesis_insert(?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLUpdate(callable = true, check = ResultCheckStyle.NONE, sql = "{call thesis_update(?, ?, ?, ?, ?, ?, ?, ?, ?)}")
@SQLDelete(callable = true, check = ResultCheckStyle.NONE, sql = "{call thesis_delete(?, ?)}")
public class Thesis extends TemporalEntity {

	public enum ThesisFilter {
		PARENT, CHILD
	}

	private static final long serialVersionUID = -5805501158029422019L;

	@OneToMany(mappedBy = "thesis", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ThesisEdge> thesisEdges;

	public Thesis() {
		super(0);
		thesisEdges = new HashSet<>();
	}

	public Thesis(Iterable<ThesisEdge> edges) {
		thesisEdges = CollectionUtils.asSet(edges);
	}

	/**
	 * Adds a ThesisPoint to a parent at the specified position
	 */
	public ThesisEdge addThesisPoint(ThesisPoint child, ThesisPoint parent, Integer position) {
		ThesisEdge result = addThesisPoint(child, parent);
		if (position != null)
			moveToNthPosition(child, parent, position);
		return result;
	}

	/**
	 * Removes a thesis point. Removes all references to the thesis point in the
	 * tree
	 */
	public Iterable<ThesisEdge> removeThesisPoint(long thesisPointId) {
		return thesisPoint(thesisPointId).map(this::removeThesisPoint).orElse(Collections.emptyList());
	}

	/**
	 * Removes a thesis edge
	 */
	public Iterable<ThesisEdge> removeThesisEdge(long childThesisPointId, Long parentThesisPointId) {
		return thesisEdge(childThesisPointId, parentThesisPointId).map(edge -> removeThesisEdge(edge))
				.orElse(Collections.emptyList());
	}

	/**
	 * Removes a thesis edge by id
	 */
	public Iterable<ThesisEdge> removeThesisEdge(long thesisEdgeId) {
		return thesisEdge(thesisEdgeId).map(this::removeThesisEdge).orElse(Collections.emptyList());
	}

	/**
	 * Removes a ThesisPoint. Removes all references of the point in this tree
	 * (including children)
	 */
	public Iterable<ThesisEdge> removeThesisPoint(ThesisPoint thesisPoint) {
		return thesisEdges(thesisPoint, EnumSet.allOf(ThesisFilter.class)).collect(Collectors.toList()).stream()
				.flatMap(x -> StreamSupport.stream(removeThesisEdge(x).spliterator(), false)).distinct()
				.collect(Collectors.toList());
	}

	/**
	 * Removes the edge and any children which aren't referenced elsewhere in this
	 * tree
	 */
	public Iterable<ThesisEdge> removeThesisEdge(ThesisEdge thesisEdge) {
		// remove all children of the node
		List<ThesisEdge> result = removeChildren(thesisEdge.getChildThesisPoint());

		thesisEdges.remove(thesisEdge);
		result.add(thesisEdge);

		// rearrange all siblings
		thesisEdges(thesisEdge.getParentThesisPoint(), EnumSet.of(ThesisFilter.PARENT))
				.filter(x -> x.getOrderBy() > thesisEdge.getOrderBy()).forEach(x -> x.setOrderBy(x.getOrderBy() - 1));

		// if the points are no longer in the tree and the points originated from this
		// tree - mark the points for deletion
		Stream.concat(result.stream().map(x -> x.getParentThesisPoint()),
				result.stream().map(x -> x.getChildThesisPoint())).filter(Objects::nonNull).distinct().filter(x -> {
					return x.getOriginalThesisId() == (long) getId() && !thesisPoints().anyMatch(y -> y.equals(x));
				}).forEach(x -> x.setStatus(TemporalEntity.Status.MARKED_FOR_DELETE));

		return result;
	}

	/**
	 * Cascade delete of all children. If the ThesisPoint is in the tree more than
	 * once then the children edges can't be removed
	 */
	private List<ThesisEdge> removeChildren(ThesisPoint point) {
		// if this point is in the tree more than once as a child then don't continue
		// down the tree
		if (thesisEdges(point, EnumSet.of(ThesisFilter.CHILD)).count() > 1)
			return new ArrayList<ThesisEdge>();

		List<ThesisEdge> result = new ArrayList<ThesisEdge>();

		thesisEdges(point, EnumSet.of(ThesisFilter.PARENT)).collect(Collectors.toList()).forEach(x -> {
			result.addAll(removeChildren(x.getChildThesisPoint()));
			thesisEdges.remove(x);
			result.add(x);
		});

		return result;
	}

	public Stream<ThesisEdge> thesisEdges(long thesisPointId, EnumSet<ThesisFilter> filter) {
		return thesisEdges(thesisPointUnchecked(thesisPointId), filter);
	}

	public Stream<ThesisEdge> thesisEdges(ThesisPoint point, EnumSet<ThesisFilter> filter) {
		return thesisEdges.stream().filter(x ->
			(filter.contains(ThesisFilter.PARENT) && ThesisPoint.equals(x.getParentThesisPoint(), point))
					|| (filter.contains(ThesisFilter.CHILD) && ThesisPoint.equals(point, x.getChildThesisPoint()))
		).sorted();
	}

	public Stream<ThesisPoint> thesisPoints() {
		return thesisEdges.stream().flatMap(x -> Stream.of(x.getParentThesisPoint(), x.getChildThesisPoint()))
				.filter(Objects::nonNull).distinct();
	}

	public ThesisEdge addThesisPoint(ThesisPoint child) {
		return addThesisPoint(child, null);
	}

	public ThesisEdge addThesisPoint(String text) {
		return addThesisPoint(text, null, null);
	}

	public ThesisEdge addThesisPoint(String text, ThesisPoint parent, Integer order) {
		ThesisPoint result = new ThesisPoint(text, getId());
		return addThesisPoint(result, parent, order);
	}

	public ThesisEdge addThesisPoint(String text, ThesisPoint parent) {
		return addThesisPoint(text, parent, null);
	}

	public ThesisEdge addThesisPoint(String text, Integer order) {
		ThesisPoint result = new ThesisPoint(text, getId());
		return addThesisPoint(result, null, order);
	}

	public ThesisEdge addThesisPoint(ThesisPoint child, ThesisPoint parent) {
		// can only add the child if the parent is owned by this tree
		if (parent != null && parent.getOriginalThesisId() != (long) getId()) {
			throw new RuntimeException("Cannot add a child thesis point to a thesis point not in this tree");
		}

		// find how many nodes with the same parent node
		long count = thesisEdges.stream().filter(x -> ThesisPoint.equals(parent, x.getParentThesisPoint())).count();

		ThesisEdge thesisEdge = new ThesisEdge(this, child, parent, (int) count);

		thesisEdges.add(thesisEdge);

		return thesisEdge;
	}

	public void changeParentOfThesisPoint(ThesisPoint child, ThesisPoint oldParent, ThesisPoint newParent,
			int newPosition) {
		changeParentOfThesisPoint(child, oldParent, newParent);
		moveToNthPosition(child, newParent, newPosition);
	}

	public void changeParentOfThesisPoint(ThesisPoint child, ThesisPoint oldParent, ThesisPoint newParent) {
		// find the node
		ThesisEdge thesisNode = thesisEdgeChecked(child, oldParent);

		// rearrange all siblings
		thesisEdges(thesisNode.getParentThesisPoint(), EnumSet.of(ThesisFilter.PARENT))
				.filter(x -> x.getOrderBy() > thesisNode.getOrderBy()).forEach(x -> x.setOrderBy(x.getOrderBy() - 1));

		// find how many nodes that have the same parent node
		long count = thesisEdges.stream().filter(x -> ThesisPoint.equals(newParent, x.getParentThesisPoint())).count();

		// set the new parent
		thesisNode.setParentThesisPoint(newParent);
		thesisNode.setOrderBy((int) count);
	}

	public void moveToNthPosition(ThesisPoint child, ThesisPoint parent, int newPosition) {
		// find the node
		ThesisEdge thesisNode = thesisEdgeChecked(child, parent);

		// find all the nodes that have the same parent node
		List<ThesisEdge> thesisPoints = thesisEdges(parent, EnumSet.of(ThesisFilter.PARENT))
				.collect(Collectors.toList());

		// ensure that the new position is not greater that the last element
		newPosition = Math.min(thesisPoints.size() - 1, newPosition);

		int currentPosition = thesisNode.getOrderBy();

		// get the range of items that need to be adjusted
		int minPosition = Math.min(newPosition, currentPosition);
		int maxPosition = Math.max(newPosition, currentPosition);

		// determine if the items are going to be moved forward/backward
		int direction = newPosition > currentPosition ? -1 : 1;

		// adjust existing points up/down
		thesisPoints.subList(minPosition, maxPosition).forEach(x -> {
			x.setOrderBy(x.getOrderBy() + direction);
		});

		// set the new thesis points position
		thesisNode.setOrderBy(newPosition);
	}

	/**
	 * Given a parent and a child ThesisPoint, return the edge that represents the
	 * relationship
	 */
	public Optional<ThesisEdge> thesisEdge(ThesisPoint child, ThesisPoint parent) {
		return thesisEdges.stream().filter(x -> ThesisPoint.equals(x.getChildThesisPoint(), child)
				&& ThesisPoint.equals(parent, x.getParentThesisPoint())).findAny();
	}

	private ThesisEdge thesisEdgeChecked(ThesisPoint child, ThesisPoint parent) {
		return thesisEdge(child, parent).orElseThrow(() -> new RuntimeException("point is not a member of the thesis"));
	}

	/**
	 * Given a parent and a child ThesisPoint, return the edge that represents the
	 * relationship
	 */
	public Optional<ThesisEdge> thesisEdge(long thesisChildId, Long thesisParentId) {
		return thesisEdges.stream()
				.filter(x -> x.getChildThesisPoint().getId().equals(thesisChildId)
						&& (x.getParentThesisPoint() == null ? thesisParentId == null
								: x.getParentThesisPoint().getId().equals(thesisParentId)))
				.findAny();
	}

	/**
	 * ThesisPoint for its id
	 */
	public Optional<ThesisPoint> thesisPoint(long thesisPointId) {
		return thesisEdges.stream().flatMap(x -> Stream.of(x.getParentThesisPoint(), x.getChildThesisPoint()))
				.filter(Objects::nonNull).filter(x -> x.getId() == thesisPointId).findAny();
	}

	/**
	 * ThesisPoint for its id
	 */
	public ThesisPoint thesisPointUnchecked(long thesisPointId) {
		return thesisPoint(thesisPointId)
				.orElseThrow(() -> new RuntimeException("ThesisPoint not found with id " + thesisPointId));
	}

	/**
	 * All ThesisEdges
	 */
	public Stream<ThesisEdge> thesisEdges() {
		return thesisEdges.stream();
	}

	/**
	 * Returns a ThesisEdge for the id
	 */
	public Optional<ThesisEdge> thesisEdge(long thesisEdgeId) {
		return thesisEdges.stream().filter(x -> x.getId() == thesisEdgeId).findAny();
	}
}
