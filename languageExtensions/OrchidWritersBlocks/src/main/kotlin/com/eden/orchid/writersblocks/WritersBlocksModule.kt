package com.eden.orchid.writersblocks

import com.eden.orchid.api.compilers.TemplateFunction
import com.eden.orchid.api.compilers.TemplateTag
import com.eden.orchid.api.registration.OrchidModule
import com.eden.orchid.api.resources.resourceSource.PluginResourceSource
import com.eden.orchid.writersblocks.functions.Nl2brFunction
import com.eden.orchid.writersblocks.functions.PluralizeFunction
import com.eden.orchid.writersblocks.tags.AlertTag
import com.eden.orchid.writersblocks.tags.GistTag
import com.eden.orchid.writersblocks.tags.InstagramTag
import com.eden.orchid.writersblocks.tags.TwitterTag
import com.eden.orchid.writersblocks.tags.YoutubeTag

class WritersBlocksModule : OrchidModule() {

    override fun configure() {
        addToSet(PluginResourceSource::class.java,
                WritersBlocksResourceSource::class.java)

        addToSet(TemplateFunction::class.java,
                PluralizeFunction::class.java,
                Nl2brFunction::class.java
        )

        addToSet(TemplateTag::class.java,
                AlertTag::class.java,
                GistTag::class.java,
                YoutubeTag::class.java,
                TwitterTag::class.java,
                InstagramTag::class.java
        )
    }
}

