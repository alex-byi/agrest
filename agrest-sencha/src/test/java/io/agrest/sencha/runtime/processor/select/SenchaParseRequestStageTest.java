package io.agrest.sencha.runtime.processor.select;

import io.agrest.AgRequest;
import io.agrest.it.fixture.cayenne.E2;
import io.agrest.protocol.Dir;
import io.agrest.protocol.Sort;
import io.agrest.runtime.jackson.IJacksonService;
import io.agrest.runtime.jackson.JacksonService;
import io.agrest.runtime.processor.select.SelectContext;
import io.agrest.runtime.protocol.CayenneExpParser;
import io.agrest.runtime.protocol.ExcludeParser;
import io.agrest.runtime.protocol.ICayenneExpParser;
import io.agrest.runtime.protocol.IExcludeParser;
import io.agrest.runtime.protocol.IIncludeParser;
import io.agrest.runtime.protocol.ISizeParser;
import io.agrest.runtime.protocol.ISortParser;
import io.agrest.runtime.protocol.IncludeParser;
import io.agrest.runtime.protocol.SizeParser;
import io.agrest.runtime.protocol.SortParser;
import io.agrest.runtime.request.DefaultRequestBuilderFactory;
import io.agrest.runtime.request.IAgRequestBuilderFactory;
import io.agrest.sencha.SenchaRequest;
import io.agrest.sencha.runtime.protocol.ISenchaFilterParser;
import io.agrest.sencha.runtime.protocol.SenchaFilterParser;
import io.agrest.unit.TestWithCayenneMapping;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SenchaParseRequestStageTest extends TestWithCayenneMapping {

    private SenchaParseRequestStage parseStage;

	@Before
	public void before() {

		IJacksonService jacksonService = new JacksonService();

		// prepare parse request stage
        ICayenneExpParser expParser = new CayenneExpParser(jacksonService);
		ISortParser sortParser = new SortParser(jacksonService);
        ISizeParser sizeParser = new SizeParser();
        IIncludeParser includeParser = new IncludeParser(jacksonService, expParser, sortParser, sizeParser);
        IExcludeParser excludeParser = new ExcludeParser(jacksonService);
        ISenchaFilterParser filterParser = new SenchaFilterParser(jacksonService);

		IAgRequestBuilderFactory requestBuilderFactory
				= new DefaultRequestBuilderFactory(expParser, sortParser, includeParser, excludeParser);
        this.parseStage = new SenchaParseRequestStage(requestBuilderFactory, filterParser);
	}

    @Test
	public void testSelectRequest_Filter() {

		@SuppressWarnings("unchecked")
		MultivaluedMap<String, String> params = mock(MultivaluedMap.class);
		when(params.get(SenchaParseRequestStage.FILTER)).thenReturn(Collections.singletonList("[{\"property\":\"name\",\"value\":\"xyz\"}]"));

        SelectContext<E2> context = prepareContext(params, E2.class);

        parseStage.doExecute(context);

        SenchaRequest senchaRequest = SenchaRequest.get(context);

		assertNotNull(senchaRequest);
		assertFalse(senchaRequest.getFilters().isEmpty());
		assertEquals("name", senchaRequest.getFilters().get(0).getProperty());
		assertEquals("xyz", senchaRequest.getFilters().get(0).getValue());
		assertEquals("like", senchaRequest.getFilters().get(0).getOperator());
	}


	@Test
	public void testSelectRequest_Filter_CayenneExp() {

		@SuppressWarnings("unchecked")
		MultivaluedMap<String, String> params = mock(MultivaluedMap.class);
		when(params.get("cayenneExp")).thenReturn(Collections.singletonList("{\"exp\" : \"address = '1 Main Street'\"}"));
		when(params.get(SenchaParseRequestStage.FILTER)).thenReturn(Collections.singletonList("[{\"property\":\"name\",\"value\":\"xyz\"}]"));

        SelectContext<E2> context = prepareContext(params, E2.class);

        parseStage.doExecute(context);

		SenchaRequest senchaRequest = SenchaRequest.get(context);

		assertNotNull(senchaRequest);
		assertFalse(senchaRequest.getFilters().isEmpty());
		assertEquals("name", senchaRequest.getFilters().get(0).getProperty());
		assertEquals("xyz", senchaRequest.getFilters().get(0).getValue());
		assertEquals("like", senchaRequest.getFilters().get(0).getOperator());

		AgRequest request = context.getMergedRequest();

		assertNotNull(request.getCayenneExp());
		assertEquals("address = '1 Main Street'", request.getCayenneExp().getExp());
	}

	@Test
	public void testSelectRequest_Sort_Group() {

		@SuppressWarnings("unchecked")
		MultivaluedMap<String, String> params = mock(MultivaluedMap.class);
		when(params.get("sort")).thenReturn(
				Collections.singletonList("[{\"property\":\"name\",\"direction\":\"DESC\"},{\"property\":\"address\",\"direction\":\"ASC\"}]"));
		when(params.get(SenchaParseRequestStage.GROUP)).thenReturn(
				Collections.singletonList("[{\"property\":\"id\",\"direction\":\"DESC\"},{\"property\":\"address\",\"direction\":\"ASC\"}]"));

        SelectContext<E2> context = prepareContext(params, E2.class);

        parseStage.doExecute(context);

		List<Sort> orderings = context.getMergedRequest().getOrderings();
		assertEquals(4, orderings.size());

		assertEquals("id", orderings.get(0).getProperty());
		assertEquals(Dir.DESC, orderings.get(0).getDirection());
		assertEquals("address", orderings.get(1).getProperty());
		assertEquals(Dir.ASC, orderings.get(1).getDirection());

		assertEquals("name", orderings.get(2).getProperty());
		assertEquals(Dir.DESC, orderings.get(2).getDirection());
		assertEquals("address", orderings.get(3).getProperty());
		assertEquals(Dir.ASC, orderings.get(3).getDirection());
	}
}
