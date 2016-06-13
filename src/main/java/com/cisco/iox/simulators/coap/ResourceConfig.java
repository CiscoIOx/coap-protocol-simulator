package com.cisco.iox.simulators.coap;

import java.io.StringWriter;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 * @author Santhosh Kumar Tekuri
 */
public class ResourceConfig {
    public String uri;
    public ContentType contentType;
    public String contentTemplate;
    public String contentTemplateFile;
    public Template template;

    public String evaluate(List<String[]> rows, int row){
        String[] headers = rows.get(0);
        String values[] = rows.get(row);
        StringWriter writer = new StringWriter();
        VelocityContext context = new VelocityContext();
        for (int i = 0; i < headers.length; i++)
            context.put(headers[i], values[i]);
        template.merge(context, writer);
        return writer.toString();
    }
}
