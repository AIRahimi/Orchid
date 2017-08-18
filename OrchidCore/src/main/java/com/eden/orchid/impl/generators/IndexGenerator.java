package com.eden.orchid.impl.generators;

import com.eden.common.json.JSONElement;
import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.generators.OrchidGenerator;
import com.eden.orchid.api.indexing.OrchidIndex;
import com.eden.orchid.api.render.OrchidRenderer;
import com.eden.orchid.api.resources.resource.JsonResource;
import com.eden.orchid.api.resources.resource.OrchidResource;
import com.eden.orchid.api.theme.pages.OrchidPage;
import com.eden.orchid.api.theme.pages.OrchidReference;
import com.eden.orchid.impl.indexing.OrchidInternalIndex;
import com.eden.orchid.impl.indexing.OrchidRootInternalIndex;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class IndexGenerator extends OrchidGenerator {

    @Inject
    public IndexGenerator(OrchidContext context, OrchidRenderer renderer) {
        super(1, "indices", context, renderer);
    }

    @Override
    public String getDescription() {
        return "Generates index files to connect your site to others.";
    }

    @Override
    public List<? extends OrchidPage> startIndexing() {
        return null;
    }

    @Override
    public void startGeneration(List<? extends OrchidPage> pages) {
        generateIndexFiles();
    }

    private void generateIndexFiles() {
        OrchidRootInternalIndex internalIndex = context.getInternalIndex();
        Map<String, OrchidInternalIndex> mappedIndex = internalIndex.getAllIndexedPages();

        OrchidIndex indices = new OrchidInternalIndex("indices");

        // Render an page for each generator's individual index
        mappedIndex.keySet().forEach(key -> {
            OrchidResource resource = new JsonResource(new JSONElement(mappedIndex.get(key).toJSON()), new OrchidReference(context, "meta/" + key + ".index.json"));
            OrchidPage page = new OrchidPage(resource, "index");
            page.getReference().setUsePrettyUrl(false);
            renderer.renderRaw(page);

            indices.addToIndex(indices.getOwnKey() + "/" + page.getReference().getPath(), page);
        });

        // Render full composite index page
        OrchidResource compositeIndexResource = new JsonResource(new JSONElement(internalIndex.toJSON()), new OrchidReference(context, "meta/index.json"));
        OrchidPage compositeIndexPage = new OrchidPage(compositeIndexResource, "index");
        compositeIndexPage.getReference().setUsePrettyUrl(false);
        renderer.renderRaw(compositeIndexPage);
        indices.addToIndex(indices.getOwnKey() + "/" + compositeIndexPage.getReference().getPath(), compositeIndexPage);

        // Render an index of all indices, so individual index pages can be found
        OrchidResource indicesIndexResource = new JsonResource(new JSONElement(indices.toJSON()), new OrchidReference(context, "meta/indices.json"));
        OrchidPage indicesPage = new OrchidPage(indicesIndexResource, "index");
        indicesPage.getReference().setUsePrettyUrl(false);
        renderer.renderRaw(indicesPage);
    }
}