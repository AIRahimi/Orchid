package com.eden.orchid.api.indexing;

import com.eden.orchid.api.theme.pages.OrchidPage;

import java.util.List;

public final class OrchidCompositeIndex extends OrchidIndex {

    public OrchidCompositeIndex(String ownKey) {
        super(ownKey);
    }

    @Override
    public Class<? extends OrchidIndex> childIndexClass() {
        return OrchidCompositeIndex.class;
    }

    public void mergeIndex(OrchidIndex index) {
        List<OrchidPage> indexPages = index.getAllPages();
        for(OrchidPage page : indexPages) {
            this.addToIndex(page.getReference().getPath(), page);
        }
    }
}
