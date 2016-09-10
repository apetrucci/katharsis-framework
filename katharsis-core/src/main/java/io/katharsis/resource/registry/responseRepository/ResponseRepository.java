package io.katharsis.resource.registry.responseRepository;

import io.katharsis.queryParams.QueryParams;
import io.katharsis.queryspec.DefaultQuerySpecConverter;
import io.katharsis.queryspec.FilterOperator;
import io.katharsis.queryspec.FilterOperatorRegistry;
import io.katharsis.queryspec.QuerySpec;
import io.katharsis.queryspec.internal.QueryAdapter;
import io.katharsis.queryspec.internal.QueryParamsAdapter;
import io.katharsis.queryspec.internal.QuerySpecAdapter;
import io.katharsis.repository.LinksRepository;
import io.katharsis.repository.MetaRepository;
import io.katharsis.repository.annotated.AnnotatedRepositoryAdapter;
import io.katharsis.resource.information.ResourceInformation;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.response.JsonApiResponse;
import io.katharsis.response.LinksInformation;
import io.katharsis.response.MetaInformation;

import java.util.Collections;
import java.util.Set;

/**
 * The adapter is used to create a common layer between controllers and repositories. Every repository can return either
 * a resource object or a {@link JsonApiResponse} response which should be returned by a controller. Ok, the last
 * sentence is not 100% true since interface based repositories can return only resources, but who's using it anyway?
 *
 * The methods need to know if a repository is interface- or annotation-based since repository methods have different
 * signatures.
 */
public abstract class ResponseRepository {


	protected ResourceInformation resourceInformation;
	protected ResourceRegistry resourceRegistry;
	
    public ResponseRepository(ResourceInformation resourceInformation, ResourceRegistry resourceRegistry) {
    	this.resourceRegistry = resourceRegistry;
    	this.resourceInformation = resourceInformation;
	}

	protected JsonApiResponse getResponse(Object repository, Object resource, QueryAdapter queryAdapter) {
        if (resource instanceof JsonApiResponse) {
            return (JsonApiResponse) resource;
        }

        Iterable resources;
        if (resource instanceof Iterable) {
            resources = (Iterable) resource;
        } else {
            resources = Collections.singletonList(resource);
        }
        MetaInformation metaInformation = getMetaInformation(repository, resources, queryAdapter);
        LinksInformation linksInformation = getLinksInformation(repository, resources, queryAdapter);

        return new JsonApiResponse()
            .setEntity(resource)
            .setLinksInformation(linksInformation)
            .setMetaInformation(metaInformation);
    }

    @SuppressWarnings("unchecked")
    private MetaInformation getMetaInformation(Object repository, Iterable<?> resources, QueryAdapter queryAdapter) {
        if (repository instanceof AnnotatedRepositoryAdapter) {
            if (((AnnotatedRepositoryAdapter) repository).metaRepositoryAvailable()) {
                return ((MetaRepository) repository).getMetaInformation(resources, toQueryParams(queryAdapter));
            }
        } else if (repository instanceof MetaRepository) {
            return ((MetaRepository) repository).getMetaInformation(resources, toQueryParams(queryAdapter));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private LinksInformation getLinksInformation(Object repository, Iterable<?> resources, QueryAdapter queryAdapter) {
        if (repository instanceof AnnotatedRepositoryAdapter) {
            if (((AnnotatedRepositoryAdapter) repository).linksRepositoryAvailable()) {
                return ((LinksRepository) repository).getLinksInformation(resources, toQueryParams(queryAdapter));
            }
        } else if (repository instanceof LinksRepository) {
            return ((LinksRepository) repository).getLinksInformation(resources, toQueryParams(queryAdapter));
        }
        return null;
    }
    
	protected QuerySpec toQuerySpec(QueryAdapter queryAdapter) {
	 	if(queryAdapter == null)
    		return null;
		if(queryAdapter instanceof QuerySpecAdapter){
			return ((QuerySpecAdapter)queryAdapter).getQuerySpec();
		}
		QueryParams queryParams = toQueryParams(queryAdapter);
    	FilterOperatorRegistry operatorRegistry = new FilterOperatorRegistry();
    	operatorRegistry.setDefaultOperator(getDefaultOperator());
    	for(FilterOperator operator : getSupportedOperators()){
    		operatorRegistry.register(operator);
    	}
    	
    	DefaultQuerySpecConverter converter = new DefaultQuerySpecConverter(resourceRegistry, operatorRegistry);
    	
    	Class<?> resourceClass = resourceInformation.getResourceClass();
    	return converter.fromParams(resourceClass, queryParams);
	}
	
	protected abstract FilterOperator getDefaultOperator() ;

	protected abstract Set<FilterOperator> getSupportedOperators() ;

	protected QueryParams toQueryParams(QueryAdapter queryAdapter) {
    	if(queryAdapter == null)
    		return null;
    	return ((QueryParamsAdapter)queryAdapter).getQueryParams();
	}
}
