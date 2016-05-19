# Unveiled-Server (Java-Stack)
[![Build Status](https://travis-ci.org/SAS-Systems/Unveiled-Server.svg?branch=master)](https://travis-ci.org/SAS-Systems/Unveiled-Server)


## API Description
The following URLs can be used to interact with the Java-Stack of the Unveiled-Server:

| URL | Method | Header | Paremeters | Description |
|-----|--------|--------|------------|-------------|
|`{unveiled.base}/UploadFile` | POST | user:Int(ID-reference),<br/> token:String | latitude:Double,<br/> longitude:Double,<br/> public:Boolean,<br/> file:&lt;BinaryData&gt; | Is used to upload a file to the server. The parameters and the file content must be send via a multipart body (see screenshot below). |
||||

![Postman screenshot of the UploadFile](https://raw.githubusercontent.com/SAS-Systems/Unveiled-Server/master/UploadFilePostman.png)
