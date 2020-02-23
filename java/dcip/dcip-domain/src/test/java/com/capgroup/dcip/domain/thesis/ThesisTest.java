package com.capgroup.dcip.domain.thesis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.capgroup.dcip.domain.thesis.Thesis.ThesisFilter;

@RunWith(SpringRunner.class)
public class ThesisTest {

	@Test
	public void rootThesisPointTest() {
		Thesis thesis = new Thesis();

		ThesisEdge result = thesis.addThesisPoint("test");

		assertRootThesisEdge(thesis, result, "test");
	}

	private static void assertRootThesisEdge(Thesis thesis, ThesisEdge edge, String text) {
		assertNotNull(edge);
		assertEquals(text, edge.getChildThesisPoint().getText());
		assertEquals((long)thesis.getId(), edge.getChildThesisPoint().getOriginalThesisId());
		assertEquals(thesis.thesisEdges(edge.getChildThesisPoint(), EnumSet.of(ThesisFilter.CHILD)).count(), 1);
		assertEquals(thesis.thesisEdges(edge.getChildThesisPoint(), EnumSet.of(ThesisFilter.PARENT)).count(), 0);
	}

	@Test
	public void multipleRootThesisPointTest() {
		Thesis thesis = new Thesis();

		ThesisEdge result1 = thesis.addThesisPoint("test1");
		ThesisEdge result2 = thesis.addThesisPoint("test2");

		assertRootThesisEdge(thesis, result1, "test1");
		assertRootThesisEdge(thesis, result2, "test2");
	}

	@Test
	public void addChildThesisPointTest() {
		Thesis thesis = new Thesis();

		ThesisEdge result1 = thesis.addThesisPoint("test1");
		ThesisEdge result2 = thesis.addThesisPoint("test2", result1.getChildThesisPoint());

		assertChildThesisEdge(thesis, result2.getChildThesisPoint(), result1.getChildThesisPoint(), "test2");
	}
	
	@Test
	public void removeThesisEdgeTest() {
		Thesis thesis = new Thesis();
		ThesisEdge edge1 = thesis.addThesisPoint("test1");
		ThesisEdge edge2 = thesis.addThesisPoint("test2", edge1.getChildThesisPoint());

		Iterable<ThesisEdge> removedEdges = thesis.removeThesisEdge(edge1);
		assertTrue(StreamSupport.stream(removedEdges.spliterator(), false).anyMatch(x->x==edge1));
//		assertTrue(StreamSupport.stream(removedEdges.spliterator(), false).anyMatch(x->x==edge2));
		assertFalse(thesis.thesisPoints().findAny().isPresent());
	}

	@Test
	public void orderingTest() {
		Thesis thesis = new Thesis();

		ThesisEdge result1 = thesis.addThesisPoint("test1");
		ThesisEdge result2 = thesis.addThesisPoint("test2", 0);

		List<ThesisEdge> thesisPoints = thesis.thesisEdges(null, EnumSet.of(ThesisFilter.PARENT))
				.collect(Collectors.toList());

		assertEquals(thesisPoints.get(0).getChildThesisPoint(), result2.getChildThesisPoint());
		assertEquals(thesisPoints.get(1).getChildThesisPoint(), result1.getChildThesisPoint());
	}

	@Test
	public void removeChildTest() {
		Thesis thesis = new Thesis();

		ThesisEdge result1 = thesis.addThesisPoint("test1");
		ThesisEdge result2 = thesis.addThesisPoint("test2", result1.getChildThesisPoint());

		thesis.removeThesisPoint(result2.getChildThesisPoint());

		Stream<ThesisEdge> thesisPoints = thesis.thesisEdges(result1.getChildThesisPoint(),
				EnumSet.of(ThesisFilter.PARENT));

		assertEquals(0, thesisPoints.count());
	}

	@Test
	public void removeParentTest() {
		Thesis thesis = new Thesis();

		ThesisEdge result1 = thesis.addThesisPoint("test1");
		ThesisEdge result2 = thesis.addThesisPoint("test2", result1.getChildThesisPoint());

		thesis.removeThesisPoint(result1.getChildThesisPoint());

		List<ThesisEdge> thesisEdges = thesis.thesisEdges(null, EnumSet.of(ThesisFilter.CHILD))
				.collect(Collectors.toList());
		assertTrue(thesisEdges.isEmpty());

		thesisEdges = thesis.thesisEdges(result1.getChildThesisPoint(), EnumSet.of(ThesisFilter.CHILD))
				.collect(Collectors.toList());
		assertTrue(thesisEdges.isEmpty());

		thesisEdges = thesis.thesisEdges(result2.getChildThesisPoint(), EnumSet.of(ThesisFilter.CHILD))
				.collect(Collectors.toList());
		assertTrue(thesisEdges.isEmpty());
	}

	@Test
	public void removeChildInMiddleOfEntriesTest() {
		Thesis thesis = new Thesis();

		ThesisEdge result1 = thesis.addThesisPoint("test1");
		ThesisEdge result2 = thesis.addThesisPoint("test2", result1.getChildThesisPoint());
		ThesisEdge result3 = thesis.addThesisPoint("test3", result1.getChildThesisPoint());
		ThesisEdge result4 = thesis.addThesisPoint("test4", result1.getChildThesisPoint());

		thesis.removeThesisPoint(result3.getChildThesisPoint());

		List<ThesisEdge> thesisEdges = thesis
				.thesisEdges(result1.getChildThesisPoint(), EnumSet.of(ThesisFilter.PARENT))
				.collect(Collectors.toList());
		assertEquals(thesisEdges.size(), 2);
		assertEquals(thesisEdges.get(0).getChildThesisPoint(), result2.getChildThesisPoint());
		assertEquals(thesisEdges.get(1).getChildThesisPoint(), result4.getChildThesisPoint());
	}

	@Test
	public void addChildMultipleTimesTest() {
		Thesis thesis = new Thesis();

		ThesisEdge result1 = thesis.addThesisPoint("test1");
		ThesisEdge result2 = thesis.addThesisPoint("test2");
		ThesisEdge result3 = thesis.addThesisPoint("test3", result1.getChildThesisPoint());
		thesis.addThesisPoint(result3.getChildThesisPoint(), result2.getChildThesisPoint());

		List<ThesisEdge> thesisPoints = thesis
				.thesisEdges(result1.getChildThesisPoint(), EnumSet.of(ThesisFilter.PARENT))
				.collect(Collectors.toList());
		assertEquals(1, thesisPoints.size());
		assertEquals(result3.getChildThesisPoint(), thesisPoints.get(0).getChildThesisPoint());

		thesisPoints = thesis
				.thesisEdges(result2.getChildThesisPoint(), EnumSet.of(ThesisFilter.PARENT))
				.collect(Collectors.toList());

		assertEquals(1, thesisPoints.size());
		assertEquals(result3.getChildThesisPoint(), thesisPoints.get(0).getChildThesisPoint());
	}

	@Test
	public void removeParentInTreeMultipleTimes() {
		Thesis thesis = new Thesis();

		// result3 is a child of result2 and a child of result1

		ThesisEdge result1 = thesis.addThesisPoint("test1");
		ThesisEdge result2 = thesis.addThesisPoint("test2");
		ThesisEdge result3 = thesis.addThesisPoint("test3", result1.getChildThesisPoint());
		thesis.addThesisPoint(result3.getChildThesisPoint(), result2.getChildThesisPoint());
		ThesisEdge result4 = thesis.addThesisPoint("test4", result3.getChildThesisPoint());

		thesis.removeThesisPoint(result3.getChildThesisPoint());

		List<ThesisEdge> thesisEdges = thesis
				.thesisEdges(result1.getChildThesisPoint(), EnumSet.of(ThesisFilter.PARENT))
				.collect(Collectors.toList());
		assertEquals(0, thesisEdges.size());

		thesisEdges = thesis.thesisEdges(result3.getChildThesisPoint(), EnumSet.of(ThesisFilter.CHILD))
				.collect(Collectors.toList());
		assertEquals(0, thesisEdges.size());

		thesisEdges = thesis.thesisEdges(result2.getChildThesisPoint(), EnumSet.of(ThesisFilter.PARENT))
				.collect(Collectors.toList());
		assertEquals(0, thesisEdges.size());
	}

	private static void assertChildThesisEdge(Thesis thesis, ThesisPoint child, ThesisPoint parent, String text) {
		assertNotNull(child);
		assertEquals(text, child.getText());
		assertEquals((long)thesis.getId(), child.getOriginalThesisId());
		assertEquals(thesis.thesisEdges(child, EnumSet.of(ThesisFilter.PARENT)).count(), 0);
		assertEquals(thesis.thesisEdges(child, EnumSet.of(ThesisFilter.CHILD)).count(), 1);
		assertEquals(thesis.thesisEdges(child, EnumSet.of(ThesisFilter.CHILD)).findFirst().get().getParentThesisPoint(), parent);
		assertEquals(thesis.thesisEdges(parent, EnumSet.of(ThesisFilter.PARENT)).count(), 1);
		assertEquals(thesis.thesisEdges(parent, EnumSet.of(ThesisFilter.PARENT)).findAny().get().getChildThesisPoint(), child);
	}

}
