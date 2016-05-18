# Unveiled-Server (Java-Stack)


## API Description
The following URLs can be used to interact with the Java-Stack of the Unveiled-Server:

| URL | Method | Paremeters | Description |
|-----|--------|------------|-------------|
|`{unveiled.base}/UploadFile` | POST | author:Int(ID-reference),<br/> latitude:Double,<br/> longitude:Double,<br/> public:Boolean,<br/> file:&lt;BinaryData&gt; | Is used to upload a file to the server. The parameters and the file content must be send via a multipart body (see screenshot below). |
||||

![Postman screenshot of the UploadFile](https://raw.githubusercontent.com/SAS-Systems/Unveiled-Server/master/UploadFilePostman.png)
