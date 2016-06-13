### What does CoAP client simulator do?
The CoAP simulator reads values based on input read from a CSV file.  The client simulator will send these values to the CoAP server hosted within the IoX middleware. CoAP server runs at 5684 on the middleware.

The URL to which the request will be posted - coap://<host>/<port>/<urn>/<resource.uri>?deviceId=$deviceId

Where,
- host – CoAP server IP, provided as command line argument during the simulator start.
- port - CoAP server IP, provided as command line argument during the simulator start.
- urn – part of the configuration, see below. Unique resource path for this device.
- resource.uri – identifier for each resource defined in the configuration, see below.
- deviceId – the device identifier defined during the device provisioning. This parameter will help in matching the data posted to the device configured.

### Configure simulator

#### Sample configuration – raw content 

```json
Sample configuration – raw content 
{  
  "urn":"Container",
  "dataset":"temperature.csv", 
  “deviceId”:”containerA”,
  “intervalInMilliSeconds”:1000,
  "resources":[  
    {  
      "uri":"temperature",
      "contentType":"raw",
      "contentTemplate":"${temperature}"
    },
    {  
      "uri":"humidity",
      "contentType":"raw",
      "contentTemplate":"${humidity}"
     }
  ]
}
```

#### Sample configuration - JSON content
```json
{  
      "uri":" Container/json",
      "contentType":"json",
      "contentTemplateFile":"temperature.template"   
}
```

#### Sample configuration  - CBOR content
```json
{  
      "uri":" Container/cbor",
      "contentType":"cbor",
      "contentTemplateFile":"temperature.template"
    }
```

- **dataset**: relative path to the CSV file. The dataset is a comma separated file which defines a set of columns, which can be configured as sensor fields and each role defines a set of data points. This is mandatory. The data from this CSV file is used to serve the requests. Each request picks up a row in the CSV file. it will wait for the defined interval “intervalInMilliSeconds “ between each row. Once all rows are used,  the cycle repeats from the first row onwards.

- **resources.contentType**: This is mandatory. Value can be raw or JSON or CBOR. For raw, the data reference is inlined as a CSV column. In case of JSON, the velocity template file option should be used to define the JSON structure. The values from CSV row are substituted into this velocity template to generate velocity response. In case of CBOR, the template should generate a JSON response; the simulator will convert it into a binary JSON format (CBOR).

- **resources.contentTemplate and resources.contentTemplateFile**: One of these two must be specified for each resource. contentTemplate if specified is treated as velocity template content. Alternatively, one can create a file containing the velocity template content. Specify the relative path to that template file in contentTemplateFile property. 

#### Sample Input - CSV
```csv
temperature,humidity
50.1,22.3
39.2,31.4
99.23,61.2
105.6,71.6
78.2,51.4
65.2,48.12
111.5,99.67
89.0,26.21
18.3,2.67
```

#### Run the simulator

	java -jar coap-simulator-1.0.jar client <config-file> <host> <port>

### CoAP server simulator

#### What does CoAP server simulator do?

The CoAP simulator reads values based on input read from a CSV file.  The server simulator will respond with these values when a request arrives from the CoAP client.

The URL to which the server will respond - coap://<host>/<port>/<urn>/<resource.uri> 

Where,
- host – Host interface, provided as command line argument during the simulator start.
- port – port on which the server simulator is bound, provided as command line argument during the simulator start.
- urn – part of the configuration, see below. Unique resource path for this device.
- resource.uri – identifier for each resource defined in the configuration, see below.

#### Configure simulator

##### Sample configuration – raw content 
```json
{  
  "urn":"Container",
  "dataset":"temperature.csv", 
  "resources":[  
    {  
      "uri":"temperature",
      "contentType":"raw",
      "contentTemplate":"${temperature}"
    },
    {  
      "uri":"humidity",
      "contentType":"raw",
      "contentTemplate":"${humidity}"
     }
  ]
}
```

##### Sample configuration - JSON content
```json
{  
      "uri":"Container/json",
      "contentType":"json",
      "contentTemplateFile":"temperature.template"   
}
```

##### Sample configuration  - CBOR content
```json
{  
      "uri":" Container/cbor",
      "contentType":"cbor",
      "contentTemplateFile":"temperature.template"
    }
```

- **dataset**: relative path to the CSV file. The dataset is a comma separated file which defines a set of columns, which can be configured as sensor fields and each role defines a set of data points. This is mandatory.
The data from this CSV file is used to serve the requests. Each request picks up a row in the CSV file. Once all rows are used,  the cycle repeats from the first row onwards.
- **resources.contentType**: This is mandatory. Value can be raw or JSON or CBOR. For raw, the data reference is inlined as a CSV column. In case of JSON, the velocity template file option should be used to define the JSON structure. The values from CSV row are substituted into this velocity template to generate velocity response. In case of CBOR, the template should generate a JSON response; the simulator will convert it into a binary JSON format (CBOR).
- **resources.contentTemplate and resources.contentTemplateFile**: One of these two must be specified for each resource. contentTemplate if specified is treated as velocity template content. Alternatively, one can create a file containing the velocity template content. Specify the relative path to that template file in contentTemplateFile property. 

#### Sample Input - CSV
```csv
temperature,humidity
50.1,22.3
39.2,31.4
99.23,61.2
105.6,71.6
78.2,51.4
65.2,48.12
111.5,99.67
89.0,26.21
18.3,2.67
```

#### Run the simulator
	java -jar coap-simulator-1.0.jar server <config-file> <host> <port>