package com.eden.orchid.api.resources.resource;

import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.options.OrchidFlags;
import com.eden.orchid.api.theme.pages.OrchidReference;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * A Resource type that provides a content to a template from a resource file on disk. When used with
 * renderTemplate() or renderString(), this resource will supply the `page.content` variable to the template renderer as
 * the file contents after having the embedded data removed, and any embedded data will be available in the renderer
 * through the `page` variable. When used with renderRaw(), the raw contents (after having the embedded data removed)
 * will be written directly instead.
 */
public final class FileResource extends FreeableResource  {

    private final File file;

    public FileResource(OrchidContext context, File file) {
        this(file, new OrchidReference(context, FileResource.pathFromFile(context, file)));
    }

    public FileResource(File file, OrchidReference reference) {
        super(reference);
        this.file = file;
    }

    @Override
    protected void loadContent() {
        if(rawContent == null) {
            try {
                if (file != null) {
                    rawContent = IOUtils.toString(new FileInputStream(file), Charset.defaultCharset());
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.loadContent();
    }

    private static String pathFromFile(OrchidContext context, File file) {
        String filePath = file.getPath();
        String basePath = OrchidFlags.getInstance().getString("resourcesDir");
        basePath = basePath.replaceAll("\\\\", "/");
        filePath = filePath.replaceAll("\\\\", "/");

        if(filePath.startsWith(basePath)) {
            filePath = filePath.replaceAll(basePath, "");
        }

        return filePath;
    }

    @Override
    public InputStream getContentStream() {
        try {
            return new FileInputStream(file);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
