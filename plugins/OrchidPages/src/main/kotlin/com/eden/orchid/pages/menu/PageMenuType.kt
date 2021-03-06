package com.eden.orchid.pages.menu

import com.eden.common.util.EdenUtils
import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.options.annotations.Option
import com.eden.orchid.api.theme.menus.menuItem.OrchidMenuItem
import com.eden.orchid.api.theme.menus.menuItem.OrchidMenuItemImpl
import java.util.TreeSet
import javax.inject.Inject

class PageMenuType @Inject
constructor(context: OrchidContext) : OrchidMenuItem(context, "page", 100) {

    @Option
    lateinit var page: String

    override fun getMenuItems(): List<OrchidMenuItemImpl> {
        val menuItems = ArrayList<OrchidMenuItemImpl>()

        val pages = context.internalIndex.getGeneratorPages("pages")

        if (!EdenUtils.isEmpty(this.page)) {
            for (page in pages) {
                if (page.title == this.page) {
                    menuItems.add(OrchidMenuItemImpl(context, page))
                }
            }
        }

        return menuItems
    }
}

