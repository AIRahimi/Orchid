package com.eden.orchid.api.render;

import com.eden.orchid.api.OrchidService;
import com.eden.orchid.api.theme.pages.OrchidPage;

import java.io.InputStream;

public interface RenderService extends OrchidService {

    @Override
    default String getKey() {
        return "renderer";
    }

    /**
     * Render the given page with the default template determined by the page, returning an InputStream representing the
     * final contents. The layout chosen for the page is determined by {@link TemplateResolutionStrategy}.
     *
     * @param page the page to render
     * @return InputStream the stream representing the final contents
     *
     * @since v1.0.0
     */
    default InputStream getRenderedTemplate(OrchidPage page) {
        return getService(RenderService.class).getRenderedTemplate(page);
    }

    /**
     * Render the given page with the default template determined by the page, producing a side-effect as the intended
     * final output. The layout chosen for the page is determined by {@link TemplateResolutionStrategy}.
     *
     * @param page the page to render
     * @return true if the page was successfully rendered, false otherwise
     *
     * @since v1.0.0
     */
    default boolean renderTemplate(OrchidPage page) {
        return getService(RenderService.class).renderTemplate(page);
    }

    /**
     * Render the given page using a literal String as a template, returning an InputStream representing the final
     * contents. More useful for testing, as templates should be preferred for the ability to be overridden.
     *
     * @param page the page to render
     * @param extension the extension that the content represents and should be compiled against
     * @param templateString the template string
     * @return InputStream the stream representing the final contents
     *
     * @since v1.0.0
     */
    default InputStream getRenderedString(OrchidPage page, String extension, String templateString) {
        return getService(RenderService.class).getRenderedString(page, extension, templateString);
    }

    /**
     * Render the given page using a literal String as a template, producing a side-effect as the intended final output.
     * More useful for testing, as templates should be preferred for the ability to be overridden.
     *
     * @param page the page to render
     * @param extension the extension that the content represents and should be compiled against
     * @param templateString the template string to render
     * @return true if the page was successfully rendered, false otherwise
     *
     * @since v1.0.0
     */
    default boolean renderString(OrchidPage page, String extension, String templateString) {
        return getService(RenderService.class).renderString(page, extension, templateString);
    }

    /**
     * Render the content of a page directly, without any template, returning an InputStream representing the
     * final contents. The contents may still be preprocessed, and is useful for rendering text assets like CSS or JS.
     *
     * @param page the page to render
     * @return InputStream the stream representing the final contents
     *
     * @since v1.0.0
     */
    default InputStream getRenderedRaw(OrchidPage page) {
        return getService(RenderService.class).getRenderedRaw(page);
    }

    /**
     * Render the content of a page directly, without any template, producing a side-effect as the intended final
     * output. The contents may still be preprocessed, and is useful for rendering text assets like CSS or JS.
     *
     * @param page the page to render
     * @return true if the page was successfully rendered, false otherwise
     *
     * @since v1.0.0
     */
    default boolean renderRaw(OrchidPage page) {
        return getService(RenderService.class).renderRaw(page);
    }

    /**
     * Render the content of a page directly, as a binary stream, returning an InputStream representing the final
     * contents. No further processing is performed on the file contents, so as to preserve the binary format.
     *
     * @param page the page to render
     * @return InputStream the stream used to create side-effects by the render operation
     *
     * @since v1.0.0
     */
    default InputStream getRenderedBinary(OrchidPage page) {
        return getService(RenderService.class).getRenderedBinary(page);
    }

    /**
     * Render the content of a page directly, as a binary stream, producing a side-effect as the intended final
     * output. No further processing is performed on the file contents, so as to preserve the binary format.
     *
     * @param page the page to render
     * @return true if the page was successfully rendered, false otherwise
     *
     * @since v1.0.0
     */
    default boolean renderBinary(OrchidPage page) {
        return getService(RenderService.class).renderBinary(page);
    }

}
