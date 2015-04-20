package io.katharsis.queryParams;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class RequestParams {
    private JsonNode filters;
    private JsonNode sorting;
    private List<String> grouping;
    private JsonNode pagination;
    private List<String> includedFields;
    private List<String> includedRelations;
    private ObjectMapper objectMapper;

    public RequestParams(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode getFilters() {
        return filters;
    }

    public void setFilters(String filters) throws IOException {
        this.filters = objectMapper.readTree(filters);
    }

    public JsonNode getSorting() {
        return sorting;
    }

    public void setSorting(String sorting) throws IOException {
        this.sorting = objectMapper.readTree(sorting);
    }

    public List getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) throws IOException {
        this.grouping = objectMapper.readValue(grouping, new TypeReference<List<String>>() {
        });
    }

    public JsonNode getPagination() {
        return pagination;
    }

    public void setPagination(String pagination) throws IOException {
        this.pagination = objectMapper.readTree(pagination);
    }

    public List getIncludedFields() {
        return includedFields;
    }

    public void setIncludedFields(String includedFields) throws IOException {
        this.includedFields = objectMapper.readValue(includedFields, new TypeReference<List<String>>() {
        });
    }

    public List getIncludedRelations() {
        return includedRelations;
    }

    public void setIncludedRelations(String includedRelations) throws IOException {
        this.includedRelations = objectMapper.readValue(includedRelations, new TypeReference<List<String>>() {
        });
    }

}
