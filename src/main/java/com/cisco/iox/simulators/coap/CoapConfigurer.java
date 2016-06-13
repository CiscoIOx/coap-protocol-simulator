package com.cisco.iox.simulators.coap;

import com.opencsv.CSVReader;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class CoapConfigurer {

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected VelocityEngine velocity; {
        velocity = new VelocityEngine();
        velocity.addProperty("resource.loader", "string");
        velocity.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
        velocity.init();
    }

    protected String confFile;

    protected CoapConfigurer() {}

    protected CoapConfigurer(String confFile) {
        this.confFile = confFile;
    }

    /**
     * Loads and returns the data sets from CSV file
     *
     * @param config
     * @param configFile
     * @return
     * @throws IOException
     */
    protected List<String[]> readDataset(Configuration config, File configFile) throws IOException {
        parseConfig(config, configFile);
        File csvFile = new File(config.dataset);
        if(!csvFile.isAbsolute())
            csvFile = new File(configFile.getParent(), csvFile.getPath());
        CSVReader csvReader = new CSVReader(new FileReader(csvFile));
        List<String[]> rows = csvReader.readAll();
        if(rows.size() < 2){
            throw new CoapSimulatorException("No data found in csv file " + csvFile.getName());
        }
        validateData(rows);
        loadTemplates(config, configFile);
        return rows;
    }

    /**
     * Performs row count validations. Row count has to be > 1
     * Removes invalid rows, which do not match with the column headers
     * @param rows
     */
    private void validateData(List<String[]> rows) {
        int line = 0;
        final Iterator<String[]> iter = rows.iterator();
        ++line;
        int columnsCount = iter.next().length;
        if(columnsCount==0){
            throw new CoapSimulatorException("zero columns found in csv file");
        }
        // remove invalid rows
        while(iter.hasNext()){
            ++line;
            final String[] row = iter.next();
            int len = row.length;
            if(len<columnsCount) {
                log.error("removed invalid row at line "+line+": "+ Arrays.asList(row));
                iter.remove();
            }
        }
        if(rows.size()==1){
            throw new CoapSimulatorException("No data found in csv file");
        }
    }

    /**
     * Returns a file object for the given file path: confFile
     * To be used by subclasses alone.. not to be confused with the getter for confFile
     * @return
     * @throws URISyntaxException
     */
    protected File getConfigFile() throws URISyntaxException {
        File confFile = new File(this.confFile);
        if (!confFile.exists() && !confFile.isAbsolute()) {
            confFile = new File(getClass().getResource("/"+this.confFile).getFile());
        }
        return confFile;
    }

    /**
     * Loads content templates from file if required
     *
     * @param config
     * @param configFile
     * @throws IOException
     */
    private void loadTemplates(Configuration config, File configFile) throws IOException {
        for (ResourceConfig resource : config.resources) {
            resource.uri = trimURI(resource.uri, "resource uri");
            String template = resource.contentTemplate;
            if(template==null) {
                if(resource.contentTemplateFile==null)
                    throw new IllegalArgumentException("either contentTemplate or contentTemplateFile must be specified for resource");
                File templateFile = new File(resource.contentTemplateFile);
                if(!templateFile.isAbsolute())
                    templateFile = new File(configFile.getParent(), templateFile.getPath());
                if(!templateFile.exists())
                    throw new IllegalArgumentException("contentTemplateFile "+templateFile+" does not exist");
                if(!templateFile.isFile())
                    throw new IllegalArgumentException("contentTemplateFile "+templateFile+" is not a file");
                resource.contentTemplate = new String(Files.readAllBytes(Paths.get(templateFile.getPath())));
            }
            StringResourceLoader.getRepository().putStringResource(resource.uri, resource.contentTemplate);
            try {
                resource.template = velocity.getTemplate(resource.uri);
            } catch (ParseErrorException ex) {
                throw new IllegalArgumentException("invalid velocity template for resource "+resource.uri, ex);
            }
        }
    }

    private void parseConfig(Configuration config, File configFile) throws IOException {
        config.urn = trimURI(config.urn, "urn");
        for (ResourceConfig resource : config.resources) {
            resource.uri = trimURI(resource.uri, "resource uri");
            String template = resource.contentTemplate;
            if(template==null) {
                if(resource.contentTemplateFile==null)
                    throw new CoapSimulatorException("either contentTemplate or contentTemplateFile " +
                            "must be specified for resource");
                File templateFile = new File(resource.contentTemplateFile);
                if(!templateFile.isAbsolute())
                    templateFile = new File(configFile.getParent(), templateFile.getPath());
                if(!templateFile.exists())
                    throw new CoapSimulatorException("contentTemplateFile "+templateFile+" does not exist");
                if(!templateFile.isFile())
                    throw new CoapSimulatorException("contentTemplateFile "+templateFile+" is not a file");
                resource.contentTemplate = new String(Files.readAllBytes(Paths.get(templateFile.getPath())));
            }
            StringResourceLoader.getRepository().putStringResource(resource.uri, resource.contentTemplate);
            try {
                resource.template = velocity.getTemplate(resource.uri);
            } catch (ParseErrorException ex) {
                throw new CoapSimulatorException("invalid velocity template for resource "+resource.uri, ex);
            }
        }
    }

    protected static String trimURI(String uri, String name){
        if(uri==null)
            throw new IllegalArgumentException(name+" is not specified");
        uri = uri.trim();
        if(uri.startsWith("/"))
            uri = uri.substring(1);
        if(uri.endsWith("/"))
            uri = uri.substring(0, uri.length()-1);
        if(uri.isEmpty())
            throw new IllegalArgumentException(name+" is empty");
        return uri;
    }

    public String getConfFile() {
        return confFile;
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }
}
