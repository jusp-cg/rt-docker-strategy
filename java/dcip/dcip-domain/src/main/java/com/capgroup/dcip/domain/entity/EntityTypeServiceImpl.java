package com.capgroup.dcip.domain.entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.poi.ss.formula.functions.T;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Slf4j
public class EntityTypeServiceImpl implements EntityTypeService {
	private static String DEFAULT_PREFIX = "api/dcip";

	private EntityFinder<EntityType> entityFinder;
	private BidiMap<Class<?>, Long> mapping = new DualHashBidiMap<>();
	private ConcurrentMap<Long, Expression> expression = new ConcurrentHashMap<>();

	@Value("${application.url}")
	private String baseUrl;

	@Autowired
	public EntityTypeServiceImpl(EntityFinder<EntityType> entityFinder) {
		this.entityFinder = entityFinder;
	}

	@Override
	public Optional<EntityType> findEntityTypeForClass(Class<?> clazz) {
		return Optional.ofNullable(mapping.get(clazz)).map(id -> entityFinder.findById(id)).orElse(Optional.empty());
	}

	@Override
	public Optional<Class<?>> findClassForEntityType(EntityType entityType) {
		return Optional.ofNullable(mapping.inverseBidiMap().get(entityType.getId()));
	}

	@PostConstruct
	protected void init() {
		Set<String> allClasses = new Reflections("com.capgroup.dcip.domain", new SubTypesScanner(false)).getAllTypes();
		entityFinder.findAll().forEach(entityType -> {
			allClasses.stream().filter(x -> x.endsWith("." + entityType.getName())).findAny().ifPresent(type -> {
				try {
					Class impl = Class.forName(type);
					mapping.put(impl, entityType.getId());
					log.debug("Adding class: {}, for id: {}", impl.getName(), entityType.getId());
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			});
		});
	}

	@Override
	public <T> Optional<URL> findResourceUrl(EntityType entityType, T id) {

		@Data
		@AllArgsConstructor
		class EvaluationContext {
			EntityType entityType;
			T entityId;
			String baseUrl;
			String defaultPrefix;
		}

		return Optional.ofNullable(entityType.getEntityUrlTemplate()).map(url -> {
			// find/create the expression
			return expression.computeIfAbsent(entityType.getId(), x -> {
				ExpressionParser parser = new SpelExpressionParser();
				return parser.parseExpression(url, new TemplateParserContext());

			});
		}).map(parser -> parser.getValue(
				new StandardEvaluationContext(new EvaluationContext(entityType, id, baseUrl, DEFAULT_PREFIX)),
				String.class)).map(url -> {
					try {
						return new URL(url);
					} catch (MalformedURLException e) {
						throw new RuntimeException(e);
					}
				});
	}
}
